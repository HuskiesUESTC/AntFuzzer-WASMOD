package edu.uestc.antfuzzer.framework.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.uestc.antfuzzer.framework.annotation.Component;

@Component
public class JsonUtil {

    public String getJson(String... args) {
        return getJson(false, args);
    }

    public String getJson(boolean isObject, String... args) {
        int len = args.length;
        if (len == 0)
            return "{}";
        if (isObject && len % 2 == 0) {
            JsonObject arguments = new JsonObject();
            for (int i = 0; i < len - 1; i += 2)
                arguments.addProperty(args[i], args[i + 1]);
            return arguments.toString();
        }
        JsonArray arguments = new JsonArray();
        for (String str : args)
            arguments.add(str);
        return arguments.toString();
    }

    public String covertObjectToJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public <T> T parseJsonToObject(Class<T> clazz, String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, clazz);
    }
}
