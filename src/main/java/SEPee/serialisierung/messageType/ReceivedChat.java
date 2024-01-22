package SEPee.serialisierung.messageType;

/**
 * class for the ReceivedChat message type
 */
public class ReceivedChat extends Message {
    private ReceivedChatBody messageBody;

    public ReceivedChat(String message, int from, boolean isPrivate) {
        super("ReceivedChat");
        // this.messageType = "ReceivedChat";
        this.messageBody = new ReceivedChatBody(message, from, isPrivate);
    }
    public ReceivedChatBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(ReceivedChatBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the ReceivedChat message type
     */
    public static class ReceivedChatBody {
        private String message;
        private int from;
        private boolean isPrivate;

        public ReceivedChatBody(String message, int from, boolean isPrivate) {
            this.message = message;
            this.from = from;
            this.isPrivate = isPrivate;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getFrom() {
            return from;
        }

        public void setFrom(int from) {
            this.from = from;
        }

        public boolean isPrivate() {
            return isPrivate;
        }

        public void setPrivate(boolean isPrivate) {
            this.isPrivate = isPrivate;
        }
    }
}