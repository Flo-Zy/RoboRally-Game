package SEPee.serialisierung.messageType;

import java.util.ArrayList;

public class PickDamage extends Message{
    private PickDamage.PickDamageBody messageBody;

    public PickDamage(int count, ArrayList<String> availablePiles) {
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
        private ArrayList<String> availablePiles;

        public PickDamageBody(int count, ArrayList<String> availablePiles) {
            this.count = count;
            this.availablePiles = availablePiles;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public ArrayList<String> getAvailablePiles() {
            return availablePiles;
        }

        public void setAvailablePiles(ArrayList<String> availablePiles) {
            this.availablePiles = availablePiles;
        }
    }
}
