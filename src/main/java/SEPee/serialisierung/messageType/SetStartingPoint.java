package SEPee.serialisierung.messageType;

public class SetStartingPoint extends Message{
   // private String messageType;
    private SetStartingPointBody messageBody;

    public SetStartingPoint(int x, int y){
        super("SetStartingPoint");
        // this.messageType = "SetStartingPoint";
        this.messageBody = new SetStartingPointBody(x, y);
    }

    /*
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

     */

    public SetStartingPointBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(SetStartingPointBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class SetStartingPointBody{
        private int x;

        private int y;

        public SetStartingPointBody(int x, int y){
            this.x = x;
            this.y = y;
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

    }
}
