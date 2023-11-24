package SEPee.serialisierung.messageType;

public class GameFinished {
    private String messageType;
    private GameFinishedBody messageBody;

    public GameFinished(int clientID) {
        this.messageType = "GameFinished";
        this.messageBody = new GameFinishedBody(clientID);
    }

    public static class GameFinishedBody {
        int clientID;

        public GameFinishedBody(int clientID) {
            this.clientID = clientID;
        }

        public int getClientID() {
            return clientID;
        }

        public void setClientID(int clientID) {
            this.clientID = clientID;
        }
    }
}
