package SEPee.serialisierung.messageType;

/**
 * class for the RebootDirection message type
 */
public class RebootDirection extends Message {
    private RebootDirectionBody messageBody;

    public RebootDirection(String direction){
        super ("RebootDirection");
        this.messageBody = new RebootDirectionBody(direction);
    }

    public RebootDirectionBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(RebootDirectionBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the RebootDirection message type
     */
    public static class RebootDirectionBody {
        private String direction;

        public RebootDirectionBody(String direction) {
            this.direction = direction;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }
    }

}
