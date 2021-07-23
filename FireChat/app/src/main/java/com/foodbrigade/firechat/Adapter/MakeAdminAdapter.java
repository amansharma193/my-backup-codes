package com.foodbrigade.firechat.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foodbrigade.firechat.MakeAdminActivity;
import com.foodbrigade.firechat.MakeAdminBottomSheet;
import com.foodbrigade.firechat.R;
import com.foodbrigade.firechat.User;
import com.foodbrigade.firechat.fragment.MessageBottomSheet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MakeAdminAdapter extends RecyclerView.Adapter<MakeAdminAdapter.ShowMemberViewHolder> {
    Context context;
    ArrayList<String>al;
    String userKey,groupkey;
    DatabaseReference rootReference;
    public MakeAdminAdapter(Context context, ArrayList<String>al){
        userKey= FirebaseAuth.getInstance().getCurrentUser().getUid();
        rootReference=FirebaseDatabase.getInstance().getReference();
        this.context=context;
        groupkey=al.get(0);
        al.remove(0);
        this.al=al;
    }
    @NonNull
    @Override
    public ShowMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.friend_view,parent,false);
        return new ShowMemberViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ShowMemberViewHolder holder, int position) {
        rootReference.child("Users").child(al.get(position)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        final User u = snapshot.getValue(User.class);
                        holder.tvname.setText(u.getUsername());
                        holder.tvStatus.setText(u.getStatus());
                        Picasso.get().load(u.getImage()).into(holder.civ);
                        holder.ll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View view) {
                                BottomSheetDialogFragment dialogFragment=new MakeAdminBottomSheet(userKey,u.getKey(),groupkey);
                                dialogFragment.show(((MakeAdminActivity)context).getSupportFragmentManager(),"dialog");
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    public class ShowMemberViewHolder extends RecyclerView.ViewHolder{
        CircleImageView civ;
        TextView tvname,tvStatus;
        LinearLayout ll;
        public ShowMemberViewHolder(@NonNull View v) {
            super(v);
            civ=v.findViewById(R.id.civ_pro);
            tvname=v.findViewById(R.id.tvName);
            tvStatus=v.findViewById(R.id.tvStatus);
            ll=v.findViewById(R.id.lladapter);
        }
    }
}
