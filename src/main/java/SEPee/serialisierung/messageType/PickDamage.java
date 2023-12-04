package SEPee.serialisierung.messageType;

public class PickDamage extends Message{
    private PickDamage.PickDamageBody messageBody;

    public PickDamage(int count, String[] availablePiles) {
        super("PickDamage");
        this.messageBody = new PickDamage.PickDamageBody(count, availablePiles);
    }

    public PickDamage.PickDamageBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(PickDamage.PickDamageBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class PickDamageBody {

        private int count;
        private String[] availablePiles;

        public PickDamageBody(int count, String[] availablePiles) {
            this.count = count;
            this.availablePiles = availablePiles;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String[] getAvailablePiles() {
            return availablePiles;
        }

        public void setAvailablePiles(String[] availablePiles) {
            this.availablePiles = availablePiles;
        }
    }
}
