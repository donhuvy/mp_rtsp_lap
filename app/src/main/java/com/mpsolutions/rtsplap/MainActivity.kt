package com.mpsolutions.rtsplap

import android.app.Activity
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.mpsolutions.rtsplap.databinding.ActivityMainBinding
import org.videolan.libvlc.IVLCVout
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer


class MainActivity : AppCompatActivity(), IVLCVout.Callback {

    private lateinit var binding: ActivityMainBinding
    private var holder: SurfaceHolder? = null
    private var libvlc: LibVLC? = null
    private lateinit var mMediaPlayer: MediaPlayer
    private var mVideoWidth = 0
    private var mVideoHeight = 0

    /**
     * Registering callbacks
     */
    private val mPlayerListener: MediaPlayer.EventListener = MyPlayerListener(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        holder = binding.surfaceView.holder

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        mVideoHeight = displayMetrics.heightPixels
        mVideoWidth = displayMetrics.widthPixels
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        setSize(mVideoWidth, mVideoHeight)
        super.onConfigurationChanged(newConfig)
    }

    override fun onResume() {
        super.onResume()
        createPlayer("rtsp://192.168.10.169:5554/camera")
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    // region -> Other

    /**
     * Used to set size for SurfaceView
     *
     * @param width:
     * @param height:
     */
    private fun setSize(width: Int, height: Int) {
        mVideoWidth = width
        mVideoHeight = height

        if (mVideoWidth * mVideoHeight <= 1) return
        if (holder == null) return
        var w = window.decorView.width
        var h = window.decorView.height
        val isPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        if (w > h && isPortrait || w < h && !isPortrait) {
            val i = w
            w = h
            h = i
        }
        val videoAR = mVideoWidth.toFloat() / mVideoHeight.toFloat()
        val screenAR = w.toFloat() / h.toFloat()
        if (screenAR < videoAR) h = (w / videoAR).toInt() else w = (h * videoAR).toInt()
        holder!!.setFixedSize(mVideoWidth, mVideoHeight)
        val lp = binding.surfaceView.layoutParams
        lp.width = w
        lp.height = h
        binding.surfaceView.layoutParams = lp
        binding.surfaceView.invalidate()
    }

    /**
     * Creates MediaPlayer and plays video
     *
     * @param media
     */
    private fun createPlayer(media: String) {
        releasePlayer()
        try {
            if (media.isNotEmpty()) {
                val toast = Toast.makeText(this, media, Toast.LENGTH_LONG)
                toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
                toast.show()
            }
            // Create LibVLC
            // TODO: make this more robust, and sync with audio demo
            val options = ArrayList<String>()
            //options.add("--subsdec-encoding <encoding>");
            options.add("--aout=opensles")
            options.add("--audio-time-stretch") // time stretching
            options.add("-vvv") // verbosity
            libvlc = LibVLC(this, options)
            holder!!.setKeepScreenOn(true)
            // Creating media player
            mMediaPlayer = MediaPlayer(libvlc)
//            mMediaPlayer.setEventListener(mPlayerListener)
            // Seting up video output
            val vout: IVLCVout = mMediaPlayer.vlcVout
            vout.setVideoView(binding.surfaceView)
            vout.setWindowSize(mVideoWidth, mVideoHeight)
            //vout.setSubtitlesView(mSurfaceSubtitles);
            vout.addCallback(this)
            vout.attachViews()
            val m = Media(libvlc, Uri.parse(media))
            m.setHWDecoderEnabled(true,false)
            m.addOption(":network-caching=100")
            m.addOption(":clock-jitter=0")
            m.addOption(":clock-synchro=0")
            m.addOption(":fullscreen")
            mMediaPlayer.media = m
            mMediaPlayer.play()
        } catch (e: Exception) {
            Toast.makeText(this, "Error in creating player!", Toast.LENGTH_LONG).show()
        }
    }

    private fun releasePlayer() {
        if (libvlc == null) return
        mMediaPlayer.stop()
        val vout: IVLCVout = mMediaPlayer.vlcVout
        vout.removeCallback(this)
        vout.detachViews()
        holder = null
        libvlc!!.release()
        libvlc = null
        mVideoWidth = 0
        mVideoHeight = 0
    }

    // endregion

    // region -> IVLCVout.Callback

    override fun onSurfacesCreated(vlcVout: IVLCVout?) {
        Log.d("TAGs", "onSurfacesCreated")
    }

    override fun onNewLayout(vlcVout: IVLCVout?, width: Int, height: Int, visibleWidth: Int, visibleHeight: Int, sarNum: Int, sarDen: Int) {
        if (width * height == 0) return
        // store video size
        mVideoWidth = width
        mVideoHeight = height
        setSize(mVideoWidth, mVideoHeight)
    }

    override fun onHardwareAccelerationError(vlcVout: IVLCVout?) {
        Log.e("TAGs", "Error with hardware acceleration");
        this.releasePlayer();
        Toast.makeText(this, "Error with hardware acceleration", Toast.LENGTH_LONG).show();
    }

    override fun onSurfacesDestroyed(vlcVout: IVLCVout?) {
        Log.d("TAGs", "onSurfacesDestroyed")
    }

    // endregion

    private class MyPlayerListener(activity: Activity) : MediaPlayer.EventListener {

        override fun onEvent(event: MediaPlayer.Event?) {

        }
    }
}