package com.example.speakpediaforfinals;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DictionaryService {

    @GET("api/v3/references/collegiate/json/{word}")
    Call<WordResponse[]> getWordDefinition(@Path("word") String word, @Query("key") String apikey);
}
