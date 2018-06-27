package com.bluapp.journalapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bluapp.journalapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class EditActivity extends AppCompatActivity {
    private EditText mEntryTitle;
    private EditText mEntryContent;
    private Button mAddEntryBtn;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String title;
    private String content;
    private static String Tag="Journal";
    private ProgressBar mProgress;

    /**
     * Created by Emmanuel on 26/06/18.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Journal");
        mDatabase.keepSynced(true);


        mEntryTitle = (EditText) findViewById(R.id.topic_field);
        mEntryContent = (EditText) findViewById(R.id.details_field);
        mAddEntryBtn = (Button) findViewById(R.id.edit);
        mProgress = (ProgressBar) findViewById(R.id.progressBar);

        //get data from viewActivity Recyclew view
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        content = intent.getStringExtra("content");

        mEntryTitle.setText(title);
        mEntryContent.setText(content);

        mAddEntryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.setVisibility(View.VISIBLE);
                editEntry();
            }
        });

    }


    private void editEntry() {

        final String title_val = mEntryTitle.getText().toString().trim();
        final String content_val = mEntryContent.getText().toString().trim();

        if(!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(content_val)){  //Check if all content is provided
            mProgress.setVisibility(View.GONE);
            Query editQuery = mDatabase.orderByChild("title").equalTo(title);
            editQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot delData: dataSnapshot.getChildren()){
                        delData.getRef().child("title").setValue(title_val);
                        delData.getRef().child("content").setValue(content_val);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i(Tag, "editItem() - onCancelled(), error: "+ databaseError.getMessage());
                }
            });
            startActivity(new Intent(EditActivity.this, ViewActivity.class));
            finish();
        }else{
            mProgress.setVisibility(View.GONE);
            Toast.makeText(EditActivity.this,"field are empty",Toast.LENGTH_LONG).show();
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
