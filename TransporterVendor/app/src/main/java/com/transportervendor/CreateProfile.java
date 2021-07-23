package com.transportervendor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.google.android.gms.common.internal.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.transportervendor.apis.LeadsService;
import com.transportervendor.apis.TransporterService;
import com.transportervendor.beans.Leads;
import com.transportervendor.beans.Transporter;
import com.transportervendor.beans.Vehicle;
import com.transportervendor.databinding.CreateProfileBinding;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class CreateProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 500f;
    CreateProfileBinding binding;
    public static final Integer RecordAudioRequestCode = 1;
    String imgUrl = "",phone;
    ArrayList<Vehicle> al = new ArrayList<>();
    ContentResolver contentResolver;
    Uri imgUri;
    int flag = 2;
    int code = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CreateProfileBinding.inflate(LayoutInflater.from(this));
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Create Profile");
        Intent in = getIntent();
        if (checkLanguage()){
            getSupportActionBar().setTitle("प्रोफ़ाइल बनाये");
        }
        //checkPermission();
        code = in.getIntExtra("code", 0);
        setContentView(binding.getRoot());
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.sp.setAdapter(adapter);
        binding.sp.setOnItemSelectedListener(this);
        binding.pending.setVisibility(View.GONE);
        binding.completed.setVisibility(View.GONE);
        binding.total.setVisibility(View.GONE);
        binding.civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 111);
            }
        });
        if (code == 2) {
            SharedPreferences mPrefs = getSharedPreferences("Transporter", MODE_PRIVATE);
            getSupportActionBar().setTitle("Update Profile");
            binding.create.setText("update");
            if (checkLanguage()){
                getSupportActionBar().setTitle("प्रोफ़ाइल अपडेट करें");
                binding.pd.setText("लंबित");
                binding.cp.setText("पूरा किया हुआ");
                binding.tt.setText("कुल");
                binding.create.setText("अपडेट करें");
            }
            String json = mPrefs.getString("Transporter", "");
            Gson gson = new Gson();
            String completed = mPrefs.getString("completed", "0");
            String pending = mPrefs.getString("pending", "0");
            Transporter transporter = gson.fromJson(json, Transporter.class);
            binding.pp.setText(transporter.getName());
            if (!(pending.equals("")) && !(pending.equals(""))) {
                int p = Integer.parseInt(pending);
                int c = Integer.parseInt(completed);
                binding.cd1.setVisibility(View.VISIBLE);
                binding.pending.setVisibility(View.VISIBLE);
                binding.completed.setVisibility(View.VISIBLE);
                binding.total.setVisibility(View.VISIBLE);
                binding.pending.setText("" + p);
                binding.completed.setText("" + c);
                p += c;
                binding.total.setText("" + p);
            }
            if (NetworkUtil.getConnectivityStatus(this)) {
                Picasso.get().load(transporter.getImage()).placeholder(R.drawable.transporter_logo).into(binding.civ);
            } else
                Toast.makeText(this, "please enable internet connection.", Toast.LENGTH_SHORT).show();
            if (transporter.getType().equalsIgnoreCase("Truck Owner")) {
                binding.aadhar.setVisibility(View.VISIBLE);
                binding.sp.setSelection(1);
            } else if (transporter.getType().equalsIgnoreCase("Transport Company")) {
                binding.gst.setVisibility(View.VISIBLE);
                binding.sp.setSelection(2);
                Log.e("spanshoe", "error ......." + transporter.getGstNumber());
            } else if (transporter.getType().equalsIgnoreCase("Packers and Movers")) {
                binding.gst.setVisibility(View.VISIBLE);
                binding.sp.setSelection(3);
                Log.e("spanshoe", "error ......." + transporter.getGstNumber());
            }
            binding.etname.setText(transporter.getName());
            binding.address.setText(transporter.getAddress());
            al = transporter.getVehicleList();
            binding.gst.setText(transporter.getGstNumber());
            binding.aadhar.setText(transporter.getAadharCardNumber());
            binding.phone.setText(transporter.getContactNumber());
            imgUrl = transporter.getImage();
            Toast.makeText(this, transporter.getGstNumber()+"...."+transporter.getAadharCardNumber(), Toast.LENGTH_SHORT).show();
        }
        binding.create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.etname.getText().toString();
                if (ActivityCompat.checkSelfPermission(CreateProfile.this, READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(CreateProfile.this, WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
                    Toast.makeText(CreateProfile.this, "if on click", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(CreateProfile.this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, 100);
                    return;
                }
                if (name.isEmpty()) {
                    binding.etname.setError("this field can't be empty.");
                    return;
                }
                final String address = binding.address.getText().toString();
                if (address.isEmpty()) {
                    binding.address.setError("this field can't be empty.");
                    return;
                }
                String phone = binding.phone.getText().toString();
                if (phone.isEmpty() || phone.length() != 10) {
                    binding.phone.setError("this field can't be empty.");
                    return;
                }
                String adhar = "", gst = "", type = "";
                if (flag == 0) {
                    adhar = binding.aadhar.getText().toString();
                    type = "Truck Owner";
                    if (adhar.isEmpty() || adhar.length() != 12) {
                        binding.aadhar.setError("this field can't be empty.");
                        return;
                    }
                } else if (flag == 1 || flag == 3) {
                    type = "Transport Company";
                    if (flag == 3)
                        type = "Packers and Movers";
                    gst = binding.gst.getText().toString();
                    if (TextUtils.isEmpty(gst) || gst.length()!=15 || gst.length()!=14) {
                        binding.gst.setError("this field can't be empty.");
                        return;
                    }
                } else if (flag == 2) {
                    Toast.makeText(CreateProfile.this, "please select a category.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String token = FirebaseInstanceId.getInstance().getToken();
                if (code != 2) {
                    if (imgUri == null) {
                        Toast.makeText(CreateProfile.this, "please select profile picture", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    File file = FileUtils.getFile(CreateProfile.this, imgUri);
                    RequestBody requestFile =
                            RequestBody.create(
                                    MediaType.parse(getContentResolver().getType(imgUri)),
                                    file
                            );
                    MultipartBody.Part body =
                            MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                    RequestBody etname = RequestBody.create(okhttp3.MultipartBody.FORM, name);
                    RequestBody typ = RequestBody.create(okhttp3.MultipartBody.FORM, type);
                    RequestBody gs = RequestBody.create(okhttp3.MultipartBody.FORM, gst);
                    RequestBody adh = RequestBody.create(okhttp3.MultipartBody.FORM, adhar);
                    RequestBody pho = RequestBody.create(okhttp3.MultipartBody.FORM, phone);
                    RequestBody add = RequestBody.create(okhttp3.MultipartBody.FORM, address);
                    RequestBody rat = RequestBody.create(okhttp3.MultipartBody.FORM, "");
                    RequestBody tok = RequestBody.create(okhttp3.MultipartBody.FORM, token);
                    RequestBody id = RequestBody.create(okhttp3.MultipartBody.FORM, FirebaseAuth.getInstance().getCurrentUser().getUid());

                    TransporterService.TransporterApi transporterApi = TransporterService.getTransporterApiInstance();
                    Call<Transporter> call = transporterApi.createTransporter(body, typ, etname, pho, add, gs, rat, tok, adh, id);
                    if (NetworkUtil.getConnectivityStatus(CreateProfile.this)) {
                        String s="Please wait...";
                        if (checkLanguage()){
                            s="कृपया प्रतीक्षा करें...";
                        }
                        final CustomProgressDialog pd=new CustomProgressDialog(CreateProfile.this,s);
                        pd.show();
                        call.enqueue(new Callback<Transporter>() {
                            @Override
                            public void onResponse(Call<Transporter> call, Response<Transporter> response) {
                                pd.dismiss();
                                Log.e("spanshoe", "..........." + response.code());
                                if (response.code() == 200) {
                                    Toast.makeText(CreateProfile.this, "profile created", Toast.LENGTH_SHORT).show();
                                    SharedPreferences mPrefs = getSharedPreferences("Transporter", MODE_PRIVATE);
                                    Transporter t = response.body();
                                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                    Gson gson = new Gson();
                                    String json = gson.toJson(t);
                                    prefsEditor.putString("Transporter", json);
                                    prefsEditor.commit();
                                    Intent in = new Intent(CreateProfile.this, ManageVehicle.class);
                                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(in);
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<Transporter> call, Throwable t) {
                                pd.dismiss();
                                Log.e("spanshoe", "" + t.getMessage());

                                Toast.makeText(CreateProfile.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else
                        Toast.makeText(CreateProfile.this, "please enable internet connection.", Toast.LENGTH_SHORT).show();
                } else {
                    Transporter transporter = new Transporter(name, FirebaseAuth.getInstance().getCurrentUser().getUid(), type, imgUrl, phone, address, adhar, gst, "", token, al);
                    TransporterService.TransporterApi transporterApi = TransporterService.getTransporterApiInstance();
                    Call<Transporter> call = transporterApi.updateTransporter(transporter);
                    if (NetworkUtil.getConnectivityStatus(CreateProfile.this)) {
                        String s="Please wait...";
                        if (checkLanguage()){
                            s="कृपया प्रतीक्षा करें...";
                        }
                        final CustomProgressDialog pd=new CustomProgressDialog(CreateProfile.this,s);
                        pd.show();
                        call.enqueue(new Callback<Transporter>() {
                            @Override
                            public void onResponse(Call<Transporter> call, Response<Transporter> response) {
                                pd.dismiss();
                                if (response.code() == 200) {
                                    Toast.makeText(CreateProfile.this, "profile updated", Toast.LENGTH_SHORT).show();
                                    SharedPreferences mPrefs = getSharedPreferences("Transporter", MODE_PRIVATE);
                                    Transporter t = response.body();
                                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                    Gson gson = new Gson();
                                    String json = gson.toJson(t);
                                    prefsEditor.putString("Transporter", json);
                                    prefsEditor.commit();
                                    Log.e("mprefs", mPrefs.getString("Transporter", "..........."));
                                    Intent in = new Intent(CreateProfile.this, ManageVehicle.class);
                                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(in);
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<Transporter> call, Throwable t) {
                                pd.dismiss();
                                Toast.makeText(CreateProfile.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else
                        Toast.makeText(CreateProfile.this, "please enable internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        ActivityCompat.requestPermissions(CreateProfile.this, new String[]{READ_PHONE_NUMBERS, READ_PHONE_STATE, ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION}, 100);
//        contentResolver = getBaseContext()
//                .getContentResolver();
//        boolean gpsStatus = Settings.Secure
//                .isLocationProviderEnabled(contentResolver,
//                        LocationManager.GPS_PROVIDER);
//        LocationManager locationManager;
//        if (!gpsStatus) {
//            alertbox("Gps Status", "Your Device's GPS is Disable");
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();
            if (code == 2) {
                TransporterService.TransporterApi transporterApi = TransporterService.getTransporterApiInstance();
                RequestBody id = RequestBody.create(okhttp3.MultipartBody.FORM, FirebaseAuth.getInstance().getCurrentUser().getUid());
                File file = FileUtils.getFile(CreateProfile.this, imgUri);
                RequestBody requestFile =
                        RequestBody.create(
                                MediaType.parse(getContentResolver().getType(imgUri)),
                                file
                        );
                MultipartBody.Part body =
                        MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                Call<Transporter> cal = transporterApi.updateImage(id, body);
                String s="Please wait...";
                if (checkLanguage()){
                    s="कृपया प्रतीक्षा करें...";
                }
                final CustomProgressDialog pd=new CustomProgressDialog(CreateProfile.this,s);
                pd.show();
                cal.enqueue(new Callback<Transporter>() {
                    @Override
                    public void onResponse(Call<Transporter> call, Response<Transporter> response) {
                        pd.dismiss();
                        if (response.code() == 200) {
                            imgUrl = response.body().getImage();
                            Picasso.get().load(imgUrl).placeholder(R.drawable.transporter_logo).into(binding.civ);
                            Toast.makeText(CreateProfile.this, "photo uploaded.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Transporter> call, Throwable t) {
                        pd.dismiss();
                        Toast.makeText(CreateProfile.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                binding.civ.setImageURI(imgUri);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        if (item.equalsIgnoreCase("Truck Owner")) {
            flag = 0;
            binding.aadhar.setVisibility(View.VISIBLE);
            binding.gst.setVisibility(View.GONE);
        } else if (item.equalsIgnoreCase("Transport Company")) {
            binding.gst.setVisibility(View.VISIBLE);
            flag = 1;
            binding.aadhar.setVisibility(View.GONE);
        } else if (item.equalsIgnoreCase("Packers and Movers")) {
            flag = 3;
            binding.gst.setVisibility(View.VISIBLE);
            binding.aadhar.setVisibility(View.GONE);
        }
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, 23);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 24);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

//    @SuppressLint("HardwareIds")
//    @Override
//    protected void onStart() {
//        super.onStart();
//        Location location = new Location("abcd");
////        if (code!=2){
////            if (ActivityCompat.checkSelfPermission(CreateProfile.this, READ_PHONE_NUMBERS) ==
////                    PermissionChecker.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CreateProfile.this,
////                    READ_PHONE_STATE) == PermissionChecker.PERMISSION_GRANTED) {
////                TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
////                //phone = manager.getLine1Number();
////            } else {
////                ActivityCompat.requestPermissions(CreateProfile.this, new String[]{READ_PHONE_NUMBERS, READ_PHONE_STATE}, 100);
////                TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
////                //phone = manager.getLine1Number();
////            }
////        }
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        try {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                ActivityCompat.requestPermissions(CreateProfile.this,new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION},103);
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            locationManager.requestLocationUpdates(
//                    LocationManager.NETWORK_PROVIDER,
//                    5000,
//                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (locationManager != null) {
//            location = locationManager
//                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        }
//        Geocoder gcd = new Geocoder(getBaseContext(),
//                Locale.getDefault());
//        List<Address> addresses;
//        String currentLocation,current_locality;
//        try {
//            addresses = gcd.getFromLocation(location.getLatitude(),
//                    location.getLongitude(), 1);
//            if (addresses.size() > 0) {
//                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                String locality = addresses.get(0).getLocality();
//                String subLocality = addresses.get(0).getSubLocality();
//                String state = addresses.get(0).getAdminArea();
//                String country = addresses.get(0).getCountryName();
//                String postalCode = addresses.get(0).getPostalCode();
//                String knownName = addresses.get(0).getFeatureName();
//                binding.address.setText(address);
//                if (subLocality != null) {
//
//                    currentLocation = locality + "," + subLocality;
//                } else {
//
//                    currentLocation = locality;
//                }
//                current_locality = locality;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(CreateProfile.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//        if (code != 2) {
//            binding.phone.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    binding.phone.setText(phone);
//                    return false;
//                }
//            });
//        }
//    }

//    protected void alertbox(String title, String mymessage) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("Your Device's GPS is Disable")
//                .setCancelable(false)
//                .setTitle("** Gps Status **")
//                .setPositiveButton("Gps On",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                // finish the current activity
//                                // AlertBoxAdvance.this.finish();
//                                Intent myIntent = new Intent(
//                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                                startActivity(myIntent);
//                                dialog.cancel();
//                            }
//                        })
//                .setNegativeButton("Cancel",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                // cancel the dialog box
//                                dialog.cancel();
//                            }
//                        });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }

//    @Override
//    public void onLocationChanged(@NonNull Location loc) {
//        String longitude = "Longitude: " + loc.getLongitude();
//        String latitude = "Latitude: " + loc.getLatitude();
//
//
//        /*----------to get City-Name from coordinates ------------- */
//        String cityName = null;
//        Geocoder gcd = new Geocoder(getBaseContext(),
//                Locale.getDefault());
//        List<Address> addresses;
//        try {
//            addresses = gcd.getFromLocation(loc.getLatitude(), loc
//                    .getLongitude(), 1);
//            if (addresses.size() > 0) {
//                System.out.println(addresses.get(0).getLocality());
//                cityName = addresses.get(0).getLocality();
//                binding.address.setText(addresses.get(0).getAddressLine(0));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        String s = longitude + "\n" + latitude +
//                "\n\nMy Currrent City is: " + cityName;
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//        // TODO Auto-generated method stub
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//        // TODO Auto-generated method stub
//    }
//
//    @Override
//    public void onStatusChanged(String provider,
//                                int status, Bundle extras) {
//        // TODO Auto-generated method stub
//    }
    public  boolean checkLanguage() {
        SharedPreferences mprefs =getSharedPreferences("Transporter",MODE_PRIVATE);
        String s=mprefs.getString("language","");
        if (s.equalsIgnoreCase("hindi")){
            return true;
        }
        return false;
    }
}

