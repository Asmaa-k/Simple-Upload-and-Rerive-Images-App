package com.example.mya;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImagesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageAdapter adapter;

    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Gallery");

        recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        setFirebaseAdp();
    }

    private void setFirebaseAdp() {

        Query query = FirebaseDatabase.getInstance().getReference().child("uploads");

        FirebaseRecyclerOptions<Upload> options = new FirebaseRecyclerOptions.Builder<Upload>()
                .setQuery(query, Upload.class)
                .build();

        adapter = new ImageAdapter(options);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
            }

            @Override
            public void onWhatEverClick(int position) {

            }

            @Override
            public void onDeleteClick(final int position) {
                Upload selectedItem = adapter.getSnapshots().getSnapshot(position).getValue(Upload.class);
                selectedItem.setKey(adapter.getSnapshots().getSnapshot(position).getKey());

                final String key = selectedItem.getKey();
                StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
                imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //deleteItem(position);
                        mDatabaseRef.child(key).removeValue();
                        Toast.makeText(ImagesActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}