package com.bluapp.journalapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.bluapp.journalapp.Data;
import com.bluapp.journalapp.R;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Emmanuel on 25/06/18.
 */

public class ViewActivity extends AppCompatActivity {

    private RecyclerView mEntryList;
    private static DatabaseReference mDatabase;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Query mQueryCurrentUser;
    private String currentUserId;
    private static String Tag="Journal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        mAuth = FirebaseAuth.getInstance();

        currentUserId = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Journal");
        mDatabase.keepSynced(true);

        mQueryCurrentUser = mDatabase.orderByChild("uid").equalTo(currentUserId);


        mEntryList = (RecyclerView) findViewById(R.id.journal_list);
        mEntryList.setHasFixedSize(true);
        mEntryList.setLayoutManager(new LinearLayoutManager(this));


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                if(firebaseAuth.getCurrentUser() == null ){
                    startActivity(new Intent(ViewActivity.this, LoginActivity.class));
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Data, EntryViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Data, EntryViewHolder>(
                Data.class,
                R.layout.journal_cardview,
                EntryViewHolder.class,
                mQueryCurrentUser

        ) {
            @Override
            protected void populateViewHolder(EntryViewHolder viewHolder, Data model, int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setContent(model.getContent());
                viewHolder.setDate(model.getDate());
            }
        };

        mEntryList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class EntryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mView;
        ImageView del;
        ImageView edit;
        TextView e_title;
        TextView e_content;
        TextView e_date;

        public EntryViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            del = (ImageView) itemView.findViewById(R.id.journal_delete);
            edit = (ImageView) itemView.findViewById(R.id.journal_edit);
            del.setOnClickListener(this);
            edit.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            switch (v.getId()){
                case R.id.journal_delete:
                    Query deleteQuery = mDatabase.orderByChild("title").equalTo(e_title.getText().toString());
                    deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot delData: dataSnapshot.getChildren()){
                                delData.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.i(Tag, "deleteItem() - onCancelled(), error: "+ databaseError.getMessage());
                        }
                    });
               break;
                case R.id.journal_edit:
                    Intent intent = new Intent(mView.getContext(), EditActivity.class);
                    intent.putExtra("title", e_title.getText().toString())
                            .putExtra("content",e_content.getText().toString());
                    mView.getContext().startActivity(intent);
                    ((Activity)mView.getContext()).finish();
                break;
            }
        }




        public void setTitle(String title){

            e_title = (TextView) mView.findViewById(R.id.journal_title);
            e_title.setText(title);

        }

        public void setContent(String content){

            e_content = (TextView) mView.findViewById(R.id.journal_content);
            e_content.setText(content);
        }

        public void setDate(String date){

            e_date = (TextView) mView.findViewById(R.id.journal_date);
            e_date.setText(date);
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){

            case R.id.action_logout:
                mAuth.signOut();
                return true;
        }

        return true;
    }
}
