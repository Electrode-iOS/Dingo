package io.theholygrale.dingo;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;

import io.theholygrail.jsbridge.JSWebView;

/**
 * Contains the Javascript accessible methods in the navigation namespace.
 */
public class NavigationBridge {
    private static final String TAG = NavigationBridge.class.getSimpleName();

    AppCompatActivity mContext;
    JSWebView mWebView;
    Handler mHandler;
    AnimationController mAnimationController;

    public NavigationBridge(AppCompatActivity context, JSWebView webView, AnimationController animationController) {
        mContext = context;
        mWebView = webView;
        mHandler = new Handler();
        mAnimationController = animationController;
    }

    @SuppressWarnings("unused")
    @JavascriptInterface
    public void animateForward() {
        Log.d(TAG, "animateForward() called");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAnimationController.animateForward();
            }
        });
    }

    @SuppressWarnings("unused")
    @JavascriptInterface
    public void animateBackward() {
        Log.d(TAG, "animateBackward() called");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAnimationController.animateBackward();
            }
        });
    }
}
