package com.foodbrigade.firechat.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.foodbrigade.firechat.Message;
import com.foodbrigade.firechat.R;
import com.foodbrigade.firechat.User;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowMemberAdapter extends RecyclerView.Adapter<ShowMemberAdapter.ShowMemberViewHolder> {
    Context context;
    ArrayList<String>al;
    String userKey,groupkey;
    DatabaseReference rootReference;
    ArrayList<Message>messageList;
    User u;
    public ShowMemberAdapter(Context context, ArrayList<String>al,String groupkey){
        userKey= FirebaseAuth.getInstance().getCurrentUser().getUid();
        rootReference=FirebaseDatabase.getInstance().getReference();
        this.context=context;
        this.groupkey=groupkey;
        this.al=al;
    }

    private void getMessage() {

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
                         u = snapshot.getValue(User.class);
                        holder.tvname.setText(u.getUsername());
                        holder.tvStatus.setText(u.getStatus());
                        Picasso.get().load(u.getImage()).into(holder.civ);
                        holder.ll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                rootReference.child("Group").child(groupkey).child("Messages").orderByChild("from").equalTo(u.getKey()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            messageList=new ArrayList<>();
                                            Iterator<DataSnapshot>itr=snapshot.getChildren().iterator();
                                            while(itr.hasNext()){
                                                DataSnapshot ds=itr.next();
                                                Message msg=ds.getValue(Message.class);
                                                messageList.add(msg);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                if (messageList!=null) {
                                    AlertDialog.Builder ab = new AlertDialog.Builder(context);
                                    View v = LayoutInflater.from(context).inflate(R.layout.member_message, null);
                                    ab.setView(v);
                                    TextView tvname = v.findViewById(R.id.tvfrnd_name);
                                    tvname.setText(u.getUsername());
                                    ListView lv = v.findViewById(R.id.lv);
                                    final MemberMessagaAdapter adapter = new MemberMessagaAdapter(context, R.layout.member_message_list, messageList);
                                    lv.setAdapter(adapter);
                                    CircleImageView civ = v.findViewById(R.id.civ_chat);
                                    Picasso.get().load(u.getImage()).into(civ);
                                    ImageView ivback = v.findViewById(R.id.btn_back);
                                    ivback.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                    ab.show();
                                }
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
            ll=v.findViewById(R.id.lladapter);
            tvStatus=v.findViewById(R.id.tvStatus);
        }
    }

}
