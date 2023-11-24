package SEPee.serialisierung.messageType;
import java.util.List;

public class CurrentCards {
    private String messageType;
    private CurrentCardsBody messageBody;

    public CurrentCards(List<ActiveCard> activeCards) {
        this.messageType = "CurrentCards";
        this.messageBody = new CurrentCardsBody(activeCards);
    }

    public static class CurrentCardsBody {
        List<ActiveCard> activeCards;

        public CurrentCardsBody(List<ActiveCard> activeCards) {
            this.activeCards = activeCards;
        }

        public List<ActiveCard> getActiveCards() {
            return activeCards;
        }

        public void setActiveCards(List<ActiveCard> activeCards) {
            this.activeCards = activeCards;
        }
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
