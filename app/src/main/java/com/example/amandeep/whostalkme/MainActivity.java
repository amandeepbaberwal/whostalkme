package com.example.amandeep.whostalkme;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.MobileAds;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class MainActivity extends AppCompatActivity {

    public static Bitmap userProfilePictureBitmap;
    CallbackManager callbackManager;
    LoginButton loginButton;
    JSONObject jsonObjectFullData;
    public static String userName = "";
    //ProgressBar progressBar;
    String urlOfUserImage = "";
    public static final String EMAIL = "email";
    public static ArrayList<String> arrayListOfLikesIds = new ArrayList<>();
    public static ArrayList<String> arrayListOfCommentsIds = new ArrayList<>();
    public static ArrayList<String> arrayListOfAllIds = new ArrayList<>();

    public void getFriendsList() throws Exception {
        arrayListOfLikesIds.clear();
        arrayListOfCommentsIds.clear();
        arrayListOfAllIds.clear();
        userName = jsonObjectFullData.getString("name");
        //Log.i("username",userName);
        String postData = jsonObjectFullData.getString("posts");
        //Log.i("postdata",postData);
        JSONObject jsonObject = new JSONObject(postData);
        postData = jsonObject.getString("data");
        //Log.i("postdata",postData);
        JSONArray jsonArray = new JSONArray(postData);
        for(int i = 0;i<jsonArray.length();i++){
            jsonObject = jsonArray.getJSONObject(i);
            postData = jsonObject.getString("id");
            //Log.i("postdata",postData);
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/" + postData + "?fields=likes.limit(200),comments.limit(200)",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            JSONObject jsonObjectLikes = response.getJSONObject();
                            try {
                                String likesString = jsonObjectLikes.getString("likes");
                                //Log.i("likes",likesString);
                                jsonObjectLikes = new JSONObject(likesString);
                                likesString = jsonObjectLikes.getString("data");
                                //Log.i("likes",likesString);
                                JSONArray jsonArrayLikes = new JSONArray(likesString);
                                for(int j = 0;j<jsonArrayLikes.length();j++){
                                    jsonObjectLikes = jsonArrayLikes.getJSONObject(j);
                                    likesString = jsonObjectLikes.getString("id");
                                    String name = jsonObjectLikes.getString("name");
//                                    mapOfLikesIds.put(name,likesString);
                                    arrayListOfLikesIds.add(likesString);
                                    arrayListOfAllIds.add(likesString);
                                }
                                //likesidsgatheringends
                                JSONObject jsonObjectComments = response.getJSONObject();
                                if(jsonObjectComments.has("comments")) {
                                    String commentsString = jsonObjectComments.getString("comments");
                                    //Log.i("comments", commentsString);
                                    jsonObjectComments = new JSONObject(commentsString);
                                    commentsString = jsonObjectComments.getString("data");
                                    //Log.i("comments", commentsString);
                                    JSONArray jsonArrayComments = new JSONArray(commentsString);
                                    for(int k = 0; k<jsonArrayComments.length();k++){
                                        jsonObjectComments = jsonArrayComments.getJSONObject(k);
                                        commentsString = jsonObjectComments.getString("from");
                                        jsonObjectComments = new JSONObject(commentsString);
                                        commentsString = jsonObjectComments.getString("id");
                                        arrayListOfCommentsIds.add(commentsString);
                                        arrayListOfAllIds.add(commentsString);
                                    }
                                }
//                                Log.i("likesids",arrayListOfLikesIds.toString());
//                                Log.i("commentids",arrayListOfCommentsIds.toString());
//                                Log.i("Allids",arrayListOfAllIds.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }
            ).executeAsync();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplicationContext());
        MobileAds.initialize(this, "ca-app-pub-7408688078890575~2766417691");
        arrayListOfAllIds.clear();
        arrayListOfCommentsIds.clear();
        arrayListOfLikesIds.clear();
        //progressBar = findViewById(R.id.progressBar);
        //progressBar.setVisibility(View.INVISIBLE);
        callbackManager = CallbackManager.Factory.create();
        boolean loggedIn = AccessToken.getCurrentAccessToken() == null;
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL,"public_profile","user_posts"));
       // LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(EMAIL,"public_profile","user_posts"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("are you here","four");
                //progressBar.setVisibility(View.VISIBLE);
                Log.i("are you here","one");
                graphRequestOnLoggedInSuccessfully();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

//        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Log.i("are you here","four");
//                //progressBar.setVisibility(View.VISIBLE);
//                Log.i("are you here","one");
//                graphRequestOnLoggedInSuccessfully();
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//
//            }
//        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().logOut();
    }

    public void graphRequestOnLoggedInSuccessfully() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me?fields=name,picture,posts",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.i("response",response.toString());
                        jsonObjectFullData = response.getJSONObject();
                        try {
                            urlOfUserImage = jsonObjectFullData.getString("picture");
                            JSONObject jsonObjectOfUserImage = new JSONObject(urlOfUserImage);
                            urlOfUserImage = jsonObjectOfUserImage.getString("data");
                            jsonObjectOfUserImage = new JSONObject(urlOfUserImage);
                            urlOfUserImage = jsonObjectOfUserImage.getString("url");
                            ImageDownloadTask task = new ImageDownloadTask();
                            userProfilePictureBitmap = task.execute(urlOfUserImage).get();
                            getFriendsList();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //progressBar.setVisibility(View.INVISIBLE);
                        Log.i("here","here");
                        Intent i = new Intent(getApplicationContext(),ProfileInfo.class);
                        startActivity(i);
                        Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_LONG).show();
                    }
                }
        ).executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }
    public static class ImageDownloadTask extends AsyncTask<String,Void,Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap bitmap;
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                InputStream in = httpURLConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(in);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }
    }

}
