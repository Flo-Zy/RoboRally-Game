package SEPee.serialisierung.messageType;

public class TimerStarted extends Message{
    // private String messageType;
    private TimerStartedBody messageBody;

    public TimerStarted() {
        super("TimerStarted");
        // this.messageType = "TimerStarted";
        this.messageBody = new TimerStartedBody();
    }

    /*
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
     */

    public TimerStartedBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(TimerStartedBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class TimerStartedBody {
        public TimerStartedBody() {
        }
    }
}
