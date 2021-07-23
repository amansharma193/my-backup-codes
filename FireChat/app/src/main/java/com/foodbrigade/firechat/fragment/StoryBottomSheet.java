package com.foodbrigade.firechat.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foodbrigade.firechat.Activity_create_story;
import com.foodbrigade.firechat.MainActivity;
import com.foodbrigade.firechat.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class StoryBottomSheet extends BottomSheetDialogFragment {
    String userkey;
    DatabaseReference rootReference;
    Context context;
    StorageReference filepath,storageReference;
    public StoryBottomSheet(String userkey, Context context) {
        this.userkey = userkey;
        this.context=context;
        rootReference= FirebaseDatabase.getInstance().getReference();
        filepath= FirebaseStorage.getInstance().getReference();
        storageReference= FirebaseStorage.getInstance().getReference("Documents");
    }

    public StoryBottomSheet() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.bottom_sheet_story,container,false);
        ImageView ivpdf=v.findViewById(R.id.ivpdf);
        ImageView ivdoc=v.findViewById(R.id.ivdoc);
        LinearLayout ll=v.findViewById(R.id.ll);
        ImageView ivcamera=v.findViewById(R.id.ivcamera);
        ImageView ivgallery=v.findViewById(R.id.ivgallery);
        ivgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(i, 101);
                    }
        });
        ivpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(Intent.ACTION_PICK,
                        MediaStore.Video.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(in,"select video file of 15sec"),444);
            }
        });
        ivdoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(getContext(),Activity_create_story.class);
                in.putExtra("key",333);
                startActivity(in);
                dismiss();
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
        if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getContext(), "Canceled", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                   if(data.getData()!=null){
                       Uri imageUri=data.getData();
                       Intent in=new Intent(getActivity(), Activity_create_story.class);
                       in.putExtra("uri",imageUri+"");
                       in.putExtra("key",101);
                       getContext().startActivity(in);
                       dismiss();
                   }
                }
            }
        }
        else if (requestCode == 444 && resultCode == getActivity().RESULT_OK && data!=null && data.getData()!=null){
            Uri videouri=data.getData();
            Intent in=new Intent(getActivity(), Activity_create_story.class);
            in.putExtra("uri",videouri+"");
            in.putExtra("key",444);
            getContext().startActivity(in);
            dismiss();
        }
        else if (requestCode == 222 ) {
            try {
                dismiss();
                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bmp, "title", null);
                Uri uri = Uri.parse(path);
                Intent in=new Intent(getContext(),Activity_create_story.class);
                in.putExtra("uri",uri+"");
                in.putExtra("key",222);
                getContext().startActivity(in);
            }
            catch (Exception e){
                Log.e("exception","   "+e.getMessage());
            }
        }
        }
}
