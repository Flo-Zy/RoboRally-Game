package SEPee.serialisierung.messageType;

public class PlayerStatus {
    private String messageType;
    private PlayerStatusBody messageBody;

    public PlayerStatus(int clientID, boolean ready) {
        this.messageType = "PlayerStatus";
        this.messageBody = new PlayerStatusBody(clientID, ready);
    }

    public static class PlayerStatusBody {
        private int clientID;
        private boolean ready;

        public PlayerStatusBody(int clientID, boolean ready) {
            this.clientID = clientID;
            this.ready = ready;
        }

        public int getClientID() {
            return clientID;
        }

        public void setClientID(int clientID) {
            this.clientID = clientID;
        }

        public boolean isReady() {
            return ready;
        }

        public void setReady(boolean ready) {
            this.ready = ready;
        }
    }
}