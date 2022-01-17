package com.example.orderrawmaterials.notification

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import com.example.orderrawmaterials.notification.models.Responce
import com.example.orderrawmaterials.notification.models.Sender

interface APIService {
    @Headers(
        "Content-type:application/json",
        "Authorization:key=AAAAjn-adVI:APA91bF4a9Pudsap9McU6aXSZ_FzC2AbDEVgxP6OxXYgmDpqIkKB16IurWKtpRcsBv0DJtOQGtO6KnlCFDTTLgmNAnoq4IzDAp-Rsp62KYrPf_FB7z7Ij9fQcp-o0PvQb_PzoxFQPdmw"
    )
    @POST("fcm/send")
    fun sendNotification(@Body sender: Sender): Call<Responce>
}