package io.theholygrail.dingo.tabbar;

import android.webkit.JavascriptInterface;

import io.theholygrail.jsbridge.JSLog;

/**
 * Exposed methods for the tabBar. iOS specific, not used on android.
 *
 */
public class TabBarBridge {
    private static final String TAG = TabBarBridge.class.getSimpleName();
    public static final String NAMESPACE = "tabBar";

    public TabBarBridge() {
    }

    /**
     * Show the native iOS tab bar.
     */
    @SuppressWarnings("unused")
    @JavascriptInterface
    public void show() {
        JSLog.d(TAG, "show()");
    }

    /**
     * Hide the native iOS tab bar.
     */
    @SuppressWarnings("unused")
    @JavascriptInterface
    public void hide() {
        JSLog.d(TAG, "hide()");
    }
}
