package io.theholygrail.dingo.navigation;

public interface NavigationBridgeCallback {
    void animateForward();
    void animateBackwards();
    void popToRoot();
    void presentModal();
    void dismissModal();
}
