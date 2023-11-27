package SEPee.serialisierung.messageType;

public class GameFinished {
    private String messageType;
    private GameFinishedBody messageBody;

    public GameFinished(int clientID) {
        this.messageType = "GameFinished";
        this.messageBody = new GameFinishedBody(clientID);
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public GameFinishedBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(GameFinishedBody messageBody) {
        this.messageBody = messageBody;
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
