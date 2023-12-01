package SEPee.serialisierung.messageType;

public class Message {


    private String messageType;

    public Message(String messageType){
        this.messageType=messageType;
    }

    public String getMessageType(){
        return messageType;
    }
}
