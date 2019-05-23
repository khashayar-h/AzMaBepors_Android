package com.azmabepors.android;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.ajts.androidmads.fontutils.FontUtils;

public class Splash extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/yekan.ttf");
        // Init Library
        FontUtils fontUtils = new FontUtils(Splash.this);

        TextView title = (TextView)findViewById(R.id.txttitle);
        fontUtils.applyFontToView(title,typeface);

         /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(Splash.this,MainActivity.class);
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    }

