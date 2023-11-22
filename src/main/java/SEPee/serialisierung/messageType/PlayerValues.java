package SEPee.serialisierung.messageType;


public class PlayerValues {
    private String playerName;
    private int score;

    // Konstruktor
    public PlayerValues(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
    }

    // Getter und Setter für playerName
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    // Getter und Setter für score
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    // Weitere Methoden oder Attribute, die zur PlayerValues-Klasse gehören könnten
}