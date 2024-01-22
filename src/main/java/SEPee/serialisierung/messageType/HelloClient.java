package SEPee.serialisierung.messageType;

/**
 * class for body of the HelloClient message type
 */
public class HelloClient extends Message{
    private HelloClientBody messageBody;

    public HelloClient(String protocol) {
        super ("HelloClient");
        this.messageBody = new HelloClientBody(protocol);
    }

    public HelloClientBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(HelloClientBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the HelloClient message type
     */
    public static class HelloClientBody {
        private String protocol;

        public HelloClientBody(String protocol) {
            this.protocol = protocol;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }
    }


}