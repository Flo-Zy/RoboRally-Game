package SEPee.serialisierung.messageType;

import java.util.ArrayList;

public class SelectedDamage extends Message{
    private SelectedDamage.SelectedDamageBody messageBody;

    public SelectedDamage(ArrayList<String> cards) {
        super("SelectedDamage");
        this.messageBody = new SelectedDamage.SelectedDamageBody(cards);
    }

    public SelectedDamage.SelectedDamageBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(SelectedDamage.SelectedDamageBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class SelectedDamageBody {
        private ArrayList<String> cards;

        public SelectedDamageBody(ArrayList<String> cards) {
            this.cards = cards;
        }

        public ArrayList<String> getCards() {
            return cards;
        }

        public void setCards(ArrayList<String> cards) {
            this.cards = cards;
        }
    }
}
