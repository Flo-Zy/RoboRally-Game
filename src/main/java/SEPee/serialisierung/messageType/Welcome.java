package SEPee.serialisierung.messageType;

/**
 * class for the Welcome message type
 * @author Felix
 */
public class Welcome extends Message{
    private WelcomeBody messageBody;

    public Welcome(int clientID) {
        super("Welcome");
        this.messageBody = new WelcomeBody(clientID);
    }

    public WelcomeBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(WelcomeBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the Welcome message type
     */
    public static class WelcomeBody {
        private int clientID;

        public WelcomeBody(int clientID) {
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