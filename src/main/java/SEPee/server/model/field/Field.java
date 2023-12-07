package SEPee.server.model.field;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public abstract class Field {
    @SerializedName("type")
    private String type;
    @SerializedName("isOnBoard")
    private String isOnBoard;

    public Field(String type, String isOnBoard) {
        this.type = type;
        this.isOnBoard = isOnBoard;
    }
}