package com.tanaka.mazivanhanga.tanakaanimeprototype

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.cast.CastPlayer
import com.google.android.exoplayer2.ext.cast.SessionAvailabilityListener
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.MediaQueueItem
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.common.images.WebImage
import com.tanaka.mazivanhanga.tanakaanimeprototype.Constants.CURRENT_SHOW
import com.tanaka.mazivanhanga.tanakaanimeprototype.Constants.SHOW_EPISODE_URL
import com.tanaka.mazivanhanga.tanakaanimeprototype.databinding.ActivityVideoBinding
import com.tanaka.mazivanhanga.tanakaanimeprototype.models.LatestShow
import com.tanaka.mazivanhanga.tanakaanimeprototype.models.VideoRequestBody
import com.tanaka.mazivanhanga.tanakaanimeprototype.views.VideoViewModel


class VideoActivity : AppCompatActivity(), SessionAvailabilityListener {
    lateinit var binding: ActivityVideoBinding
    lateinit var episodeUrl: String
    private var _player: SimpleExoPlayer? = null
//    val player get() = _player!!

    // the local and remote players
    private var _castPlayer: CastPlayer? = null
    private var _currentPlayer: Player? = null
    lateinit var latestShow: LatestShow


    // the Cast context
    private lateinit var castContext: CastContext
    private lateinit var castButton: MenuItem

    // Player state params
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    lateinit var viewModel: VideoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_video)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        castContext = CastContext.getSharedInstance(this)
        setContentView(binding.root)
        episodeUrl = intent.getStringExtra(SHOW_EPISODE_URL)!!
        latestShow = (intent.getSerializableExtra(CURRENT_SHOW) as LatestShow)
        binding.root.setBackgroundColor(Color.BLACK)
        viewModel = ViewModelProviders.of(this).get(VideoViewModel::class.java)
        binding.videoProgress.apply{
            visibility = VISIBLE
        }
        viewModel.videLiveData.observe(this, Observer {
//            hideSystemUi()
            Log.d("Video", it.video)
            binding.videoProgress.apply{
                visibility = INVISIBLE
            }
            initPlayer(it.video)
        })
        viewModel.getData(VideoRequestBody(episodeUrl), this)
       when(resources.configuration.orientation){
           Configuration.ORIENTATION_LANDSCAPE -> {
               hideSystemUi()
           }
           Configuration.ORIENTATION_PORTRAIT -> {
               showSystemUI()
           }
       }


    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val newOrientation = newConfig.orientation

        when (newOrientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                hideSystemUi()
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                showSystemUI()
            }

        }


    }

    lateinit var videoURL: String

    private fun initPlayer(videoURL: String) {
        this.videoURL = videoURL
        _player = SimpleExoPlayer.Builder(this).build()
        binding.videoView.player = _player
        if (_castPlayer == null) {

            _castPlayer = CastPlayer(castContext)
            _castPlayer?.setSessionAvailabilityListener(this)
        }

        // start the playback
        if (_castPlayer?.isCastSessionAvailable == true) {
            playOnPlayer(_castPlayer)
            binding.apply {
                castControlView.visibility = VISIBLE
            }
        } else {
            playOnPlayer(_player)
        }

    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(this, "exoplayer-codelab")
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(uri)
    }


    /**
     * We need to populate the Cast button across all activities as suggested by Google Cast Guide:
     * https://developers.google.com/cast/docs/design_checklist/cast-button#sender-cast-icon-available
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val result = super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.video_menu, menu)
        menu?.findItem(R.id.app_bar_search)?.isVisible = false
        castButton = CastButtonFactory.setUpMediaRouteButton(
            applicationContext,
            menu,
            R.id.media_route_menu_item
        )
        return result
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        binding.videoView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            supportActionBar?.hide()

    }

    private fun showSystemUI() {
        binding.videoView.systemUiVisibility = VISIBLE
        supportActionBar?.show()
    }

    override fun onResume() {
        super.onResume()
        if (this::videoURL.isInitialized) {
            // There is no Cast context to work with. Do nothing.
            initPlayer(videoURL)
            return
        }
        binding.videoView.visibility = GONE

    }

    /**
     * Before API Level 24 there is no guarantee of onStop being called. So we have to release the
     * player as early as possible in onPause. Starting with API Level 24 (which brought multi and
     * split window mode) onStop is guaranteed to be called. In the paused state our activity is still
     * visible so we wait to release the player until onStop.
     */
    override fun onPause() {
        super.onPause()

        _currentPlayer?.rememberState()
        releaseLocalPlayer()

    }

    override fun onStop() {
        super.onStop()
        _currentPlayer?.rememberState()
        releaseLocalPlayer()
    }

    /**
     * We release the remote player when activity is destroyed
     */
    override fun onDestroy() {
        releaseRemotePlayer()
        _currentPlayer = null
        super.onDestroy()
    }


    override fun onCastSessionAvailable() {
        playOnPlayer(_castPlayer)
    }

    override fun onCastSessionUnavailable() {
        playOnPlayer(_player)
    }


    private fun startPlayback(videoURL: String) {
        // if the current player is the ExoPlayer, play from it
        if (_currentPlayer == _player) {
            // build the MediaSource from the URI
            binding.videoView.visibility = VISIBLE
            binding.castControlView.visibility = GONE

            // use stored state (if any) to resume (or start) playback

            val uri = Uri.parse(videoURL)
            val mediaSource = buildMediaSource(uri)
            _player?.playWhenReady = playWhenReady;
            _player?.seekTo(currentWindow, playbackPosition);
            _player?.prepare(mediaSource, false, false);
        }

        // if the current player is the CastPlayer, play from it
        if (_currentPlayer == _castPlayer) {
            binding.videoView.visibility = GONE
            val metadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE)
            metadata.putString(MediaMetadata.KEY_TITLE, latestShow.title)
            metadata.putString(MediaMetadata.KEY_SUBTITLE, latestShow.currentEp)
            metadata.addImage(WebImage(Uri.parse(latestShow.image)))

            val mediaInfo = MediaInfo.Builder(videoURL)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType(MimeTypes.VIDEO_MP4)
                .setMetadata(metadata)
                .build()
            val mediaItem = MediaQueueItem.Builder(mediaInfo).build()
            binding.castControlView.player = _castPlayer
            _castPlayer?.loadItem(mediaItem, playbackPosition)
            binding.castControlView.visibility = VISIBLE
//            binding.castControlView
//            _castPlayer?.
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return super.dispatchKeyEvent(event) || dispatchKeyEvents(event)
    }

    /**
     * Dispatches a given [KeyEvent] to whichever view corresponds according to the current
     * playback location.
     *
     * @param event The [KeyEvent].
     * @return Whether the event was handled by the target view.
     */
    fun dispatchKeyEvents(event: KeyEvent?): Boolean {

        return if (_currentPlayer == _castPlayer) {
            binding.castControlView.dispatchKeyEvent(event!!)
        } else  /* playbackLocation == PLAYBACK_REMOTE */ {
            binding.videoView.dispatchKeyEvent(event!!)
        }
    }

    /**
     * Sets the current player to the selected player and starts playback.
     */
    private fun playOnPlayer(player: Player?) {
        if (_currentPlayer == player) {
            return
        }

        // save state from the existing player
        _currentPlayer?.let {
            if (it.playbackState != Player.STATE_ENDED) {
                it.rememberState()
            }
            it.stop(true)
        }

        // set the new player
        _currentPlayer = player

        // set up the playback
        startPlayback(videoURL)
    }

    /**
     * Remembers the state of the playback of this Player.
     */
    private fun Player.rememberState() {
        this@VideoActivity.playWhenReady = playWhenReady
        this@VideoActivity.playbackPosition = currentPosition
        this@VideoActivity.currentWindow = currentWindowIndex
    }

    /**
     * Releases the resources of the local player back to the system.
     */
    private fun releaseLocalPlayer() {
        _player?.release()
        _player = null
        binding.videoView.player = null
    }


    /**
     * Releases the resources of the remote player back to the system.
     */
    private fun releaseRemotePlayer() {
        _castPlayer?.setSessionAvailabilityListener(null)
        _castPlayer?.release()
        _castPlayer = null
    }
}