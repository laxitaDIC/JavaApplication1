package vspeech;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

// For creating and adding functionality to either sides player controls
public final class PlayerControl extends GridPane {

    public final PlayerButton PitchVB;              //Pitch Scaling button
    public final PlayerButton EnergyVB;             // Energy scaling Button
    public final PlayerButton FlipVB;               // Flipping Button
    // public final PlayerButton SoundVB;              //Audio Video playing Button    
    public final PlayerButton RestartVB;            //Restarting the animation
    public final PlayerButton PauseVB;              //Pausing the animation
    public final PlayerButton PlayVB;               //Playing the animation
    public final PlayerButton CircleVB;             // Place of Articulation Button    
    public final PlayerButton BarVB;                //Energy and pitch Bar Button
    public final PlayerButton GirlFaceVB;               //Face changing Button
    public final PlayerButton WomanFaceVB;               //Face changing Button
    public final PlayerButton ManFaceVB;               //Face changing Button
    public final PlayerButton BoyFaceVB;               //Face changing Button

    public final PlayerButton MessageVB;            //Message Window Button
    public Timeline timeline;
    private PlayerBar playBar;
    public Analyzer analyzer;
    private boolean isLeft;
    public int frameVal;
    public double slider_old;
    public double previous_val;
    private AudioInputStream stream;
    private AudioFormat format;
    private DataLine.Info info;
    public Clip clip;
    private long clipTime;

    public PlayerControl(boolean isLeft, Analyzer parent) {
        analyzer = parent;
        setHgap(LayoutConstants.fullWidth / 300);
        char ch;
        if (isLeft) {
            ch = 'L';
        } else {
            ch = 'R';

        }
        PlayVB = new PlayerButton("/images/play_11_grey.png", "/images/play_11.png", ch + "-Animation");
        PauseVB = new PlayerButton("/images/pause13_grey.png", "/images/pause13.png", ch + "-Pause");
        RestartVB = new PlayerButton("/images/restart13_grey.png", "/images/restart13.png", ch + "-Reset");
        //   SoundVB = new PlayerButton("/images/sound13_grey.png", "/images/sound13.png");
        FlipVB = new PlayerButton("/images/flip13_grey.png", "/images/flip13.png", ch + "-Mirroring");
        EnergyVB = new PlayerButton("/images/escl13_grey.png", "/images/escl13.png", ch + "-Level Scale (20-601/20-80 dB)");
        PitchVB = new PlayerButton("/images/pscl13_grey.png", "/images/pscl13.png", ch + "-Pitch Scale (250/500 Hz)");
        CircleVB = new PlayerButton("/images/poa13_grey.png", "/images/poa13.png", ch + "-POA (on/off)");
        BarVB = new PlayerButton("/images/E_P_hori_grey.png", "/images/E_P_hori.png", ch + "-Pitch/Level (on/off)");
        GirlFaceVB = new PlayerButton("/images/face_34_grey.png", "/images/face_34.png", ch + "-Animation face");
        BoyFaceVB = new PlayerButton("/images/boy_face_grey.png", "/images/boy_face.png", ch + "-Animation face");
        ManFaceVB = new PlayerButton("/images/man_face_grey.png", "/images/man_face.png", ch + "-Animation face");
        WomanFaceVB = new PlayerButton("/images/woman_face_grey.png", "/images/woman_face.png", ch + "-Animation face");

        MessageVB = new PlayerButton("/images/msg_icon.png", "/images/msg_black.png", ch + "-Message");
        this.isLeft = isLeft;
        if (isLeft) {
            Analyzer.faceCount = 1;

            add(MessageVB, 0, 0);
            add(PitchVB, 1, 0);
            add(EnergyVB, 2, 0);
            add(BarVB, 4, 0);
            add(CircleVB, 5, 0);
            add(GirlFaceVB, 7, 0);
            add(FlipVB, 8, 0);
            //       add(SoundVB, 10, 0);
            add(PlayVB, 12, 0);
            add(RestartVB, 13, 0);
        } else {
            Analyzer.faceCount = 3;

            setAlignment(Pos.CENTER_RIGHT);
            add(MessageVB, 13, 0);
            add(PitchVB, 12, 0);
            add(EnergyVB, 11, 0);
            add(BarVB, 9, 0);
            add(CircleVB, 8, 0);
            add(WomanFaceVB, 6, 0);
            add(FlipVB, 5, 0);
            //    add(SoundVB, 3, 0);
            add(RestartVB, 1, 0);
            add(PlayVB, 0, 0);
        }
        EnergyVB.setOnMouseClicked((MouseEvent e) -> {
            try {
                parent.scaleEnergy();
            } catch (FileNotFoundException ex) {
                new newLogging("SEVERE", PlayerControl.class.getName(), "PlayerControl()", ex);
            }
        });
        PitchVB.setOnMouseClicked((MouseEvent e) -> {
            try {
                parent.scalePitch();
            } catch (FileNotFoundException ex) {
                new newLogging("SEVERE", PlayerControl.class.getName(), "PlayerControl()", ex);
            }
        });
        FlipVB.setOnMouseClicked((MouseEvent e) -> {
            parent.imgFlip();
        });

        ManFaceVB.setOnMouseClicked((MouseEvent e) -> {
            try {
                parent.imgFace();
            } catch (FileNotFoundException ex) {
                new newLogging("SEVERE", PlayerControl.class.getName(), "PalyerControl()", ex);
            }
        });
        WomanFaceVB.setOnMouseClicked((MouseEvent e) -> {
            try {
                parent.imgFace();
            } catch (FileNotFoundException ex) {
                new newLogging("SEVERE", PlayerControl.class.getName(), "PalyerControl()", ex);
            }
        });
        BoyFaceVB.setOnMouseClicked((MouseEvent e) -> {
            try {
                parent.imgFace();
            } catch (FileNotFoundException ex) {
                new newLogging("SEVERE", PlayerControl.class.getName(), "PalyerControl()", ex);
            }
        });
        GirlFaceVB.setOnMouseClicked((MouseEvent e) -> {
            try {
                parent.imgFace();
            } catch (FileNotFoundException ex) {
                new newLogging("SEVERE", PlayerControl.class.getName(), "PalyerControl()", ex);
            }
        });
        /*   SoundVB.setOnMouseClicked((MouseEvent e) -> {
            try {
                parent.sound();
            } catch (UnsupportedAudioFileException ex) {
                new newLogging("SEVERE", PlayerControl.class.getName(), "PlayerControl()", ex);
            } catch (IOException ex) {
                new newLogging("SEVERE", PlayerControl.class.getName(), "PlayerControl()", ex);
            } catch (LineUnavailableException ex) {
                new newLogging("SEVERE", PlayerControl.class.getName(), "PlayerControl()", ex);
            } catch (Exception ex) {
                new newLogging("SEVERE", PlayerControl.class.getName(), "PlayerControl()", ex);
            }
        });*/
        MessageVB.setOnMouseClicked((MouseEvent e) -> {
            try {
                parent.mesaagePane();
            } catch (Exception ex) {
                new newLogging("SEVERE", PlayerControl.class.getName(), "PlayerControl()", ex);
            }
        });
        disableAllControls();
    }

    public void disableAllControls() {
        PlayVB.setDisable(true);
        PauseVB.setDisable(true);
        RestartVB.setDisable(true);
        // SoundVB.setDisable(true);
        FlipVB.setDisable(true);
        EnergyVB.setDisable(true);
        PitchVB.setDisable(true);
        CircleVB.setDisable(true);
        BarVB.setDisable(true);
        GirlFaceVB.setDisable(true);
        WomanFaceVB.setDisable(true);
        ManFaceVB.setDisable(true);
        BoyFaceVB.setDisable(true);

        MessageVB.setDisable(true);
    }

    public void enableAnalysisControls() {
        EnergyVB.setDisable(false);
        PitchVB.setDisable(false);
        //SoundVB.setDisable(false);

    }

    public void disableAnalysisControls() {
        PitchVB.setDisable(true);
        EnergyVB.setDisable(true);
        //  SoundVB.setDisable(true);
    }

    public void enableAnimationControls() {
        PlayVB.setDisable(false);
        PauseVB.setDisable(false);
        RestartVB.setDisable(false);
        //   SoundVB.setDisable(false);
        FlipVB.setDisable(false);
        EnergyVB.setDisable(false);
        PitchVB.setDisable(false);
        CircleVB.setDisable(false);
        BarVB.setDisable(false);
        GirlFaceVB.setDisable(false);
        WomanFaceVB.setDisable(false);
        ManFaceVB.setDisable(false);
        BoyFaceVB.setDisable(false);
        MessageVB.setDisable(false);
    }

    public void disableAnimationControls() {
        PlayVB.setDisable(true);
        PauseVB.setDisable(true);
        RestartVB.setDisable(true);
        //  SoundVB.setDisable(true);
        FlipVB.setDisable(true);
        EnergyVB.setDisable(true);
        PitchVB.setDisable(true);
        CircleVB.setDisable(true);
        BarVB.setDisable(true);
        GirlFaceVB.setDisable(true);
        WomanFaceVB.setDisable(true);
        ManFaceVB.setDisable(true);
        BoyFaceVB.setDisable(true);
        MessageVB.setDisable(true);
    }

    public void playControl() {
        playBar = analyzer.getPlayBar();
        // For controlling frame change on animation slider controller 
        analyzer.slider.lowValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double low = newValue.doubleValue();
                double high = analyzer.slider.getHighValue();
                previous_val = 0;
                if (low + 0.1 > analyzer.slider.getHighValue()) {
                    analyzer.slider.setLowValue(high - 0.1);
                    slider_old = analyzer.slider.getLowValue();
                }
            }
        });
        analyzer.slider.highValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double high = newValue.doubleValue();
                double low = analyzer.slider.getLowValue();
                previous_val = 1;
                if (low + 0.1 > high) {
                    analyzer.slider.setHighValue(low + 0.1);
                }
            }
        });
        analyzer.slider.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (previous_val == 0) {
                    analyzer.counter = (analyzer.slider.getLowValue() * analyzer.objVisual.getMat_x().length) / (analyzer.objVisual.getDataWave_x().size() / 10000.0);
                    analyzer.play(frameVal);
                }
            }
        });
        analyzer.slider.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                ((Node) mouseEvent.getSource()).setCursor(Cursor.HAND);
                if (!analyzer.slider.highValueChangingProperty().getValue()) {
                    analyzer.counter = analyzer.slider.getLowValue() * (analyzer.objVisual.getMat_x().length / (analyzer.objVisual.getDataWave_x().size() / 10000.0));
                    analyzer.play(frameVal);
                }
                PlayVB.setDisable(false);
            }
        });
        analyzer.slider.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (analyzer.slider.highValueChangingProperty().getValue()) {
                    if (analyzer.slider.lowValueChangingProperty().getValue()) {
                        analyzer.counter = analyzer.slider.getLowValue() * (analyzer.objVisual.getMat_x().length / (analyzer.objVisual.getDataWave_x().size() / 10000.0));
                        analyzer.play(frameVal);
                    }
                }
            }
        });
        // For action when Play Button click 
        PlayVB.setOnMouseClicked((MouseEvent e) -> {
            try {
                playBar.commonPlayVB.setDisable(true);
                playBar.commonRestartVB.setDisable(true);
                RestartVB.setDisable(true);
                //  SoundVB.setDisable(true);
                FlipVB.setDisable(true);
                EnergyVB.setDisable(true);
                PitchVB.setDisable(true);
                CircleVB.setDisable(true);
                BarVB.setDisable(true);
                GirlFaceVB.setDisable(true);
                WomanFaceVB.setDisable(true);
                ManFaceVB.setDisable(true);
                BoyFaceVB.setDisable(true);
                MessageVB.setDisable(true);
                getChildren().clear();

                // Changing play button to pause button when play button is clicked
                if (isLeft) {
                    add(MessageVB, 0, 0);
                    add(PitchVB, 1, 0);
                    add(EnergyVB, 2, 0);
                    add(BarVB, 4, 0);
                    add(CircleVB, 5, 0);
                    if (analyzer.baseImg.getId().contains("men")) {
                        add(ManFaceVB, 7, 0);
                    }
                    if (analyzer.baseImg.getId().contains("women")) {
                        add(WomanFaceVB, 7, 0);
                    }
                    if (analyzer.baseImg.getId().contains("boy")) {
                        add(BoyFaceVB, 7, 0);
                    }
                    if (analyzer.baseImg.getId().contains("girl")) {
                        add(GirlFaceVB, 7, 0);
                    }
                    add(FlipVB, 8, 0);
                    //       add(SoundVB, 10, 0);
                    add(PauseVB, 12, 0);
                    add(RestartVB, 13, 0);
                } else {
                    setAlignment(Pos.CENTER_RIGHT);
                    add(MessageVB, 13, 0);
                    add(PitchVB, 12, 0);
                    add(EnergyVB, 11, 0);
                    add(BarVB, 9, 0);
                    add(CircleVB, 8, 0);
                    if (analyzer.baseImg.getId().contains("men")) {
                        add(ManFaceVB, 6, 0);
                    }
                    if (analyzer.baseImg.getId().contains("women")) {
                        add(WomanFaceVB, 6, 0);
                    }
                    if (analyzer.baseImg.getId().contains("boy")) {
                        add(BoyFaceVB, 6, 0);
                    }
                    if (analyzer.baseImg.getId().contains("girl")) {
                        add(GirlFaceVB, 6,0);
                    }
                    add(FlipVB, 5, 0);
                    //    add(SoundVB, 3, 0);
                    add(RestartVB, 1, 0);
                    add(PauseVB, 0, 0);
                }
                if (timeline != null) {
                    timeline.stop();
                }
                ArrayList<Double> temp = null;
                int selected_scale = 0;

                if (playBar.group.getSelectedToggle().getUserData().toString().equals("10")) {
                    frameVal = 0;
                    selected_scale = 10;
                    temp = analyzer.objVisual.getSoundWave_10();

                }
                if (playBar.group.getSelectedToggle().getUserData().toString().equals("20")) {
                    frameVal = 0;
                    temp = analyzer.objVisual.getSoundWave_20();
                    selected_scale = 20;
                }
                if (playBar.group.getSelectedToggle().getUserData().toString().equals("5")) {
                    frameVal = 0;
                    temp = analyzer.objVisual.getSoundWave_5();
                    selected_scale = 5;
                }
                if (playBar.group.getSelectedToggle().getUserData().toString().equals("2")) {
                    frameVal = 2;
                    temp = analyzer.objVisual.getSoundWave_2();
                    selected_scale = 2;
                }
                if (playBar.group.getSelectedToggle().getUserData().toString().equals("1")) {
                    frameVal = 4;
                    temp = analyzer.objVisual.getSoundWave_1();
                    selected_scale = 1;

                }

                int frame_length = analyzer.data_jaw_x.length;
                int audio_initial = (int) (frame_length * analyzer.slider.getLowValue() / (analyzer.waveform_x.size() / 10000.0));
                int i = 0;

                double[] tempSound = new double[temp.size()];
                i = (tempSound.length / frame_length) * audio_initial;

                long length = (long) (((frame_length * analyzer.slider.getHighValue() / (analyzer.waveform_x.size() / 10000.0)) - 20));

                length = length * (tempSound.length / frame_length);
                System.out.println("Length= " + length);

                System.out.println("Size= " + temp.size());

                for (; i < length; i++) {
                    tempSound[i] = temp.get(i);
                }
                File tempFile = new File("tempSound.wav");
                WavFile wavFile = WavFile.newWavFile(tempFile, 1, i, 16, 10000);
                wavFile.writeFrames(tempSound, i);
                wavFile.close();

                stream = AudioSystem.getAudioInputStream(tempFile);
                format = stream.getFormat();
                info = new DataLine.Info(Clip.class, format);
                clip = (Clip) AudioSystem.getLine(info);
                clip.open(stream);

                if (analyzer.counter == 0) {
                    clipTime = 0;
                }
                clipTime = (long) ((audio_initial) * (tempSound.length / frame_length) * 100);

                //When you want to resume the clip from the last position
                clip.setMicrosecondPosition(clipTime);

                timeline = new Timeline();
                timeline.getKeyFrames().add(new KeyFrame(Duration.millis(playBar.frame_rate), ae -> analyzer.play(frameVal)));
                timeline.setCycleCount(Animation.INDEFINITE);

                timeline.play();
                clip.start();
            } catch (Exception ex) {
                Logger.getLogger(PlayerControl.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        // For action when Pause Button click 
        PauseVB.setOnMouseClicked((MouseEvent e) -> {
            RestartVB.setDisable(false);
            FlipVB.setDisable(false);
            EnergyVB.setDisable(false);
            PitchVB.setDisable(false);
            CircleVB.setDisable(false);
            BarVB.setDisable(false);
            ManFaceVB.setDisable(false);
            WomanFaceVB.setDisable(false);
            BoyFaceVB.setDisable(false);
            GirlFaceVB.setDisable(false);
            MessageVB.setDisable(false);

            // Changing pause button to play button when play button is clicked
            getChildren().clear();
            if (isLeft) {
                add(MessageVB, 0, 0);
                add(PitchVB, 1, 0);
                add(EnergyVB, 2, 0);
                add(BarVB, 4, 0);
                add(CircleVB, 5, 0);
                if (analyzer.baseImg.getId().contains("men")) {
                    add(ManFaceVB, 7, 0);
                }
                if (analyzer.baseImg.getId().contains("women")) {
                    add(WomanFaceVB, 7, 0);
                }
                if (analyzer.baseImg.getId().contains("boy")) {
                    add(BoyFaceVB, 7, 0);
                }
                if (analyzer.baseImg.getId().contains("girl")) {
                    add(GirlFaceVB, 7, 0);
                }
                add(FlipVB, 8, 0);
                // add(SoundVB, 10, 0);
                add(PlayVB, 12, 0);
                add(RestartVB, 13, 0);

            } else {
                setAlignment(Pos.CENTER_RIGHT);
                add(MessageVB, 13, 0);
                add(PitchVB, 12, 0);
                add(EnergyVB, 11, 0);
                add(BarVB, 9, 0);
                add(CircleVB, 8, 0);
                if (analyzer.baseImg.getId().contains("men")) {
                    add(ManFaceVB, 6, 0);
                }
                if (analyzer.baseImg.getId().contains("women")) {
                    add(WomanFaceVB, 6, 0);
                }
                if (analyzer.baseImg.getId().contains("boy")) {
                    add(BoyFaceVB, 6, 0);
                }
                if (analyzer.baseImg.getId().contains("girl")) {
                    add(GirlFaceVB, 6, 0);
                }
                add(FlipVB, 5, 0);
                //  add(SoundVB, 3, 0);
                add(RestartVB, 1, 0);
                add(PlayVB, 0, 0);
            }
            clip.stop();
            timeline.pause();
            clipTime = clip.getMicrosecondPosition();
        });
        // For action when Restart Button click 

        RestartVB.setOnMouseClicked((MouseEvent e) -> {
            //    SoundVB.setDisable(false);
            FlipVB.setDisable(false);
            EnergyVB.setDisable(false);
            PitchVB.setDisable(false);
            CircleVB.setDisable(false);
            BarVB.setDisable(false);
            ManFaceVB.setDisable(false);
            WomanFaceVB.setDisable(false);
            BoyFaceVB.setDisable(false);
            GirlFaceVB.setDisable(false);
            MessageVB.setDisable(false);
            getChildren().clear();
            if (isLeft) {
                add(MessageVB, 0, 0);
                add(PitchVB, 1, 0);
                add(EnergyVB, 2, 0);
                add(BarVB, 4, 0);
                add(CircleVB, 5, 0);
                if (analyzer.baseImg.getId().contains("men")) {
                    add(ManFaceVB, 7, 0);
                }
                if (analyzer.baseImg.getId().contains("women")) {
                    add(WomanFaceVB, 7, 0);
                }
                if (analyzer.baseImg.getId().contains("boy")) {
                    add(BoyFaceVB, 7, 0);
                }
                if (analyzer.baseImg.getId().contains("girl")) {
                    add(GirlFaceVB, 7, 0);
                }
                add(FlipVB, 8, 0);
                // add(SoundVB, 10, 0);
                add(PlayVB, 12, 0);
                add(RestartVB, 13, 0);
            } else {
                setAlignment(Pos.CENTER_RIGHT);
                add(MessageVB, 13, 0);
                add(PitchVB, 12, 0);
                add(EnergyVB, 11, 0);
                add(BarVB, 9, 0);
                add(CircleVB, 8, 0);
                if (analyzer.baseImg.getId().contains("men")) {
                    add(ManFaceVB, 6, 0);
                }
                if (analyzer.baseImg.getId().contains("women")) {
                    add(WomanFaceVB, 6, 0);
                }
                if (analyzer.baseImg.getId().contains("boy")) {
                    add(BoyFaceVB, 6, 0);
                }
                if (analyzer.baseImg.getId().contains("girl")) {
                    add(GirlFaceVB, 6, 0);
                }
                add(FlipVB, 5, 0);
                // add(SoundVB, 3, 0);
                add(RestartVB, 1, 0);
                add(PlayVB, 0, 0);
            }
            analyzer.slider.setLowValue(0);
            analyzer.counter = 0;
            analyzer.play(frameVal);
            analyzer.counter--;
            if (timeline != null) {
                timeline.stop();
            }
            double studAnalyzer_low = playBar.studAnalyzer.slider.getLowValue();
            double studAnalyzer_high = playBar.studAnalyzer.slider.getHighValue();
            double refAnalyzer_low = playBar.refAnalyzer.slider.getLowValue();
            double refAnalyzer_high = playBar.refAnalyzer.slider.getHighValue();
            double diff = studAnalyzer_high - studAnalyzer_low;
            double mid = 2 * diff;
            double out = Math.ceil(mid);
            double stud_diff = out / 2.0;
            diff = refAnalyzer_high - refAnalyzer_low;
            mid = 2 * diff;
            out = Math.ceil(mid);
            double ref_diff = out / 2.0;

            double stud_wave = playBar.studAnalyzer.objVisual.getDataWave_x().size() / 10000.0;
            double ref_wave = playBar.refAnalyzer.objVisual.getDataWave_x().size() / 10000.0;

            if ((stud_diff == stud_wave) && (ref_diff == ref_wave)) {
                playBar.commonPlayVB.setDisable(false);
                playBar.commonRestartVB.setDisable(false);
            }
            if (playBar.studAnalyzer.isAnalysisDisplayed) {
                playBar.commonPlayVB.setDisable(true);
                playBar.commonRestartVB.setDisable(true);
            }
            if (playBar.refAnalyzer.isAnalysisDisplayed) {
                playBar.commonPlayVB.setDisable(true);
                playBar.commonRestartVB.setDisable(true);
            }

            enableAnimationControls();
        });
        CircleVB.setOnMouseClicked((MouseEvent e) -> {
            analyzer.circleDisplay();
        });
        BarVB.setOnMouseClicked((MouseEvent e) -> {
            analyzer.barDisplay();
        });
    }
}
