package io.theholygrail.dingo.navigation;

import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;

import io.theholygrail.jsbridge.JSWebView;

/**
 * Contains the Javascript accessible methods in the navigation namespace.
 */
public class NavigationBridge {
    private static final String TAG = NavigationBridge.class.getSimpleName();
    public static final String NAMESPACE = "navigation";

    JSWebView mWebView;
    Handler mHandler;
    NavigationBridgeCallback mCallback;

    public NavigationBridge(JSWebView webView, NavigationBridgeCallback navigationCallback) {
        mWebView = webView;
        mHandler = new Handler();
        mCallback = navigationCallback;
    }

    @SuppressWarnings("unused")
    @JavascriptInterface
    public void animateForward() {
        Log.d(TAG, "animateForward()");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.animateForward();
            }
        });
    }

    @SuppressWarnings("unused")
    @JavascriptInterface
    public void animateForward(String tabBarHidden) {
        Log.d(TAG, "animateForward(tabBarHidden)");
        animateForward();
    }

    @SuppressWarnings("unused")
    @JavascriptInterface
    public void animateBackward() {
        Log.d(TAG, "animateBackward()");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.animateBackwards();
            }
        });
    }

    @SuppressWarnings("unused")
    @JavascriptInterface
    public void popToRoot() {
        Log.d(TAG, "popToRoot()");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.popToRoot();
            }
        });
    }

    @SuppressWarnings("unused")
    @JavascriptInterface
    public void presentModal() {
        Log.d(TAG, "presentModal() called");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.presentModal();
            }
        });
    }

    @SuppressWarnings("unused")
    @JavascriptInterface
    public void dismissModal() {
        Log.d(TAG, "dismissModal() called");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.dismissModal();
            }
        });
    }
}
