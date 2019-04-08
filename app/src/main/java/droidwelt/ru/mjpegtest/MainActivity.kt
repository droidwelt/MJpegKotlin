package droidwelt.ru.mjpegtest

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient

import java.io.IOException
import java.net.URI

class MainActivity : AppCompatActivity() {
    private var mv: MjpegView? = null


    /* Creates the menu items */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, MENU_QUIT, 0, "Quit")
        menu.add(0, MENU_SPB1, 1, "Saint-Peterburg 1")
        menu.add(0, MENU_SPB2, 1, "Saint-Peterburg 2")
        menu.add(0, MENU_USA, 1, "USA")
        menu.add(0, MENU_Greece, 1, "Greece")
        menu.add(0, MENU_Iceland, 1, "Iceland, Reykjavik")
        return true
    }

    /* Handles item selections */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            MENU_QUIT -> {
                finish()
                return true
            }

            MENU_SPB1 -> {
                viewURL("http://94.72.4.191:80/mjpg/video.mjpg")
                return true
            }

            MENU_SPB2 -> {
                viewURL("http://178.162.34.110:82/cam_1.cgi")
                return true
            }

            MENU_USA -> {
                viewURL("http://207.192.232.2:8000/mjpg/video.mjpg")
                return true
            }

            MENU_Greece -> {
                viewURL("http://195.97.20.246:80/mjpg/video.mjpg")
                return true
            }

            MENU_Iceland -> {
                viewURL("http://157.157.138.235:80/mjpg/video.mjpg")
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }


    fun viewURL(u: String) {
        URL = u
        if (mv != null)
            mv!!.stopPlayback()
        mv = MjpegView(this)
        setContentView(mv)
        DoRead().execute(u)
    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)

        viewURL(URL)
    }

    public override fun onPause() {
        super.onPause()
        mv!!.stopPlayback()
    }


    inner class DoRead : AsyncTask<String, Void, MjpegInputStream>() {
        override fun doInBackground(vararg url: String): MjpegInputStream? {
            //TODO: if camera has authentication deal with it and don't just not work
            val res: HttpResponse?
            val httpclient = DefaultHttpClient()
            Log.d(TAG, "1. Sending http request")
            try {
                res = httpclient.execute(HttpGet(URI.create(url[0])))
                Log.d(TAG, "2. Request finished, status = " + res!!.statusLine.statusCode)
                return if (res.statusLine.statusCode == 401) {
                    //You must turn off camera User Access Control before this will work
                    null
                } else MjpegInputStream(res.entity.content)
            } catch (e: ClientProtocolException) {
                e.printStackTrace()
                Log.d(TAG, "Request failed-ClientProtocolException", e)
                //Error connecting to camera
            } catch (e: IOException) {
                e.printStackTrace()
                Log.d(TAG, "Request failed-IOException", e)
                //Error connecting to camera
            }

            return null
        }

        override fun onPostExecute(result: MjpegInputStream) {
            if (mv != null) {
                mv!!.setSource(result)
                mv!!.setDisplayMode(MjpegView.SIZE_BEST_FIT)
                mv!!.showFps(true)
            }
        }
    }

    companion object {

        //  https://stackoverflow.com/questions/3205191/android-and-mjpeg

        private val TAG = "MJPEGTEST-MainActivity"
        private var URL = "http://94.72.4.191:80/mjpg/video.mjpg"
        private val MENU_QUIT = 1
        private val MENU_SETTING = 2
        private val MENU_SPB1 = 11
        private val MENU_SPB2 = 12
        private val MENU_USA = 13
        private val MENU_Greece = 14
        private val MENU_Iceland = 15
    }


}
