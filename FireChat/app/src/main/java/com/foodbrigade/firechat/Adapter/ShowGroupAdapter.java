package com.foodbrigade.firechat.Adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodbrigade.firechat.AddMemberActivity;
import com.foodbrigade.firechat.Group;
import com.foodbrigade.firechat.MemberInfoActivity;
import com.foodbrigade.firechat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowGroupAdapter extends FirebaseRecyclerAdapter<Group, ShowGroupAdapter.ShowGroupViewHolder> {
    OnRecyclerViewClick listener;
    String userkey;
    DatabaseReference rootReference;
    public ShowGroupAdapter(@NonNull FirebaseRecyclerOptions<Group> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ShowGroupViewHolder holder, int i, @NonNull final Group group) {
        userkey= FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userkey.equals(group.getCreatedBy())){
            holder.ivmore.setVisibility(View.VISIBLE);
        }
        else{
            holder.ivmore.setVisibility(View.GONE);
        }
        holder.tvdescription.setText(group.getDescription());
        holder.tvname.setText(group.getName());
        Picasso.get().load(group.getIcon()).into(holder.civgroupicon);
        holder.ivmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popupMenu=new PopupMenu(view.getContext(),view);
                final Menu menu=popupMenu.getMenu();
                menu.add("Group Info");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        String title=menuItem.getTitle().toString();
                        if(title.equalsIgnoreCase("Group info")){
                            Intent in=new Intent(view.getContext(), MemberInfoActivity.class);
                            in.putExtra("groupkey",group);
                            view.getContext().startActivity(in);
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @NonNull
    @Override
    public ShowGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.group_view,parent,false);
        return new ShowGroupViewHolder(v);
    }

    public class ShowGroupViewHolder extends RecyclerView.ViewHolder{
        CircleImageView civgroupicon;
        TextView tvname,tvdescription;
        LinearLayout ll;
        ImageView ivmore;
        public ShowGroupViewHolder(@NonNull final View itemView) {
            super(itemView);
            civgroupicon=itemView.findViewById(R.id.civ_pro);
            tvname=itemView.findViewById(R.id.tvName);
            ll=itemView.findViewById(R.id.ll);
            ivmore=itemView.findViewById(R.id.ivmore);
            tvdescription=itemView.findViewById(R.id.tvStatus);
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position=getAdapterPosition();
                    Group group=getItem(position);
                    if (position!=RecyclerView.NO_POSITION&& listener!=null){
                        listener.onItemClick(group,position);
                    }
                }
            });
        }
    }
    public  interface OnRecyclerViewClick{
        void onItemClick(Group group,int position);
    }
    public void setOnClickListener(OnRecyclerViewClick listener){
        this.listener=listener;
    }
}
