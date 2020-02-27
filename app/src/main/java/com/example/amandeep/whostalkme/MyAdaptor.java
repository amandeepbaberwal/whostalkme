package com.example.amandeep.whostalkme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by amandeep on 12-02-2018.
 */


class MyAdaptor extends RecyclerView.Adapter<MyAdaptor.ViewHolder> {
    public static int changingOfPosition = 3;
    Context context;
    public static ArrayList<String> namesOfStalkers = new ArrayList<>();
    public static ArrayList<Bitmap> profilePicBitmaps = new ArrayList<>();
    public MyAdaptor(Context context, ArrayList<String> namesOfStalkers, ArrayList<Bitmap> profilePicBitmaps) {
        this.context = context;
        this.namesOfStalkers = namesOfStalkers;
        this.profilePicBitmaps = profilePicBitmaps;
    }

    @Override
    public MyAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_layout_2,parent,false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("recyclerview","clicked");
                ProfileInfo.rewardedVideoAd.show();
                //ProfileInfo.showRewardedVideoAd();
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            holder.index.setText(String.valueOf(position+1));
            if(position < changingOfPosition){
                holder.userName.setText("Click to Unlock by watching a video");
                holder.userProfilePicture.setImageBitmap(null);
            }
            else if(position >= changingOfPosition) {
                holder.userName.setText(namesOfStalkers.get(position));
                holder.userProfilePicture.setImageBitmap(profilePicBitmaps.get(position));
            }

    }

    @Override
    public int getItemCount() {
        return namesOfStalkers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public ImageView userProfilePicture;
        public TextView index;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.stalkerName);
            userProfilePicture = itemView.findViewById(R.id.profilePic);
            index = itemView.findViewById(R.id.index);
        }
    }
}
