package com.mpsolutions.rtsplap

import android.util.Log
import java.net.Socket
import java.net.SocketException
import java.util.concurrent.ConcurrentHashMap

class RtspSocket : Socket {

    val RTSP_HEADER_LENGTH = 4
    val RTP_HEADER_LENGTH = 12
    val MTU = 1400

    val PAYLOAD_OFFSET = RTSP_HEADER_LENGTH + RTP_HEADER_LENGTH
    val RTP_OFFSET = RTSP_HEADER_LENGTH

    private val headerMap: ConcurrentHashMap<String, String> = ConcurrentHashMap()

    private val kCRLF = "\r\n"

    // RTSP request format strings
    private val kOptions = "OPTIONS %s RTSP/1.0\r\n"
    private val kDescribe = "DESCRIBE %s RTSP/1.0\r\n"
    private val kAnnounce = "ANNOUNCE %s RTSP/1.0\r\n"
    private val kSetupPublish = "SETUP %s/trackid=%d RTSP/1.0\r\n"
    private val kSetupPlay = "SETUP %s/trackid=%d RTSP/1.0\r\n"
    private val kRecord = "RECORD %s RTSP/1.0\r\n"
    private val kPlay = "PLAY %s RTSP/1.0\r\n"
    private val kTeardown = "TEARDOWN %s RTSP/1.0\r\n"

    // RTSP header format strings
    private val kCseq = "Cseq: %d\r\n"
    private val kContentLength = "Content-Length: %d\r\n"
    private val kContentType = "Content-Type: %s\r\n"
    private val kTransport = "Transport: RTP/AVP/%s;unicast;mode=%s;%s\r\n"
    private val kSession = "Session: %s\r\n"
    private val kRange = "range: %s\r\n"
    private val kAccept = "Accept: %s\r\n"
    private val kAuthBasic = "Authorization: Basic %s\r\n"
    private val kAuthDigest =
        "Authorization: Digest username=\"%s\",realm=\"%s\",nonce=\"%s\",uri=\"%s\",response=\"%s\"\r\n"

    // RTSP header keys
    private val kSessionKey = "Session"
    private val kWWWAuthKey = "WWW-Authenticate"

    private val RTSP_MAX_HEADER = 4095
    var header = ByteArray(RTSP_MAX_HEADER + 1)
    private val RTSP_MAX_BODY = 4095

    private val RTSP_RESP_ERR = -6

    // static private final int RTSP_RESP_ERR_SESSION = -7;
    val RTSP_OK = 200
    private val RTSP_BAD_USER_PASS = 401

    private val SOCK_ERR_READ = -5

    /* Number of channels including control ones. */
    private val channelCount = 0

    /* RTSP negotiation cmd seq counter */
    private val seq = 0

    private val authentication: String? = null
    private val session: String? = null

    private val path: String? = null
    private val url: String? = null
    private val user: String? = null
    private val pass: String? = null
    private val sdp: String? = null

    private val buffer = ByteArray(MTU)

    constructor() : super() {
        try {
            tcpNoDelay = true
            soTimeout = 60000
        } catch (e: SocketException) {
            Log.e("RtspSocket", "Faild to set ")
        }
        buffer[RTSP_HEADER_LENGTH] = "10000000".toInt(2).toByte()
    }

    fun getBuffer(): ByteArray? {
        return buffer
    }


}