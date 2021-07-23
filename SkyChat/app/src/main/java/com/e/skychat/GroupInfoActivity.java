package com.e.skychat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.e.skychat.adapter.GroupMemberMessageAdapter;
import com.e.skychat.adapter.MemberListAdapter;
import com.e.skychat.beans.Group;
import com.e.skychat.beans.Message;
import com.e.skychat.beans.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;

public class GroupInfoActivity extends AppCompatActivity {

    Group group;
    RecyclerView rv;
    ImageView ivGroupIcon;
    Toolbar toolBar;
    DatabaseReference userReference,memberReference,groupReference;
    MemberListAdapter adapter;
    FloatingActionButton btnAddMember,btnExitGroup;
    String currentUserId;
    ArrayList<Message> memberMessageList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        Intent in = getIntent();
        group = (Group) in.getSerializableExtra("group");

        initComponent();

        userReference = FirebaseDatabase.getInstance().getReference("Users");
        // Group--->dfdkfhkdfkdhfkdh--->member
        memberReference = FirebaseDatabase.getInstance().getReference("Group").child(group.getGroupId()).child("member");
        groupReference = FirebaseDatabase.getInstance().getReference("Group");

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(currentUserId.equals(group.getCreatedBy())){
           removeMember();
        }
        else{
            btnAddMember.setVisibility(View.GONE);
        }
        btnExitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(currentUserId.equals(group.getCreatedBy())){
                  Intent in = new Intent(GroupInfoActivity.this,MakeAdminActivity.class);
                  in.putExtra("group",group);
                  startActivity(in);
               }
               else{
                  exitGroup();
               }
            }
        });
    }
    private void exitGroup(){
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("Alert");
        ab.setMessage("Do you want exit group ?");
        ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final ProgressDialog pd = new ProgressDialog(GroupInfoActivity.this);
                pd.setTitle("please wait");
                pd.show();
                memberReference.child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        groupReference.child(group.getGroupId()).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.dismiss();
                                if(task.isSuccessful()){
                                   finish();
                                }
                            }
                        });
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
    private void removeMember() {
       new ItemTouchHelper(new DeleteSwipe(0,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT))
               .attachToRecyclerView(rv);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<User>option = new FirebaseRecyclerOptions.Builder<User>()
                .setIndexedQuery(memberReference,userReference,User.class)
                .build();

        adapter = new MemberListAdapter(option);
        adapter.setOnItemClick(new MemberListAdapter.OnRecyclerViewClick() {
            @Override
            public void onItemClick(final User user, int position) {
              DatabaseReference messageReference;
              messageReference = FirebaseDatabase.getInstance().getReference("Group")
                      .child(group.getGroupId()).child("messages");
              messageReference.orderByChild("from").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      if(dataSnapshot.exists()){
                         memberMessageList = new ArrayList<>();
                         Iterator<DataSnapshot>itr =  dataSnapshot.getChildren().iterator();
                         while(itr.hasNext()){
                             DataSnapshot ds = itr.next();
                             Message msg = ds.getValue(Message.class);
                             memberMessageList.add(msg);
                         }
                         showMessageInListView(memberMessageList,user);
                      }
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {

                  }
              });
            }
        });
        rv.setAdapter(adapter);
        adapter.startListening();
    }

    private void showMessageInListView(ArrayList<Message> memberMessageList, User user) {
      final AlertDialog ab = new AlertDialog.Builder(GroupInfoActivity.this).create();
      View v = LayoutInflater.from(GroupInfoActivity.this).inflate(R.layout.layout_listview,null);
      TextView tvName = v.findViewById(R.id.tvName);
      ImageView ivClose = v.findViewById(R.id.ivClose);
      ListView lv = v.findViewById(R.id.lv);
      GroupMemberMessageAdapter adapter = new GroupMemberMessageAdapter(GroupInfoActivity.this,memberMessageList);
      lv.setAdapter(adapter);
      tvName.setText("Message sent by "+user.getName());
      ab.setView(v);
      ivClose.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              ab.dismiss();
          }
      });
      ab.show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        groupReference.child(group.getGroupId()).child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                    finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void initComponent(){
       rv = findViewById(R.id.rv);
       ivGroupIcon = findViewById(R.id.ivGroupIcon);
       toolBar = findViewById(R.id.groupinfotoolbar);

       Picasso.get().load(group.getIcon()).placeholder(R.drawable.firechaticon).into(ivGroupIcon);
       toolBar.setTitle(group.getGroupName());
       setSupportActionBar(toolBar);
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       btnAddMember = findViewById(R.id.btnAddMember);
       btnExitGroup = findViewById(R.id.btnExitGroup);

    }
   class DeleteSwipe extends ItemTouchHelper.SimpleCallback{

       public DeleteSwipe(int dragDirs, int swipeDirs) {
           super(dragDirs, swipeDirs);
       }

       @Override
       public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
           return false;
       }

       @Override
       public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
          int position = viewHolder.getAdapterPosition();
          final User user = adapter.getItem(position);
          memberReference.child(user.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                  groupReference.child(group.getGroupId()).child(user.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                          if(task.isSuccessful()){
                              Snackbar.make(rv,"Member removed",Snackbar.LENGTH_LONG)
                                      .setAction("Undo", new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                            memberReference.child(user.getUid()).setValue(user.getName()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                   if(task.isSuccessful()){
                                                       groupReference.child(group.getGroupId()).child(user.getUid()).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                           @Override
                                                           public void onComplete(@NonNull Task<Void> task) {
                                                              if(task.isSuccessful()){
                                                                  Snackbar.make(rv,"Member added",Snackbar.LENGTH_LONG).show();
                                                              }
                                                              else
                                                                  Toast.makeText(GroupInfoActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                           }
                                                       });
                                                   }
                                                }
                                            });
                                          }
                              }).show();
                          }
                      }
                  });
              }
          });
       }
   }
}
