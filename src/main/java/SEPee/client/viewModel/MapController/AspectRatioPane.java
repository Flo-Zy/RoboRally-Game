package SEPee.client.viewModel.MapController;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class AspectRatioPane extends Pane {
    private final double ratio;
    private final Node content;

    public AspectRatioPane(Node content, double widthRatio, double heightRatio) {
        this.ratio = widthRatio / heightRatio;
        this.content = content;
        getChildren().add(content);
    }

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
