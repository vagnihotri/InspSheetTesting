package com.credr.inspsheettesting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.credr.inspsheettesting.models.PartsObject;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    boolean fetchedData = false, fetchedParts = false;
    private PartsObject partsObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView totalData = (TextView) findViewById(R.id.samplesText);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dataCollectionRef = database.getReference();
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Fetching data");
        mProgressDialog.show();
        dataCollectionRef.child("data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!isFinishing()) {
                    if (fetchedParts && mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    } else {
                        fetchedData = true;
                    }
                    totalData.setText(dataSnapshot.getChildrenCount() + " samples collected");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (!isFinishing()) {
                    if (fetchedParts && mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    } else {
                        fetchedData = true;
                    }
                    totalData.setText("0 samples collected");
                }
            }
        });

        dataCollectionRef.child("parts").child("partsObject").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!isFinishing()) {
                    if (fetchedData && mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    } else {
                        fetchedParts = true;
                    }
                    partsObject = dataSnapshot.getValue(PartsObject.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (!isFinishing()) {
                    if (fetchedData && mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    } else {
                        fetchedParts = true;
                    }
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CollectSampleActivity.class);
                if(partsObject != null) {
                    intent.putExtra("parts", partsObject);
                }
                startActivity(intent);
            }
        });
    }
}
