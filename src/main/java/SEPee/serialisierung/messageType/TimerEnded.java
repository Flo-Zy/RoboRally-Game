package SEPee.serialisierung.messageType;

import java.util.ArrayList;

public class TimerEnded extends Message{
    // private String messageType;
    private TimerEndedBody messageBody;

    public TimerEnded(ArrayList<Integer> clientIDs) {
        super("TimerEnded");
        // this.messageType = "TimerEnded";
        this.messageBody = new TimerEndedBody(clientIDs);
    }

    /*
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    */

    public TimerEndedBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(TimerEndedBody messageBody) {
        this.messageBody = messageBody;
    }

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
