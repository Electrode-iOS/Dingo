package io.theholygrail.dingo.platform;

import android.app.Dialog;

public interface PlatformBridgeCallback {
    void showDialog(Dialog dialog);
    void onDialogDismissed(Dialog dialog);
}
