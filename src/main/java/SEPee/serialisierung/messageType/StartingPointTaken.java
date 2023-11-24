package SEPee.serialisierung.messageType;

public class StartingPointTaken {
    private String messageType;
    private StartingPointTakenBody messageBody;

    public StartingPointTaken(int x, int y, int clientID){
        this.messageType = "StartingPointTaken";
        this.messageBody = new StartingPointTakenBody(x, y, clientID);
    }

    public static class StartingPointTakenBody{
        private int x;
        private int y;
        private int clientID;

        public StartingPointTakenBody(int x, int y, int clientID){
            this.x = x;
            this.y = y;
            this.clientID = clientID;
        }

        public int getX(){
            return x;
        }

        public void setX(int x){
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getClientID() {
            return clientID;
        }

        public void setClientID(int clientID) {
            this.clientID = clientID;
        }

    }
}
