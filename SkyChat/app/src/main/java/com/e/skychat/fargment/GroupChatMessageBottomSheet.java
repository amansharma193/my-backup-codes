package com.e.skychat.fargment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.e.skychat.R;
import com.e.skychat.beans.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GroupChatMessageBottomSheet extends BottomSheetDialogFragment {

    String currentUserId,groupId,currentUserImageUrl;
    StorageReference storageReference;
    DatabaseReference groupReference;
    public GroupChatMessageBottomSheet(String currentUserId, String groupId,String currentUserImageUrl){
        this.currentUserId = currentUserId;
        this.groupId = groupId;
        this.currentUserImageUrl = currentUserImageUrl;

        storageReference = FirebaseStorage.getInstance().getReference("Document Messages");
        groupReference = FirebaseDatabase.getInstance().getReference("Group");
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.file_dialog,container,false);
        ImageView ivPdfFile = v.findViewById(R.id.ivPdfFile);
        ImageView ivWordFile = v.findViewById(R.id.ivWordFile);

        ivPdfFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_GET_CONTENT);
                in.setType("application/pdf");
                startActivityForResult(Intent.createChooser(in,"send pdf file"),333);
            }
        });
        ivWordFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent in = new Intent(Intent.ACTION_GET_CONTENT);
               in.setType("application/msword");
               startActivityForResult(Intent.createChooser(in,"send word file"),444);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 333 && resultCode == getActivity().RESULT_OK && data != null && data.getData()!=null){
            Uri fileUri = data.getData();
            sendDocumentMessage(fileUri,"pdf");
        }
        if(requestCode == 444 && resultCode == getActivity().RESULT_OK && data!=null && data.getData()!=null){
            Uri fileUri = data.getData();
            sendDocumentMessage(fileUri,"docx");
        }
    }

    private void sendDocumentMessage(Uri fileUri, final String type){
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setTitle("please wait");
        pd.setMessage("Uploading...");
        pd.show();

       final String messageId = groupReference.push().getKey();
       StorageReference filePath = storageReference.child(messageId+"."+type);
       filePath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                   @Override
                   public void onSuccess(Uri uri) {
                       String fileUrl = uri.toString();
                       Calendar calendar = Calendar.getInstance();
                       SimpleDateFormat sd = new SimpleDateFormat("MMM dd,yyy");
                       String date = sd.format(calendar.getTime());

                       sd = new SimpleDateFormat("hh:mm a");
                       String time = sd.format(calendar.getTime());

                       final Message msg = new Message();
                       msg.setDate(date);
                       msg.setTime(time);
                       msg.setType(type);
                       msg.setTimestamp(calendar.getTimeInMillis());
                       msg.setMessageId(messageId);
                       msg.setMessage(fileUrl);
                       msg.setFrom(currentUserId);
                       msg.setSenderIcon(currentUserImageUrl);

                       groupReference.child(groupId).child("messages").child(messageId).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                             pd.dismiss();
                             dismiss();
                             if(!task.isSuccessful())
                                 Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                           }
                       });

                   }
               });


           }
       });
    }
}
