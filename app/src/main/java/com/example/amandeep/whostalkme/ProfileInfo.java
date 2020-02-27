package com.example.amandeep.whostalkme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ProfileInfo extends AppCompatActivity implements RewardedVideoAdListener {
    TextView userName;
    ImageView userProfilePicture;
    public static Map<String, Integer> map = new HashMap<>();
    public static Multiset<String> idsMultiSet = HashMultiset.create();
    public static ArrayList<String> stalkers = new ArrayList<>();
    public static ArrayList<Bitmap> profilePicBitmaps = new ArrayList<>();
    public static ArrayList<String> namesOfStalkers = new ArrayList<>();
    ArrayList<String> profilePicUrls = new ArrayList<>();
    String titleString = "";
    Intent i;
    ArrayList<String> allids = new ArrayList<>();
    ArrayList<String> alllikesids = new ArrayList<>();
    ArrayList<String> allcommentids = new ArrayList<>();
    InterstitialAd interstitialAd;
    public static RewardedVideoAd rewardedVideoAd;

    public void process(View view)throws Exception {

        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
            Log.i("adstatus", "The interstitial is loaded.");
        } else {
            Log.i("adstatus", "The interstitial wasn't loaded yet.");
        }

        int id = view.getId();
        map.clear();
        idsMultiSet.clear();
        namesOfStalkers.clear();
        profilePicBitmaps.clear();

//        if(Stalkers.myAdaptor != null){
//            Stalkers.myAdaptor.notifyDataSetChanged();
//        }
            //Log.i("pressedbutton",String.valueOf(id));

            if(id == R.id.stalkersButton){
//                Log.i("pressedbutton","stalkers Button");
                idsMultiSet.addAll(allids);
                titleString = "Top Stalkers";
            }
            else if(id == R.id.likersButton){
//                Log.i("pressedbutton","likers Button");
                idsMultiSet.addAll(alllikesids);
                titleString = "Top Likers";

            }
            else if (id == R.id.commentersButton){
//                Log.i("pressedbutton","commenters Button");
                idsMultiSet.addAll(allcommentids);
                titleString  ="Top Commenters";
            }

            //System.out.println("results: " + arrayListOfAllIds.toString());
            for (Multiset.Entry<String> entry : idsMultiSet.entrySet()) {
                map.put(entry.getElement(), entry.getCount());
                //System.out.println("Word : "+entry.getElement()+" count -> "+entry.getCount());
            }

            ValueComparator bvc = new ValueComparator(map);
            TreeMap<String, Integer> sorted_map = new TreeMap<>(bvc);
            sorted_map.putAll(map);
            stalkers.addAll(sorted_map.keySet());
        //System.out.println("results: " + sorted_map);
        int s = 0;
        if(stalkers.size() >= 40){
            s = 40;
        }else{
            s = stalkers.size();
        }
            for (int i = 0; i < s; i++) {
                final int finalI = i;
                Log.i("stalkers",String.valueOf(stalkers.get(i)));
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/" + stalkers.get(i) + "?fields=name,picture",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                //Log.i("Response",response.toString());
                                JSONObject jsonObject = response.getJSONObject();
                                try {
                                    String name = jsonObject.getString("name");
                                    namesOfStalkers.add(name);

                                    String urlOfUserImage = jsonObject.getString("picture");
                                    jsonObject = new JSONObject(urlOfUserImage);
                                    urlOfUserImage = jsonObject.getString("data");
                                    jsonObject = new JSONObject(urlOfUserImage);
                                    urlOfUserImage = jsonObject.getString("url");
                                    profilePicUrls.add(urlOfUserImage);
//                                Log.i("names",name);
//                                Log.i("Url",urlOfUserImage);
                                    MainActivity.ImageDownloadTask task = new MainActivity.ImageDownloadTask();
                                    Bitmap bitmap = task.execute(urlOfUserImage).get();
                                    profilePicBitmaps.add(bitmap);
                                    Stalkers.myAdaptor.notifyDataSetChanged();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).executeAsync();
            }



            i = new Intent(getApplicationContext(), Stalkers.class);
            i.putExtra("title",titleString);
            startActivity(i);

        }
        public static void showRewardedVideoAd(){
                rewardedVideoAd.show();
                //Stalkers.progressBar.setVisibility(View.INVISIBLE);
        }
    public static void loadRewardedVideoAd() {
        rewardedVideoAd.loadAd("ca-app-pub-7408688078890575/5222024796",
                new AdRequest.Builder()
                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .build());
    }

        @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_profile_info);

            //reward video start
            rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
            rewardedVideoAd.setRewardedVideoAdListener(this);
            loadRewardedVideoAd();
            allids = MainActivity.arrayListOfAllIds;
            alllikesids = MainActivity.arrayListOfLikesIds;
            allcommentids = MainActivity.arrayListOfCommentsIds;

            userProfilePicture = findViewById(R.id.userProfilePicture);
            userName = findViewById(R.id.userName);
            userProfilePicture.setImageBitmap(MainActivity.userProfilePictureBitmap);
            userName.setText(MainActivity.userName);

            //reward video ends
            //Ads start
            interstitialAd = new InterstitialAd(this);
            interstitialAd.setAdUnitId("ca-app-pub-7408688078890575/4014607100");
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            interstitialAd.loadAd(adRequest);
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    // Load the next interstitial.
                    interstitialAd.loadAd(new AdRequest.Builder().build());
                }

            });

            AdView adView = findViewById(R.id.adView);
            AdView adView2 = findViewById(R.id.adview9);
            AdView adView3 = findViewById(R.id.adView3);
            adView.loadAd(adRequest);
            adView2.loadAd(adRequest);
            adView3.loadAd(adRequest);
            //Ads end
        }

    @Override
    public void onRewardedVideoAdLoaded() {
        Log.i("rewardedvideo","videoadloaded");

    }

    @Override
    public void onRewardedVideoAdOpened() {
        Log.i("rewardedvideo","adopened");
    }

    @Override
    public void onRewardedVideoStarted() {
        Log.i("rewardedvideo","videostarted");
    }
    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Log.i("rewardedvideo","reward on the way");
        MyAdaptor.changingOfPosition = MyAdaptor.changingOfPosition - 1;
        Stalkers.myAdaptor.notifyDataSetChanged();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.i("rewardedvideo","leftapp");

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Log.i("rewardedvideo","failedtoload");
        loadRewardedVideoAd();

    }


    class ValueComparator implements Comparator<String> {
            Map<String, Integer> base;

            public ValueComparator(Map<String, Integer> base) {
                this.base = base;
            }

            // Note: this comparator imposes orderings that are inconsistent with
            // equals.
            public int compare(String a, String b) {
                if (base.get(a) >= base.get(b)) {
                    return -1;
                } else {
                    return 1;
                } // returning 0 would merge keys
            }
        }

    }

