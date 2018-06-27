package com.bluapp.journalapp.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.bluapp.journalapp.R;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddActivity extends AppCompatActivity {

    private EditText mEntryTitle;
    private EditText mEntryContent;
    private Button mAddEntryBtn;
    private DatabaseReference mDatabase;
    private ProgressBar mProgress;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Journal");
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mEntryTitle = (EditText) findViewById(R.id.topic_field);
        mEntryContent = (EditText) findViewById(R.id.details_field);
        mAddEntryBtn = (Button) findViewById(R.id.submit);

        mAddEntryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.setVisibility(View.VISIBLE);
                createEntry();
            }
        });
    }

    private void createEntry() {
        final String title_val = mEntryTitle.getText().toString().trim();
        final String content_val = mEntryContent.getText().toString().trim();

        if(!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(content_val)){  //Check if all content is provided
            mProgress.setVisibility(View.GONE);
                    DatabaseReference newEntry = mDatabase.push();
                    newEntry.child("title").setValue(title_val);
                    newEntry.child("content").setValue(content_val);
                    newEntry.child("uid").setValue(mCurrentUser.getUid());
                    newEntry.child("date").setValue(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
                    startActivity(new Intent(AddActivity.this, ViewActivity.class));
                    finish();
        }else{
            mProgress.setVisibility(View.GONE);
            Toast.makeText(AddActivity.this,"field are empty",Toast.LENGTH_LONG).show();
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
