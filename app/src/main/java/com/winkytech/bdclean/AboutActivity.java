package com.winkytech.bdclean;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AboutActivity extends AppCompatActivity {

    Toolbar toolbar;
    WebView bdcleanWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        bdcleanWeb=findViewById(R.id.about_webView);

        toolbar=findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bdcleanWeb=findViewById(R.id.about_webView);
        bdcleanWeb.loadUrl("https://pages.bdclean.org/about-us/");
        WebSettings settings= bdcleanWeb.getSettings();
        settings.setJavaScriptEnabled(true);
        bdcleanWeb.setWebViewClient(new WebViewClient());
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}