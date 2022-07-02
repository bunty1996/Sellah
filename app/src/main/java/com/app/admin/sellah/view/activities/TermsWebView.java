package com.app.admin.sellah.view.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.app.admin.sellah.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TermsWebView extends Activity implements View.OnClickListener {

    @BindView(R.id.termsBackIcon)
    ImageView termsBackIcon;
    @BindView(R.id.termsTitle)
    TextView termsTitle;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private String str = "1";
    private String policyURL = "https://www.sellah.org/privacy-policy";
    private String termsURL = "https://www.sellah.org/terms-of-service";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_webview);
        ButterKnife.bind(TermsWebView.this);// view ids injection with ButterKnife.

        if (getIntent() != null) {
            str = getIntent().getStringExtra("str");
        }

        setWebView();

        termsBackIcon.setOnClickListener(this);

    }

    private void setWebView() {

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {

                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                //SHOW LOADING IF IT ISNT ALREADY VISIBLE
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);

            }
        });

        if (str.equalsIgnoreCase("1")) {
            webView.loadUrl(termsURL);
            termsTitle.setText("Terms Condition");

        } else {
            webView.loadUrl(policyURL);
            termsTitle.setText("Privacy Policy");
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.termsBackIcon:

                onBackPressed();

                break;

        }

    }

}
