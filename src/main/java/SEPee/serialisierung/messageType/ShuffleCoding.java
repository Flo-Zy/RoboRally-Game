package SEPee.serialisierung.messageType;

public class ShuffleCoding {
    private String messageType;
    private ShuffleCodingBody messageBody;

    public ShuffleCoding(int clientID){
        this.messageType = "ShuffleCoding";
        this.messageBody = new ShuffleCodingBody(clientID);
    }

    public static class ShuffleCodingBody{
        private int clientID;

        public ShuffleCodingBody(int clientID){
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
