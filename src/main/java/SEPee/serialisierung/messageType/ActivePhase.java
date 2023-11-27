package SEPee.serialisierung.messageType;

public class ActivePhase {
    private String messageType;
    private ActivePhaseBody messageBody;

    public ActivePhase(int phase){
        this.messageType = "ActivePhase";
        this.messageBody = new ActivePhaseBody(phase);
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public ActivePhaseBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(ActivePhaseBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class ActivePhaseBody{
        private int phase;

        public ActivePhaseBody(int phase){
            this.phase = phase;
        }

        public int getPhase(){
            return phase;
        }

        public void setPhase(int phase){
            this.phase = phase;
        }

    }
}
