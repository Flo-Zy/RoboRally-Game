package SEPee.serialisierung.messageType;

public class TimerStarted {
    private String messageType;
    private TimerStartedBody messageBody;

    public TimerStarted() {
        this.messageType = "TimerStarted";
        this.messageBody = new TimerStartedBody();
    }

    public static class TimerStartedBody {
        public TimerStartedBody() {
        }
    }
}
