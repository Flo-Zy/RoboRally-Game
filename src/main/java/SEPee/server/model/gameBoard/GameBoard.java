package SEPee.server.model.gameBoard;

import SEPee.server.model.Position;
import SEPee.server.model.field.Empty;
import SEPee.server.model.field.Field;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
public class GameBoard {
    private String boardId;
    private String name;
    private List<List<List<Field>>> gameBoard;
    int x;
    int y;

    GameBoard(String id, String name, int x, int y){
        this.boardId = id;
        this.name = name;
        this.x = x;
        this.y = y;
        gameBoard = new ArrayList<>(x);
        for(int i = 0; i < x; i++){
            List<List<Field>> column = new ArrayList<>(y);
            for(int j = 0; j < y; j++){
                column.add(new ArrayList<>());
            }
            gameBoard.add(column);
        }
        for(int i = 0; i < x; i++){
            for(int j = 0; j < y; j++){
                addField(i, j, new Empty(new Position(i,j)));
            }
        }
    }

    public void addField(int x, int y, Field field){
        gameBoard.get(x).get(y).add(field);
    }

}
