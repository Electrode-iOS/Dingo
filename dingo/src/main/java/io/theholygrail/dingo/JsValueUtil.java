package io.theholygrail.dingo;

public class JsValueUtil {
    public static String parseQuotes(String stringifiedString) {
        return stringifiedString.replaceAll("^\"|\"$", "");
    }
}
