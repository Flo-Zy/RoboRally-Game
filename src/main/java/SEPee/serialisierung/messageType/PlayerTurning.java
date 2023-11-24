package SEPee.serialisierung.messageType;

public class PlayerTurning {
    private String messageType;
    private PlayerTurningBody messageBody;

    public PlayerTurning(int clientID, String rotation){
        this.messageType = "PlayerTurning";
        this.messageBody = new PlayerTurningBody(clientID, rotation);
    }

    public static class PlayerTurningBody {
        int clientID;
        String rotation;

        public PlayerTurningBody(int clientID, String rotation) {
            this.clientID = clientID;
            this.rotation = rotation;
        }

        public int getClientID() {
            return clientID;
        }

        public void setClientID(int clientID) {
            this.clientID = clientID;
        }

        public String getRotation() {
            return rotation;
        }

        public void setRotation(String rotation) {
            this.rotation = rotation;
        }
    }
}
