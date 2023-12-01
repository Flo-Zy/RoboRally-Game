package SEPee.serialisierung.messageType;

public class PlayerAdded extends Message{
    // private String messageType;
    private PlayerAddedBody messageBody;

    public PlayerAdded(int clientID, String name, int figure) {
        super("PlayerAdded");
        //this.messageType = "PlayerAdded";
        this.messageBody = new PlayerAddedBody(clientID, name, figure);
    }

    /*
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    */

    public PlayerAddedBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(PlayerAddedBody messageBody) {
        this.messageBody = messageBody;
    }

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