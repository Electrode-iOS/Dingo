package io.theholygrail.dingo.platform;

public class DialogData {
    public String title;
    public String message;
    public Action[] actions;

    public static class Action {
        public String id;
        public String label;
    }
}
