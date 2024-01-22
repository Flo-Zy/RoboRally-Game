package SEPee.serialisierung.messageType;

/**
 * class for the MapSelected message type
 */
public class MapSelected extends Message{
    private MapSelectedBody messageBody;

    public MapSelected(String map) {
        super("MapSelected");
        //this.messageType = "MapSelected";
        this.messageBody = new MapSelectedBody(map);
    }

    public MapSelectedBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(MapSelectedBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the MapSelected message type
     */
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