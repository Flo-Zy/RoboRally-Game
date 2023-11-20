package SEPee.server.model;

import SEPee.server.model.field.Field;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameBoard {
    private int boardId;
    private Field[][] board = new Field[11][14];
}
