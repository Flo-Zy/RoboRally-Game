package SEPee.serialisierung.messageType;

/**
 * class for the TimerStarted message type
 */
public class TimerStarted extends Message{
    private TimerStartedBody messageBody;

    public TimerStarted() {
        super("TimerStarted");
        this.messageBody = new TimerStartedBody();
    }

    public TimerStartedBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(TimerStartedBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the TimerStarted message type
     */
    public static class TimerStartedBody {
        public TimerStartedBody() {
        }
    }
}
