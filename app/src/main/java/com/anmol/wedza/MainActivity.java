package com.anmol.wedza;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    private Button join, login;
    private FirebaseAuth auth;
    private DatabaseReference db;
    private EditText wedid;
    private ProgressBar prgbr;
    private int checker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth  = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        join = (Button)findViewById(R.id.joinwed);
        login = (Button)findViewById(R.id.login);
        wedid = (EditText)findViewById(R.id.wedid);
        prgbr = (ProgressBar)findViewById(R.id.prgbr);
        prgbr.setVisibility(View.GONE);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //receives wedding id
                prgbr.setVisibility(View.VISIBLE);
                final String weddingid = wedid.getText().toString().trim();
                // edit firestore query to realtimedb
                db.child("weddings").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        checker = 0;
                        //checks whether id is valid or not
                        for(DataSnapshot ds: dataSnapshot.getChildren()) {
                            String data = ds.getKey();

                            if(data.equals(weddingid)) {
                                if (auth.getCurrentUser()!=null){
                                    checker = 1;
                                    Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                                    intent.putExtra("weddingid",weddingid);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    prgbr.setVisibility(View.GONE);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
                                    finish();
                                }
                                else {
                                    // sends wedding id to loginactivity
                                    checker = 1;
                                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                                    intent.putExtra("weddingid",weddingid);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    prgbr.setVisibility(View.GONE);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
                                    finish();
                                }
                            }
                        }

                        if(checker!=1){
                            checker = 0;
                            prgbr.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this,"Wedding does not exist",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this,"Network Error!!!",Toast.LENGTH_SHORT).show();
                        prgbr.setVisibility(View.GONE);
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.still,R.anim.slide_out_down);
    }
}
