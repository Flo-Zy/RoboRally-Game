package SEPee.serialisierung.messageType;

public class TimerEnded {
    private String messageType;
    private TimerEndedBody messageBody;

    public TimerEnded(int[] clientIDs) {
        this.messageType = "TimerEnded";
        this.messageBody = new TimerEndedBody(clientIDs);
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public TimerEndedBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(TimerEndedBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class TimerEndedBody {
        int[] clientIDs;
        public TimerEndedBody(int[] clientIDs) {
            this.clientIDs = clientIDs;
        }

        public int[] getClientIDs() {
            return clientIDs;
        }

        public void setClientIDs(int[] clientIDs) {
            this.clientIDs = clientIDs;
        }
    }
}
