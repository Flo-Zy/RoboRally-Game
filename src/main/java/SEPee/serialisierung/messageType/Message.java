package SEPee.serialisierung.messageType;

/**
 * parent class for all message types
 */
public class Message {
    private String messageType;

    public Message(String messageType){
        this.messageType=messageType;
    }

    public String getMessageType(){
        return messageType;
    }
}
