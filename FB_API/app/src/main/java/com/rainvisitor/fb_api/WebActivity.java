package com.rainvisitor.fb_api;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SeekBar;
import android.widget.TextView;

public class WebActivity extends AppCompatActivity {
    private WebView myWebView;
    private String name;
    private TextView textView_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        name = null;
        if(getIntent().getExtras()!=null) {
            name = getIntent().getExtras().getString("url");
        }
        TextView textView_URL = (TextView)findViewById(R.id.textView_URL);
        TextView cancel = (TextView)findViewById(R.id.cancel);
        textView_title = (TextView)findViewById(R.id.textView_title);
        final SeekBar seekBer = (SeekBar)findViewById(R.id.seekBer);
        myWebView = (WebView) findViewById(R.id.webView);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.requestFocus();
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.loadUrl(name);
        if(name.length()>=40)name = name.substring(0,40) + "...";
        textView_URL.setText(name);
        myWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {
                seekBer.setProgress(progress);
                if(progress == 100) {
                    seekBer.setProgress(100);
                    seekBer.setVisibility(View.GONE);
                    textView_title.setText(myWebView.getTitle());
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivity.this.finish();

            }
        });
    }
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
