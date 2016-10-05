package com.zhuimeng.lsy.doit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;


/**
 * Created by lsy on 16-5-3.
 */
public class WelcomeActivity extends AppCompatActivity {
    private ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = View.inflate(this, R.layout.activity_welcome, null);
        setContentView(view);
        imageView=(ImageView)findViewById(R.id.imageView);
        Bmob.initialize(this, "ae39585e8e631ab8c67efb404afac2e6");
        Animation animationImage= AnimationUtils.loadAnimation(this,R.anim.welcome_amin);
        imageView.startAnimation(animationImage);

        final AlphaAnimation anim = new AlphaAnimation(0.3f,1.0f);
        anim.setDuration(500);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.startAnimation(anim);
            }
        },800);
        anim.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationEnd(Animation arg0) {
                redirectTo();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationStart(Animation animation) {}

        });
    }

    private void redirectTo() {
        BmobUser bmobUser = BmobUser.getCurrentUser(WelcomeActivity.this);
        if(bmobUser!=null){
            finish();
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            intent.putExtra("user_name",bmobUser.getUsername());
            intent.putExtra("user_object_id",bmobUser.getObjectId());
            intent.putExtra("isFristLogin",false);
            startActivity(intent);
        }else{
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        finish();
    }
}
