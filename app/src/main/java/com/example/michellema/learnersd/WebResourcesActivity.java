package com.example.michellema.learnersd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class WebResourcesActivity extends AppCompatActivity {

    private WebView resources;
    private Button btn_chercher;
    private Button btn_apprentissage;
    private Global get_global;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_resources);

        resources = (WebView) findViewById(R.id.wv_resources);
        resources.setWebViewClient(new MyWebViewClient());
        btn_chercher = (Button) findViewById((R.id.btn_chercher));
        btn_apprentissage = (Button) findViewById((R.id.btn_apprentissage));
        get_global = Global.getInstance();
        url = get_global.getUrl();

        resources.getSettings().setJavaScriptEnabled(true);
        resources.loadUrl(url);

        btn_apprentissage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LearnActivity.class);
                startActivity(intent);
            }
        });

        btn_chercher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
