package com.foodbrigade.firechat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MakeAdminBottomSheet extends BottomSheetDialogFragment {
    String userkey,friendkey,groupkey;
    User friend;
    DatabaseReference rootReference;
    public MakeAdminBottomSheet(String userkey, String friendkey,String groupkey) {
        this.userkey = userkey;
        this.friendkey = friendkey;
        this.groupkey=groupkey;
        rootReference= FirebaseDatabase.getInstance().getReference();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v=inflater.inflate(R.layout.dialog_make_admin,container,false);
        rootReference.child("Users").child(friendkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    friend=snapshot.getValue(User.class);
                    TextView tvname=v.findViewById(R.id.tvadmname);
                    tvname.setText(friend.getUsername());
                    CircleImageView civ=v.findViewById(R.id.civ_mkadm);
                    Picasso.get().load(friend.getImage()).into(civ);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Button btncancel=v.findViewById(R.id.btncancel);
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        Button btnExit=v.findViewById(R.id.btnexit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String,Object>hm=new HashMap<>();
                hm.put("createdBy",friendkey);
                rootReference.child("Group").child(groupkey).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            rootReference.child("Group").child(groupkey).child(userkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        rootReference.child("Group").child(groupkey).child("members").child(userkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    dismiss();
                                                    ((MakeAdminActivity)getContext()).finish();
                                                }else{
                                                    Toast.makeText(getContext(), "error... try again", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        return v;
    }
}
