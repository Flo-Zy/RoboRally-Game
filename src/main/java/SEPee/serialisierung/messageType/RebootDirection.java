package SEPee.serialisierung.messageType;

public class RebootDirection {
    private String messageType;
    private RebootDirectionBody messageBody;

    public RebootDirection(String direction){
        this.messageType = "RebootDirection";
        this.messageBody = new RebootDirectionBody(direction);
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public RebootDirectionBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(RebootDirectionBody messageBody) {
        this.messageBody = messageBody;
    }

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
