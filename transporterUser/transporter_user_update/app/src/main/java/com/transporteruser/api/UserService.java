package com.transporteruser.api;


import com.transporteruser.bean.Bid;
import com.transporteruser.bean.Lead;
import com.transporteruser.bean.Transporter;
import com.transporteruser.bean.User;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public class UserService {

    public static UserApi userApi;

    public static UserApi getUserApiInstance() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100,TimeUnit.SECONDS).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerAddress.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        if (userApi == null)
            userApi = retrofit.create(UserApi.class);
        return userApi;
    }

    public interface UserApi {
        @Multipart
        @POST("/user/")
        public Call<User> saveProfile(@Part MultipartBody.Part file,
                                      @Part("userId") RequestBody userId,
                                      @Part("name") RequestBody name,
                                      @Part("address") RequestBody address,
                                      @Part("contactNumber") RequestBody contactNumber,
                                      @Part("token") RequestBody token);

        //check user profile created or not
        @GET("/user/{id}")
        public Call<User> checkProfile(@Path("id") String id);

        @GET("lead/create/confirmed/{userId}")
        public Call<ArrayList<Lead>> getCreateAndConfirmed(@Path("userId")String id);

        @GET("/lead/all-lead/{userId}")
        public Call<ArrayList<Lead>> getAllLeads(@Path("userId")String id);

        @GET("/lead/completed-lead/{userId}")
        public  Call<ArrayList<Lead>> getAllCompletedLeadsByUserId(@Path("userId")String userId);

        @GET("/lead/created-lead/{userId}")
        public  Call<ArrayList<Lead>> getAllCreatedLeadsByUserId(@Path("userId")String userId);

        @GET("/bid/{leadId}")
        public  Call<ArrayList<Bid>> getAllBidsByLeadId(@Path("leadId")String leadId);

        @GET("transporter/{id}")
        public Call<Transporter> getCurrentTransporter(@Path("id") String id);

        @POST("/user/update")
        public Call<User> updateProfile(@Body User user);

        @Multipart
        @POST("/user/image")
        public Call<User> updateImage(@Part MultipartBody.Part file, @Part ("userId") RequestBody userId);

        @DELETE("lead/{leadId}")
        public  Call<ArrayList<Lead>> deleteLeadById(@Path("leadId")String leadId);

        @GET("/lead/{leadId}")
        public  Call<Lead> getLeadByLeadId(@Path("leadId")String leadId);

        @POST("/lead/update")
        public  Call<Lead> updateLead(@Body Lead lead);

    }
}
