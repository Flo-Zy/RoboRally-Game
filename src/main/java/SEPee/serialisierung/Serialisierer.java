package SEPee.serialisierung;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Serialisierer {
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static String serialize(Object obj) {
        return gson.toJson(obj);
    }
}