package SEPee.serialisierung.messageType;

/**
 * class for body of the GameFinished message type
 */
public class GameFinished extends Message{
    private GameFinishedBody messageBody;

    public GameFinished(int clientID) {
        super("GameFinished");
        this.messageBody = new GameFinishedBody(clientID);
    }

    public GameFinishedBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(GameFinishedBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the GameFinished message type
     */
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
