package SEPee.serialisierung.messageType;

/**
 * class for the ActivePhase message type
 */
public class ActivePhase extends Message{
    private ActivePhaseBody messageBody;

    public ActivePhase(int phase){
        super("ActivePhase");
        //this.messageType = "ActivePhase";
        this.messageBody = new ActivePhaseBody(phase);
    }

    public ActivePhaseBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(ActivePhaseBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the Active Phase message type
     */
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
