package com.example.manager.retrofit;


import com.example.manager.model.NotiResponse;
import com.example.manager.model.NotiSendData;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiPushNotification {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization: key=AAAAPyUj7UQ:APA91bHX-v1j8JVRKS465ROKKKhzI3ImEx3u8R99dY875oRnLXUn6ItIQYAVSzglrZhqytlXrmQPO4AC7ygac8z6q78lIOPYudB82zo6t4MVfpMydiLAnO7zI7mdg6Bpz67AHGzGTRpq"
            }

    )
    @POST("fcm/send")
    Observable<NotiResponse> sendNotification(@Body NotiSendData data);
}
