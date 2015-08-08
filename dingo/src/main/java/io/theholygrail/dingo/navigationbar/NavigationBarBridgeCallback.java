package io.theholygrail.dingo.navigationbar;

public interface NavigationBarBridgeCallback {
    void setTitle(String title);
    void setButtons(Button[] buttons, OnClickListener clickListener);

    interface OnClickListener {
        void onClick(String id);
    }
}
