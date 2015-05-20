package io.theholygrale.dingo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import io.theholygrail.jsbridge.JSWebView;

public class AnimationController {
    private static final String TAG = AnimationController.class.getSimpleName();

    private final long ANIMATION_TIME = 250;

    private View mRootView;
    private View mScreenShotContainer;

    private BackAnimation mBackAnimation;
    private ForwardAnimation mForwardAnimation;

    private WebView mWebView;

    public void init(View rootView) {
        mRootView = rootView;
        mWebView = (JSWebView) mRootView.findViewById(R.id.webview);
        mScreenShotContainer = mRootView.findViewById(R.id.screenshot_container);
    }

    public void animateForward() {
        if (mForwardAnimation == null) {
            mForwardAnimation = new ForwardAnimation();
            if (!mForwardAnimation.start()) {
                Log.w(TAG, "Failed to start forward animation.");
                mForwardAnimation = null;
            }
        } else {
            Log.i(TAG, "Calling animateForward while animating");
        }
    }

    public void animateBackward() {
        mBackAnimation = new BackAnimation();
        mBackAnimation.start();
    }

    private class ForwardAnimation {

        public boolean start() {
            Log.d(TAG, "ForwardAnimation start()");
            boolean started = false;

            final ImageView imageView = (ImageView) mRootView.findViewById(R.id.screenshot_view);
            final View loadingView = mRootView.findViewById(R.id.screenshot_loading_view);
            final View webViewContainer = mRootView.findViewById(R.id.webview_container);
            Bitmap screenshot = screenshot(mWebView);

            if (screenshot != null) {
                imageView.setImageBitmap(screenshot);
                loadingView.setVisibility(View.INVISIBLE);
                mScreenShotContainer.setVisibility(View.VISIBLE);

                final ObjectAnimator inAnimator = ObjectAnimator
                        .ofFloat(webViewContainer, View.TRANSLATION_X, webViewContainer.getWidth(), 0);

                inAnimator.setDuration(ANIMATION_TIME);
                inAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        Log.d(TAG, "ForwardAnimation onAnimationEnd()");

                        // reset screenshot view
                        mScreenShotContainer.setVisibility(View.INVISIBLE);
                        mScreenShotContainer.setTranslationX(0);
                        imageView.setImageBitmap(null);

                        mForwardAnimation = null;
                    }
                });
                inAnimator.start();

                mScreenShotContainer.animate()
                        .translationXBy(-webViewContainer.getWidth())
                        .setDuration(ANIMATION_TIME)
                        .start();

                started = true;
            }

            return started;
        }
    }

    private Bitmap screenshot(WebView webView) {
        Bitmap bmp;

        try {
            bmp = Bitmap.createBitmap(webView.getWidth(), webView.getHeight(), Bitmap.Config.RGB_565);
            Canvas c = new Canvas(bmp);
            c.translate(-webView.getScrollX(), -webView.getScrollY());
            webView.draw(c);
        } catch (OutOfMemoryError e) {
            bmp = null;
            Log.e(TAG, "Out of memory while creating screenshot", e);
        }

        return bmp;
    }

    private class BackAnimation {
        public void start() {
            Log.d(TAG, "BackAnimation start()");

            mScreenShotContainer.setVisibility(View.VISIBLE);
            mScreenShotContainer.findViewById(R.id.screenshot_loading_view).setVisibility(View.VISIBLE);

            final ObjectAnimator inAnimator = ObjectAnimator.ofFloat(mScreenShotContainer, View.TRANSLATION_X, -mScreenShotContainer.getWidth(), 0);
            inAnimator.setDuration(ANIMATION_TIME);
            inAnimator.start();

            final View webViewContainer = mRootView.findViewById(R.id.webview_container);
            webViewContainer.animate()
                    .translationXBy(mWebView.getWidth()).setDuration(ANIMATION_TIME)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            Log.d(TAG, "BackAnimation end()");

                            webViewContainer.setTranslationX(0);
                            mWebView.goBack();

                            mScreenShotContainer.setVisibility(View.GONE);
                            mScreenShotContainer.findViewById(R.id.screenshot_loading_view).setVisibility(View.GONE);

                            mBackAnimation = null;
                        }
                    }).start();
        }
    }
}
