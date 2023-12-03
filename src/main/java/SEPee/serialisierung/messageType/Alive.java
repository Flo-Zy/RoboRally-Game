package SEPee.serialisierung.messageType;

public class Alive extends Message{
    //private String messageType;
    private AliveBody messageBody;

    public Alive() {
        super("Alive");
        //this.messageType = "Alive";
        this.messageBody = new AliveBody();
    }

    /*
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

     */

    public AliveBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(AliveBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class AliveBody {


        public AliveBody() {

        }
    }
}