package SEPee.serialisierung.messageType;

public class SetStatus {
    private String messageType;
    private SetStatusBody messageBody;

    public SetStatus(boolean ready) {
        this.messageType = "SetStatus";
        this.messageBody = new SetStatusBody(ready);
    }

    public static class SetStatusBody {
        private boolean ready;

        public SetStatusBody(boolean ready) {
            this.ready = ready;
        }

        public boolean isReady() {
            return ready;
        }

        public void setReady(boolean ready) {
            this.ready = ready;
        }
    }
}