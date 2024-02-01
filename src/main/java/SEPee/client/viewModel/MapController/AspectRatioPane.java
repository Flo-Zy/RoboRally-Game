package SEPee.client.viewModel.MapController;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * a Pane that maintains a specified aspect ratio for its child Node
 * it adjusts the child's size and position to preserve this ratio within the Pane's current dimensions
 * @author Florian
 */
public class AspectRatioPane extends Pane {
    private final double ratio;
    private final Node content;

    /**
     * constructs an AspectRatioPane with a child Node and a desired aspect ratio
     * @param content the child Node to be displayed and resized according to the aspect ratio
     * @param widthRatio the width component of the aspect ratio
     * @param heightRatio the height component of the aspect ratio, used with widthRatio to determine the aspect ratio
     */
    public AspectRatioPane(Node content, double widthRatio, double heightRatio) {
        this.ratio = widthRatio / heightRatio;
        this.content = content;
        getChildren().add(content);
    }

    /**
     * adjusts the size and position of the child Node to maintain the aspect ratio within the Pane's current size
     */
    @Override
    protected void layoutChildren() {
        double width = getWidth();
        double height = getHeight();
        double actualRatio = width / height;
        double scale;

        if (actualRatio >= ratio) {
            scale = height * ratio;
            content.setLayoutX((width - scale) / 2);
            content.setLayoutY(0);
        } else {
            scale = width / ratio;
            content.setLayoutX(0);
            content.setLayoutY((height - scale) / 2);
        }

        content.resize(scale, scale / ratio);
    }
}
