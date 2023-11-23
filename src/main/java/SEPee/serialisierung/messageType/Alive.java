package SEPee.serialisierung.messageType;

public class Alive {
    private String messageType;
    private Object messageBody; // leeres Objekt

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Object getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(Object messageBody) {
        this.messageBody = messageBody;
    }

}
