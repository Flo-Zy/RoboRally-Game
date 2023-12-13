package SEPee.serialisierung.messageType;

import java.util.ArrayList;

public class YourCards extends Message{
    // private String messageType;
    private YourCardsBody messageBody;

    public YourCards(ArrayList<String> cardsInHand){
        super("YourCards");
        // this.messageType = "YourCards";
        this.messageBody = new YourCardsBody(cardsInHand);
    }

    /*
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
     */

    public YourCardsBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(YourCardsBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class YourCardsBody {
        private ArrayList<String> cardsInHand;

        public YourCardsBody(ArrayList<String> cardsInHand){
            this.cardsInHand = cardsInHand;
        }

        public ArrayList<String> getCardsInHand(){
            return cardsInHand;
        }

        // macht aus ArrayList<String> einen formatierten String
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
