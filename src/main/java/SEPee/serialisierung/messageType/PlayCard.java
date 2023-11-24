package SEPee.serialisierung.messageType;

public class PlayCard {
    private String messageType;
    private CardBody messageBody;

    public PlayCard(String card){
        this.messageType = "PlayCard";
        this.messageBody = new CardBody(card);
    }

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
