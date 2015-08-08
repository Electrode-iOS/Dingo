package io.theholygrail.dingo.navigation;

import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;

import io.theholygrail.jsbridge.JSValue;
import io.theholygrail.jsbridge.JSWebView;

/**
 * Contains the Javascript accessible methods in the navigation namespace.
 *
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


    /**
     * Trigger a native push navigation transition.
     */
    @SuppressWarnings("unused")
    @JavascriptInterface
    public void animateForward() {
        Log.d(TAG, "animateForward()");
        animateForward(null);
    }

    /**
     * Trigger a native push navigation transition.
     *
     * @param options json string that sets the title of the new view.
     */
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

    /**
     * Trigger a native pop navigation transition.
     *
     */
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

    /**
     * Pops the native navigation stack all the way back to the root view.
     *
     */
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

    /**
     * Trigger a native modal transition.
     *
     */
    @SuppressWarnings("unused")
    @JavascriptInterface
    public void presentModal() {
        presentModal(null);
    }

    /**
     * Trigger a native modal transition
     *
     * The optional options provided are:
     * <ul>
     * <li>title</li>
     * <li>navigationBarButtons - Array of navigation bar buttons.</li>
     * <li>onNavigationBarButtonTap - callback function when a navigation bar button is clicked.</li>
     * <li>onAppear - callback function to be triggered once the animation is completed, and a new view is ready.</li>
     * </ul>
     *
     * @param options
     */
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

    /**
     * Close the existing native modal view.
     *
     */
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

    /**
     * Set a function callback to call when the up action, or hardware back button is clicked.
     * If back button is clicked and this callback is not set, the bridge will fallback to going back in the web history.
     *
     * @param callback function callback
     */
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
