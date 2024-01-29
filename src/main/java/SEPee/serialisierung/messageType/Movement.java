package SEPee.serialisierung.messageType;

/**
 * class for the Movement message type
 * @author Maximilian
 */
public class Movement extends Message{
    private MovementBody messageBody;

    public Movement(int clientID, int x, int y){
        super("Movement");
        this.messageBody = new MovementBody(clientID, x, y);
    }

    public MovementBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(MovementBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the Movement message type
     */
    public static class MovementBody {
        private int clientID;
        private int x;
        private int y;

        public MovementBody(int clientID, int x, int y) {
            this.clientID = clientID;
            this.x = x;
            this.y = y;
        }

        public int getClientID() {
            return clientID;
        }

        public void setClientID(int clientID) {
            this.clientID = clientID;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
}
