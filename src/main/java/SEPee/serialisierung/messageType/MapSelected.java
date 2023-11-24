package SEPee.serialisierung.messageType;

public class MapSelected {
    private String messageType;
    private MapSelectedBody messageBody;

    public MapSelected(String map) {
        this.messageType = "MapSelected";
        this.messageBody = new MapSelectedBody(map);
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