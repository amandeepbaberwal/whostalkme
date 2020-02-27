package com.example.amandeep.whostalkme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

public class Stalkers extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    //public static ProgressBar progressBar;
    public static RecyclerView.Adapter myAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stalkers);

        String title = getIntent().getStringExtra("title");
        setTitle(title);
        //progressBar = findViewById(R.id.progressBar2);
        //progressBar.setVisibility(View.VISIBLE);
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(layoutManager);
        myAdaptor = new MyAdaptor(getApplicationContext(), ProfileInfo.namesOfStalkers, ProfileInfo.profilePicBitmaps);

        recyclerView.setAdapter(myAdaptor);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {

            finish();
            //Log.i("button", "back pressed");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
