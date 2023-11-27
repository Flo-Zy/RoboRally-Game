package SEPee.serialisierung.messageType;

public class MapSelected {
    private String messageType;
    private MapSelectedBody messageBody;

    public MapSelected(String map) {
        this.messageType = "MapSelected";
        this.messageBody = new MapSelectedBody(map);
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public MapSelectedBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(MapSelectedBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class MapSelectedBody {
        private String map;

        public MapSelectedBody(String map) {
            this.map = map;
        }

        public String getMap() {
            return map;
        }

        public void setMap(String map) {
            this.map = map;
        }
    }
}