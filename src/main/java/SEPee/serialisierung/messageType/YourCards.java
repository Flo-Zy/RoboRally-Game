package SEPee.serialisierung.messageType;

public class YourCards {
    private String messageType;
    private YourCardsBody messageBody;

    public YourCards(String[] cardsInHand){
        this.messageType = "YourCards";
        this.messageBody = new YourCardsBody(cardsInHand);
    }

    public static class YourCardsBody {
        private String[] cardsInHand;

        public YourCardsBody(String[] cardsInHand){
            this.cardsInHand = cardsInHand;
        }

        public String[] getCardsInHand(){
            return cardsInHand;
        }

        public void setCardsInHand(String[] cardsInHand){
            this.cardsInHand = cardsInHand;
        }

    }
}
