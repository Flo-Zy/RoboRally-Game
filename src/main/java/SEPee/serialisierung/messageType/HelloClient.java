package SEPee.serialisierung.messageType;

public class HelloClient {
    private String messageType;
    private HelloClientBody messageBody;

    public HelloClient(String protocol) {
        this.messageType = "HelloClient";
        this.messageBody = new HelloClientBody(protocol);
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