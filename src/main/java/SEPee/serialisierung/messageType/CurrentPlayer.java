package SEPee.serialisierung.messageType;

/**
 * class for the CurrentPlayer message type
 * @author Franziska
 */
public class CurrentPlayer extends Message{
    private CurrentPlayerBody messageBody;

    public CurrentPlayer(int clientID){
        super("CurrentPlayer");
        //this.messageType = "CurrentPlayer";
        this.messageBody = new CurrentPlayerBody(clientID);
    }

    public CurrentPlayerBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(CurrentPlayerBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the CurrentPlayer message type
     */
    public static class CurrentPlayerBody{
        private int clientID;

        public CurrentPlayerBody(int clientID){
            this.clientID = clientID;
        }

        public int getClientID(){
            return clientID;
        }

        public void setClientID(int clientID){
            this.clientID = clientID;
        }

    }
}
