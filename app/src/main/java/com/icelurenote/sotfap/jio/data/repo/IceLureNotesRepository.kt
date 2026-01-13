package com.icelurenote.sotfap.jio.data.repo

import android.util.Log
import com.icelurenote.sotfap.jio.domain.model.IceLureNotesEntity
import com.icelurenote.sotfap.jio.domain.model.IceLureNotesParam
import com.icelurenote.sotfap.jio.presentation.app.IceLureNotesApplication.Companion.ICE_LURE_NOTES_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface IceLureNotesApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun iceLureNotesGetClient(
        @Body jsonString: JsonObject,
    ): Call<IceLureNotesEntity>
}


private const val ICE_LURE_NOTES_MAIN = "https://icelurenotes.com/"
class IceLureNotesRepository {

    suspend fun iceLureNotesGetClient(
        iceLureNotesParam: IceLureNotesParam,
        iceLureNotesConversion: MutableMap<String, Any>?
    ): IceLureNotesEntity? {
        val gson = Gson()
        val api = iceLureNotesGetApi(ICE_LURE_NOTES_MAIN, null)

        val iceLureNotesJsonObject = gson.toJsonTree(iceLureNotesParam).asJsonObject
        iceLureNotesConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            iceLureNotesJsonObject.add(key, element)
        }
        return try {
            val iceLureNotesRequest: Call<IceLureNotesEntity> = api.iceLureNotesGetClient(
                jsonString = iceLureNotesJsonObject,
            )
            val iceLureNotesResult = iceLureNotesRequest.awaitResponse()
            Log.d(ICE_LURE_NOTES_MAIN_TAG, "Retrofit: Result code: ${iceLureNotesResult.code()}")
            if (iceLureNotesResult.code() == 200) {
                Log.d(ICE_LURE_NOTES_MAIN_TAG, "Retrofit: Get request success")
                Log.d(ICE_LURE_NOTES_MAIN_TAG, "Retrofit: Code = ${iceLureNotesResult.code()}")
                Log.d(ICE_LURE_NOTES_MAIN_TAG, "Retrofit: ${iceLureNotesResult.body()}")
                iceLureNotesResult.body()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(ICE_LURE_NOTES_MAIN_TAG, "Retrofit: Get request failed")
            Log.d(ICE_LURE_NOTES_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun iceLureNotesGetApi(url: String, client: OkHttpClient?) : IceLureNotesApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
