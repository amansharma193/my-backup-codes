package com.foodbrigade.firechat.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foodbrigade.firechat.ChatActivity;
import com.foodbrigade.firechat.Message;
import com.foodbrigade.firechat.R;
import com.foodbrigade.firechat.Setting_Activity;
import com.foodbrigade.firechat.dialog.CustomProgressDialogue;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class MessageBottomSheet extends BottomSheetDialogFragment {
    String userkey,friendkey;
    DatabaseReference rootReference;
    StorageReference filepath,storageReference;
    public MessageBottomSheet(String userkey, String friendkey) {
        this.userkey = userkey;
        this.friendkey = friendkey;
        rootReference= FirebaseDatabase.getInstance().getReference();
        filepath= FirebaseStorage.getInstance().getReference();
        storageReference= FirebaseStorage.getInstance().getReference("Documents");
    }

    public MessageBottomSheet() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.bottom_sheet,container,false);
        ImageView ivpdf=v.findViewById(R.id.ivpdf);
        ImageView ivdoc=v.findViewById(R.id.ivdoc);
        ImageView ivgallery=v.findViewById(R.id.ivgallery);
        ImageView ivcamera=v.findViewById(R.id.ivcamera);
        ivpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(Intent.ACTION_GET_CONTENT);
                in.setType("application/pdf");
                startActivityForResult(Intent.createChooser(in,"select pdf file"),333);
            }
        });
        ivdoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(Intent.ACTION_GET_CONTENT);
                in.setType("application/msword");
                startActivityForResult(Intent.createChooser(in,"select word file"),444);
            }
        });
        ivgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i,"select picture"),111);
            }
        });
        ivcamera.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(getContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.CAMERA},100);
                }else {
                    Intent in = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(in, 222);
                }
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==222 ){
            rootReference= FirebaseDatabase.getInstance().getReference();
            filepath= FirebaseStorage.getInstance().getReference();
            storageReference= FirebaseStorage.getInstance().getReference("Documents");
            userkey= FirebaseAuth.getInstance().getCurrentUser().getUid();
            friendkey=getArguments().getString("groupkey");
            final CustomProgressDialogue object = new CustomProgressDialogue(getContext(), "Sending...");
            object.show();
            dismiss();
            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bmp, "title", null);
            Uri imageUri = Uri.parse(path);
            final String messageId = rootReference.child("Messages").child(userkey).child(friendkey).push().getKey();
            filepath = storageReference.child(messageId + ".jpg");
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat sd = new SimpleDateFormat("MMM dd, yyyy");
                            String date = sd.format(c.getTime());
                            sd = new SimpleDateFormat("hh:mm a");
                            String time = sd.format(c.getTime());
                            final Message msg = new Message(url, "image", date, time, userkey, friendkey, messageId, Calendar.getInstance().getTimeInMillis());

                            rootReference.child("Messages").child(userkey).child(friendkey).child(messageId).setValue(msg)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                rootReference.child("Messages").child(friendkey).child(userkey).child(messageId).setValue(msg)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                object.dismiss();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    });
                }
            });
        }
        if (requestCode==444 && resultCode==getActivity().RESULT_OK && data!=null && data.getData()!=null){
            Uri fileUri=data.getData();
            sendDocumentMessage(fileUri,"docx");
        }
        if (requestCode==333 && resultCode==getActivity().RESULT_OK && data!=null && data.getData()!=null){
            Uri fileUri=data.getData();
            sendDocumentMessage(fileUri,"pdf");
        }
        if (requestCode==111 && resultCode==getActivity().RESULT_OK && data!=null && (data.getClipData()!=null||data.getData()!=null)) {
                dismiss();
                final CustomProgressDialogue object = new CustomProgressDialogue(getContext(), "Sending...");
                object.show();
                if (data.getClipData()!=null) {
                    int total = data.getClipData().getItemCount();
                    for (int i = 0; i < total; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();

                        final String messageId = rootReference.child("Messages").child(userkey).child(friendkey).push().getKey();
                        filepath = storageReference.child(messageId + ".jpg");
                        filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String url = uri.toString();
                                        Calendar c = Calendar.getInstance();
                                        SimpleDateFormat sd = new SimpleDateFormat("MMM dd, yyyy");
                                        String date = sd.format(c.getTime());
                                        sd = new SimpleDateFormat("hh:mm a");
                                        String time = sd.format(c.getTime());
                                        final Message msg = new Message(url, "image", date, time, userkey, friendkey, messageId, Calendar.getInstance().getTimeInMillis());

                                        rootReference.child("Messages").child(userkey).child(friendkey).child(messageId).setValue(msg)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            rootReference.child("Messages").child(friendkey).child(userkey).child(messageId).setValue(msg)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            object.dismiss();
//                                                if (task.isSuccessful()){
//                                                    Toast.makeText(getActivity(), "sent...", Toast.LENGTH_SHORT).show();
//                                                }
//                                                else{
//                                                    Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
//                                                }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                    }
                                });
                            }
                        });
                    }
                }
                else{
                    Uri imageUri=data.getData();
                    final String messageId = rootReference.child("Messages").child(userkey).child(friendkey).push().getKey();
                    filepath = storageReference.child(messageId + ".jpg");
                    filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    Calendar c = Calendar.getInstance();
                                    SimpleDateFormat sd = new SimpleDateFormat("MMM dd, yyyy");
                                    String date = sd.format(c.getTime());
                                    sd = new SimpleDateFormat("hh:mm a");
                                    String time = sd.format(c.getTime());
                                    final Message msg = new Message(url, "image", date, time, userkey, friendkey, messageId, Calendar.getInstance().getTimeInMillis());

                                    rootReference.child("Messages").child(userkey).child(friendkey).child(messageId).setValue(msg)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        rootReference.child("Messages").child(friendkey).child(userkey).child(messageId).setValue(msg)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        object.dismiss();
//                                                if (task.isSuccessful()){
//                                                    Toast.makeText(getActivity(), "sent...", Toast.LENGTH_SHORT).show();
//                                                }
//                                                else{
//                                                    Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
//                                                }
                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    });
                }
            }
        }


    private void sendDocumentMessage(Uri fileUri, final String type) {
        dismiss();
        final CustomProgressDialogue object=new CustomProgressDialogue(getContext(),"Sending...");
        object.show();
        final String msgid=rootReference.child("Messages").push().getKey();
        StorageReference filepath=storageReference.child(msgid+"."+type);
        filepath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String fileurl=uri.toString();
                        Calendar c=Calendar.getInstance();
                        SimpleDateFormat sd=new SimpleDateFormat("MMM dd, yyyy");
                        String date=sd.format(c.getTime());
                        sd=new SimpleDateFormat("hh:mm a");
                        String time=sd.format(c.getTime());
                        final Message message=new Message(fileurl,type,date,time,userkey,friendkey,msgid,c.getTimeInMillis());
                        rootReference.child("Messages").child(userkey).child(friendkey).child(msgid).setValue(message)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            rootReference.child("Messages").child(friendkey).child(userkey).child(msgid).setValue(message)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            object.dismiss();
//                                                if (task.isSuccessful()){
//                                                    Toast.makeText(getActivity(), "sent...", Toast.LENGTH_SHORT).show();
//                                                }
//                                                else{
//                                                    Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
//                                                }
                                                        }
                                                    });
                                        }
                                    }
                                });
                    }
                });


            }
        });
    }
}
