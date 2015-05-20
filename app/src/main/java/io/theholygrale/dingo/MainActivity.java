package io.theholygrale.dingo;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import io.theholygrail.jsbridge.JSWebView;
import io.theholygrail.jsbridge.JSInterface;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String URL = "file:///android_asset/index.html";

    private boolean mBridgeReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final JSWebView webView = (JSWebView) findViewById(R.id.webview);

        // Enable remote debugging
        if (DEBUG) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }

        AnimationController animationController = new AnimationController();
        animationController.init(findViewById(R.id.root_view));

        final JsBridge bridge = new JsBridge(this, webView);

        JSInterface mainInterface = new JSInterface(bridge, "NativeBridge");
        mainInterface.addSubInterface(new JSInterface(new NavigationBridge(this, webView, animationController), "navigation"));
        webView.setupInterfaces(mainInterface);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // TODO: Handle onPageFinished not called, or called multiple times
                if (!mBridgeReady) {
                    mBridgeReady = true;
                    findViewById(R.id.webview_loading_view).setVisibility(View.GONE);
                    bridge.nativeBridgeReady();
                }
            }
        });

        webView.loadUrl(URL);
    }
}
