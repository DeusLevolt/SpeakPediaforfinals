package com.example.speakpediaforfinals;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface OpenAiTranslationService {
    @POST("v1/engines/text-davinci-003/completions")
    Call<ResponseBody> translateText(@Header("Authorization") String authHeader, @Body RequestBody body);
}
