package SEPee.serialisierung.messageType;

public class RebootDirection {
    private String messageType;
    private RebootDirectionBody messageBody;

    public RebootDirection(String direction){
        this.messageType = "RebootDirection";
        this.messageBody = new RebootDirectionBody(direction);
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
