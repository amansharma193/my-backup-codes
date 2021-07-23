package com.freelannceritservices.digitalsignature;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.freelannceritservices.digitalsignature.databinding.ActivityMainBinding;
import com.freelannceritservices.digitalsignature.databinding.SignatureViewBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    //boolean check = true;
    private SignatureViewBinding s;
    Uri img;
    String img_url, key;
    //    String ServerUploadPath = "https://192.168.137.1/aman/image.php";
    //    private final String url = "https://192.168.137.1/";
    int sp = 2;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Survey Form");
        binding.addsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("spanshoe", "click..............");
                if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                MainActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            showDialog("External storage", MainActivity.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

                        } else {
                            ActivityCompat
                                    .requestPermissions(
                                            (Activity) MainActivity.this,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            123);
                        }
                    }
                }
//                if (ContextCompat.checkSelfPermission(
//                        MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
//                        PackageManager.PERMISSION_GRANTED) {
//                    int result = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
//                    if (ContextCompat.checkSelfPermission(
//                            MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
//                            PackageManager.PERMISSION_GRANTED) {
//                        int res = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                        if (result == PackageManager.PERMISSION_GRANTED && res == PackageManager.PERMISSION_GRANTED) {
//                        }else
//                            return;
//                    }
//                }

                AlertDialog ab = new AlertDialog.Builder(MainActivity.this).create();
                s = SignatureViewBinding.inflate(LayoutInflater.from(MainActivity.this));
                ab.setView(s.getRoot());
                s.clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        s.sign.clearCanvas();
                    }
                });
                s.save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap bm = s.sign.getSignatureBitmap();
                        img = getImageUri(bm);
                        binding.sign.setImageURI(img);
                        Log.e("spansheo", "" + img);
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        key = FirebaseFirestore.getInstance().collection("Public").document().getId();
                        StorageReference filepath = storageReference.child(key + ".jpg");
                        filepath.putFile(img).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        img_url = uri.toString();
                                    }
                                });
                            }
                        });
                        Toast.makeText(MainActivity.this, "saved.", Toast.LENGTH_SHORT).show();
                        ab.dismiss();
                    }
                });
                ab.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ArrayList<String> al = new ArrayList<>();
        al.add("Relationship");
        al.add("Father");
        al.add("Husband");
        al.add("Brother");
        al.add("Other");
        ArrayList<String> al1 = new ArrayList<>();
        al1.add("Occupation");
        al1.add("Job");
        al1.add("Self Employed");
        al1.add("Farmer");
        al1.add("Retired");
        al1.add("Other");
        ArrayList<String> al2 = new ArrayList<>();
        al2.add("Gender");
        al2.add("Male");
        al2.add("Female");
        al2.add("Other");
        binding.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, binding.more);
                Menu menu = popup.getMenu();
                menu.add("Log Out");
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String title = item.getTitle().toString();
                        if (title.equalsIgnoreCase("Log Out")) {
                            SharedPreferences mPrefs = getSharedPreferences("Digital", MODE_PRIVATE);
                            SharedPreferences.Editor prefsEditor = mPrefs.edit();
                            prefsEditor.putString("user", "");
                            prefsEditor.commit();
                            Intent intent = new Intent(MainActivity.this, Login.class);
                            startActivity(intent);
                            finish();
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, al);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter adapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, al1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, al2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(adapter);
        binding.spinner1.setAdapter(adapter1);
        binding.spinner2.setAdapter(adapter2);
        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s.sign.clearCanvas();
                String wardNumber, wardName, cast, remarks, head, relation, gender;
                wardNumber = binding.wardNumber.getText().toString();
                if (TextUtils.isEmpty(wardNumber)) {
                    binding.wardNumber.setError("this field can't be empty");
                    binding.wardNumber.setFocusable(true);
                    return;
                }
                wardName = binding.wardName.getText().toString();
                if (TextUtils.isEmpty(wardName)) {
                    binding.wardName.setError("this field can't be empty");
                    binding.wardName.setFocusable(true);
                    return;
                }
                String name = binding.name.getText().toString();
                String occu = (String) binding.spinner1.getSelectedItem();
                String mobile = binding.contact.getText().toString();
                String whatsapp = binding.mobile.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    binding.name.setError("this field can't be empty");
                    binding.name.setFocusable(true);
                    return;
                }
                String address = binding.address.getText().toString();
                if (TextUtils.isEmpty(address)) {
                    binding.address.setError("this field can't be empty");
                    binding.address.setFocusable(true);
                    return;
                }
                head = binding.headname.getText().toString();
                if (TextUtils.isEmpty(head)) {
                    binding.headname.setError("this field can't be empty");
                    binding.headname.setFocusable(true);
                    return;
                }
                relation = (String) binding.spinner.getSelectedItem();
                gender = (String) binding.spinner2.getSelectedItem();
                if (relation.equalsIgnoreCase("Relationship")) {
                    Toast.makeText(MainActivity.this, "Please select Relation", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (occu.equalsIgnoreCase("Occupation")) {
                    Toast.makeText(MainActivity.this, "please select occupation", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (gender.equalsIgnoreCase("gender")) {
                    Toast.makeText(MainActivity.this, "Please select gender", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mobile.length() != 10) {
                    binding.contact.setError("this field can't be empty");
                    binding.contact.setFocusable(true);
                    return;
                }
                if (whatsapp.length() != 10) {
                    binding.mobile.setError("this field can't be empty");
                    binding.mobile.setFocusable(true);
                    return;
                }
                cast = binding.cast.getText().toString();
                if (TextUtils.isEmpty(cast)) {
                    binding.cast.setError("this field can't be empty");
                    binding.cast.setFocusable(true);
                    return;
                }
                remarks = binding.remarks.getText().toString();
                if (TextUtils.isEmpty(remarks)) {
                    binding.remarks.setError("this field can't be empty");
                    binding.remarks.setFocusable(true);
                    return;
                }
                if (img_url == null) {
                    Toast.makeText(MainActivity.this, "please upload signature", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.e("spanshoe", "uri" + img);
                String key = FirebaseFirestore.getInstance().collection("Public").document().getId();
                Family family = new Family(wardNumber, wardName, name, head, relation, remarks, img_url, key, gender, occu, mobile, whatsapp, address, cast);
                FirebaseFirestore.getInstance().collection("Public").document(key).set(family).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            clear();
                            Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //String qrystring = "aman?ward=" + wardNumber + "&colony_name=" + wardNumber + "&cast=" + cast + "&remarks=" + remarks + "&img=" + img_url + "&name=" +name+"&occupation="+occu+"&mobile="+mobile+"&whatsapp="+whatsapp+"&address="+address+"&headname="+head+"&relation="+relation+"&gender="+gender;
                //http://localhost/aman/?ward=123&booth=111&wname=111&house=111&cast=111&remarks=111&img=111&array=111
//                class DbClass extends AsyncTask<String, String, String> {
//
//                    @Override
//                    protected void onPreExecute() {
//                        super.onPreExecute();
//                    }
//
//                    @Override
//                    protected String doInBackground(String... s) {
//                        try {
//                            URL url = new URL(s[0]);
//                            HttpsTrustManager.allowAllSSL();
//                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                            BufferedReader bf = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
//                            return bf.readLine();
//                        } catch (Exception e) {
//                            Log.e("spanshoe", "" + e.getMessage());
//                            return e.getMessage();
//                        }
//                    }
//
//                    @Override
//                    protected void onPostExecute(String s) {
//                        super.onPostExecute(s);
//                        Log.e("spanshoe", "" + s);
//                        clear();
//                    }
//                }
//                DbClass obj = new DbClass();
//                obj.execute(url + qrystring);
            }
        });
    }

    private void clear() {
        binding.sign.setImageDrawable(getDrawable(R.drawable.white_back));
        binding.wardName.setText("");
        binding.wardNumber.setText("");
        binding.cast.setText("");
        binding.remarks.setText("");
        binding.name.setText("");
        binding.contact.setText("");
        binding.mobile.setText("");
        binding.headname.setText("");
        binding.spinner.setSelection(0);
        binding.spinner1.setSelection(0);
        binding.spinner2.setSelection(0);
    }

    public Uri getImageUri(Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        byte[] byteArrayVar = bytes.toByteArray();
//        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
//        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {
//            @Override
//            protected void onPreExecute() {
//
//                super.onPreExecute();
//            }
//
//            @Override
//            protected void onPostExecute(String string1) {
//
//                super.onPostExecute(string1);
//
//                // Dismiss the progress dialog after done uploading.
//
//                // Printing uploading success message coming from server on android app.
//                img_url = "images/" + string1;
//                Log.e("spanshoe", "" + img_url);
//                // Setting image as transparent after done uploading.
//
//
//            }
//
//            @Override
//            protected String doInBackground(Void... params) {
//
//                ImageProcessClass imageProcessClass = new ImageProcessClass();
//
//                HashMap<String, String> HashMapParams = new HashMap<String, String>();
//
//                HashMapParams.put("imageName", Calendar.getInstance().getTimeInMillis() + "");
//
//                HashMapParams.put("imagePath", ConvertImage);
//
//                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);
//
//                return FinalData;
//            }
//        }
//        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
//
//        AsyncTaskUploadClassOBJ.execute();
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "IMG_" + Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }
//    public class ImageProcessClass {
//
//        public String ImageHttpRequest(String requestURL, HashMap<String, String> PData) {
//
//            StringBuilder stringBuilder = new StringBuilder();
//
//            try {
//
//                URL url;
//                HttpURLConnection httpURLConnectionObject;
//                OutputStream OutPutStream;
//                BufferedWriter bufferedWriterObject;
//                BufferedReader bufferedReaderObject;
//                int RC;
//
//                url = new URL(requestURL);
//                Log.e("spanshoe", "image upload");
//                HttpsTrustManager.allowAllSSL();
//                httpURLConnectionObject = (HttpURLConnection) url.openConnection();
//
//                httpURLConnectionObject.setReadTimeout(19000);
//
//                httpURLConnectionObject.setConnectTimeout(19000);
//
//                httpURLConnectionObject.setRequestMethod("POST");
//
//                httpURLConnectionObject.setDoInput(true);
//
//                httpURLConnectionObject.setDoOutput(true);
//
//                OutPutStream = httpURLConnectionObject.getOutputStream();
//
//                bufferedWriterObject = new BufferedWriter(
//
//                        new OutputStreamWriter(OutPutStream, "UTF-8"));
//
//                bufferedWriterObject.write(bufferedWriterDataFN(PData));
//
//                bufferedWriterObject.flush();
//
//                bufferedWriterObject.close();
//
//                OutPutStream.close();
//
//                RC = httpURLConnectionObject.getResponseCode();
//
//                if (RC == HttpsURLConnection.HTTP_OK) {
//
//                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));
//
//                    stringBuilder = new StringBuilder();
//
//                    String RC2;
//
//                    while ((RC2 = bufferedReaderObject.readLine()) != null) {
//
//                        stringBuilder.append(RC2);
//                    }
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return stringBuilder.toString();
//        }
//
//        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {
//
//            StringBuilder stringBuilderObject;
//
//            stringBuilderObject = new StringBuilder();
//
//            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
//
//                if (check)
//
//                    check = false;
//                else
//                    stringBuilderObject.append("&");
//
//                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
//
//                stringBuilderObject.append("=");
//
//                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
//            }
//
//            return stringBuilderObject.toString();
//        }
//
//    }
    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                123);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 123:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                    Toast.makeText(MainActivity.this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }
}
