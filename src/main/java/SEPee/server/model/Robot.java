package SEPee.server.model;
import SEPee.client.model.Client;
import SEPee.client.viewModel.MapController.MapController;
import SEPee.server.model.gameBoard.GameBoard;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Robot {
    @Getter
    private int x;
    @Getter
    private int y;
    //private Orientation orientation;
    //private int energyReserve;
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

    public void fireLaser(Orientation target){
    }

    public void addPositionChangeListener(RobotPositionChangeListener listener) {
        listeners.add(listener);
    }

    public void removePositionChangeListener(RobotPositionChangeListener listener) {
        listeners.remove(listener);
    }

    public void setX(int x) {
        this.x = x;
        notifyPositionChange();
        handleReboot();
    }

    public void setY(int y) {
        this.y = y;
        notifyPositionChange();
        handleReboot();
    }

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

    private void notifyPositionChange() {
        for (RobotPositionChangeListener listener : listeners) {
            listener.onRobotPositionChange(this);
        }
    }

    public interface RobotPositionChangeListener {
        void onRobotPositionChange(Robot robot);
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
        notifyPositionChange();
    }

}