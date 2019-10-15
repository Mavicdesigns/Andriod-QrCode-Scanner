package scanner.designs.mavic.app.myqrscanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class BrowserActivity extends AppCompatActivity {

    private WebView webView;
    private ActionBar actionBar;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    Toolbar toolbar;
    String Url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
         toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        actionBar = getSupportActionBar();



         webView =(WebView) findViewById(R.id.myWebView);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("No Url ");
        AlertDialog dialog = builder.create();

        Intent intent = getIntent();

        Url = intent.getStringExtra("Url");
        if (Url != null ){
            webView.loadUrl(Url);
            // Start long running operation in a background thread


        }else {
            dialog.show();
        }
        actionBar.setTitle(webView.getTitle());
        actionBar.setSubtitle(webView.getUrl());
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.reload_ico_light);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.reload();
            }
        });

        webView.setWebViewClient(new MyWebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

    }
    boolean isLoading = false;



    private class MyWebViewClient extends WebViewClient {



        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals("https://www.google.com/")) {
                // This is my website, so do not override; let my WebView load the page
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @Override
        public void onPageFinished(final WebView view, String url) {
            toolbar.setNavigationIcon(R.drawable.reload_ico_light);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.reload();
                }
            });
            isLoading = false;
        }

        @Override
        public void onPageStarted(final WebView view, String url, Bitmap favicon) {

            toolbar.setNavigationIcon(R.drawable.cancel_load_light);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.stopLoading();
                }
            });

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.browser_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.newIntent:
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(Url)));
                break;
        }

        return true;
    }
}
