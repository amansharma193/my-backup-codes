package com.transporteruser;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;
import com.transporteruser.api.UserService;
import com.transporteruser.bean.User;
import com.transporteruser.databinding.ActivityCreateProfileBinding;

import java.io.File;
import java.security.PKCS12Attribute;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity {
    ActivityCreateProfileBinding binding;
    SharedPreferences sp;
    String currentUserId,imageUrl;
    UserService.UserApi userApi;
    Uri imageUri;
    String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userApi=UserService.getUserApiInstance();
        binding=ActivityCreateProfileBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 11);
        }
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        sp = getSharedPreferences("user",MODE_PRIVATE);
        binding.createProfile.setText("Update Profile");
        binding.userName.setText(sp.getString("name",""));
        binding.address.setText(sp.getString("address",""));
        binding.phoneNumber.setText(sp.getString("contactNumber",""));
        imageUrl=sp.getString("imageUrl","");
        userId=(sp.getString("userId",""));
        Picasso.get().load(imageUrl).into(binding.civ);

        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent();
                in.setAction(Intent.ACTION_GET_CONTENT);
                in.setType("image/*");
                startActivityForResult(in, 1);

            }
        });
        binding.createProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtility.checkInternetConnection(UpdateProfileActivity.this)){
                    String name = binding.userName.getText().toString();

                    String address = binding.address.getText().toString();

                    String phoneNumber = binding.phoneNumber.getText().toString();
                    String token = FirebaseInstanceId.getInstance().getToken();

                    if (TextUtils.isEmpty(name)) {
                        binding.userName.setError("Username required");
                        return;
                    }
                    if (TextUtils.isEmpty(address)) {
                        binding.address.setError("address is required ");
                        return;
                    }
                    if (TextUtils.isEmpty(phoneNumber)) {
                        binding.phoneNumber.setError("Phone number is required");
                        return;
                    }
                    User user=new User();
                    user.setToken(token);
                    user.setName(name);
                    user.setAddress(address);
                    user.setContactNumber(phoneNumber);
                    user.setUserId(userId);
                    user.setImageUrl(imageUrl);
                    Call<User> call = userApi.updateProfile(user);
                    final ProgressDialog pd = new ProgressDialog(UpdateProfileActivity.this);
                    pd.setMessage("please wait while updating profile..");
                    pd.show();
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            pd.dismiss();
                            try {
                                int status = response.code();
                                if (status == 200) {
                                    User user = response.body();
                                    Log.e("Message----->", "onResponse: "+user );
                                    saveUserLocally(user);
                                    Toast.makeText(UpdateProfileActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                } else if (status == 500) {
                                    Toast.makeText(UpdateProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e){
                                Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Toast.makeText(UpdateProfileActivity.this, "Something went wrong : " + t, Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    });

                }
                else {
                    Toast.makeText(UpdateProfileActivity.this, "Check Internent connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setVisibility(View.VISIBLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        userId=(sp.getString("userId",""));
        if (requestCode == 1 && resultCode == RESULT_OK) {
            imageUri = data.getData();
            try{
                File file = FileUtils.getFile(UpdateProfileActivity.this, imageUri);
                RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(imageUri)),file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                RequestBody userId = RequestBody.create(MultipartBody.FORM,currentUserId);
                final ProgressDialog pd = new ProgressDialog(UpdateProfileActivity.this);
                pd.setMessage("please wait...");
                pd.show();
                Call<User> call = userApi.updateImage(body, userId);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        pd.dismiss();
                        if (response.code() == 200){
                            binding.civ.setImageURI(imageUri);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("imageUrl","").clear().commit();
                            editor.putString("imageUrl",imageUrl).commit();
                            Toast.makeText(UpdateProfileActivity.this, "Image changed", Toast.LENGTH_SHORT).show();
                        }
                        else if (response.code() == 500) {
                            Toast.makeText(UpdateProfileActivity.this, "failed to changed image", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        pd.dismiss();
                        Toast.makeText(UpdateProfileActivity.this, ""+t, Toast.LENGTH_SHORT).show();
                    }
                });

            }
            catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void saveUserLocally(User user) {
        SharedPreferences.Editor editor =sp.edit();
        editor.putString("name",user.getName());
        editor.putString("address",user.getAddress());
        editor.putString("contactNumber",user.getContactNumber());
        editor.putString("token",user.getToken());
        editor.putString("userId",user.getUserId());
        editor.commit();


    }


}
