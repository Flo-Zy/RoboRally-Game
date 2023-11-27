package SEPee.serialisierung.messageType;

public class Error {
    private String messageType;
    private ErrorBody messageBody;

    public Error(String error) {
        this.messageType = "Error";
        this.messageBody = new ErrorBody(error);
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public ErrorBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(ErrorBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class ErrorBody {
        private String error;

        public ErrorBody(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}