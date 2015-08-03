package io.theholygrail.dingo;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.IOException;

public class JsonHandler implements JsonTransformer {
    private ObjectMapper mMapper;
    private static JsonHandler sInstance;

    public static synchronized JsonHandler get() {
        if (sInstance == null) {
            sInstance = new JsonHandler();
        }
        return sInstance;
    }

    private JsonHandler() {
        mMapper = new ObjectMapper();
        mMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        mMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public <To>To fromJson(String json, Class<To> valueType) {
        if (json != null) {
            try {
                return mMapper.readValue(json, valueType);
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public String toJson(Object object) {
        if (object != null) {
            try {
                return mMapper.writeValueAsString(object);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
