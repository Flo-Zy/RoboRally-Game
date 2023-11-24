package SEPee.serialisierung.messageType;

import SEPee.serialisierung.Deserialisierer;
import SEPee.serialisierung.Serialisierer;

public class Main {
    public static void main(String[] args) {

        // test PlayerValues serialisation
        PlayerValues player = new PlayerValues("Alice", 100);
        String serializedPlayer = Serialisierer.serialize(player);
        System.out.println(serializedPlayer);
        // test PlayerValues de-serialisation
        PlayerValues deserializedPlayer = Deserialisierer.deserialize(serializedPlayer, PlayerValues.class);
        System.out.println("Deserialized PlayerValues:");
        System.out.println("Player Name: " + deserializedPlayer.getMessageBody().getPlayerName());
        System.out.println("Figure: " + deserializedPlayer.getMessageBody().getFigure());

        // test SendChat serialisation
        SendChat sendChat = new SendChat("Hi", 4);
        String serializedSendChat = Serialisierer.serialize(sendChat);
        System.out.println(serializedSendChat);

        // test YourCards serialisation
        String[] string = {"card 1", "card 2"};
        YourCards yourCards = new YourCards(string);
        String serializedYourCards = Serialisierer.serialize(yourCards);
        System.out.println(serializedYourCards);

        // test CardSelected serialisation
        CardSelected cardSelected = new CardSelected(42, 5,true);
        String serializedCardSelected = Serialisierer.serialize(cardSelected);
        System.out.println(serializedCardSelected);

        // test SelectionFinished serialisation
        SelectionFinished selectionFinished = new SelectionFinished(42);
        String serializedSelectionFinished = Serialisierer.serialize(selectionFinished);
        System.out.println(serializedSelectionFinished);

        // test TimerStarted serialisation
        TimerStarted timerStarted = new TimerStarted();
        String serializedTimerStarted = Serialisierer.serialize(timerStarted);
        System.out.println(serializedTimerStarted);

        // test TimerEnded serialisation
        int[] clientIDs = {1, 3, 6};
        TimerEnded timerEnded = new TimerEnded(clientIDs);
        String serializedTimerEnded = Serialisierer.serialize(timerEnded);
        System.out.println(serializedTimerEnded);

        // test CardsYouGotNow serialisation
        String[] cards = {"card1", "..."};
        CardsYouGotNow cardsYouGotNow = new CardsYouGotNow(cards);
        String serializedCardsYouGotNow = Serialisierer.serialize(cardsYouGotNow);
        System.out.println(serializedCardsYouGotNow);

        /** test CurrentCards serialisation */
        CurrentCards.CurrentCardsBody.ActiveCard[] activeCards = {new CurrentCards.CurrentCardsBody.ActiveCard(1,"MoveI"), new CurrentCards.CurrentCardsBody.ActiveCard(2,"Spam")};
        CurrentCards currentCards = new CurrentCards(activeCards);
        String serializedCurrentCards = Serialisierer.serialize(currentCards);
        System.out.println(serializedCurrentCards);

        // test ReplaceCard serialisation
        ReplaceCard replaceCard = new ReplaceCard(3, "MoveI", 9001);
        String serializedReplaceCard = Serialisierer.serialize(replaceCard);
        System.out.println(serializedReplaceCard);

        // test Movement serialisation
        Movement movement = new Movement(42, 4, 2);
        String serializedMovement = Serialisierer.serialize(movement);
        System.out.println(serializedMovement);

        // test Movement serialisation
        PlayerTurning playerTurning = new PlayerTurning(42, "counterclockwise");
        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
        System.out.println(serializedPlayerTurning);

    }
}