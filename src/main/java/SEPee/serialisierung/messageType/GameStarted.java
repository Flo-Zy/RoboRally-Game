package SEPee.serialisierung.messageType;

import java.util.List;

public class GameStarted {
    private String messageType;
    private GameStartedBody messageBody;

    public GameStarted(List<List<List<Cell>>> gameMap) {
        this.messageType = "GameStarted";
        this.messageBody = new GameStartedBody(gameMap);
    }

    public static class GameStartedBody {
        private List<List<List<Cell>>> gameMap;

        public GameStartedBody(List<List<List<Cell>>> gameMap) {
            this.gameMap = gameMap;
        }

        public List<List<List<Cell>>> getGameMap() {
            return gameMap;
        }

        public void setGameMap(List<List<List<Cell>>> gameMap) {
            this.gameMap = gameMap;
        }
    }

    public static class Cell {
        private List<Component> components;

        public List<Component> getComponents() {
            return components;
        }

        public void setComponents(List<Component> components) {
            this.components = components;
        }
    }

    public static class Component {
        private String type;
        private String isOnBoard;
        private List<String> orientations;
        private List<Integer> registers;
        private int count;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getIsOnBoard() {
            return isOnBoard;
        }

        public void setIsOnBoard(String isOnBoard) {
            this.isOnBoard = isOnBoard;
        }

        public List<String> getOrientations() {
            return orientations;
        }

        public void setOrientations(List<String> orientations) {
            this.orientations = orientations;
        }

        public List<Integer> getRegisters() {
            return registers;
        }

        public void setRegisters(List<Integer> registers) {
            this.registers = registers;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}