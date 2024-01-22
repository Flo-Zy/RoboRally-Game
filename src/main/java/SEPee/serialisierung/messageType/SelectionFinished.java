package SEPee.serialisierung.messageType;

/**
 * class for the SelectionFinished message type
 */
public class SelectionFinished extends Message{
    private SelectionFinishedBody messageBody;

    public SelectionFinished(int clientID) {
        super("SelectionFinished");
        this.messageBody = new SelectionFinishedBody(clientID);
    }

    public SelectionFinishedBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(SelectionFinishedBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the SelectionFinished message type
     */
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
