package com.tanaka.mazivanhanga.tanakaanimeprototype

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.tanaka.mazivanhanga.tanakaanimeprototype.api.ApiHandler
import com.tanaka.mazivanhanga.tanakaanimeprototype.databinding.ActivityMainBinding
import com.tanaka.mazivanhanga.tanakaanimeprototype.databinding.ShowDetailEpisodeListBinding
import com.tanaka.mazivanhanga.tanakaanimeprototype.models.Episode
import com.tanaka.mazivanhanga.tanakaanimeprototype.models.LatestShow
import com.tanaka.mazivanhanga.tanakaanimeprototype.models.SearchResult
import com.tanaka.mazivanhanga.tanakaanimeprototype.util.DataState
import com.tanaka.mazivanhanga.tanakaanimeprototype.views.LatestEpisodeAdapter
import com.tanaka.mazivanhanga.tanakaanimeprototype.views.LatestEpisodeViewModel
import com.tanaka.mazivanhanga.tanakaanimeprototype.views.SearchResultAdapter


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var adapter: LatestEpisodeAdapter
    lateinit var searchAdapter: SearchResultAdapter
    lateinit var viewModel: LatestEpisodeViewModel
    var episodeList: List<Episode> = ArrayList()
    lateinit var latestShow: LatestShow
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        iniitAdapters()

        episodeAdapter = getMyEpisodeAdapter()

        initViews()
        viewModel = ViewModelProviders.of(this).get(LatestEpisodeViewModel::class.java)

        subscribeObservers()


        viewModel.getData(this)

    }

    private fun iniitAdapters() {
        searchAdapter = SearchResultAdapter(object : SearchResultAdapter.Interaction {
            override fun onItemSelected(position: Int, item: SearchResult) {
                Log.i("VIDEO", item.toString())
                latestShow = LatestShow(item.title, item.poster, item.link, "", "")
                //call get for episodes
                viewModel.getEpisodes(item.link, this@MainActivity)

            }

        })
        adapter = LatestEpisodeAdapter(object : LatestEpisodeAdapter.Interaction {
            override fun onItemSelected(position: Int, item: LatestShow) {
                Log.i("VIDEO", item.toString())
                latestShow = item
                viewModel.getEpisodes(item.url, this@MainActivity)


            }
        })
    }

    private fun initViews() {
        binding.searchResultRecyclerView.apply {
            adapter = searchAdapter
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }
        binding.latestEpisodeRecyclerView.apply {
            this.adapter = this@MainActivity.adapter
            this.layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            this.setHasFixedSize(true)
        }
        binding.swiperefresh.setOnRefreshListener {
            val runnable = Runnable {
                viewModel.deleteCacheEpisodes(this)
            }
            AsyncTask.execute(runnable)
            viewModel.getData(this)
        }
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(this, Observer {
            when (it) {
                is DataState.Success<List<LatestShow>> -> {
                    binding.swiperefresh.isRefreshing = false
                    adapter.submitList(it.data)
                }
                is DataState.Loading -> {
                    binding.swiperefresh.isRefreshing = true
                    Log.d("Main", "Is Loading")
                }
                is DataState.Error -> {
                    Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                    binding.swiperefresh.isRefreshing = false
                }
            }
        })

        viewModel.searchDataState.observe(this, Observer {
            when (it) {
                is DataState.Success -> {
                    binding.swiperefresh.isRefreshing = false
                    searchAdapter.submitList(it.data)
                }
                is DataState.Loading -> {
                    binding.swiperefresh.isRefreshing = true
                    Log.d("Main", "Is Loading")
                }
                is DataState.Error -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                    Log.e("Main", it.exception.message, it.exception)
                    binding.swiperefresh.isRefreshing = false
                }
            }
        })

        viewModel.episodeDataState.observe(this, Observer {
            when (it) {
                is DataState.Success -> {
                    binding.swiperefresh.isRefreshing = false
                    println(">>>>>>>>>>>>>>>>>>TANAKA SUCCESS>>>>>>>>>>>>>>>>>>>>>")
                    println(it.data)
                    episodeList = it.data
                    episodeAdapter.notifyDataSetChanged()
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle(latestShow.title)
                        .setView(buildSampleListView())
                        .setPositiveButton(android.R.string.ok, null)
                        .create()
                        .show()
                }
                is DataState.Loading -> {
                    binding.swiperefresh.isRefreshing = true
                    Log.d("Main", "Is Loading")
                }
                is DataState.Error -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                    Log.e("Main", it.exception.message, it.exception)
                    binding.swiperefresh.isRefreshing = false
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        val searchItem = menu?.findItem(R.id.app_bar_search)
        val searchView = searchItem?.actionView as SearchView

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                if (searchAdapter.itemCount > 0) {
                    binding.apply {
                        searchResultLayout.visibility = VISIBLE
                        latestEpisodeLayout.visibility = GONE
                    }
                }
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                binding.apply {
                    searchResultLayout.visibility = GONE
                    latestEpisodeLayout.visibility = VISIBLE
                }
                return true
            }

        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(this@MainActivity, query, Toast.LENGTH_SHORT).show()
                query?.let {
                    viewModel.searchForShow(it, this@MainActivity)
                    binding.apply {

                        searchResultLayout.visibility = VISIBLE
                        latestEpisodeLayout.visibility = GONE
                    }

                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.choose_url -> {
                val builder =
                    AlertDialog.Builder(this)
                builder.setTitle("Choose a base url")
                val baseUrls =
                    arrayOf("http://10.147.1.162:8004/","http://10.147.1.153:8004/")
                builder.setItems(
                    baseUrls
                ) { dialog, which ->
                    ApiHandler.setBaseUrl(baseUrls[which])
                dialog.dismiss()
                }
                val dialog = builder.create()
                dialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearDisposable()
    }


    private fun buildSampleListView(): View? {
        val dialogList: View = ShowDetailEpisodeListBinding.inflate(layoutInflater).root
        val sampleList = dialogList.findViewById<ListView>(R.id.episode_list)
        episodeAdapter = getMyEpisodeAdapter()
        sampleList.adapter = episodeAdapter
        sampleList.onItemClickListener =
            OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                //go to video view
                val intent = Intent(this, VideoActivity::class.java)
                val episode = episodeList[position]
                latestShow.currentEp = episode.title
                intent.putExtra(Constants.CURRENT_SHOW, latestShow)
                intent.putExtra(Constants.SHOW_EPISODE_URL, episode.link)
                startActivity(intent)
            }
        return dialogList
    }

    private fun getMyEpisodeAdapter(): ArrayAdapter<Episode> {
        return object : ArrayAdapter<Episode>(
            this,
            android.R.layout.simple_list_item_2,
            android.R.id.text1,
            episodeList
        ) {
            override fun getView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val views = super.getView(position, convertView, parent)
                val text1 =
                    views.findViewById<View>(android.R.id.text1) as TextView
                val text2 =
                    views.findViewById<View>(android.R.id.text2) as TextView
                val episode = episodeList[position]
                text1.text = episode.title
                text2.text = episode.subtitle
                return views
            }
        }
    }

    lateinit var episodeAdapter: ArrayAdapter<Episode>


}
