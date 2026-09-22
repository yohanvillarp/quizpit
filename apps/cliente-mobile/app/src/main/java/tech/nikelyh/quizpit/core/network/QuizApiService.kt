package tech.nikelyh.quizpit.core.network

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import tech.nikelyh.quizpit.feature.home.data.GeneratedQuizResult
import okhttp3.RequestBody

interface QuizApiService {
    @Multipart
    @POST("quizzes/generate-from-pdf")
    suspend fun generateFromPdf(
        @Part file: MultipartBody.Part,
        @Part("hostId") hostId: RequestBody
    ): Response<GeneratedQuizResult>

    @retrofit2.http.GET("time")
    suspend fun getServerTime(): Response<TimeResponse>
}

data class TimeResponse(
    val serverTimeMs: Long,
    val iso: String
)
