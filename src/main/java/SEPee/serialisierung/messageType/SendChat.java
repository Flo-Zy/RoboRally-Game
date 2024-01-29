package SEPee.serialisierung.messageType;

/**
 * class for the SendChat message type
 * @author Franziska
 */
public class SendChat extends Message{
    private SendChatBody messageBody;

    public SendChat(String message, int to) {
        super("SendChat");
        this.messageBody = new SendChatBody(message, to);
    }

    public SendChatBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(SendChatBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the SendChat message type
     */
    public static class SendChatBody {
        private int clientID;
        private String message;
        private int to;

        public SendChatBody(String message, int to) {
            this.clientID = clientID;
            this.message = message;
            this.to = to;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getTo() {
            return to;
        }

        public void setTo(int to) {
            this.to = to;
        }
    }
}