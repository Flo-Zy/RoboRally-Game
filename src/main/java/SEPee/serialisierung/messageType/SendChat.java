package SEPee.serialisierung.messageType;

public class SendChat {
    private String messageType;
    private SendChatBody messageBody;

    public SendChat(String message, int to){
        this.messageType = "SendChat";
        this.messageBody = new SendChatBody(message, to);
    }

    public static class SendChatBody{
        private String message;
        private int to;

        public SendChatBody(String message, int to){
            this.message = message;
            this.to = to;
        }

        public String getMessage(){
            return message;
        }

        public void setMessage(String message){
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
