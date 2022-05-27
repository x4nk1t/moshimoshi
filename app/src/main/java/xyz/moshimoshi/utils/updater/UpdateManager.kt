package xyz.moshimoshi.utils.updater

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.*
import xyz.moshimoshi.BuildConfig
import java.io.IOException

class UpdateManager(private val context: Context) {
    private val updateUrl = "https://ankit252.com.np/moshimoshi/"
    private val client = OkHttpClient()
    private val TAG = "UPDATE"
    private val gson = GsonBuilder().create()

    fun checkForUpdate(callback: (updateFound: Boolean, release: ReleaseModel) -> Unit){
        get(updateUrl, ReleaseModel::class.java){ releaseModel ->
            if(releaseModel.tag_name != BuildConfig.VERSION_NAME){
                callback.invoke(true, releaseModel)
            } else {
                callback.invoke(false, releaseModel)
            }
        }
    }

    private operator fun <T> get(url: String, type: Class<T>, callback: (output: T) -> Unit) {
        client.newCall(Request.Builder().url(url).get().build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Error getting $url.", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body
                if (body == null) {
                    Log.e(TAG, "Null response body for $url.")
                    return
                }
                callback.invoke(gson.fromJson(body.charStream(), type))
            }
        })
    }
}