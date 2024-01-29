package SEPee.serialisierung.messageType;

/**
 * class for the PlayCard message type
 * @author Franziska
 */
public class PlayCard extends Message{
    private CardBody messageBody;

    public PlayCard(String card){
        super("PlayCard");
        this.messageBody = new CardBody(card);
    }

    public CardBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(CardBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the PlayCard message type
     */
    public static class CardBody{
        private String card;

        public CardBody(String card){
            this.card = card;
        }

        public String getCard(){
            return card;
        }

        public void setCard(String card){
            this.card = card;
        }

    }
}
