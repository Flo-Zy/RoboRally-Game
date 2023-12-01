package SEPee.serialisierung.messageType;

public class HelloClient extends Message{
    //private String messageType;
    private HelloClientBody messageBody;

    public HelloClient(String protocol) {
        super ("HelloClient");
        //this.messageType = "HelloClient";
        this.messageBody = new HelloClientBody(protocol);
    }

    /*
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

     */

    public HelloClientBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(HelloClientBody messageBody) {
        this.messageBody = messageBody;
    }



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