package SEPee.client.model;

import lombok.Getter;
import lombok.Setter;

/**
 * so smartAi can create a robot object
 * @author Maximilian
 */
public class RobotAI {
    @Setter
    @Getter
    private String orientation;
    @Setter
    @Getter
    private int x;
    @Setter
    @Getter
    private int y;
}