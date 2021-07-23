package com.transportervendor.apis;

import com.transportervendor.beans.Bid;
import com.transportervendor.beans.BidWithLead;
import com.transportervendor.beans.Leads;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class BidService {
    public static BidService.BidApi bidApi=null;
    public static BidService.BidApi getBidApiInstance(){
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
        if(bidApi==null){
            bidApi=retrofit.create(BidService.BidApi.class);
        }
        return bidApi;
    }
    public interface BidApi{
        @GET("/bid/bid-lead/{id}")
        public Call<ArrayList<BidWithLead>> getAllBids(@Path("id") String id);
        @POST("/bid/")
        public Call<Bid> createBid(@Body Bid bid);
        @POST("/bid/update")
        public Call<Bid> updateBid(@Body Bid bid);
        @DELETE("/bid/{id}")
        public Call<Bid> deleteBid(@Path("id") String id);
    }
}
