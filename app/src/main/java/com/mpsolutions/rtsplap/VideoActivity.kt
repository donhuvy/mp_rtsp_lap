package com.mpsolutions.rtsplap

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket


class VideoActivity : AppCompatActivity(), MediaPlayer.OnPreparedListener, SurfaceHolder.Callback {

    private var _mediaPlayer: MediaPlayer? = null
    private var _surfaceHolder: SurfaceHolder? = null

    private val MESSAGE_RTSP_OK = 1
    private val MESSAGE_RTSP_ERROR = -1

    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

//        _surfaceHolder = surface_view.holder
//        _surfaceHolder!!.addCallback(this)
//        _surfaceHolder!!.setFixedSize(320, 240)

//        mediaPlayer.setDataSource(this, Uri.parse("rtsp://192.168.10.169:5554/camera"))
//        mediaPlayer.setSurface(surface_view)

//        video_view.setVideoURI(Uri.parse("rtsp://192.168.10.169:5554/"))
//
//        val mediaController = MediaController(this)
//        mediaController.setAnchorView(video_view)
//
//        video_view.setMediaController(mediaController)
//        video_view.requestFocus()
////        video_view.start()
//
//        val videoSync = VideoSync()
//        videoSync.execute()

        @SuppressLint("HandlerLeak")
        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MESSAGE_RTSP_OK -> {
                        Log.d("TAGs", "MESSAGE_RTSP_OK")
                    }
                    MESSAGE_RTSP_ERROR -> {
                        Log.d("TAGs", "MESSAGE_RTSP_ERROR")
                    }
                }
            }
        }

        Thread {

            try {
                val client = Socket("192.168.10.169", 5554)
                val os = client.getOutputStream()
                os.write("OPTIONS * RTSP/1.0\n".toByteArray())
                os.write("CSeq: 1\n\n".toByteArray())
                os.flush()

                val br = BufferedReader(InputStreamReader(BufferedInputStream(client.getInputStream())))

                val sb = StringBuilder()
                var responseLine: String? = null

                while (null != br.readLine().also { responseLine = it }) {
                    sb.append(responseLine)
                }
                val rtspResponse = sb.toString()
                if (rtspResponse.startsWith("RTSP/1.0 200 OK")) {
                    // RTSP SERVER IS UP!!
                    handler.obtainMessage(MESSAGE_RTSP_OK).sendToTarget()
                } else {
                    // SOMETHING'S WRONG
                    handler.obtainMessage(MESSAGE_RTSP_ERROR).sendToTarget()
                }
                Log.d("RTSP reply", rtspResponse)
                client.close()
            } catch (e: Exception) {
                e.printStackTrace()

                handler.obtainMessage(MESSAGE_RTSP_ERROR).sendToTarget()
            }
        }.start()
    }

    override fun onPrepared(p0: MediaPlayer?) {
//        Log.d("TAGs", "onPrepared")
//        _mediaPlayer!!.start()
//        p0?.start()
    }

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
        // do nothing
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
//        _mediaPlayer!!.release()
    }

    override fun surfaceCreated(p0: SurfaceHolder?) {
//        _mediaPlayer = MediaPlayer()
//        _mediaPlayer!!.setDisplay(_surfaceHolder)
//
//        val source: Uri = Uri.parse("rtsp://192.168.10.169:5554/camera")
//        try {
//            // Specify the IP camera's URL and auth headers.
//            _mediaPlayer!!.setDataSource(this, source)
//
//            // Begin the process of setting up a video stream.
//            _mediaPlayer!!.setOnPreparedListener(this)
//            _mediaPlayer!!.prepareAsync()
//        } catch (e: Exception) {
//            Log.d("TAGs", "Exception")
//        }
    }

//    private fun getRtspHeaders(): Map<String, String>? {
//        val headers: MutableMap<String, String> = HashMap()
//        val basicAuthValue = getBasicAuthValue(USERNAME, PASSWORD)
//        headers["Authorization"] = basicAuthValue
//        return headers
//    }
//
//    private fun getBasicAuthValue(usr: String, pwd: String): String {
//        val credentials = "$usr:$pwd"
//        val flags: Int = Base64.URL_SAFE or Base64.NO_WRAP
//        val bytes = credentials.toByteArray()
//        return "Basic " + Base64.encodeToString(bytes, flags)
//    }

    override fun onPause() {
        super.onPause()
        //JCVideoPlayer.releaseAllVideos()
    }

    override fun onBackPressed() {
//        if (JCVideoPlayer.backPress()) {
//            return
//        }
        super.onBackPressed()
    }

//    inner class VideoSync : AsyncTask<Unit, Unit, Unit>() {
//
//        override fun doInBackground(vararg p0: Unit?) {
//            video_view.setOnPreparedListener {
//                video_view.start()
//            }
//        }
//
//    }
}