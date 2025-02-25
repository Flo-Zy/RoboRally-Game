package SEPee.serialisierung.messageType;
import java.util.ArrayList;

/**
 * class for the CurrentCards message type
 * @author Maximilian
 */
public class CurrentCards extends Message{
    private CurrentCardsBody messageBody;

    public CurrentCards(ArrayList<ActiveCard> activeCards) {
        super("CurrentCards");
        //this.messageType = "CurrentCards";
        this.messageBody = new CurrentCardsBody(activeCards);
    }

    public CurrentCardsBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(CurrentCardsBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the CurrentCards message type
     */
    public static class CurrentCardsBody {
        ArrayList<ActiveCard> activeCards;

        public CurrentCardsBody(ArrayList<ActiveCard> activeCards) {
            this.activeCards = activeCards;
        }

        public ArrayList<ActiveCard> getActiveCards() {
            return activeCards;
        }

        public void setActiveCards(ArrayList<ActiveCard> activeCards) {
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

        public int getClientID() {
            return clientID;
        }

        public void setClientID(int clientID) {
            this.clientID = clientID;
        }

        public String getCard() {
            return card;
        }

        public void setCard(String card) {
            this.card = card;
        }
    }
}
