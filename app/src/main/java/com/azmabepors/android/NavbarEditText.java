package com.azmabepors.android;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.ajts.androidmads.fontutils.FontUtils;

/**
 * Created by Data System on 11/7/2018.
 */

public class NavbarEditText extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_header_main);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/yekan.ttf");
        // Init Library
        FontUtils fontUtils = new FontUtils(NavbarEditText.this);

        TextView title = (TextView)findViewById(R.id.navbartxt);
        fontUtils.applyFontToView(title,typeface);

    }
}
