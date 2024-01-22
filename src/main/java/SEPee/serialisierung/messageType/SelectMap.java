package SEPee.serialisierung.messageType;

import java.util.ArrayList;

/**
 * class for the SelectMap message type
 */
public class SelectMap extends Message{
    private SelectMapBody messageBody;

    public SelectMap() {
        super("SelectMap");
        this.messageBody = new SelectMapBody();
    }

    public SelectMapBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(SelectMapBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the SelectMap message type
     */
    public static class SelectMapBody {
        private ArrayList<String> availableMaps = new ArrayList<>();

        public SelectMapBody() {
            availableMaps.add("Dizzy Highway");
            availableMaps.add("Extra Crispy");
            availableMaps.add("Lost Bearings");
            availableMaps.add("Death Trap");
        }

        public ArrayList<String> getAvailableMaps() {
            return availableMaps;
        }

        public void setAvailableMaps(ArrayList<String> availableMaps) {
            this.availableMaps = availableMaps;
        }
    }
}