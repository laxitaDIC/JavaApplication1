package vspeech;

import javafx.scene.layout.GridPane;


// For creating vertical menu bar

public class MenuBar extends GridPane {

    private final paneButton resetFileVB;
    private final paneButton LoadFileVB;
    private final paneButton AnalysisVB;
    private final paneButton AnimationHB;
    Analyzer parent;
  

    public MenuBar(Analyzer parent, boolean isLeft) {
        resetFileVB = new paneButton("Clear", "Clear Signal", "/images/clear_icon_black.png", "/images/clear_icon_black.png", isLeft, parent);
        LoadFileVB = new paneButton("Signal", "Signal Acquisition", "/images/wave.png", "/images/wave.png", isLeft, parent);
        AnalysisVB = new paneButton("Analysis", "Signal Analysis", "/images/analysis_icon_black.png", "/images/analysis_icon_grey.png", isLeft, parent);
        AnimationHB = new paneButton("Animation", "VT Animation", "/images/animation_black.png", "/images/animation_icon.png", isLeft, parent);
  
        this.parent = parent;
        if (isLeft) {
            setStyle("-fx-background-color:" + ColorConstants.menuBar + "; -fx-border-width: 0 0 5 3; -fx-border-color: " + ColorConstants.border + ";-fx-padding: 30 0 0 10;");
        } else {
            setStyle("-fx-background-color:" + ColorConstants.menuBar + ";-fx-border-width: 0 3 5 0; -fx-border-color: " + ColorConstants.border + ";-fx-padding: 30 0 0 10;");
        }
        setMinWidth(LayoutConstants.fullWidth / 20);
        setPrefHeight(LayoutConstants.fullHeight);

        setVgap(LayoutConstants.fullHeight / 35);

        add(resetFileVB, 0, 0);
        add(LoadFileVB, 0, 1);
        add(AnalysisVB, 0, 2);
        add(AnimationHB, 0, 3);
    

        AnimationHB.setDisable(true);
        AnalysisVB.setDisable(true);

    }

   

    public void disableAnimation() {
        AnimationHB.setDisable(true);
    }

    public void enableAnimation() {

        AnimationHB.setDisable(false);

    }

    public void disableAnalysis() {
        AnalysisVB.setDisable(true);
    }

    public void enableAnalysis() {
        AnalysisVB.setDisable(false);
    }
}
