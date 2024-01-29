package SEPee.serialisierung.messageType;

/**
 * class for the PlayerTurning message type
 * @author Maximilian
 */
public class PlayerTurning extends Message{
    private PlayerTurningBody messageBody;

    public PlayerTurning(int clientID, String rotation){
        super("PlayerTurning");
        // this.messageType = "PlayerTurning";
        this.messageBody = new PlayerTurningBody(clientID, rotation);
    }

    public PlayerTurningBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(PlayerTurningBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the PlayerTurning message type
     */
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
