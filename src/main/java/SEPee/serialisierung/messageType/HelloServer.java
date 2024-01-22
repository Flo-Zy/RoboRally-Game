package SEPee.serialisierung.messageType;

/**
 * class for the HelloServer message type
 */
public class HelloServer extends Message{
    private HelloServerBody messageBody;

    public HelloServer(String group, boolean isAI, String protocol) {
        super("HelloServer");
        this.messageBody = new HelloServerBody(group, isAI, protocol);
    }

    public HelloServerBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(HelloServerBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the HelloServer message type
     */
    public static class HelloServerBody {
        private String group;
        private boolean isAI;
        private String protocol;

        public HelloServerBody(String group, boolean isAI, String protocol) {
            this.group = group;
            this.isAI = isAI;
            this.protocol = protocol;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public boolean isAI() {
            return isAI;
        }

        public void setAI(boolean AI) {
            isAI = AI;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }
    }
}
