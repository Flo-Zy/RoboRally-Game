package SEPee.serialisierung.messageType;

/**
 * class for the SetStatus message type
 * @author Florian
 */
public class SetStatus extends Message{
    private SetStatusBody messageBody;

    public SetStatus(boolean ready) {
        super("SetStatus");
        this.messageBody = new SetStatusBody(ready);
    }

    public SetStatusBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(SetStatusBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the SetStatus message type
     */
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