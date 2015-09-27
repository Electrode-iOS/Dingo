package io.theholygrail.dingo.platform;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import io.theholygrail.dingo.JsonTransformer;
import io.theholygrail.jsbridge.JSLog;
import io.theholygrail.jsbridge.JSValue;
import io.theholygrail.jsbridge.JSWebView;

/**
 * Exposed methods in the main platform namespace
 *
 */
public class PlatformBridge {
    private static final String TAG = PlatformBridge.class.getSimpleName();
    public static final String NAMESPACE = "platform";

    private static final String CANCELLED_ACTION_ID = "back";

    private Context mContext;
    private JSWebView mWebView;
    private Handler mHandler;
    private JsonTransformer mJsonTransformer;
    private PlatformBridgeCallback mCallback;

    public PlatformBridge(Context context, JSWebView webView, JsonTransformer transformer, @NonNull PlatformBridgeCallback callback) {
        mContext = context;
        mWebView = webView;
        mJsonTransformer = transformer;
        mCallback = callback;
        mHandler = new Handler();
    }

    /**
     * Provides information that identifies the device and platform.
     *
     * @param callback The object containing the device and platform info
     */
    @SuppressWarnings("unused")
    @JavascriptInterface
    public void info(String callback) {
        JSLog.d(TAG, "info()");

        final JSValue callbackValue = new JSValue(callback);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String appVersion = "Unknown";
                String model = Build.MODEL;
                String os = String.valueOf(Build.VERSION.SDK_INT);
                try {
                    appVersion = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    JSLog.d(TAG, "Failed to get appversion: ", e);
                }

                if (callbackValue.isFunction()) {
                    Info info = new Info();
                    info.appVersion = appVersion;
                    info.platform = os;
                    info.device = model;
                    Object args[] = {mJsonTransformer.toJson(info)};
                    callbackValue.callFunction(mWebView, args, null);
                }
            }
        });
    }

    /**
     * Shows a dialogSets the title for the current page.
     * @param param The new title
     */
    @SuppressWarnings("unused")
    @JavascriptInterface
    public void dialog(String param, String callback) {
        JSLog.d(TAG, "dialog: " + param);

        final DialogData dialogData = mJsonTransformer.fromJson(param, DialogData.class);
        final JSValue callbackValue = new JSValue(callback);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                        .setTitle(dialogData.title)
                        .setMessage(dialogData.message);
                boolean showDialog = false;
                boolean positiveButtonSet = false;
                for (final DialogData.Action action : dialogData.actions) {
                    if ("cancel".equalsIgnoreCase(action.id)) {
                        builder.setNegativeButton(action.label, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                JSLog.d(TAG, "negative button clicked!");
                                sendToJs("", action.id);
                            }
                        });
                        showDialog = true;
                    } else if (!TextUtils.isEmpty(action.id)) {
                        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                JSLog.i(TAG, action.id + " button clicked!");
                                sendToJs("", action.id);
                            }
                        };

                        if (positiveButtonSet) {
                            builder.setPositiveButton(action.label, clickListener);
                        } else {
                            builder.setNeutralButton(action.label, clickListener);
                            positiveButtonSet = true;
                        }

                        showDialog = true;
                    }
                }

                if (showDialog) {
                    final AlertDialog dialog = builder.create();
                    stackButtonsIfNeeded(dialog); // Lollipop layout bug workaround
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            JSLog.d(TAG, "onCancel()");
                            sendToJs("", CANCELLED_ACTION_ID);
                        }
                    });
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface d) {
                            JSLog.d(TAG, "onDismiss()");
                            mCallback.onDialogDismissed(dialog);
                        }
                    });

                    mCallback.showDialog(dialog);
                } else {
                    sendToJs("Could not create dialog", "");
                    JSLog.w(TAG, "Could not create Dialog!");
                }
            }

            private void sendToJs(String error, String id) {
                JSLog.d(TAG, "error: " + error + " id: " + id);
                if (callbackValue.isFunction()) {
                    Object args[] = { error, new JSValue(id) };
                    callbackValue.callFunction(mWebView, args, null);
                }
            }
        });
    }

    /**
     * Simple sharing of a message, and an URL, using the platforms built in sharing mechanism.
     *
     * @param options Object containing the message, and url to share.
     */
    @SuppressWarnings("unused")
    @JavascriptInterface
    public void share(String options) {
        ShareData shareData = mJsonTransformer.fromJson(options, ShareData.class);

        final String message = shareData.message;
        String url = shareData.url;

        final Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, message);
        share.putExtra(Intent.EXTRA_TEXT, url);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mContext.startActivity(Intent.createChooser(share, message));
            }
        });
    }

    /**
     * Workaround/hack to handle the platform bug on API 21 and 22 where buttons in an AlertDialog are not stacked when
     * they are too wide to fit horizontally (and are rendered outside of the screen). The workaround is to find the
     * parent (layout) of the buttons, check if the total width of the buttons including padding is too wide to fit in
     * the layout, and if so switch to a vertical layout and aligning to the right.
     *
     * @param dialog
     *          The dialog to modify if it is deemed necessary
     */
    private void stackButtonsIfNeeded(final AlertDialog dialog) {
        int platform = Build.VERSION.SDK_INT;
        if (platform == Build.VERSION_CODES.LOLLIPOP || platform == Build.VERSION_CODES.LOLLIPOP_MR1) {
            // Not until the dialog is displayed can we get the real view dimensions
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    try {
                        // Get the buttons
                        Button button1 = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        Button button2 = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                        Button button3 = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                        int contentWidth = button1.getWidth() + button2.getWidth() + button3.getWidth();

                        // Get the layout holding the buttons
                        LinearLayout layout = (LinearLayout) button1.getParent();
                        int padding = layout.getPaddingLeft() + layout.getPaddingRight();
                        int orientation = layout.getOrientation();

                        // If orientation is horizontal and content is too wide, rework the layout
                        if (orientation == LinearLayout.HORIZONTAL && contentWidth + padding > layout.getWidth()) {
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.setGravity(Gravity.RIGHT);
                            reverseChildOrder(layout);
                        }
                    } catch (Exception e) {
                        // If something went wrong there's nothing we can do, so ignore
                    }
                }
            });
        }
    }

    /**
     * Reverses the order of the children in a LinearLayout
     *
     * @param layout
     *          The layout to reverse the order of
     */
    private void reverseChildOrder(LinearLayout layout) {
        if (layout != null) {
            List<View> buttons = new ArrayList<>();
            for (int k = layout.getChildCount() - 1; k >= 0; k--) {
                buttons.add(layout.getChildAt(k));
            }
            layout.removeAllViews();
            for (View button : buttons) {
                layout.addView(button);
            }
        }
    }
}
