package io.theholygrail.dingo.view;

import android.os.Handler;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import io.theholygrail.jsbridge.JSLog;
import io.theholygrail.jsbridge.JSValue;
import io.theholygrail.jsbridge.JSWebView;

/**
 * Exposed methods in the view namespace
 *
 */
public class ViewBridge {
    private static final String TAG = ViewBridge.class.getSimpleName();
    public static final String NAMESPACE = "view";

    JSWebView mWebView;
    Handler mHandler;
    ViewBridgeCallback mCallback;

    public ViewBridge(JSWebView webView, ViewBridgeCallback callback) {
        mWebView = webView;
        mHandler = new Handler();
        mCallback = callback;
    }

    /**
     * Shows the current webview.
     */
    @SuppressWarnings("unused")
    @JavascriptInterface
    public void show() {
        JSLog.d(TAG, "show()");

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.show();
                }
            }
        });
    }

    /**
     * setOnAppear()
     * @param callback function to be triggered when the current webview becomes visible to the user.
     */
    @SuppressWarnings("unused")
    @JavascriptInterface
    public void setOnAppear(String callback) {
        JSLog.d(TAG, "setOnAppear(): " + callback);
        ViewBridgeCallback.OnAppearListener listener = null;

        if (!TextUtils.isEmpty(callback)) {
            final JSValue callbackValue = new JSValue(callback);

            listener = new ViewBridgeCallback.OnAppearListener() {
                @Override
                public void onAppear() {
                    JSLog.d(TAG, "onAppear");
                    callbackValue.callFunction(mWebView, null, null);
                }
            };
        }

        mCallback.setOnAppear(listener);
    }

    /**
     * setOnDisappear()
     *
     * @param callback function to be triggered when the current webview will be removed for the user.
     */
    @SuppressWarnings("unused")
    @JavascriptInterface
    public void setOnDisappear(String callback) {
        JSLog.d(TAG, "setOnDisappear(): " + callback);
        ViewBridgeCallback.OnDisappearListener listener = null;

        if (!TextUtils.isEmpty(callback)) {
            final JSValue callbackValue = new JSValue(callback);

            listener = new ViewBridgeCallback.OnDisappearListener() {
                @Override
                public void onDisappear() {
                    JSLog.d(TAG, "onDisappear()");
                    callbackValue.callFunction(mWebView, null, null);
                }
            };
        }
        mCallback.setOnDisappear(listener);
    }
}
