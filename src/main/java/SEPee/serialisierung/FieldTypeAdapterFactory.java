package SEPee.serialisierung;

import SEPee.server.model.field.*;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * is needed to serialize abstract classes -> needed for serializing the game maps
 */
public class FieldTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!Field.class.isAssignableFrom(type.getRawType())) {
            return null;
        }

        TypeAdapter<T> defaultAdapter = gson.getDelegateAdapter(this, type);

        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                defaultAdapter.write(out, value);
            }

            @Override
            public T read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }

                if (in.peek() == JsonToken.BEGIN_OBJECT) {
                    in.beginObject();

                    String type = "";
                    String[] orientations = null;
                    int[] registers = null;
                    String isOnBoard = "";
                    int checkPointNumber = 0;
                    int count = 0;
                    int speed = 0;

                    while (in.hasNext()) {
                        String name = in.nextName();
                        switch (name) {
                            case "type":
                                type = gson.fromJson(in, String.class);
                                break;
                            case "isOnBoard":
                                isOnBoard = gson.fromJson(in, String.class);
                                break;
                            case "checkPointNumber":
                                checkPointNumber = gson.fromJson(in, int.class);
                                break;
                            case "orientations":
                                orientations = gson.fromJson(in, String[].class);
                                break;
                            case "registers":
                                registers = gson.fromJson(in, int[].class);
                                break;
                            case "count":
                                count = gson.fromJson(in, int.class);
                                break;
                            case "speed":
                                speed = gson.fromJson(in, int.class);
                                break;
                            default:
                                in.skipValue();
                                break;
                        }
                    }

                    in.endObject();

                    switch (type) {
                        case "Empty":
                            return (T) new Empty(isOnBoard);
                        case "Antenna":
                            return (T) new Antenna(isOnBoard, orientations);
                        case "StartPoint":
                            return (T) new StartPoint(isOnBoard);
                        case "Wall":
                            return (T) new Wall(isOnBoard, orientations);
                        case "PushPanel":
                            return (T) new PushPanel(isOnBoard, orientations, registers);
                        case "Laser":
                            return (T) new Laser(isOnBoard, orientations, count);
                        case "ConveyorBelt":
                            return (T) new ConveyorBelt(isOnBoard, speed, orientations);
                        case "Gear":
                            return (T) new Gear(isOnBoard, orientations);
                        case "Pit":
                            return (T) new Pit(isOnBoard);
                        case "EnergySpace":
                            return (T) new EnergySpace(isOnBoard, count);
                        case "CheckPoint":
                            return (T) new CheckPoint(isOnBoard, checkPointNumber);
                        case "RestartPoint":
                            return (T) new RestartPoint(isOnBoard);
                        default:
                            throw new IllegalArgumentException("Unknown field type: " + type);
                    }

                } else {
                    return defaultAdapter.read(in);
                }
            }
        };
    }
}