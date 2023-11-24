package SEPee.serialisierung.messageType;

public class Animation {
    private String messageType;
    private AnimationBody messageBody;

    public Animation(String type){
        this.messageType = "Animation";
        this.messageBody = new AnimationBody(type);
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
