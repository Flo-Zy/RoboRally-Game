package SEPee.serialisierung.messageType;

public class TimerEnded {
    private String messageType;
    private TimerEndedBody messageBody;

    public TimerEnded(int[] clientIDs) {
        this.messageType = "TimerEnded";
        this.messageBody = new TimerEndedBody(clientIDs);
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
