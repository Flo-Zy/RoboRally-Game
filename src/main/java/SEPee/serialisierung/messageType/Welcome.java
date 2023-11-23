package SEPee.serialisierung.messageType;

public class Welcome {
    private String messageType;
    private WelcomeMessageBody messageBody;

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public WelcomeMessageBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(WelcomeMessageBody messageBody) {
        this.messageBody = messageBody;
    }
}

class WelcomeMessageBody {
    private int clientID;

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }
}