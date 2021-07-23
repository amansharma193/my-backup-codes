package com.transporteruser.api;

import com.transporteruser.bean.Lead;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class LeadService implements Serializable {

    public static LeadApi leadApi;

    public static LeadApi getLeadApiInstance() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerAddress.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        if (leadApi == null)
            leadApi = retrofit.create(LeadApi.class);
        return leadApi;
    }

    public interface LeadApi {
        @POST("/lead/create")
        public Call<Lead> createLeads(@Body Lead leads);
        @POST("/lead/update")
        public Call<Lead> updateLeads(@Body Lead leads);
        @DELETE("/lead/{leadId}")
        public  Call<Lead> deleteLeadById(@Path("leadId") String id);

    }

}