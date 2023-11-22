package SEPee.serialisierung.messageType;

public class HelloClient {
    private String messageType;
    private HelloClientMessageBody messageBody;

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public HelloClientMessageBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(HelloClientMessageBody messageBody) {
        this.messageBody = messageBody;
    }
}

class HelloClientMessageBody {
    private String protocol;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
