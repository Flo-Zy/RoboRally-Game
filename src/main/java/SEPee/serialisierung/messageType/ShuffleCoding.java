package SEPee.serialisierung.messageType;

/**
 * class for the ShuffleCoding message type
 * @author Franziska
 */
public class ShuffleCoding extends Message{
    private ShuffleCodingBody messageBody;

    public ShuffleCoding(int clientID){
        super("ShuffleCoding");
        this.messageBody = new ShuffleCodingBody(clientID);
    }

    public ShuffleCodingBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(ShuffleCodingBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the ShuffleCoding message type
     */
    public static class ShuffleCodingBody{
        private int clientID;

        public ShuffleCodingBody(int clientID){
            this.clientID = clientID;
        }

        public int getClientID(){
            return clientID;
        }

        public void setClientID(int clientID){
            this.clientID = clientID;
        }

    }
}
