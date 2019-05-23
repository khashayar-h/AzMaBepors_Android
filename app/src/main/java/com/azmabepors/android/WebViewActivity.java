package com.azmabepors.android;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebViewActivity extends AppCompatActivity {
String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Bundle extras = getIntent().getExtras();
        int getString = extras.getInt("key");
        switch(getString){
            case 0:
                url = "https://azmabepors.com/blog/معرفی-ازمابپرس/";
                break;
            case 1:
                url = "https://azmabepors.com/blog/تماس-با-ما/";
                break;
            case 2:
                url = "https://azmabepors.com/blog/";
                break;
            case 3:


                String urlString="https://azmabepors.com";
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    // Chrome browser presumably not installed so allow user to choose instead
                    intent.setPackage(null);
                    startActivity(intent);
                }


                break;
            case 4:
                Uri uri = Uri.parse("http://instagram.com/azmabepors");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/azmabepors")));
                }
                break;
        }

        WebView myWebView = (WebView) findViewById(R.id.webinfo);
        myWebView.loadUrl(url);
        //myWebView.setBackgroundResource(R.drawable.lbg);
        myWebView.setBackgroundColor(Color.TRANSPARENT);
        myWebView.getSettings().setJavaScriptEnabled(true);


    }
}
