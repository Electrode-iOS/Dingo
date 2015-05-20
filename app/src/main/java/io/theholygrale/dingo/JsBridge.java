package io.theholygrale.dingo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.webkit.JavascriptInterface;

import java.util.List;
import java.util.Map;

import io.theholygrail.jsbridge.JSValue;
import io.theholygrail.jsbridge.JSWebView;

/**
 * Contains the methods for the main interface that are possible to access from JavaScript.
 */
public class JsBridge {
    private static final String TAG = JsBridge.class.getSimpleName();

    AppCompatActivity mContext;
    JSWebView mWebView;
    Handler mHandler;

    public JsBridge(AppCompatActivity context, JSWebView webView) {
        mContext = context;
        mWebView = webView;
        mHandler = new Handler();
    }

    public void nativeBridgeReady() {
        mWebView.executeJavascript("nativeBridgeReady()");
    }

    /**
     * Sets the title for the current page.
     * Example: <pre><code>{title: "The Dingo ate my title"}</code></pre>
     *
     * @param param The new title
     */
    @SuppressWarnings("unused")
    @JavascriptInterface
    public void updatePageState(String param) {
        Log.d(TAG, "updatePageState()");
        JSValue value = new JSValue(param);

        if (value.isValid() && value.isMap()) {
            Map map = value.mapValue();

            if (map.containsKey("title")) {
                JSValue titleValue = (JSValue)map.get("title");
                final String title = titleValue.isString() ? titleValue.stringValue() : "";
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mContext.getSupportActionBar().setTitle(title);
                    }
                });
            }
        }
    }

    /**
     * Shows a dialogSets the title for the current page.
     * @param param The new title
     */
    @SuppressWarnings("unused")
    @JavascriptInterface
    public void dialog(String param, String callback) {
        Log.d(TAG, "dialog: " + param);

        JSValue value = new JSValue(param);
        final JSValue callbackValue = new JSValue(callback);

        // TODO: Handle this properly
        if (value.isValid() && value.isMap()) {
            Map map = value.mapValue();

            final String title = ((JSValue)map.get("title")).stringValue();
            final String message = ((JSValue)map.get("message")).stringValue();
            JSValue actions = (JSValue)map.get("actions");
            Pair<String, String> negativeButton = null;
            Pair<String, String> positiveButton = null;

            if (actions.isValid() && actions.isList()) {
                List<JSValue> actionList = actions.listValue();

                for (JSValue listValue : actionList) {
                    if (listValue.isValid() && listValue.isMap()) {
                        Map<String, JSValue> actionMap = listValue.mapValue();

                        // TODO: Generic actions
                        String id = actionMap.get("id").stringValue();
                        if ("cancel".equalsIgnoreCase(id)) {
                            negativeButton = new Pair<>(id, actionMap.get("label").stringValue());
                        } else if ("ok".equalsIgnoreCase(id)) {
                            positiveButton = new Pair<>(id, actionMap.get("label").stringValue());
                        }
                    }
                }
            }
            final Pair<String, String> neg = negativeButton;
            final Pair<String, String> pos = positiveButton;

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                            .setTitle(title)
                            .setMessage(message);
                    if (neg != null) {
                        builder.setNegativeButton(neg.second, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "negative button clicked!");
                                sendToJs("", neg.first);
                            }
                        });
                    }
                    if (pos != null) {
                        builder.setPositiveButton(pos.second, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "positive button clicked!");
                                sendToJs("", pos.first);
                            }
                        });
                    }
                    if (neg != null || pos != null) {
                        builder.show();
                    } else {
                        sendToJs("Could not create dialog", "");
                    }

                }

                private void sendToJs(String error, String id) {
                    if (callbackValue.isFunction()) {
                        Object args[] = {"", error, id};
                        callbackValue.callFunction(mWebView, args, null);
                    }
                }
            });
        }
    }

    @SuppressWarnings("unused")
    @JavascriptInterface
    public void share(String param) {
        // TODO: Add support for actionbar sharing?
        JSValue value = new JSValue(param);

        if (value.isValid() && value.isMap()) {
            Map<String, JSValue> map = value.mapValue();

            final String message = map.get("message").stringValue();
            String url = map.get("url").stringValue();

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
    }
}
