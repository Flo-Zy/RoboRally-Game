package SEPee.serialisierung.messageType;

import java.util.ArrayList;

/**
 * class for the YourCards message type
 */
public class YourCards extends Message{
    private YourCardsBody messageBody;

    public YourCards(ArrayList<String> cardsInHand){
        super("YourCards");
        this.messageBody = new YourCardsBody(cardsInHand);
    }

    public YourCardsBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(YourCardsBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the YourCards message type
     */
    public static class YourCardsBody {
        private ArrayList<String> cardsInHand;

        public YourCardsBody(ArrayList<String> cardsInHand){
            this.cardsInHand = cardsInHand;
        }

        public ArrayList<String> getCardsInHand(){
            return cardsInHand;
        }
        public String transformCardsInHandIntoString(){
            ArrayList<String> cardsInHand = getCardsInHand();
            StringBuilder stringBuilder = new StringBuilder();
            int lastElement = 0;
            for (String str : cardsInHand) {
                stringBuilder.append(str);
                if(lastElement <= 7) {
                    stringBuilder.append(" ,\n");
                }
                lastElement++;
            }
            return stringBuilder.toString();
        }


        public void setCardsInHand(ArrayList<String> cardsInHand){
            this.cardsInHand = cardsInHand;
        }

    }
}
