package SEPee.serialisierung.messageType;

/**
 * class for the StartingPointTaken message type
 */
public class StartingPointTaken extends Message {
    private StartingPointTakenBody messageBody;

    public StartingPointTaken(int x, int y, int clientID){
        super("StartingPointTaken");
        this.messageBody = new StartingPointTakenBody(x, y, clientID);
    }

    public StartingPointTakenBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(StartingPointTakenBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the StartingPointTaken message type
     */
    public static class StartingPointTakenBody{
        private int x;
        private int y;
        private int clientID;

        public StartingPointTakenBody(int x, int y, int clientID){
            this.x = x;
            this.y = y;
            this.clientID = clientID;
        }

        public int getX(){
            return x;
        }

        public void setX(int x){
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getClientID() {
            return clientID;
        }

        public void setClientID(int clientID) {
            this.clientID = clientID;
        }

    }
}