package SEPee.serialisierung.messageType;

public class Welcome {
    private String messageType;
    private WelcomeBody messageBody;

    public Welcome(int clientID) {
        this.messageType = "Welcome";
        this.messageBody = new WelcomeBody(clientID);
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