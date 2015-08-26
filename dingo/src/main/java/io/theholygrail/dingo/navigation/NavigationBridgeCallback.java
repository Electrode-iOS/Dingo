package io.theholygrail.dingo.navigation;

public interface NavigationBridgeCallback {
    void animateForward(String options);
    void animateBackwards();
    void popToRoot();
    void presentModal(NavigationOptions navigationOptions);
    void dismissModal();
    void setOnBackListener(OnBackListener backListener);
    void presentExternalUrl(ExternalUrlOptions urlOptions);

    interface OnBackListener {
        void onBack();
    }
}
