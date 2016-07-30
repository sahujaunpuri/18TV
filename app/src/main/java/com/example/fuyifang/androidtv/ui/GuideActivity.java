package com.example.fuyifang.androidtv.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.example.fuyifang.androidtv.R;
import com.example.fuyifang.androidtv.app.AppConfig;
import com.example.fuyifang.androidtv.app.MainActivity;
import com.example.fuyifang.androidtv.common.ApiStringCallback;
import com.example.fuyifang.androidtv.common.BaseActivity;
import com.example.fuyifang.androidtv.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import tcking.github.com.giraffeplayer.GiraffePlayer;

/**
 * Created by loe on 12/6/16.
 */
public class GuideActivity extends BaseActivity {
    private GiraffePlayer mPlayer;
    private ImageView mImage;
    private LinearLayout mLayout;
    private boolean isOk = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        mImage = (ImageView) findViewById(R.id.guide_bg);
        mLayout = (LinearLayout) findViewById(R.id.guide_player);
        mPlayer = new GiraffePlayer(this,false);


        HttpUtils.get(AppConfig.TV_AD, null, null, new ApiStringCallback(null) {
            @Override
            public void onSuccessEvent(String response) {
                isOk = false;
                try {
                    final JSONObject obj = new JSONObject(response);
                    Integer type =  obj.getInt("adType");
                    if (type == 1){
                        mLayout.setVisibility(View.VISIBLE);
                        mPlayer.play(obj.getString("url"));
                    }else if(type == 2){
                        Glide.with(GuideActivity.this).load(AppConfig.HOST+obj.get("url")).placeholder(R.drawable.bg).animate(new ViewPropertyAnimation.Animator() {
                            @Override
                            public void animate(View view) {
                                try {
                                    new Timer().schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(mContext, InfoActivity.class);
                                            mContext.startActivity(intent);
                                            finish();
                                        }
                                    },obj.getInt("showTime"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).into(mImage);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(isOk) {
                    Intent intent = new Intent(mContext, InfoActivity.class);
                    mContext.startActivity(intent);
                    finish();
                }
            }
        },5000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayer != null)
        {
            mPlayer.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPlayer !=null)
        {
            mPlayer.onResume();
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpUtils.cancel();
    }
}
