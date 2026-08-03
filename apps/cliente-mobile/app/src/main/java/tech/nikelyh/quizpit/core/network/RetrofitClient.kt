package tech.nikelyh.quizpit.core.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // IMPORTANTE: Si usas un dispositivo físico, cambia esta IP por la IP local de tu PC (ej. 192.168.1.X)
    // 10.0.2.2 es el "localhost" para el Emulador de Android Studio.
    private const val BASE_URL = "http://10.0.2.2:3000/api/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS) // Gemini puede tardar un poco en procesar PDFs grandes
        .build()

    val apiService: QuizApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuizApiService::class.java)
    }
}
