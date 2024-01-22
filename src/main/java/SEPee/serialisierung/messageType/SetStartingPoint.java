package SEPee.serialisierung.messageType;

/**
 * class for the SetStartingPoint message type
 */
public class SetStartingPoint extends Message{
    private SetStartingPointBody messageBody;

    public SetStartingPoint(int x, int y){
        super("SetStartingPoint");
        this.messageBody = new SetStartingPointBody(x, y);
    }

    public SetStartingPointBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(SetStartingPointBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the SetStartingPoint message type
     */
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
