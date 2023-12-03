package SEPee.serialisierung.messageType;

public class Animation extends Message{
    //private String messageType;
    private AnimationBody messageBody;

    public Animation(String type){
        super("Animation");
        //this.messageType = "Animation";
        this.messageBody = new AnimationBody(type);
    }

    /*
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

     */

    public AnimationBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(AnimationBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class AnimationBody {
        private String type;

        public AnimationBody(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
