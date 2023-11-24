package SEPee.serialisierung.messageType;

public class Reboot {
    private String messageType;
    private RebootBody messageBody;

    public Reboot(int clientID){
        this.messageType = "Reboot";
        this.messageBody = new RebootBody(clientID);
    }

    public static class RebootBody {
        private int clientID;

        public RebootBody(int clientID) {
            this.clientID = clientID;
        }

        public int getClientID() {
            return clientID;
        }

        public void setClientID(int clientID) {
            this.clientID = clientID;
        }
    }
}
