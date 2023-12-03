package SEPee.server.model.field;
import SEPee.server.model.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnergySpace extends Field{

    private int counter;
    public EnergySpace(Position position){
        super("EnergySpace", position);
    }
}
