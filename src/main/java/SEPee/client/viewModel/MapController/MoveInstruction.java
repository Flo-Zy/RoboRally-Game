package SEPee.client.viewModel.MapController;

class MoveInstruction {
    int clientId;
    String rotation;
    int newX;
    int newY;

    MoveInstruction(int clientId, String rotation) {
        this.clientId = clientId;
        this.rotation = rotation;
    }

    MoveInstruction(int clientId, int newX, int newY) {
        this.clientId = clientId;
        this.newX = newX;
        this.newY = newY;
    }
}
