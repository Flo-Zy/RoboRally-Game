package SEPee.server.model.gameBoard;

import SEPee.server.model.field.Field;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
public class GameBoard {
    private String boardId;
    private String bordName;
    private List<List<List<Field>>> gameBoard; // Spalte(Zeile(FelderTypen()))

    GameBoard(String boardId, String bordName, int sumColumns){
        this.boardId = boardId;
        this.bordName = bordName;
        this.gameBoard = new ArrayList<>(sumColumns);
    }

    public void addRow(List<List<Field>> row) {
        gameBoard.add(row);
    }
}