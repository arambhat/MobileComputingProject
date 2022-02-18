package com.example.smarthomegesturetrainer.ui.Practice;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


interface RetrofitClientEndpoint {

    @Multipart
    @POST("upload")
    Call<FlaskResponse> uploadFile(@Part MultipartBody.Part file,
                                   @Part("file") RequestBody name);
    @Multipart
    @POST("upload")
    Call<FlaskResponse> uploadMulFile(@Part MultipartBody.Part file0,
                                      @Part MultipartBody.Part file1,
                                      @Part MultipartBody.Part file2);
}