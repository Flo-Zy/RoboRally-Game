package SEPee.serialisierung.messageType;

public class HelloServer {
    private String messageType;
    private HelloServerMessageBody messageBody;

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public HelloServerMessageBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(HelloServerMessageBody messageBody) {
        this.messageBody = messageBody;
    }
}

class HelloServerMessageBody {
    private String group;
    private boolean isAI;
    private String protocol;

    // Getter and Setter for group
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isAI() {
        return isAI;
    }

    public void setAI(boolean isAI) {
        this.isAI = isAI;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}