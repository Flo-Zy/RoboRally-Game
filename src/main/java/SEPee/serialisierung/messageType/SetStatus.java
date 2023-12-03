package SEPee.serialisierung.messageType;

public class SetStatus extends Message{
    // private String messageType;
    private SetStatusBody messageBody;

    public SetStatus(int clientID, boolean ready) {
        super("SetStatus");
        // this.messageType = "SetStatus";
        this.messageBody = new SetStatusBody(clientID, ready);
    }

    /*
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    */

    public SetStatusBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(SetStatusBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class SetStatusBody {

        private int clientID;
        private boolean ready;

        public SetStatusBody(int clientID, boolean ready) {
            this.clientID = clientID;
            this.ready = ready;
        }

        public boolean isReady() {
            return ready;
        }

        public void setReady(boolean ready) {
            this.ready = ready;
        }

        public int getClientID() {
            return clientID;
        }

        public void setClientID(int clientID) {
            this.clientID = clientID;
        }
    }
}