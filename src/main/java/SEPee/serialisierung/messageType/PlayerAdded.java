package SEPee.serialisierung.messageType;

/**
 * class for the PlayerAdded message type
 * @author Florian
 */
public class PlayerAdded extends Message{
    private PlayerAddedBody messageBody;

    public PlayerAdded(int clientID, String name, int figure) {
        super("PlayerAdded");
        //this.messageType = "PlayerAdded";
        this.messageBody = new PlayerAddedBody(clientID, name, figure);
    }

    public PlayerAddedBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(PlayerAddedBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the PlayerAdded message type
     */
    public static class PlayerAddedBody {
        private int clientID;
        private String name;
        private int figure;

        public PlayerAddedBody(int clientID, String name, int figure) {
            this.clientID = clientID;
            this.name = name;
            this.figure = figure;
        }

        public int getClientID() {
            return clientID;
        }

        public void setClientID(int clientID) {
            this.clientID = clientID;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getFigure() {
            return figure;
        }

        public void setFigure(int figure) {
            this.figure = figure;
        }
    }
}