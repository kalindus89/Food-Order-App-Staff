package com.foodorderappstaff.notification_manager;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAi_TW_Ww:APA91bEFAZQCC5o2aSPFcOe7LU8DpPHlCBJNlhGNl7NRYqoosigXLy0VwAAARFlo2LVBY20jXFwtSmDjmGxGgZ8-v56jHvkOXw3RiiGM9IXEf3pi3L0QXp03dWOIAl8NpRt87MjweEBz"
                    // Your server key from firebase project settings, cloud messaging
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}

