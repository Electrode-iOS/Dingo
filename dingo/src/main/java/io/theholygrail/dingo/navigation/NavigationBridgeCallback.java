package io.theholygrail.dingo.navigation;

public interface NavigationBridgeCallback {
    void animateForward(String options);
    void animateBackwards();
    void popToRoot();
    void presentModal(String options);
    void dismissModal();
    void setOnBackListener(OnBackListener backListener);

    interface OnBackListener {
        void onBack();
    }
}
