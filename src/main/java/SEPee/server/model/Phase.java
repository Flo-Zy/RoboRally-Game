package SEPee.server.model;
import lombok.Getter;

/**
 * the current phase of the game
 */
@Getter
public enum Phase {
    Starting,
    UPGRADE,
    PROGRAMING,
    ACTIVATION;
}
