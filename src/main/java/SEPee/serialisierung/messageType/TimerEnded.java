package SEPee.serialisierung.messageType;

import java.util.ArrayList;

/**
 * class for the TimerEnded message type
 * @author Maximilian
 */
public class TimerEnded extends Message{
    private TimerEndedBody messageBody;

    public TimerEnded(ArrayList<Integer> clientIDs) {
        super("TimerEnded");
        this.messageBody = new TimerEndedBody(clientIDs);
    }

    public TimerEndedBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(TimerEndedBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the TimerEnded message type
     */
    public static class TimerEndedBody {
        ArrayList<Integer> clientIDs;
        public TimerEndedBody(ArrayList<Integer> clientIDs) {
            this.clientIDs = clientIDs;
        }

        public ArrayList<Integer> getClientIDs() {
            return clientIDs;
        }

        public void setClientIDs(ArrayList<Integer> clientIDs) {
            this.clientIDs = clientIDs;
        }
    }
}
