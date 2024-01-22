package SEPee.serialisierung.messageType;

/**
 * class for the Alive message type
 */
public class Alive extends Message{
    private AliveBody messageBody;

    public Alive() {
        super("Alive");
        //this.messageType = "Alive";
        this.messageBody = new AliveBody();
    }

    public AliveBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(AliveBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class AliveBody {


        /**
         * class for body of the Alive message type
         */
        public AliveBody() {

        }
    }
}