package com.foodbrigade.firechat.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodbrigade.firechat.Adapter.ContactAdapter;
import com.foodbrigade.firechat.R;
import com.foodbrigade.firechat.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContactsFragment extends Fragment {
    RecyclerView rv;
    String userkey;
    DatabaseReference rootReference;
    ContactAdapter adapter;
    FirebaseRecyclerOptions<User> options;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.contact,container,false);
        init(v);
        return v;
    }
    @Override
    public void onStart() {
        super.onStart();
        options=new FirebaseRecyclerOptions.Builder<User>()
                .setIndexedQuery(rootReference.child("Contacts").child(userkey),rootReference.child("Users"), User.class)
                .build();
        adapter=new ContactAdapter(options,getContext());
        rv.setAdapter(adapter);
        adapter.startListening();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        new ItemTouchHelper(new DeleteOnSwipe(0,ItemTouchHelper.RIGHT)).attachToRecyclerView(rv);
    }
    class DeleteOnSwipe extends ItemTouchHelper.SimpleCallback {
        public DeleteOnSwipe(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position=viewHolder.getAdapterPosition();
            final String friend =adapter.getkey(position);
            AlertDialog.Builder ab=new AlertDialog.Builder(getContext());
            ab.setTitle("Warning!!!");
            ab.setMessage("are you Sure?");
            ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    rootReference.child("Contacts").child(userkey).child(friend).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                rootReference.child("Contacts").child(friend).child(userkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }else{
                                String message=task.getException().toString();
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            ab.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(getContext(), "okay", Toast.LENGTH_SHORT).show();
                }
            });
            ab.show();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void init(View v) {
        rv=v.findViewById(R.id.contactrv);
        userkey=FirebaseAuth.getInstance().getCurrentUser().getUid();
        rootReference= FirebaseDatabase.getInstance().getReference();

    }
}
