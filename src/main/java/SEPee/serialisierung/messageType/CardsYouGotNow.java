package SEPee.serialisierung.messageType;

public class CardsYouGotNow {
    private String messageType;
    private CardsYouGotNowBody messageBody;

    public CardsYouGotNow(String[] cards) {
        this.messageType = "CardsYouGotNow";
        this.messageBody = new CardsYouGotNowBody(cards);
    }

    public static class CardsYouGotNowBody {
        String[] cards;

        public CardsYouGotNowBody(String[] cards) {
            this.cards = cards;
        }

        public String[] getCards() {
            return cards;
        }

        public void setCards(String[] cards) {
            this.cards = cards;
        }

    }
}
