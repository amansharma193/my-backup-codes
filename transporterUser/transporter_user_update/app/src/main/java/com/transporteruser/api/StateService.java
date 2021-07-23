package com.transporteruser.api;

import com.transporteruser.bean.State;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class StateService {
    public static StateApi stateapi;

    public static StateApi getStateApiInstance() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerAddress.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        if (stateapi == null)
            stateapi = retrofit.create(StateApi.class);
        return stateapi;
    }
    public interface StateApi{
        @GET("state/list")
        public Call<List<State>> getstateList();


    }


}
