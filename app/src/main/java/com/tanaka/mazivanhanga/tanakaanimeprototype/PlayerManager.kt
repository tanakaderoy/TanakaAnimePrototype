//package com.tanaka.mazivanhanga.tanakaanimeprototype
//
//import android.content.Context
//import com.google.android.exoplayer2.DefaultRenderersFactory
//import com.google.android.exoplayer2.ExoPlayerFactory
//import com.google.android.exoplayer2.RenderersFactory
//import com.google.android.exoplayer2.SimpleExoPlayer
//import com.google.android.exoplayer2.ext.cast.CastPlayer
//import com.google.android.exoplayer2.ext.cast.SessionAvailabilityListener
//import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
//import com.google.android.exoplayer2.source.ExtractorMediaSource
//import com.google.android.exoplayer2.source.MediaSource
//import com.google.android.exoplayer2.source.dash.DashMediaSource
//import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
//import com.google.android.exoplayer2.source.hls.HlsMediaSource
//import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource
//import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
//import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
//import com.google.android.exoplayer2.ui.PlaybackControlView
//import com.google.android.exoplayer2.ui.PlayerControlView
//import com.google.android.exoplayer2.ui.PlayerView
//import com.google.android.exoplayer2.ui.SimpleExoPlayerView
//import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
//import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
//import com.google.android.gms.cast.framework.CastContext
//
//
///**
// * Created by Tanaka Mazivanhanga on 08/08/2020
// */
//internal class PlayerManager(
//    exoPlayerView: PlayerView, castControlView: PlaybackControlView,
//    context: Context?
//) : SessionAvailabilityListener {
//    private val exoPlayerView: PlayerView
//    private val castControlView: PlayerControlView
//    private val castContext: CastContext
//    private val exoPlayer: SimpleExoPlayer?
//    private val castPlayer: CastPlayer
//    private var playbackLocation = 0
//    private var currentSample: Sample? = null
//
//    /**
//     * Starts playback of the given sample at the given position.
//     *
//     * @param currentSample The [CastDemoUtil] to play.
//     * @param positionMs The position at which playback should start.
//     * @param playWhenReady Whether the player should proceed when ready to do so.
//     */
//    fun setCurrentSample(
//        currentSample: Sample, positionMs: Long,
//        playWhenReady: Boolean
//    ) {
//        this.currentSample = currentSample
//        if (playbackLocation == PLAYBACK_REMOTE) {
//            castPlayer.load(
//                currentSample.name, currentSample.uri, currentSample.type, positionMs,
//                playWhenReady
//            )
//        } else  /* playbackLocation == PLAYBACK_LOCAL */ {
//            exoPlayer!!.playWhenReady = playWhenReady
//            exoPlayer.seekTo(positionMs)
//            exoPlayer.prepare(buildMediaSource(currentSample), true, true)
//        }
//    }
//
//    /**
//     * Dispatches a given [KeyEvent] to whichever view corresponds according to the current
//     * playback location.
//     *
//     * @param event The [KeyEvent].
//     * @return Whether the event was handled by the target view.
//     */
//    fun dispatchKeyEvent(event: KeyEvent?): Boolean {
//        return if (playbackLocation == PLAYBACK_REMOTE) {
//            castControlView.dispatchKeyEvent(event)
//        } else  /* playbackLocation == PLAYBACK_REMOTE */ {
//            exoPlayerView.dispatchKeyEvent(event)
//        }
//    }
//
//    /**
//     * Releases the manager and the players that it holds.
//     */
//    fun release() {
//        castPlayer.setSessionAvailabilityListener(null)
//        castPlayer.release()
//        exoPlayerView.player = null
//        exoPlayer!!.release()
//    }
//
//    // CastPlayer.SessionAvailabilityListener implementation.
//    fun onCastSessionAvailable() {
//        setPlaybackLocation(PLAYBACK_REMOTE)
//    }
//
//    fun onCastSessionUnavailable() {
//        setPlaybackLocation(PLAYBACK_LOCAL)
//    }
//
//    private fun setPlaybackLocation(playbackLocation: Int) {
//        if (this.playbackLocation == playbackLocation) {
//            return
//        }
//
//        // View management.
//        if (playbackLocation == PLAYBACK_LOCAL) {
//            exoPlayerView.visibility = View.VISIBLE
//            castControlView.hide()
//        } else {
//            exoPlayerView.visibility = View.GONE
//            castControlView.show()
//        }
//        var playbackPositionMs: Long = 0
//        var playWhenReady = true
//        if (exoPlayer != null) {
//            playbackPositionMs = exoPlayer.currentPosition
//            playWhenReady = exoPlayer.playWhenReady
//        } else if (this.playbackLocation == PLAYBACK_REMOTE) {
//            playbackPositionMs = castPlayer.currentPosition
//            playWhenReady = castPlayer.playWhenReady
//        }
//        this.playbackLocation = playbackLocation
//        if (currentSample != null) {
//            setCurrentSample(currentSample, playbackPositionMs, playWhenReady)
//        }
//    }
//
//    companion object {
//        private const val PLAYBACK_REMOTE = 1
//        private const val PLAYBACK_LOCAL = 2
//        private const val USER_AGENT = "ExoCastDemoPlayer"
//        private val BANDWIDTH_METER = DefaultBandwidthMeter()
//        private val DATA_SOURCE_FACTORY =
//            DefaultHttpDataSourceFactory(
//                USER_AGENT,
//                BANDWIDTH_METER
//            )
//
//        // Internal methods.
//        private fun buildMediaSource(sample: Sample): MediaSource {
//            val uri: Uri = Uri.parse(sample.uri)
//            return when (sample.type) {
//                CastDemoUtil.MIME_TYPE_SS -> SsMediaSource(
//                    uri,
//                    DATA_SOURCE_FACTORY,
//                    DefaultSsChunkSource.Factory(DATA_SOURCE_FACTORY),
//                    null,
//                    null
//                )
//                CastDemoUtil.MIME_TYPE_DASH -> DashMediaSource(
//                    uri,
//                    DATA_SOURCE_FACTORY,
//                    DefaultDashChunkSource.Factory(DATA_SOURCE_FACTORY),
//                    null,
//                    null
//                )
//                CastDemoUtil.MIME_TYPE_HLS -> HlsMediaSource(
//                    uri,
//                    DATA_SOURCE_FACTORY,
//                    null,
//                    null
//                )
//                CastDemoUtil.MIME_TYPE_VIDEO_MP4 -> ExtractorMediaSource(
//                    uri, DATA_SOURCE_FACTORY, DefaultExtractorsFactory(),
//                    null, null
//                )
//                else -> {
//                    throw IllegalStateException("Unsupported type: " + sample.type)
//                }
//            }
//        }
//    }
//
//    /**
//     * @param exoPlayerView The [SimpleExoPlayerView] for local playback.
//     * @param castControlView The [PlaybackControlView] to control remote playback.
//     * @param context A [Context].
//     */
//    init {
//        this.exoPlayerView = exoPlayerView
//        this.castControlView = castControlView
//        castContext = CastContext.getSharedInstance(context)
//        val trackSelector =
//            DefaultTrackSelector(BANDWIDTH_METER)
//        val renderersFactory: RenderersFactory = DefaultRenderersFactory(context, null)
//        exoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector)
//        exoPlayerView.player = exoPlayer
//        castPlayer = CastPlayer(castContext)
//        castPlayer.setSessionAvailabilityListener(this)
//        castControlView.player = castPlayer
//        setPlaybackLocation(if (castPlayer.isCastSessionAvailable) PLAYBACK_REMOTE else PLAYBACK_LOCAL)
//    }
//}