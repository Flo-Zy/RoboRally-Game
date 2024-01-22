package SEPee.serialisierung.messageType;

/**
 * class for the PlayerValues message type
 */
public class PlayerValues extends Message{
    private PlayerValuesBody messageBody;

    public PlayerValues(String name, int figure) {
        super ("PlayerValues");
        this.messageBody = new PlayerValuesBody(name, figure);
    }

    public PlayerValuesBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(PlayerValuesBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the PlayerValues message type
     */
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