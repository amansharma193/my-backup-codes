package com.transportervendor.apis;

import com.transportervendor.beans.State;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class StateService {

    public static StateService.StateApi stateApi=null;
    public static StateService.StateApi getStateApiInstance(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(ServerAddress.serverAddress)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        if(stateApi==null){
            stateApi=retrofit.create(StateService.StateApi.class);
        }
        return stateApi;
    }

    public interface StateApi{
        @GET("/state/list")
        public Call<ArrayList<State>> getState();
    }
}
