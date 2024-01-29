package SEPee.serialisierung.messageType;

/**
 * class for the SelectedCard message type
 * @author Franziska
 */
public class SelectedCard extends Message{
    private SelectedCardBody messageBody;

    public SelectedCard(String card, int register){
        super("SelectedCard");
        this.messageBody = new SelectedCardBody(card, register);
    }

    public SelectedCardBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(SelectedCardBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the SelectedCard message type
     */
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
