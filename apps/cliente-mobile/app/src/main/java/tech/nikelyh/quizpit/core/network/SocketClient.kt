package tech.nikelyh.quizpit.core.network

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object SocketClient {
    private val SOCKET_URL = tech.nikelyh.quizpit.BuildConfig.GAME_ENGINE_URL // URL for game-engine
    
    private var mSocket: Socket? = null

    fun getSocket(): Socket {
        if (mSocket == null) {
            try {
                mSocket = IO.socket(SOCKET_URL)
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        }
        return mSocket!!
    }
    
    fun connect() {
        getSocket().connect()
    }
    
    fun disconnect() {
        getSocket().disconnect()
    }
}
