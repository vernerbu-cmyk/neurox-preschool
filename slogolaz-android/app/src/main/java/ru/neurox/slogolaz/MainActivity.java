package ru.neurox.slogolaz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class MainActivity extends Activity {
  private WebView webView;

  @SuppressLint("SetJavaScriptEnabled")
  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setStatusBarColor(Color.rgb(22,35,59));
    getWindow().setNavigationBarColor(Color.rgb(22,35,59));
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    webView = new WebView(this);
    webView.setBackgroundColor(Color.rgb(22,35,59));
    webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    WebSettings s = webView.getSettings();
    s.setJavaScriptEnabled(true);
    s.setDomStorageEnabled(true);
    s.setAllowFileAccess(false);
    s.setAllowContentAccess(false);
    s.setJavaScriptCanOpenWindowsAutomatically(false);
    s.setSupportMultipleWindows(false);
    s.setDefaultTextEncodingName("UTF-8");
    s.setMediaPlaybackRequiresUserGesture(true);
    s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
    webView.setWebChromeClient(new WebChromeClient());
    webView.setWebViewClient(new WebViewClient() {
      @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return !"slogolaz.local".equals(request.getUrl().getHost());
      }
    });
    setContentView(webView);
    try {
      webView.loadDataWithBaseURL("https://slogolaz.local/", readBundledHtml(), "text/html", "UTF-8", null);
    } catch (IOException e) {
      webView.loadDataWithBaseURL("https://slogolaz.local/", "<html lang='ru'><meta charset='utf-8'><body><h2>Не удалось открыть игру</h2></body></html>", "text/html", "UTF-8", null);
    }
  }

  private String readBundledHtml() throws IOException {
    try (InputStream in = getAssets().open("index.html"); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      byte[] buf = new byte[16384]; int n;
      while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
      return out.toString(StandardCharsets.UTF_8.name());
    }
  }

  @Override public void onBackPressed() {
    if (webView == null) { super.onBackPressed(); return; }
    webView.evaluateJavascript("(function(){var a=document.getElementById('settingsOverlay');"+
      "if(a&&!a.classList.contains('hidden')){var b=document.getElementById('btnCloseSettings');if(b)b.click();return 'closed';}"+
      "var v=document.getElementById('victoryOverlay');if(v&&!v.classList.contains('hidden')){var c=document.getElementById('btnCloseVictory');if(c)c.click();return 'closed';}"+
      "return 'exit';})()", result -> {
      if (!"\"closed\"".equals(result)) MainActivity.super.onBackPressed();
    });
  }
  @Override protected void onDestroy() {
    if (webView != null) { webView.destroy(); webView = null; }
    super.onDestroy();
  }
}
