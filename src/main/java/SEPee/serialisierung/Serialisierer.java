package SEPee.serialisierung;

import com.google.gson.Gson;

public class Serialisierer {
    public static final Gson gson = new Gson();

    public static String serialize(Object obj) {
        return gson.toJson(obj);
    }
}