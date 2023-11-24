package SEPee.serialisierung.messageType;

import SEPee.serialisierung.Deserialisierer;
import SEPee.serialisierung.Serialisierer;

public class Main {
    public static void main(String[] args) {
        // Create an instance of PlayerValues
        PlayerValues player = new PlayerValues("Alice", 100);

        // Serialize the PlayerValues instance
        String serializedPlayer = Serialisierer.serialize(player);

        // Output the serialized PlayerValues
        System.out.println("Serialized PlayerValues:");
        System.out.println(serializedPlayer);

        // Deserialize the serialized string back to PlayerValues
        PlayerValues deserializedPlayer = Deserialisierer.deserialize(serializedPlayer, PlayerValues.class);

        // Output the deserialized PlayerValues
        System.out.println("\nDeserialized PlayerValues:");
        System.out.println("Player Name: " + deserializedPlayer.getMessageBody().getPlayerName());
        System.out.println("Figure: " + deserializedPlayer.getMessageBody().getFigure());

        SendChat sendChat = new SendChat("Hi", 4);

        String serializedSendChat = Serialisierer.serialize(sendChat);
        System.out.println(serializedSendChat);
    }
}
