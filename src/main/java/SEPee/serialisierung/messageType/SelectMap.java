package SEPee.serialisierung.messageType;

import java.util.List;

public class SelectMap {
    private String messageType;
    private SelectMapBody messageBody;

    public SelectMap(List<String> availableMaps) {
        this.messageType = "SelectMap";
        this.messageBody = new SelectMapBody(availableMaps);
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public SelectMapBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(SelectMapBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class SelectMapBody {
        private List<String> availableMaps;

        public SelectMapBody(List<String> availableMaps) {
            this.availableMaps = availableMaps;
        }

        public List<String> getAvailableMaps() {
            return availableMaps;
        }

        public void setAvailableMaps(List<String> availableMaps) {
            this.availableMaps = availableMaps;
        }
    }
}