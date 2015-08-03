package io.theholygrail.dingo;

public interface JsonTransformer {
    <To>To fromJson(String json, Class<To> dataType);
    String toJson(Object object);
}
