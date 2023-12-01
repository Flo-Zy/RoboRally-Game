package SEPee.serialisierung.messageType;

public class SetStatus extends Message{
    // private String messageType;
    private SetStatusBody messageBody;

    public SetStatus(boolean ready) {
        super("SetStatus");
        // this.messageType = "SetStatus";
        this.messageBody = new SetStatusBody(ready);
    }

    /*
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    */

    public SetStatusBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(SetStatusBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class SetStatusBody {
        private boolean ready;

        public SetStatusBody(boolean ready) {
            this.ready = ready;
        }

        public boolean isReady() {
            return ready;
        }

        public void setReady(boolean ready) {
            this.ready = ready;
        }
    }
}