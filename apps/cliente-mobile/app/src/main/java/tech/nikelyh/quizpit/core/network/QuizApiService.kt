package tech.nikelyh.quizpit.core.network

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface QuizApiService {
    @Multipart
    @POST("quizzes/generate-from-pdf")
    suspend fun generateFromPdf(
        @Part file: MultipartBody.Part
    ): Response<Any> // We use Response<Any> to just check if it was successful for now
}
