package SEPee.serialisierung.messageType;

public class SelectedDamage extends Message{
    private SelectedDamage.SelectedDamageBody messageBody;

    public SelectedDamage(String[] cards) {
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
        private String[] cards;

        public SelectedDamageBody(String[] cards) {
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
