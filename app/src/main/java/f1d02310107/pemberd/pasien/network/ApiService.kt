package f1d02310107.pemberd.pasien.network

import f1d02310107.pemberd.pasien.model.LoginRequest
import f1d02310107.pemberd.pasien.model.LoginResponse
import f1d02310107.pemberd.pasien.model.PasienResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET("pasien")
    suspend fun getPasien(
        @Header("Authorization") token: String
    ): Response<PasienResponse>
}