package SEPee.serialisierung.messageType;

public class Alive {
    private String messageType;
    private AliveBody messageBody;

    public Alive() {
        this.messageType = "Alive";
        this.messageBody = new AliveBody();
    }

    public static class AliveBody {


        public AliveBody() {

        }
    }
}