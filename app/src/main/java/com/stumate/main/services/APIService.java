package com.stumate.main.services;

import com.stumate.main.notifications.MyResponse;
import com.stumate.main.notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAZF6jc_E:APA91bER-vjxfEsmAXFZQoB2Ljsy1P8SmvQPEr8eikxIZoIjZCuDKM7LAMEHE-q0iuMDH-s29mPMV-DjT4iVTWvHv01xkGfpQYsCpBrDtB4IerzOo4tTpMhsM7MsWbWH-pccjMA3ioXM"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}