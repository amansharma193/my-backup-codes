package com.e.skychat.fargment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.e.skychat.R;
import com.e.skychat.beans.Group;
import com.e.skychat.beans.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateAdminBottomSheet extends BottomSheetDialogFragment {
    User user;
    DatabaseReference groupReference,memberReference;
    Group group;
    String currentUserId;
    public CreateAdminBottomSheet(User user, DatabaseReference groupReference, DatabaseReference memberReference, Group group, String currentUserId){
        this.currentUserId = currentUserId;
        this.group = group;
        this.user = user;
        this.groupReference = groupReference;
        this.memberReference = memberReference;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_admin,container,false);
        CircleImageView civUser = v.findViewById(R.id.civUser);
        final TextView tvName = v.findViewById(R.id.tvUser);
        Button btnExit = v.findViewById(R.id.btnExit);
        Button btnCancel = v.findViewById(R.id.btnCancel);

        Picasso.get().load(user.getImage()).placeholder(R.drawable.firechaticon).into(civUser);
        tvName.setText(user.getName());
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String,Object> hm = new HashMap<>();
                hm.put("createdBy",user.getUid());
                groupReference.child(group.getGroupId()).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if(task.isSuccessful()){
                          memberReference.child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    groupReference.child(group.getGroupId()).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                          if(task.isSuccessful()){
                                              dismiss();
                                              getActivity().finish();
                                          }
                                          else
                                              Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                              }
                          });
                       }
                       else
                           Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               dismiss();
            }
        });
        return v;
    }
}
