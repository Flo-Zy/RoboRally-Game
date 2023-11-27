package SEPee.serialisierung.messageType;

public class PlayerValues {
    private String messageType;
    private PlayerValuesBody messageBody;

    public PlayerValues(String name, int figure) {
        this.messageType = "PlayerValues";
        this.messageBody = new PlayerValuesBody(name, figure);
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public PlayerValuesBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(PlayerValuesBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class PlayerValuesBody {
        private String name;
        private int figure;

        public PlayerValuesBody(String name, int figure) {
            this.name = name;
            this.figure = figure;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getFigure() {
            return figure;
        }

        public void setFigure(int figure) {
            this.figure = figure;
        }
    }

}