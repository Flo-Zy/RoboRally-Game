package SEPee.server.model;
import SEPee.server.model.gameBoard.GameBoard;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * a player's robot
 */
@Getter
@Setter
public class Robot {
    @Getter
    private int x;
    @Getter
    private int y;
    private String orientation;
    private List<RobotPositionChangeListener> listeners = new ArrayList<>();
    @Getter
    @Setter
    private int startingPointX;
    @Getter
    @Setter
    private int startingPointY;
    @Setter
    private boolean alreadyRebooted = false;

    public Robot(int x, int y, String orientation) {
        this.x = x;
        this.y = y;
        this.orientation = orientation;
    }

    public void addPositionChangeListener(RobotPositionChangeListener listener) {
        listeners.add(listener);
    }

    public void removePositionChangeListener(RobotPositionChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * sets x and calls the method to check for a reboot
     * @param x the x coordinate
     */
    public void setX(int x) {
        this.x = x;
        notifyPositionChange();
        handleReboot();
    }

    /**
     * sets y and calls the method to check for a reboot
     * @param y the y coordinate
     */
    public void setY(int y) {
        this.y = y;
        notifyPositionChange();
        handleReboot();
    }

    /**
     * handles the rebooting of a robot
     */
    private void handleReboot() {
        GameBoard gameBoard = Server.getGame().getBoardClass();
        String rebootTo = gameBoard.checkRebootConditions(x, y);
        ServerLogger.writeToServerLog("rebootTo: " + rebootTo);

        if (!rebootTo.equals("continue") && !alreadyRebooted) {
            alreadyRebooted = true; // Set the flag to prevent repeated reboots

            ServerLogger.writeToServerLog("Rebooting this robot to: " + rebootTo);
            ClientHandler.rebootThisRobot(x, y, rebootTo);
        }
    }

    /**
     * notifies in case of a position change
     */
    private void notifyPositionChange() {
        for (RobotPositionChangeListener listener : listeners) {
            listener.onRobotPositionChange(this);
        }
    }

    public interface RobotPositionChangeListener {
        void onRobotPositionChange(Robot robot);
    }

    /**
     * sets a robot's orientation
     * @param orientation the orientation to set to
     */
    public void setOrientation(String orientation) {
        this.orientation = orientation;
        notifyPositionChange();
    }

}