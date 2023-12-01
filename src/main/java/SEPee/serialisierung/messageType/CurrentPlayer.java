package SEPee.serialisierung.messageType;

public class CurrentPlayer extends Message{
    //private String messageType;
    private CurrentPlayerBody messageBody;

    public CurrentPlayer(int clientID){
        super("CurrentPlayer");
        //this.messageType = "CurrentPlayer";
        this.messageBody = new CurrentPlayerBody(clientID);
    }

    /*
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

     */

    public CurrentPlayerBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(CurrentPlayerBody messageBody) {
        this.messageBody = messageBody;
    }

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
