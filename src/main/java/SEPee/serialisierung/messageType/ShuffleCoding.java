package SEPee.serialisierung.messageType;

public class ShuffleCoding extends Message{
    // private String messageType;
    private ShuffleCodingBody messageBody;

    public ShuffleCoding(int clientID){
        super("ShuffleCoding");
        // this.messageType = "ShuffleCoding";
        this.messageBody = new ShuffleCodingBody(clientID);
    }

    /*
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    */

    public ShuffleCodingBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(ShuffleCodingBody messageBody) {
        this.messageBody = messageBody;
    }

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
