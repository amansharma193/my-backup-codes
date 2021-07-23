package com.foodbrigade.firechat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.foodbrigade.firechat.dialog.CustomProgressDialogue;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class GroupMessageBottomSheet extends BottomSheetDialogFragment {
    String userkey,groupkey;
    DatabaseReference rootReference;
    StorageReference filepath,storageReference;
    public GroupMessageBottomSheet(String userkey, String groupkey) {
        this.userkey = userkey;
        this.groupkey = groupkey;
        rootReference= FirebaseDatabase.getInstance().getReference();
        filepath= FirebaseStorage.getInstance().getReference();
        storageReference= FirebaseStorage.getInstance().getReference("Documents");
    }
    public GroupMessageBottomSheet(){

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.bottom_sheet,container,false);
        ImageView ivpdf=v.findViewById(R.id.ivpdf);
        ImageView ivdoc=v.findViewById(R.id.ivdoc);
        ImageView ivgallery=v.findViewById(R.id.ivgallery);
        ImageView ivcamera=v.findViewById(R.id.ivcamera);
        ivcamera.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Log.e("onactivity","   before intent");
                    startActivityForResult(i, 222);
                    Log.e("onactivity","   after intent");
                } catch (Exception e){
                    Log.e("exception","   "+e.getMessage());
                }
            }
        });
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
                startActivityForResult(Intent.createChooser(in,"select doc file"),444);
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
        return v;
    }
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onactivity","   onactivityresult intent");
        rootReference= FirebaseDatabase.getInstance().getReference();
        filepath= FirebaseStorage.getInstance().getReference();
        storageReference= FirebaseStorage.getInstance().getReference("Documents");
        userkey= FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (requestCode == 222) {
            if (getArguments().getString("groupkey")!=null){
                return;
            }
            groupkey=getArguments().getString("groupkey");
            dismiss();
            final CustomProgressDialogue object = new CustomProgressDialogue(getContext(), "Sending...");
            object.show();
            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bmp, "title", null);
            Uri imageUri = Uri.parse(path);
            final String messageId = rootReference.child("Group").child(groupkey).child("Messages").push().getKey();
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
                            final Message msg = new Message(url, "image", date, time, userkey, groupkey, messageId, Calendar.getInstance().getTimeInMillis());
                            rootReference.child("Group").child(groupkey).child("Messages").child(messageId).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    object.dismiss();
                                }
                            });
                        }
                    });
                }
            });
        }
        if (requestCode == 444 && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();
            sendDocumentMessage(fileUri, "docx");
        }
        if (requestCode == 333 && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();
            sendDocumentMessage(fileUri, "pdf");
        }
        if (requestCode == 111 && resultCode == getActivity().RESULT_OK && data != null && (data.getClipData() != null||data.getData()!=null)) {
            dismiss();
            final CustomProgressDialogue object = new CustomProgressDialogue(getContext(), "Sending...");
            object.show();
            if (data.getClipData()!=null) {
                int total = data.getClipData().getItemCount();
                for (int i = 0; i < total; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    final String messageId = rootReference.child("Group").child(groupkey).child("Messages").push().getKey();
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
                                    final Message msg = new Message(url, "image", date, time, userkey, groupkey, messageId, Calendar.getInstance().getTimeInMillis());
                                    rootReference.child("Group").child(groupkey).child("Messages").child(messageId).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            object.dismiss();
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
                final String messageId = rootReference.child("Group").child(groupkey).child("Messages").push().getKey();
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
                                final Message msg = new Message(url, "image", date, time, userkey, groupkey, messageId, Calendar.getInstance().getTimeInMillis());
                                rootReference.child("Group").child(groupkey).child("Messages").child(messageId).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        object.dismiss();
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
        final String msgid=rootReference.child("Group").child(groupkey).child("Messages").push().getKey();
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
                        final Message message=new Message(fileurl,type,date,time,userkey,groupkey,msgid,c.getTimeInMillis());
                        rootReference.child("Group").child(groupkey).child("Messages").child(msgid).setValue(message)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        object.dismiss();
                                    }
                                });
                    }
                });
            }
        });
    }
}
