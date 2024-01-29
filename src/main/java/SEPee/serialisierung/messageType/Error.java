package SEPee.serialisierung.messageType;

/**
 * class for the Error message type
 * @author Franziska
 */
public class Error extends Message{
    private ErrorBody messageBody;

    public Error(String error) {
        super("Error");
        this.messageBody = new ErrorBody(error);
    }

    public ErrorBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(ErrorBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the Error message type
     */
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