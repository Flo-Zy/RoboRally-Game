package SEPee.serialisierung.messageType;

public class CurrentPlayer {
    private String messageType;
    private CurrentPlayerBody messageBody;

    public CurrentPlayer(int clientID){
        this.messageType = "CurrentPlayer";
        this.messageBody = new CurrentPlayerBody(clientID);
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
