package io.theholygrail.dingo.navigation;

import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;

import io.theholygrail.jsbridge.JSValue;
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
        animateForward(null);
    }

    @SuppressWarnings("unused")
    @JavascriptInterface
    public void animateForward(final String options) {
        Log.d(TAG, "animateForward(): " + options);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.animateForward(options);
            }
        });
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
        presentModal(null);
    }

    @SuppressWarnings("unused")
    @JavascriptInterface
    public void presentModal(final String options) {
        Log.d(TAG, "presentModal(): " + options);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.presentModal(options);
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

    @SuppressWarnings("unused")
    @JavascriptInterface
    public void setOnBack(final String callback) {
        Log.d(TAG, "setOnBack()");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                final JSValue callbackValue = new JSValue(callback);
                mCallback.setOnBackListener(new NavigationBridgeCallback.OnBackListener() {

                    @Override
                    public void onBack() {
                        Log.d(TAG, "onBack()");
                        callbackValue.callFunction(mWebView, null, null);
                    }
                });
            }
        });
    }

}
