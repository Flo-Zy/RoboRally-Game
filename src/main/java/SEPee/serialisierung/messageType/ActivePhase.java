package SEPee.serialisierung.messageType;

public class ActivePhase {
    private String messageType;
    private ActivePhaseBody messageBody;

    public ActivePhase(int phase){
        this.messageType = "ActivePhase";
        this.messageBody = new ActivePhaseBody(phase);
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
