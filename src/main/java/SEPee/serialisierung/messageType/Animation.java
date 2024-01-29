package SEPee.serialisierung.messageType;

/**
 * class for the Animation message type
 * @author Maximilian
 */
public class Animation extends Message{
    private AnimationBody messageBody;

    public Animation(String type){
        super("Animation");
        //this.messageType = "Animation";
        this.messageBody = new AnimationBody(type);
    }

    public AnimationBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(AnimationBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the Animation message type
     */
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
