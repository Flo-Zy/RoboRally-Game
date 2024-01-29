package SEPee.serialisierung;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * deserializes String objects
 * @author Felix, Maximilian
 */
public class Deserialisierer {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new FieldTypeAdapterFactory())
            .create();

    public static <T> T deserialize(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
}