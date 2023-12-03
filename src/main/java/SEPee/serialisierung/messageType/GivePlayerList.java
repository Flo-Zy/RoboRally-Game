package SEPee.serialisierung.messageType;

import SEPee.server.model.Player;

import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class GivePlayerList extends Message{

    private GivePlayerList.GivePlayerListBody messageBody;

    public GivePlayerList(ArrayList<Player> playerList) {
        super("GivePlayerList");
        this.messageBody = new GivePlayerList.GivePlayerListBody(playerList);
    }

    /*
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

     */

    public GivePlayerList.GivePlayerListBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(GivePlayerList.GivePlayerListBody messageBody) {
        this.messageBody = messageBody;
    }




    @Getter
    @Setter
    public static class GivePlayerListBody {
        private ArrayList<Player> playerList;

        public GivePlayerListBody(ArrayList<Player> playerList) {
            this.playerList = playerList;
        }


    }

}
