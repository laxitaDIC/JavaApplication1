package vspeech;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import org.controlsfx.control.RangeSlider;

// For creating Bottom icon bar
public class PlayerBar extends GridPane {

    public final PlayerButton commonPlayVB; //Common Play button
    public int frame_rate = 25; //default frame_rate
    public int val = 0;
    public PlayerControl studControl; // For left panel player control
    public PlayerControl refControl;  //For right panel player control
    public final PlayerButton commonPauseVB;  //Common Pause button
    public final PlayerButton commonRestartVB;  //Common Restart button
    public final ToggleGroup group;
    public final GridPane specialPane;
    public Analyzer studAnalyzer; // For left panel 
    public Analyzer refAnalyzer; // For right panel 
    public final TextField studMsg;
    public final TextField refMsg;

    public PlayerBar(Analyzer studAnalyzer, Analyzer refAnalyzer, double height, double width, PlayerControl student, PlayerControl reference) {

        studControl = student;
        refControl = reference;
        this.refAnalyzer = refAnalyzer;
        this.studAnalyzer = studAnalyzer;
        refAnalyzer.slider = new RangeSlider();
        studAnalyzer.slider = new RangeSlider();
        setStyle("-fx-background-color:" + ColorConstants.bottom + ";-fx-border-width: 0 3 5 3; -fx-border-color: " + ColorConstants.border + ";-fx-scale-x: 1;-fx-padding:-7 0 7 0;");
        Label SlowLabel = new Label("S");
        SlowLabel.setStyle("-fx-wrap-text:true; -fx-text-alignment: center; -fx-alignment: CENTER; -fx-font-size: 12pt; -fx-font-family: \"Segoe UI Semibold\"; -fx-text-fill: #FFFFFF;");
        SlowLabel.setPadding(new Insets(0, 0, 0, 0));

        Label FastLabel = new Label("F");
        FastLabel.setStyle("-fx-wrap-text:true; -fx-text-alignment: center; -fx-alignment: CENTER; -fx-font-size: 12pt; -fx-font-family: \"Segoe UI Semibold\"; -fx-text-fill: #FFFFFF;");
        FastLabel.setPadding(new Insets(0, 0, 0, 0));

        commonPlayVB = new PlayerButton("/images/play_11_grey.png", "/images/play_11.png", "L&R Animation (without Audio)");
        commonPlayVB.setDisable(true);
        commonPlayVB.setAlignment(Pos.CENTER);
        commonPlayVB.setMinWidth(LayoutConstants.fullWidth / 27);

        commonPauseVB = new PlayerButton("/images/pause13_grey.png", "/images/pause13.png", "L&R Animation Pause");
        commonPauseVB.setDisable(true);
        commonPauseVB.setAlignment(Pos.CENTER);
        commonPauseVB.setMinWidth(LayoutConstants.fullWidth / 27);

        commonRestartVB = new PlayerButton("/images/restart13_grey.png", "/images/restart13.png", "L&R Reset");
        commonRestartVB.setDisable(true);
        commonRestartVB.setAlignment(Pos.CENTER);
        commonRestartVB.setMinWidth(LayoutConstants.fullWidth / 27);

        group = new ToggleGroup();                                              //For creating radio buttons to select the animation scaling speed

        RadioButton rb1 = new RadioButton("20");
        rb1.setToggleGroup(group);
        rb1.setUserData("20");

        RadioButton rb2 = new RadioButton("10");
        rb2.setToggleGroup(group);
        rb2.setUserData("10");

        RadioButton rb3 = new RadioButton("5");
        rb3.setToggleGroup(group);
        rb3.setSelected(true);
        rb3.setUserData("5");

        RadioButton rb4 = new RadioButton("2");
        rb4.setToggleGroup(group);
        rb4.setUserData("2");

        RadioButton rb5 = new RadioButton("1");
        rb5.setToggleGroup(group);
        rb5.setUserData("1");

        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov,
                    Toggle old_toggle, Toggle new_toggle) {
                if (group.getSelectedToggle() != null) {
                    if (group.getSelectedToggle().getUserData().toString().equals("1")) {
                        frame_rate = 25;
                        val = 4;
                    }
                    if (group.getSelectedToggle().getUserData().toString().equals("2")) {
                        frame_rate = 25;
                        val = 4;
                    }
                    if (group.getSelectedToggle().getUserData().toString().equals("5")) {
                        frame_rate = 25;
                        val = 0;
                    }
                    if (group.getSelectedToggle().getUserData().toString().equals("10")) {
                        frame_rate = 50;
                        val = 0;
                    }
                    if (group.getSelectedToggle().getUserData().toString().equals("20")) {
                        frame_rate = 100;
                        val = 0;
                    }
                }
            }
        });

        HBox button_slider = new HBox();
        button_slider.setSpacing(30);

        button_slider.getChildren().add(rb1);
        button_slider.getChildren().add(rb2);
        button_slider.getChildren().add(rb3);
        button_slider.getChildren().add(rb4);
        button_slider.getChildren().add(rb5);

        GridPane commonPane = new GridPane();
        commonPane.setPrefWidth(width);
        commonPane.setHgap(10);

        studMsg = new TextField();
        refMsg = new TextField();

        studMsg.setFont(Font.font("Arial Unicode MS", FontWeight.BOLD, 20));
        refMsg.setFont(Font.font("Arial Unicode MS", FontWeight.BOLD, 20));

        studMsg.setMinWidth(LayoutConstants.fullWidth / 3.6);
        studMsg.setMinWidth(LayoutConstants.fullWidth / 3.6);
        studMsg.setMinHeight(37);
        studMsg.setMaxHeight(37);

        studMsg.setDisable(true);
        studMsg.setEditable(false);

        studMsg.setPadding(new Insets(0, 0, 0, 0));

        refMsg.setMinWidth(LayoutConstants.fullWidth / 3.6);
        refMsg.setMinWidth(LayoutConstants.fullWidth / 3.6);
        refMsg.setMinHeight(37);
        refMsg.setMaxHeight(37);
        refMsg.setEditable(false);

        refMsg.setDisable(true);

        commonPane.setAlignment(Pos.CENTER);
        commonPane.add(SlowLabel, 1, 0);
        commonPane.add(button_slider, 2, 0);
        commonPane.add(FastLabel, 3, 0);

        HBox msg = new HBox();
        msg.setPadding(new Insets(0, 5, 0, 5));
        msg.getChildren().addAll(studMsg, commonPane, refMsg);

        HBox common = new HBox();
        common.setSpacing(3);
        common.getChildren().addAll(commonPlayVB, commonRestartVB);
        specialPane = new GridPane();
        specialPane.setHgap(4);
        specialPane.setAlignment(Pos.CENTER);

        specialPane.setPrefWidth(width);
        specialPane.add(student, 0, 1);

        specialPane.add(common, 7, 1);

        specialPane.add(reference, 12, 1);

        setMaxHeight(75);
        setMinHeight(105);
        setAlignment(Pos.CENTER);

        add(msg, 0, 0);
        add(specialPane, 0, 1);
        commonPlayVB.setOnMouseClicked((MouseEvent e) -> {
            specialPane.getChildren().clear();

            common.getChildren().clear();
            common.getChildren().addAll(commonPauseVB, commonRestartVB);

            specialPane.add(student, 0, 1);

            specialPane.add(common, 7, 1);

            specialPane.add(reference, 12, 1);

            // For simultaneuos playing of both side animation
            if (studControl.timeline == null) {
                studControl.timeline = new Timeline();
                studControl.timeline.getKeyFrames().add(new KeyFrame(Duration.millis(frame_rate), ae -> studAnalyzer.play(val)));
                studControl.timeline.setCycleCount(Animation.INDEFINITE);
            }
            if (refControl.timeline == null) {
                refControl.timeline = new Timeline();
                refControl.timeline.getKeyFrames().add(new KeyFrame(Duration.millis(frame_rate), ae -> refAnalyzer.play(val)));
                refControl.timeline.setCycleCount(Animation.INDEFINITE);
            }

            studControl.timeline.playFromStart();
            refControl.timeline.playFromStart();

            studControl.PlayVB.setDisable(true);
            studControl.PauseVB.setDisable(true);
            studControl.RestartVB.setDisable(true);
            // studControl.SoundVB.setDisable(true);
            studControl.FlipVB.setDisable(true);
            studControl.EnergyVB.setDisable(true);
            studControl.PitchVB.setDisable(true);
            studControl.CircleVB.setDisable(true);
            studControl.BarVB.setDisable(true);
            studControl.WomanFaceVB.setDisable(true);
            studControl.ManFaceVB.setDisable(true);
            studControl.BoyFaceVB.setDisable(true);
            studControl.GirlFaceVB.setDisable(true);

            studControl.MessageVB.setDisable(true);

            refControl.PlayVB.setDisable(true);
            refControl.PauseVB.setDisable(true);
            refControl.RestartVB.setDisable(true);
            //refControl.SoundVB.setDisable(true);
            refControl.FlipVB.setDisable(true);
            refControl.EnergyVB.setDisable(true);
            refControl.PitchVB.setDisable(true);
            refControl.CircleVB.setDisable(true);
            refControl.BarVB.setDisable(true);
            refControl.WomanFaceVB.setDisable(true);
            refControl.ManFaceVB.setDisable(true);
            refControl.BoyFaceVB.setDisable(true);
            refControl.GirlFaceVB.setDisable(true);
            refControl.MessageVB.setDisable(true);

            commonRestartVB.setDisable(false);

        });
        commonPauseVB.setOnMouseClicked((MouseEvent e) -> {

            // For simultaneuos pausing of both side animation
            commonPlayVB.setDisable(false);
            commonRestartVB.setDisable(false);
            specialPane.getChildren().clear();
            common.getChildren().clear();
            common.getChildren().addAll(commonPlayVB, commonRestartVB);

            specialPane.add(student, 0, 1);

            specialPane.add(common, 7, 1);

            specialPane.add(reference, 12, 1);
            studControl.timeline.pause();
            refControl.timeline.pause();
        });

        commonRestartVB.setOnMouseClicked((MouseEvent e) -> {

            // For simultaneuos restting of both side animation
            specialPane.getChildren().clear();
            common.getChildren().clear();
            common.getChildren().addAll(commonPlayVB, commonRestartVB);

            specialPane.add(student, 0, 1);

            specialPane.add(common, 7, 1);

            specialPane.add(reference, 12, 1);
            studControl.analyzer.slider.setLowValue(0);
            studControl.analyzer.counter = 0;
            studControl.analyzer.play(studControl.frameVal);

            refControl.analyzer.slider.setLowValue(0);
            refControl.analyzer.counter = 0;
            refControl.analyzer.play(refControl.frameVal);

            if (studControl.timeline != null) {
                studControl.timeline.stop();
                studControl.analyzer.counter = 0;
                studControl.analyzer.play(studControl.frameVal);
                studControl.analyzer.counter--;
            }
            if (refControl.timeline != null) {
                refControl.timeline.stop();
                refControl.analyzer.counter = 0;
                refControl.analyzer.play(refControl.frameVal);
                refControl.analyzer.counter--;
            }
            studControl.PlayVB.setDisable(false);
            studControl.PauseVB.setDisable(false);
            studControl.RestartVB.setDisable(false);
            //  studControl.SoundVB.setDisable(false);
            studControl.FlipVB.setDisable(false);
            studControl.EnergyVB.setDisable(false);
            studControl.PitchVB.setDisable(false);
            studControl.CircleVB.setDisable(false);
            studControl.BarVB.setDisable(false);
            studControl.WomanFaceVB.setDisable(false);
            studControl.ManFaceVB.setDisable(false);
            studControl.BoyFaceVB.setDisable(false);
            studControl.GirlFaceVB.setDisable(false);
            studControl.MessageVB.setDisable(false);

            refControl.PlayVB.setDisable(false);
            refControl.PauseVB.setDisable(false);
            refControl.RestartVB.setDisable(false);
            //  refControl.SoundVB.setDisable(false);
            refControl.FlipVB.setDisable(false);
            refControl.EnergyVB.setDisable(false);
            refControl.PitchVB.setDisable(false);
            refControl.CircleVB.setDisable(false);
            refControl.BarVB.setDisable(false);
            refControl.WomanFaceVB.setDisable(false);
            refControl.ManFaceVB.setDisable(false);
            refControl.BoyFaceVB.setDisable(false);
            refControl.GirlFaceVB.setDisable(false);
            refControl.MessageVB.setDisable(false);

            commonPlayVB.setDisable(false);
            commonRestartVB.setDisable(false);
        });

    }

    public void enablePlayControls() {
        if (!studControl.PlayVB.isDisable() && !refControl.PlayVB.isDisable()) {
            commonPlayVB.setDisable(false);
            commonPauseVB.setDisable(false);
            commonRestartVB.setDisable(false);

        }
    }

    public void disablePlayControls() {
        commonPlayVB.setDisable(true);
        commonPauseVB.setDisable(true);
        commonRestartVB.setDisable(true);

    }

}
