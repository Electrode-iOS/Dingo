package io.theholygrail.dingo.view;

public interface ViewBridgeCallback {
    void setOnAppear(OnAppearListener listener);
    void setOnDisappear(OnDisappearListener listener);
    void show();

    interface OnAppearListener {
        void onAppear();
    }
    interface OnDisappearListener {
        void onDisappear();
    }
}
