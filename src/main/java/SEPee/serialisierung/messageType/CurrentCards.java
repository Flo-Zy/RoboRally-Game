package SEPee.serialisierung.messageType;

public class CurrentCards {
    private String messageType;
    private CurrentCardsBody messageBody;

    public CurrentCards(CurrentCardsBody.ActiveCard[] activeCards) {
        this.messageType = "CurrentCards";
        this.messageBody = new CurrentCardsBody(activeCards);
    }

    public static class CurrentCardsBody {
        int clientID;
        String card;
        private ActiveCard[] activeCards;

        public CurrentCardsBody(ActiveCard[] activeCards) {
            this.activeCards = new ActiveCard[]{ new ActiveCard(clientID, card) };
        }

        public static class ActiveCard {
            int clientID;
            String card;

            public ActiveCard(int clientID, String card) {
                this.clientID = clientID;
                this.card = card;
            }

        }

    }
}
