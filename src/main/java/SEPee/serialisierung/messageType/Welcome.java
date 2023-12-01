package SEPee.serialisierung.messageType;

public class Welcome extends Message{
    // private String messageType;
    private WelcomeBody messageBody;

    public Welcome(int clientID) {
        super("Welcome");
        // this.messageType = "Welcome";
        this.messageBody = new WelcomeBody(clientID);
    }

    /*
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    */

    public WelcomeBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(WelcomeBody messageBody) {
        this.messageBody = messageBody;
    }

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