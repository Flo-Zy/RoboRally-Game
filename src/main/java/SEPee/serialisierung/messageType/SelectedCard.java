package SEPee.serialisierung.messageType;

public class SelectedCard extends Message{
    // private String messageType;
    private SelectedCardBody messageBody;

    public SelectedCard(String card, int register){
        super("SelectedCard");
        // this.messageType = "SelectedCard";
        this.messageBody = new SelectedCardBody(card, register);
    }

    /*
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    */

    public SelectedCardBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(SelectedCardBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class SelectedCardBody{
        private String card;

        private int register;

        public SelectedCardBody(String card, int register){
            this.card = card;
            this.register = register;
        }

        public String getCard(){
            return card;
        }

        public void setCard(String card){
            this.card = card;
        }

        public int getRegister() {
            return register;
        }

        public void setRegister(int register) {
            this.register = register;
        }

    }
}
