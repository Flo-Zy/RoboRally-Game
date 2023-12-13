package SEPee.server.model.card;

import SEPee.server.model.card.progCard.*;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class Decks {
    private List<Card> deck;
    /*
    private List<Card> deck2;
    private List<Card> deck3;
    private List<Card> deck4;
    private List<Card> deck5;
    private List<Card> deck6;
     */
    public List<Card> getDeck() {
        return deck;
    }

    public void setDeck(List<Card> deck) {
        this.deck = deck;
    }

    public Decks() {
        deck = new ArrayList<>();
        initializeDeck(deck);
        /*
        deck2 = new ArrayList<>();
        initializeDeck(deck2);
        deck3 = new ArrayList<>();
        initializeDeck(deck3);
        deck4 = new ArrayList<>();
        initializeDeck(deck4);
        deck5 = new ArrayList<>();
        initializeDeck(deck5);
        deck6 = new ArrayList<>();
        initializeDeck(deck6);
         */
    }

    private void initializeDeck(List<Card> deck) {
        for (int i = 0; i < 5; i++) {
            deck.add(new Move1());
        }
        for (int i = 0; i < 3; i++) {
            deck.add(new Move2());
        }
        for (int i = 0; i < 1; i++) {
            deck.add(new Move3());
        }
        for (int i = 0; i < 3; i++) {
            deck.add(new RightTurn());
        }
        for (int i = 0; i < 3; i++) {
            deck.add(new LeftTurn());
        }
        for (int i = 0; i < 1; i++) {
            deck.add(new Backup());
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

    public void shuffle(List<Card> deck) {
        Collections.shuffle(deck);
    }
}
