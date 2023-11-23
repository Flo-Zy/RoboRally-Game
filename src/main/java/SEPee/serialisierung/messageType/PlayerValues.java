package SEPee.serialisierung.messageType;
import lombok.Getter;

@Getter
public class PlayerValues {
    private PlayerBody messageBody;

    public PlayerValues(String playerName, int figure) {
        String messageType = "PlayerValues";
        this.messageBody = new PlayerBody(playerName, figure);
    }

    public static class PlayerBody {
        private String name;
        @Getter
        private int figure;

        public PlayerBody(String name, int figure) {
            this.name = name;
            this.figure = figure;
        }

        public String getPlayerName() {
            return name;
        }

        /*
        //werden garnicht gebraucht weil man playerName/Figure ohne methode setten kann oder?

        public void setPlayerName(String name) {
            this.name = name;
        }

        public void setFigure(int figure) {
            this.figure = figure;
        }

         */
    }
}
