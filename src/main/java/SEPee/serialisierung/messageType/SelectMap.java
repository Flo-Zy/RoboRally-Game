package SEPee.serialisierung.messageType;

import SEPee.server.model.gameBoard.DizzyHighway;

import java.util.List;

public class SelectMap extends Message{
    // private String messageType;
    private SelectMapBody messageBody;

    public SelectMap() {
        super("SelectMap");
        // this.messageType = "SelectMap";
        this.messageBody = new SelectMapBody();
    }

    /*
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }


     */
    public SelectMapBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(SelectMapBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class SelectMapBody {
        private List<String> availableMaps;

        public SelectMapBody() {
            availableMaps.add("DizzyHighway");
        }

        public List<String> getAvailableMaps() {
            return availableMaps;
        }

        public void setAvailableMaps(List<String> availableMaps) {
            this.availableMaps = availableMaps;
        }
    }
}