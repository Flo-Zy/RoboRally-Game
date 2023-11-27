package SEPee.serialisierung.messageType;

public class SelectionFinished {
    private String messageType;
    private SelectionFinishedBody messageBody;

    public SelectionFinished(int clientID) {
        this.messageType = "SelectionFinished";
        this.messageBody = new SelectionFinishedBody(clientID);
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public SelectionFinishedBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(SelectionFinishedBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class SelectionFinishedBody {
        private int clientID;

        public SelectionFinishedBody(int clientID) {
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
