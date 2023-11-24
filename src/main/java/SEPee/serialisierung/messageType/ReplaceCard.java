package SEPee.serialisierung.messageType;

public class ReplaceCard {
    private String messageType;
    private ReplaceCardBody messageBody;

    public ReplaceCard(int register, String newCard, int clientID){
        this.messageType = "ReplaceCard";
        this.messageBody = new ReplaceCardBody(register, newCard, clientID);
    }

    public static class ReplaceCardBody {
        private int register;
        private String newCard;
        private int clientID;

        public ReplaceCardBody(int register, String newCard, int clientID) {
            this.register = register;
            this.newCard = newCard;
            this.clientID = clientID;
        }

        public int getRegister() {
            return register;
        }

        public void setRegister(int register) {
            this.register = register;
        }

        public String getNewCard() {
            return newCard;
        }

        public void setNewCard(String newCard) {
            this.newCard = newCard;
        }

        public int getClientID() {
            return clientID;
        }

        public void setClientID(int clientID) {
            this.clientID = clientID;
        }
    }
}
