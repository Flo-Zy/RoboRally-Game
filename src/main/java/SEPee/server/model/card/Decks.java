package SEPee.server.model.card;

import SEPee.server.model.card.progCard.*;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * class for the game's draw pile
 */
public class Decks {
    private ArrayList<Card> deck;
    public ArrayList<Card> getDeck() {
        return deck;
    }

    public void setDeck(ArrayList<Card> deck) {
        this.deck = deck;
    }

    public Decks() {
        deck = new ArrayList<>();
        initializeDeck(deck);
    }

    /**
     * initializes the deck
     * @param deck the deck to be initialized
     */
    private void initializeDeck(List<Card> deck) {
        for (int i = 0; i < 5; i++) {
            deck.add(new MoveI());
        }
        for (int i = 0; i < 3; i++) {
            deck.add(new MoveII());
        }
        for (int i = 0; i < 1; i++) {
            deck.add(new MoveIII());
        }
        for (int i = 0; i < 3; i++) {
            deck.add(new RightTurn());
        }
        for (int i = 0; i < 3; i++) {
            deck.add(new LeftTurn());
        }
        for (int i = 0; i < 1; i++) {
            deck.add(new BackUp());
        }
        for (int i = 0; i < 1; i++) {
            deck.add(new PowerUp());
        }
        for (int i = 0; i < 2; i++) {
            deck.add(new Again());
        }
        for (int i = 0; i < 1; i++) {
            deck.add(new UTurn());
        }
        shuffle(deck);
    }

    /**
     * duffles the deck
     * @param deck deck to be shuffled
     */
    public void shuffle(List<Card> deck) {
        Collections.shuffle(deck);
    }
}
