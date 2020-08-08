package com.tanaka.mazivanhanga.tanakaanimeprototype

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.tanaka.mazivanhanga.tanakaanimeprototype.databinding.ActivityMainBinding
import com.tanaka.mazivanhanga.tanakaanimeprototype.models.LatestShow
import com.tanaka.mazivanhanga.tanakaanimeprototype.views.LatestEpisodeAdapter
import com.tanaka.mazivanhanga.tanakaanimeprototype.views.LatestEpisodeViewModel

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var adapter: LatestEpisodeAdapter
    lateinit var viewModel: LatestEpisodeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        adapter = LatestEpisodeAdapter(object : LatestEpisodeAdapter.Interaction {
            override fun onItemSelected(position: Int, item: LatestShow) {
                Log.i("VIDEO", item.toString())
            }
        })

        binding.latestEpisodeRecyclerView.apply {
            this.adapter = this@MainActivity.adapter
            this.layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            this.setHasFixedSize(true)
        }

        viewModel = ViewModelProviders.of(this).get(LatestEpisodeViewModel::class.java)
        viewModel.latestEpisodesLiveData.observe(this, Observer {
            println(it)
            binding.swiperefresh.isRefreshing = false
            adapter.submitList(it)
        })

        binding.swiperefresh.setOnRefreshListener {
            viewModel.getData()
        }
        viewModel.getData()

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearDisposable()
    }
}