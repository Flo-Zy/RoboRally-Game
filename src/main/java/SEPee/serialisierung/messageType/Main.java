package SEPee.serialisierung.messageType;

import SEPee.serialisierung.Deserialisierer;
import SEPee.serialisierung.Serialisierer;
import SEPee.server.model.gameBoard.DizzyHighway;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        DizzyHighway dizzyHighway = new DizzyHighway();
        GameStarted gameStarted = new GameStarted(dizzyHighway.getGameBoard());
        String serializedGameStarted = Serialisierer.serialize(gameStarted);
        System.out.println(serializedGameStarted);

        /*
        //deserialisieren klappt noch nicht für die map
        GameStarted deserializedGameStarted = Deserialisierer.deserialize(serializedGameStarted, GameStarted.class);
        System.out.println(deserializedGameStarted.getMessageBody().getGameMap());

         */


        //test HelloClient als child Klasse von Message
        HelloClient helloClient = new HelloClient("Version 1.0");
        String serializedHelloClient = Serialisierer.serialize(helloClient);
        System.out.println(serializedHelloClient);

        Message test = new HelloClient("Version 1.0");
        String serializedHelloClient1 = Serialisierer.serialize(test);

        Message deserialisiertesObjekt = Deserialisierer.deserialize(serializedHelloClient1, Message.class);
        System.out.println(deserialisiertesObjekt.getMessageType());
        HelloClient deserialisierterHelloClient = Deserialisierer.deserialize(serializedHelloClient1, HelloClient.class);
        System.out.println(deserialisierterHelloClient.getMessageBody().getProtocol());


        /*

        // test PlayerValues serialisation
        PlayerValues player = new PlayerValues("Alice", 100);
        String serializedPlayer = Serialisierer.serialize(player);
        System.out.println(serializedPlayer);

        // test PlayerValues de-serialisation
        PlayerValues deserializedPlayer = Deserialisierer.deserialize(serializedPlayer, PlayerValues.class);
        System.out.println("Player Name: " + deserializedPlayer.getMessageBody().getName());
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

        // test ReplaceCard serialisation
        ReplaceCard replaceCard = new ReplaceCard(3, "MoveI", 9001);
        String serializedReplaceCard = Serialisierer.serialize(replaceCard);
        System.out.println(serializedReplaceCard);

        // test Movement serialisation
        Movement movement = new Movement(42, 4, 2);
        String serializedMovement = Serialisierer.serialize(movement);
        System.out.println(serializedMovement);

        // test PlayerTurning serialisation
        PlayerTurning playerTurning = new PlayerTurning(42, "counterclockwise");
        String serializedPlayerTurning = Serialisierer.serialize(playerTurning);
        System.out.println(serializedPlayerTurning);

        // test Animation serialisation
        Animation animation = new Animation("PlayerShooting");
        String serializedAnimation = Serialisierer.serialize(animation);
        System.out.println(serializedAnimation);

        // test Reboot serialisation
        Reboot reboot = new Reboot(42);
        String serializedReboot = Serialisierer.serialize(reboot);
        System.out.println(serializedReboot);

        // test RebootDirection serialisation
        RebootDirection rebootDirection = new RebootDirection("right");
        String serializedRebootDirection = Serialisierer.serialize(rebootDirection);
        System.out.println(serializedRebootDirection);

        // test Energy serialisation
        Energy energy = new Energy(42,1,"EnergySpace");
        String serializedEnergy = Serialisierer.serialize(energy);
        System.out.println(serializedEnergy);

        // test CheckPointReached serialisation
        CheckPointReached checkPointReached = new CheckPointReached(42, 3);
        String serializedCheckPointReached = Serialisierer.serialize(checkPointReached);
        System.out.println(serializedCheckPointReached);

        // test CheckPointReached serialisation
        GameFinished gameFinished = new GameFinished(42);
        String serializedGameFinished = Serialisierer.serialize(gameFinished);
        System.out.println(serializedGameFinished);

        // test CurrentCards serialisation
        List<CurrentCards.ActiveCard> activeCards = new ArrayList<>();
        activeCards.add(new CurrentCards.ActiveCard(1,"MoveI"));
        activeCards.add(new CurrentCards.ActiveCard(2,"Spam"));
        CurrentCards currentCards = new CurrentCards(activeCards);
        String serializedCurrentCards = Serialisierer.serialize(currentCards);
        System.out.println(serializedCurrentCards);

         */

        /**
        // test GameStarted serialisation
        // cell1
        List<String> orientations1 = new ArrayList<>();
        orientations1.add("top");
        orientations1.add("right");
        orientations1.add("bottom");
        GameStarted.Component component1 = new GameStarted.Component("ConveyorBelt", "1B", 2, orientations1);

        List<String> orientations2 = new ArrayList<>();
        orientations1.add("left");
        List<Integer> registers2 = new ArrayList<>();
        registers2.add(2);
        registers2.add(4);
        GameStarted.Component component2 = new GameStarted.Component("PushPanel", "1B", orientations2, registers2);

        List<GameStarted.Component> components1 = new ArrayList<>();
        components1.add(component1);
        components1.add(component2);

        GameStarted.Cell cell1 = new GameStarted.Cell(components1);

        // cell2
        List<String> orientations3 = new ArrayList<>();
        orientations3.add("top");
        orientations3.add("right");
        GameStarted.Component component3 = new GameStarted.Component("Wall", "4A", orientations3);
        List<String> orientations4 = new ArrayList<>();
        orientations4.add("bottom");
        GameStarted.Component component4 = new GameStarted.Component("Laser", "4A", orientations4, 2);

        List<GameStarted.Component> components2 = new ArrayList<>();
        components2.add(component3);
        components2.add(component4);
        GameStarted.Cell cell2 = new GameStarted.Cell(components2);

        List<GameStarted.Cell> cells = new ArrayList<>();
        cells.add(cell1);
        cells.add(cell2);

        GameStarted gameStarted = new GameStarted(cells);

        CurrentCards currentCards = new CurrentCards(activeCards);
        String serializedCurrentCards = Serialisierer.serialize(currentCards);
        System.out.println(serializedCurrentCards);
         **/
    }
}