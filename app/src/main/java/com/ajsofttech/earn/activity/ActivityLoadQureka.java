package com.ajsofttech.earn.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ajsofttech.earn.FirebaseProperties;
import com.ajsofttech.earn.R;

public class ActivityLoadQureka extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_qureka);

        WebView view = (WebView) findViewById(R.id.webView);
        view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        view.getSettings().setJavaScriptEnabled(true);
        view.loadUrl(FirebaseProperties.homelink);

        Toolbar toolbar=findViewById(R.id.tool);
        toolbar.setTitle(FirebaseProperties.homelink);
    }
}