package com.tanaka.mazivanhanga.tanakaanimeprototype

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.tanaka.mazivanhanga.tanakaanimeprototype.Constants.SHOW_EPISODE_URL
import com.tanaka.mazivanhanga.tanakaanimeprototype.databinding.ActivityVideoBinding
import com.tanaka.mazivanhanga.tanakaanimeprototype.models.VideoRequestBody
import com.tanaka.mazivanhanga.tanakaanimeprototype.views.VideoViewModel


class VideoActivity : AppCompatActivity() {
    lateinit var binding: ActivityVideoBinding
    lateinit var episodeUrl: String
    private var _player: SimpleExoPlayer? = null
    val player get() = _player!!

    lateinit var viewModel: VideoViewModel
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_video)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        episodeUrl = intent.getStringExtra(SHOW_EPISODE_URL)!!
        viewModel = ViewModelProviders.of(this).get(VideoViewModel::class.java)
        viewModel.videLiveData.observe(this, Observer {
            hideSystemUi()
            initPlayer(it.video)
        })
        viewModel.getData(VideoRequestBody(episodeUrl))

    }

    private fun initPlayer(episodeUrl: String) {
        _player = SimpleExoPlayer.Builder(this).build()
        binding.videoView.player = player
        val uri = Uri.parse(episodeUrl)
        val mediaSource = buildMediaSource(uri)
        player.playWhenReady = playWhenReady;
        player.seekTo(currentWindow, playbackPosition);
        player.prepare(mediaSource, false, false);
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(this, "exoplayer-codelab")
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(uri)
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        binding.videoView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun releasePlayer() {
        if (_player != null) {
            playWhenReady = player.playWhenReady
            playbackPosition = player.currentPosition
            currentWindow = player.currentWindowIndex
            player.release()
            _player = null
        }
    }

}