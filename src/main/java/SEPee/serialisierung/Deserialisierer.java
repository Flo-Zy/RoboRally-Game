package SEPee.serialisierung;
import com.google.gson.Gson;

public class Deserialisierer {
    private static final Gson gson = new Gson();

    public static <T> T deserialize(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
}