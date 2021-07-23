package com.e.skychat.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.e.skychat.GroupInfoActivity;
import com.e.skychat.R;
import com.e.skychat.beans.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowGroupAdapter extends FirebaseRecyclerAdapter<Group, ShowGroupAdapter.ShowGroupViewHolder> {

    OnRecyclerViewClick listener;
    String currentUserId;
    public ShowGroupAdapter(@NonNull FirebaseRecyclerOptions<Group> options) {
        super(options);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onBindViewHolder(@NonNull ShowGroupViewHolder holder, int i, @NonNull Group group) {
        Picasso.get().load(group.getIcon()).placeholder(R.drawable.firechaticon).into(holder.civGroupIcon);
        holder.tvGroupName.setText(group.getGroupName());
        holder.tvGroupDescription.setText(group.getDescription());
        if(currentUserId.equals(group.getCreatedBy()))
            holder.ivPopupMenu.setVisibility(View.VISIBLE);
        else
            holder.ivPopupMenu.setVisibility(View.GONE);
    }

    @NonNull
    @Override
    public ShowGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_item,parent,false);
        return new ShowGroupViewHolder(v);
    }

    public class ShowGroupViewHolder extends RecyclerView.ViewHolder{
       CircleImageView civGroupIcon;
       TextView tvGroupName,tvGroupDescription;
        ImageView ivPopupMenu;
        LinearLayout ll;
       public ShowGroupViewHolder(final View itemView){
           super(itemView);
           civGroupIcon = itemView.findViewById(R.id.civProfile);
           tvGroupName = itemView.findViewById(R.id.tvName);
           tvGroupDescription = itemView.findViewById(R.id.tvStatus);
           ivPopupMenu = itemView.findViewById(R.id.ivPopupMenu);

           ivPopupMenu.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   PopupMenu popupMenu = new PopupMenu(itemView.getContext(),ivPopupMenu);
                   Menu menu = popupMenu.getMenu();
                   menu.add("Add participants");
                   menu.add("Group info");
                   menu.add("Delete group");
                   popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                       @Override
                       public boolean onMenuItemClick(MenuItem item) {
                           int position = getAdapterPosition();
                           final Group group = getItem(position);
                           String title = item.getTitle().toString();
                           if(title.equals("Add participants")){

                           }
                           else if(title.equals("Group info")){
                               Intent in = new Intent(itemView.getContext(), GroupInfoActivity.class);
                               in.putExtra("group",group);
                               itemView.getContext().startActivity(in);
                           }
                           else if(title.equals("Delete group")){
                               AlertDialog.Builder ab = new AlertDialog.Builder(itemView.getContext());
                               ab.setTitle("Please confirm ?");
                               ab.setMessage("If you delete this group then it will automatically remove all member");
                               ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialogInterface, int i) {
                                       final ProgressDialog pd = new ProgressDialog(itemView.getContext());
                                       pd.setMessage("please wait while deleting group");
                                       pd.show();
                                       DatabaseReference groupReference = FirebaseDatabase.getInstance().getReference("Group");
                                       groupReference.child(group.getGroupId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                             pd.dismiss();
                                             if(task.isSuccessful()){
                                                 Toast.makeText(itemView.getContext(), "Group Deleted", Toast.LENGTH_SHORT).show();
                                             }
                                             else
                                                 Toast.makeText(itemView.getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                           }
                                       });
                                   }
                               });
                               ab.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialogInterface, int i) {

                                   }
                               });
                               ab.show();
                           }
                           return false;
                       }
                   });
                   popupMenu.show();
               }
           });

           ll = itemView.findViewById(R.id.ll);
           ll.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   int position = getAdapterPosition();
                   Group group = getItem(position);
                   if(position != RecyclerView.NO_POSITION && listener!=null){
                       listener.onItemClick(group,position);
                   }

               }
           });
       }
   }

   public interface OnRecyclerViewClick{
        void onItemClick(Group group, int position);
   }

   public void setOnItemClick(OnRecyclerViewClick listener){
        this.listener = listener;
   }
}
