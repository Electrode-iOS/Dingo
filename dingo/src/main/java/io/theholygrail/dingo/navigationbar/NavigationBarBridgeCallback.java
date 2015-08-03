package io.theholygrail.dingo.navigationbar;

public interface NavigationBarBridgeCallback {
    void setTitle(String title);
    void setButtons(Button[] buttons, NavigationBarBridge.OnClickListener clickListener);
}
