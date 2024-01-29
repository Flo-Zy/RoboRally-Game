package SEPee.serialisierung.messageType;

/**
 * class for the Reboot message type
 * @author Maximilian
 */
public class Reboot extends Message {
    private RebootBody messageBody;

    public Reboot(int clientID){
        super ("Reboot");
        this.messageBody = new RebootBody(clientID);
    }

    public RebootBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(RebootBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the Reboot message type
     */
    public static class RebootBody {
        private int clientID;

        public RebootBody(int clientID) {
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
