package vspeech;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.controlsfx.control.RangeSlider;
import sounds.Playback;
import sounds.Waveform;

//Signal Acqusition Window 
public class WaveLoader {

    private final static int sampleRate = 10000;

    private final Stage pStage;
    private final Analyzer pAnalyzer;
    private BorderPane root;
    private Scene scene1;
    private VBox chartBox;
    private GridPane grid;
    private File file;
    private VBox rootContainer;
    private Playback sound;
    private ProgressBar progressBar;  // Progressbar indicating the progrss of record audio signal 
    private LineChart<Number, Number> Audiochart; // Waveform showing loaded or recorded signal
    private double duration; //Duration of recording
    private Label TitleLbl;
    private Label FileName;
    private Label processBox;
    private String sName;
    private String sAge;
    private final TextField nameTxt;
    private final TextField ageTxt;
    private String temp;
    private HBox speakerInfo;
    private HBox processButton;
    private VBox zoomBox;
    private LineChart<Number, Number> Zoomchart; // Waveform showing selected portion of audio siganl
    private Pane chartHolder;
    private NumberAxis xAxis;
    private NumberAxis yAxis;
    private Line line1;
    private Line line2;
    public static File soundFile;
    private File savefile;
    private Label HeadLbl;
    private String date_time;
    Analyzer parentAnalyzer;
    private Label lowlabel;
    private Label highlabel;
    private HBox buttons;
    private RangeSlider slider;
    private Button Start;
    private Button Stop;
    private Button Load;
    private Button Record;
    private Button Proceed;
    private Button Another;
    private Button Play;
    private Button Save;
    private Button Zoom;
    private Button Reset;
    private Timeline timeline;
    private int active = 0;
    public Stage stage;
    private double max_signal = 4.8; //maximum signal length for processing
    private HBox slider_cont;
    private int width = 650;
    public Vspeech vsts;
    private Button Play_before;
    private File soundBefore;
    private int initialSound = 0;
    private HBox Record_root;
    public static int count_text = 0;
    private ProgressIndicatorBar bar;
    private Label stamp;
    private Label start_time;
    private Label end_time;
    private Label diff_time;
    private StackPane record_bar;
    private Slider record_slider;
    private Label date_stamp;
    private Label fileSave;
    private Label name;
    private Label age;
    private double time;
    public TextField message;
    public byte initial_plot;

    /*
        Input: 
         ParentScreen as parentSatge
         Analyzer main content window 
     */
    public WaveLoader(Stage parentStage, Analyzer parentAnalyzer) {
        pStage = parentStage;
        pAnalyzer = parentAnalyzer;
        sName = "XXX";
        sAge = "00";
        nameTxt = new TextField("XXX");
        ageTxt = new TextField() {
            public void replaceText(int start, int end, String text) {
                if (!text.matches("[a-z]")) {
                    super.replaceText(start, end, text);
                }
            }

            public void replaceSelection(String text) {
                if (!text.matches("[a-z]")) {
                    super.replaceSelection(text);
                }
            }
        };
        int maxLength = 2;
        ageTxt.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (ageTxt.getText().length() > maxLength) {
                    String s = ageTxt.getText().substring(0, maxLength);
                    ageTxt.setText(s);
                }
            }
        });
        ageTxt.setText("00");
        ageTxt.setMaxWidth(30);
        nameTxt.setMaxWidth(130);
        this.parentAnalyzer = parentAnalyzer;
        vsts = new Vspeech();

    }

    public File load(boolean isLeft) {
        try {
            slider = new RangeSlider(0, 10, 0, 4.8);
            slider.setId("cursor2");
            sound = new Playback();
            speakerInfo = new HBox();
            chartBox = new VBox();
            lowlabel = new Label("0.0");
            lowlabel.setPrefWidth(30);
            highlabel = new Label(String.valueOf(max_signal));
            highlabel.setPrefWidth(30);
            lowlabel.setStyle("-fx-wrap-text:true; -fx-text-alignment: center; -fx-alignment: CENTER; -fx-font-size: 8pt; -fx-font-family: \"Segoe UI Semibold\"; -fx-text-fill: #FFFFFF    ;");
            highlabel.setStyle("-fx-wrap-text:true; -fx-text-alignment: center; -fx-alignment: CENTER; -fx-font-size: 8pt; -fx-font-family: \"Segoe UI Semibold\"; -fx-text-fill: #FFFFFF    ;");

            stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(pStage);
            root = new BorderPane();
            scene1 = new Scene(root, width, 680, Color.rgb(87, 87, 87));

            if (pAnalyzer.isLeft) {
                stage.setY(pAnalyzer.content.getMaxHeight() / 40);
                stage.setX(pAnalyzer.content.getMaxWidth() / 3.3);
            } else {
                stage.setY(pAnalyzer.content.getMaxHeight() / 40);
                stage.setX(pAnalyzer.content.getMaxWidth() * 0.65);

            }
            scene1.getStylesheets().add(WaveLoader.class.getResource("/css/style.css").toExternalForm());
            chartBox = new VBox();
            zoomBox = new VBox();

            chartBox.setPadding(new Insets(10, 0, 0, -25));
            chartBox.getChildren().add(new Label());

            zoomBox.setPadding(new Insets(0, 0, -30, -25));
            zoomBox.getChildren().add(new Label());

            grid = new GridPane();
            grid.setPadding(new Insets(10, 0, 0, 0));

            StackPane layout = new StackPane();

            layout.setStyle("-fx-background-color:rgba(87,87,87,1);; -fx-padding: 10;");
            rootContainer = new VBox();

            HeadLbl = new Label();
            HeadLbl.setStyle("-fx-wrap-text:true; -fx-text-alignment: center; -fx-alignment: CENTER; -fx-font-size: 12pt; -fx-font-family: \"Segoe UI Semibold\"; -fx-text-fill: #FFFFFF   ;-fx-padding: 2 2 0 0;");

            TitleLbl = new Label();

            TitleLbl.setStyle("-fx-font-weight:bold;-fx-wrap-text:true; -fx-text-alignment: center; -fx-alignment: CENTER; -fx-font-size: 8pt; -fx-font-family: \"Segoe UI bold\"; -fx-text-fill: #FFFFFF   ;-fx-padding: 0 5 0 5;");
            TitleLbl.setVisible(false);
            FileName = new Label();

            FileName.setStyle("-fx-font-weight:bold;-fx-wrap-text:true; -fx-text-alignment: center; -fx-alignment: CENTER; -fx-font-size: 8pt; -fx-font-family: \"Segoe UI bold\"; -fx-text-fill: #FFFFFF   ;-fx-padding: 0  0 0;");
            FileName.setVisible(false);

            fileSave = new Label("Save as File Name:");
            fileSave.setStyle("-fx-padding: 5 0 0 0;-fx-wrap-text:true; -fx-text-alignment: center; -fx-alignment: CENTER; -fx-font-size: 10pt; -fx-font-family: \"Segoe UI Semibold\"; -fx-text-fill: #FFFFFF    ;");
            name = new Label("Speaker Name");
            name.setStyle("-fx-padding: 5 0 0 -6;-fx-wrap-text:true; -fx-text-alignment: center; -fx-alignment: CENTER; -fx-font-size: 10pt; -fx-font-family: \"Segoe UI Semibold\"; -fx-text-fill: #FFFFFF    ;");

            age = new Label("Age");
            age.setStyle("-fx-padding: 5 0 0 0;-fx-wrap-text:true; -fx-text-alignment: center; -fx-alignment: CENTER; -fx-font-size: 10pt; -fx-font-family: \"Segoe UI Semibold\"; -fx-text-fill: #FFFFFF    ;");

            Date date = new Date();
            String month = date.toString();
            StringTokenizer token = new StringTokenizer(month, " ");
            token.nextToken();

            DecimalFormat mFormat = new DecimalFormat("00");
            DateFormat df = new SimpleDateFormat("ddMMyy");

            date_time = df.format(new Date()) + "_" + mFormat.format(Double.valueOf(date.getHours())) + "" + mFormat.format(Double.valueOf(date.getMinutes())) + "" + mFormat.format(Double.valueOf(date.getSeconds()));
            date_stamp = new Label("");

            date_stamp.setText(date_time);
            date_stamp.setStyle("-fx-padding: 5 0 0 0;-fx-wrap-text:true; -fx-text-alignment: center; -fx-alignment: CENTER; -fx-font-size: 8pt; -fx-font-family: \"Segoe UI Semibold\"; -fx-text-fill: #FFFFFF    ;");

            message = new TextField();
            message.setMaxWidth(60);
            message.setEditable(false);

            HBox fileInfo = new HBox();
            fileInfo.getChildren().addAll(TitleLbl, FileName);
            fileInfo.setPadding(new Insets(15, 0, 0, 30));

            speakerInfo.setVisible(false);
            speakerInfo.setSpacing(12);
            speakerInfo.setPadding(new Insets(20, 0, 0, 35));
            speakerInfo.getChildren().addAll(fileSave, name, nameTxt, age, ageTxt, date_stamp);

            ImageView imageView = new ImageView(new Image("/images/browse.png"));

            Load = new Button("Load", imageView);
            Load.setContentDisplay(ContentDisplay.TOP);
            Load.setPrefHeight(40);
            Load.setPrefWidth(150);

            imageView = new ImageView(new Image("/images/record.png"));
            Record = new Button("Record", imageView);
            Record.setContentDisplay(ContentDisplay.TOP);
            Record.setPrefHeight(40);
            Record.setPrefWidth(150);

            imageView = new ImageView(new Image("/images/proceed.png"));
            Proceed = new Button("Proceed", imageView);
            Proceed.setContentDisplay(ContentDisplay.TOP);
            Proceed.setPrefHeight(40);
            Proceed.setPrefWidth(150);

            Another = new Button("Another");
            imageView = new ImageView(new Image("/images/sound.png"));

            Play_before = new Button("Play", imageView);
            Play_before.setContentDisplay(ContentDisplay.TOP);
            Play_before.setPrefHeight(60);
            Play_before.setPrefWidth(80);

            imageView = new ImageView(new Image("/images/sound.png"));
            Play = new Button("Play", imageView);
            Play.setContentDisplay(ContentDisplay.TOP);
            Play.setPrefHeight(40);
            Play.setPrefWidth(150);

            imageView = new ImageView(new Image("/images/save.png"));

            Save = new Button("Save", imageView);
            Save.setContentDisplay(ContentDisplay.TOP);
            Save.setPrefHeight(40);
            Save.setPrefWidth(150);

            imageView = new ImageView(new Image("/images/select.png"));
            Zoom = new Button("Select", imageView);
            Zoom.setContentDisplay(ContentDisplay.TOP);
            Zoom.setPrefHeight(40);
            Zoom.setPrefWidth(150);

            imageView = new ImageView(new Image("/images/reset_zoom.png"));
            Reset = new Button("Reset", imageView);
            Reset.setContentDisplay(ContentDisplay.TOP);
            Reset.setPrefHeight(40);
            Reset.setPrefWidth(150);

            // Play_before.setPadding(new Insets(-12, 100, 0, 0));
            processButton = new HBox();

            processButton.setSpacing(60);

            imageView = new ImageView(new Image("/images/start.png"));
            Start = new Button("Start", imageView);
            Start.setPrefHeight(60);
            Start.setPrefWidth(80);
            Start.setContentDisplay(ContentDisplay.TOP);

            imageView = new ImageView(new Image("/images/stop.png"));

            Stop = new Button("Stop", imageView);
            Stop.setPrefHeight(60);

            Stop.setPrefWidth(80);
            Stop.setContentDisplay(ContentDisplay.TOP);

            imageView = new ImageView(new Image("/images/text.png"));

            final int TOTAL_TIME = 10;
            final String TIME_ELAPSED_LABEL_FORMAT = "%.0f";

            final ReadOnlyDoubleWrapper timeElapsed = new ReadOnlyDoubleWrapper();

            bar = new ProgressIndicatorBar(timeElapsed.getReadOnlyProperty(),
                    TOTAL_TIME, TIME_ELAPSED_LABEL_FORMAT);
            progressBar = new ProgressBar();
            progressBar.setMaxWidth(width - 150);
            progressBar.setMinWidth(width - 150);
            progressBar.setPadding(new Insets(10, 0, 0, 50));
            progressBar.setProgress(0);

            record_bar = new StackPane();
            record_slider = new Slider(0, 9.9, 0);
            record_slider.setShowTickMarks(true);
            record_slider.setMinorTickCount(4);
            record_slider.setMajorTickUnit(3.3);

            record_bar.getChildren().addAll(record_slider, bar);

            Record_root = new HBox();
            Record_root.setPadding(new Insets(5, 0, 0, 0));
            //root.setHgap(10);
            stamp = new Label("");
            stamp.setStyle("-fx-wrap-text:true; -fx-text-alignment: center; -fx-alignment: CENTER; -fx-font-size: 10pt; -fx-font-family: \"Segoe UI Semibold\"; -fx-text-fill: #FFFFFF    ;");

            Record_root.getChildren().addAll(record_bar);
            buttons = new HBox();

            buttons.setPadding(new Insets(0, 0, 0, 60));
            buttons.setSpacing(20);
            buttons.getChildren().addAll(Start, Stop, Play_before);
            buttons.setVisible(true);
            root.setDisable(true);
            root.setVisible(false);
            processButton.setPadding(new Insets(30, 0, 0, 40));

            processButton.getChildren().addAll(Zoom, Play, Reset, Save, Proceed);

            slider_cont = new HBox();
            slider_cont.getChildren().addAll(slider);
//            slider_cont.getChildren().addAll(lowlabel, slider, highlabel);

            slider_cont.setPadding(new Insets(140, 0, 0, 50));
            initialGraph();

            HBox displayInfo = new HBox();
            displayInfo.setMaxHeight(30);
            displayInfo.setMinHeight(30);
            displayInfo.setSpacing(15);
            displayInfo.setPadding(new Insets(10, 0, 0, 0));

            //HBox.setMargin(displayInfo, new Insets(40, 0, 0, 0));
            HBox initialButton = new HBox();
            initialButton.getChildren().addAll(Load, Record, buttons);
            initialButton.setSpacing(35);
            initialButton.setPadding(new Insets(-2, 0, 0, 40));
            StackPane chart_stack = new StackPane();
            chart_stack.getChildren().addAll(chartBox, slider_cont);

            HBox new_Label = new HBox();
            new_Label.setPadding(new Insets(0, 0, 0, 50));
            new_Label.setSpacing(100);
            start_time = new Label("Start Time:\t\t");

            start_time.setStyle("-fx-wrap-text:true; -fx-text-alignment: center; -fx-alignment: CENTER; -fx-font-size: 10pt; -fx-font-family: \"Segoe UI Semibold\"; -fx-text-fill: #FFFFFF    ;");

            end_time = new Label("End Time:\t\t");
            end_time.setStyle("-fx-wrap-text:true; -fx-text-alignment: center; -fx-alignment: CENTER; -fx-font-size: 10pt; -fx-font-family: \"Segoe UI Semibold\"; -fx-text-fill: #FFFFFF    ;");

            diff_time = new Label("Selected Duration: ");
            diff_time.setStyle("-fx-wrap-text:true; -fx-text-alignment: center; -fx-alignment: CENTER; -fx-font-size: 10pt; -fx-font-family: \"Segoe UI Semibold\"; -fx-text-fill: #FFFFFF    ;");

            new_Label.getChildren().addAll(start_time, end_time, diff_time);
            rootContainer.getChildren().addAll(initialButton, displayInfo, chart_stack, new_Label, zoomBox, processButton, speakerInfo, fileInfo);
            layout.getChildren().add(rootContainer);
            // Load.setDisable(true);
            // Record.setDisable(true);

            Zoom.setDisable(true);
            Save.setDisable(true);
            Proceed.setDisable(true);
            Play.setDisable(true);
            Reset.setDisable(true);
            //  slider.setDisable(true);
            Start.setDisable(true);
            Stop.setDisable(true);
            Play_before.setDisable(true);

            // For Loading Pre-recorded sound files
            Load.setOnMouseClicked((MouseEvent e) -> {
                try {
                    //buttons.setVisible(false);
                    FileChooser fileChooser = new FileChooser();
                    FileChooser.ExtensionFilter extFilterWAV = new FileChooser.ExtensionFilter("WAV files (*.wav)", "*.wav");
                    fileChooser.getExtensionFilters().addAll(extFilterWAV);
                    fileChooser.setTitle("Select an audio file");
                    File defaultDirectory = new File("C:/VSTS");
                    fileChooser.setInitialDirectory(defaultDirectory);
                    File file_temp = fileChooser.showOpenDialog(stage);
                    if (file_temp != null) {
                        file = file_temp;
                    }
                    if (file_temp == null) {
                        file = null;
                    }
                    if (file != null) {
                        Start.setDisable(true);
                        Stop.setDisable(true);
                        displayInfo.setPadding(new Insets(10, 0, 0, -15));
                        Play_before.setDisable(false);
                        initialSound = 0;
                        initialGraph();
                        parentAnalyzer.objVisual.setReffile(file);
                        HeadLbl.setText("\t\tLoaded from: " + file.getName());
                        signalAcquring_end();
                        // processButton.setVisible(true);
                        // processButton.setDisable(false);
                        Zoom.setDisable(true);
                        Save.setDisable(true);
                        Proceed.setDisable(true);
                        Reset.setDisable(true);
                        Play.setDisable(true);
                        displayInfo.getChildren().clear();
                        displayInfo.getChildren().addAll(HeadLbl);
                        active = 1;
                        message.setEditable(true);
                        message.setText("");
                        speakerInfo.getChildren().clear();
                        speakerInfo.getChildren().addAll(fileSave, name, nameTxt, age, ageTxt, date_stamp);
                        // buttons.setDisable(true);
                        // buttons.setVisible(false);
                        if (initialSound == 0) {
                            file_generation(0);
                            initialSound++;
                        }
                        sound(soundBefore);
                    }
                } catch (Exception ex) {
                    new newLogging("SEVERE", WaveLoader.class.getName(), "load()", ex);
                }

            });

            // For Recording audio signal
            /*Record.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        Play_before.setVisible(false);
                        slider = new RangeSlider(0, 10, 0, 4.8);
                        slider_cont.getChildren().clear();
                        slider_cont.getChildren().addAll(lowlabel, slider, highlabel);
                        initialGraph();
                        lowlabel.setText("0");
                        highlabel.setText("4.8");
                        buttons.setDisable(false);
                        buttons.setVisible(true);
                        Start.setDisable(false);
                        Stop.setDisable(true);
                        progressBar.setProgress(0);
                        root.setDisable(false);
                        root.setVisible(true);
                        file = new File("save.wav");
                        displayInfo.getChildren().clear();
                        displayInfo.setMaxHeight(30);
                        displayInfo.setMinHeight(30);
                        VBox.setMargin(displayInfo, new Insets(10, 0, 0, 0));

                        VBox sliderBox = new VBox();
                        sliderBox.getChildren().add(Record_root);
                        sliderBox.setPadding(new Insets(10, 0, 0, 0));
                        displayInfo.getChildren().addAll(progressBar, Play_before);
                        slider.setDisable(false);
                        processButton.setVisible(true);
                        processButton.setDisable(false);
                        timeline = new Timeline(
                                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                                new KeyFrame(Duration.seconds(10.4), e -> {
                                    recordEnd();
                                }, new KeyValue(progressBar.progressProperty(), 1))
                        );

                    } catch (Exception ex) {
                        new newLogging("SEVERE", WaveLoader.class.getName(), "record()", ex);
                    }
                }
            });*/
            Record.setOnMouseClicked((MouseEvent e1) -> {

                try {
                    Reset.setDisable(true);
                    buttons.setVisible(true);
                    count_text = 0;
                    Play_before.setVisible(true);
                    slider = new RangeSlider(0, 10, 0, 4.8);
                    slider.setId("cursor2");
                    slider_cont.getChildren().clear();
                    slider_cont.getChildren().addAll(slider);
                    initialGraph();
                    lowlabel.setText("0");
                    highlabel.setText("4.8");
                    Start.setDisable(false);
                    Stop.setDisable(true);
                    progressBar.setProgress(0);
                    root.setDisable(false);
                    root.setVisible(true);
                    file = new File("save.wav");
                    displayInfo.getChildren().clear();
                    displayInfo.setMaxHeight(30);
                    displayInfo.setMinHeight(30);
                    displayInfo.setPadding(new Insets(5, 0, 0, 0));

                    //HBox.setMargin(displayInfo, new Insets(0, 0, 0, 0));
                    bar = new ProgressIndicatorBar(timeElapsed.getReadOnlyProperty(),
                            TOTAL_TIME, TIME_ELAPSED_LABEL_FORMAT);

                    //bar.setMaxWidth(width - 150);
                    // bar.setMinWidth(width - 150);
                    bar.setPadding(new Insets(10, 0, 0, 15));
                    VBox sliderBox = new VBox();
                    sliderBox.getChildren().add(Record_root);
                    sliderBox.setPadding(new Insets(10, 0, 0, 0));
                    record_bar.getChildren().clear();
                    record_slider.setMaxWidth(width - 33);
                    record_slider.setMinWidth(width - 33);
                    record_slider.setPadding(new Insets(23, 0, 0, 46));
                    record_slider.setShowTickMarks(true);
                    record_slider.setMinorTickCount(4);
                    record_slider.setMajorTickUnit(3.3);
                    bar.setPadding(new Insets(0, 0, 0, 48));

                    record_bar.getChildren().addAll(record_slider, bar);

                    displayInfo.getChildren().addAll(record_bar);
                    slider.setDisable(false);
                    // processButton.setVisible(true);
                    //  processButton.setDisable(false);
                    /*   timeline = new Timeline(
                                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                                new KeyFrame(Duration.seconds(10.4), e -> {
                                    recordEnd();
                                }, new KeyValue(progressBar.progressProperty(), 1))
                        );*/
                    timeline = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(timeElapsed, TOTAL_TIME)), new KeyFrame(Duration.seconds(10.31), e -> {
                        recordEnd();
                    }, new KeyValue(timeElapsed, 0)));
                    start_time.setText("Start Time:    " + "\t");
                    end_time.setText("\tEnd Time:  " + "\t");
                    diff_time.setText("\tSelected Duration:  " + "");

                } catch (Exception ex) {
                    new newLogging("SEVERE", WaveLoader.class.getName(), "record()", ex);
                }

            });

            Start.setOnMouseClicked((MouseEvent e) -> {
                sound.start();
                Stop.setDisable(false);
                Start.setDisable(true);
                Start.setDisable(true);
                timeline.setCycleCount(Animation.INDEFINITE);
                timeline.play();
                active = 1;
            });

            Stop.setOnMouseClicked((MouseEvent e) -> {
                try {
                    recordEnd();
                } catch (Exception ex) {
                    new newLogging("SEVERE", WaveLoader.class.getName(), "stop()", ex);
                }
            });

            // Sending selected audio segmaent for processing
            Proceed.setOnMouseClicked((MouseEvent e) -> {
                try {
                    /*if (pStage.isFullScreen()) {
                             stage.close();
                        pStage.toBack();
                    }*/
                    if (lowlabel.getText().equals(highlabel.getText())) {
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Information Dialog");
                        alert.setHeaderText(null);
                        alert.setContentText("No signal selection!!!");
                        alert.show();
                    } else {
                        if (!pStage.isFullScreen()) {
                            stage.close();
                        }
                        save(1);
                        pAnalyzer.analysisFile = soundFile;
                        pAnalyzer.fileAnalysis();
                        file = null;
                        if (pStage.isFullScreen()) {
                            stage.close();
                        }
                    }
                } catch (Exception ex) {
                    stage.close();
                    new newLogging("SEVERE", WaveLoader.class.getName(), "load()", ex);
                }
            });
            Play.setOnMouseClicked((MouseEvent e) -> {
                try {
                    sound(soundFile);
                    Save.setDisable(false);
                    Proceed.setDisable(false);
                    speakerInfo.setVisible(true);
                } catch (Exception ex) {
                    new newLogging("SEVERE", WaveLoader.class.getName(), "load()", ex);
                }
            });
            Play_before.setOnMouseClicked((MouseEvent e) -> {
                try {
                    sound(soundBefore);
                } catch (Exception ex) {
                    new newLogging("SEVERE", WaveLoader.class.getName(), "Play_before()", ex);
                }
            });
            // saving the selected audio segment on local disk
            Save.setOnMouseClicked((MouseEvent e) -> {
                try {
                    TitleLbl.setVisible(true);
                    FileName.setVisible(true);
                    save(0);
                    sName = nameTxt.getText().toString();
                    sAge = ageTxt.getText().toString();
                    temp = "";
                    if (temp.equals("")) {
                        temp = sName + "" + sAge + "_" + message.getText().toString() + "_" + date_time;
                    }
                    if (vsts.jar) {
                        savefile = new File(NewView.path + "/" + temp + ".wav");
                    } else {
                        savefile = new File(temp + ".wav");
                    }
                    savefile.createNewFile();
                    FileInputStream instream = new FileInputStream(soundFile);
                    FileOutputStream outstream = new FileOutputStream(savefile);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = instream.read(buffer)) > 0) {
                        outstream.write(buffer, 0, length);
                    }
                    instream.close();
                    outstream.close();
                    TitleLbl.setText("Saved as :");
                    FileName.setText(temp + ".wav");
                    Save.setDisable(true);
                    fileInfo.setVisible(true);
                } catch (Exception ex) {
                    new newLogging("SEVERE", WaveLoader.class.getName(), "load()", ex);
                }
            });

            // For selecting the particular audio segment
            Zoom.setOnMouseClicked((MouseEvent e) -> {
                try {
                    zoomAction();
                    speakerInfo.setVisible(true);
                    Save.setDisable(false);
                    Reset.setDisable(false);
                    Play.setDisable(false);
                    Proceed.setDisable(false);
                } catch (Exception ex) {
                    new newLogging("SEVERE", WaveLoader.class.getName(), "load()", ex);
                }
            });
            Reset.setOnMouseClicked((MouseEvent e) -> {
                signalReset_end();
                speakerInfo.setVisible(false);
                fileInfo.setVisible(false);
                Zoom.setDisable(false);
                Save.setDisable(false);
                Proceed.setDisable(false);
                Reset.setDisable(false);
            });
            stage.setOnCloseRequest((WindowEvent we) -> {
                if (timeline != null) {
                    timeline.stop();
                    sound.stop();
                }
            });
            if (isLeft) {
                stage.setTitle("Signal Acquistion (User1)");
            } else {
                stage.setTitle("Signal Acquistion (User2)");
            }

            scene1.setRoot(layout);
            stage.setScene(scene1);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            new newLogging("SEVERE", WaveLoader.class.getName(), "load()", e);
        }
        return file;
    }

// For generating the audio file of selected audio segment
    public void file_generation(int x) throws Exception {
        if (soundFile != null) {
            soundFile.delete();
        }
        Waveform getY = new Waveform();
        Double[] yValue = getY.Loadmatrix(file);
        Double[] yintValue = getY.Loadmatrix(file);

        double[] audio = new double[2 * yValue.length];
        double[] beforeAudio = new double[2 * yValue.length];

        NumberAxis play_xAxis = (NumberAxis) Zoomchart.getXAxis();
        double x_minCord = play_xAxis.getLowerBound();
        double x_maxCord = play_xAxis.getUpperBound();
        AudioInputStream audioInputStream = null;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(file);
        } catch (UnsupportedAudioFileException | IOException ex) {
            new newLogging("SEVERE", WaveLoader.class.getName(), "load()", ex);
        }
        AudioFormat format = audioInputStream.getFormat();
        long audioFileLength = file.length();
        int frameSize = format.getFrameSize();
        float frameRate = format.getFrameRate();
        float durationInSeconds = (audioFileLength / (frameSize * frameRate));
        int i = 0;
        int idx1 = 0;
        int idx2 = 0;
        ArrayList<Double> xCoord = new ArrayList<>();
        double duration = 1 / 10000.0;
        double time = 0.0;
        while (time < durationInSeconds) {
            time = time + duration;
            xCoord.add(time);
            i++;
        }
        double minDistance = Math.abs(xCoord.get(0) - x_minCord);
        double maxDistance = Math.abs(xCoord.get(0) - x_maxCord);
        for (int c = 0; c < xCoord.size(); c++) {
            double cdistance = Math.abs(xCoord.get(c) - x_minCord);
            if (cdistance < minDistance) {
                idx1 = c;
                minDistance = cdistance;
            }
        }
        for (int c = 0; c < xCoord.size(); c++) {
            double cdistance = Math.abs(xCoord.get(c) - x_maxCord);
            if (cdistance < maxDistance) {
                idx2 = c;
                maxDistance = cdistance;
            }
        }
        i = 0;
        if (idx1 != idx2) {
            for (int c = idx1; c <= idx2 && yValue[c] != null; c++, i++) {
                audio[i] = yValue[c];
            }
        } else {
            for (int c = idx1; c < yValue.length && yValue[c] != null; c++, i++) {
                audio[i] = yValue[c];
            }
        }
        if (x == 0) {
            int j = 0;
            for (int c = 0; c < yintValue.length; c++, j++) {
                beforeAudio[j] = yintValue[c];
            }

            soundBefore = new File("before.wav");
            WavFile wavBefore = WavFile.newWavFile(soundBefore, 1, j, 16, sampleRate);
            wavBefore.writeFrames(beforeAudio, j);
            wavBefore.close();
        }
        soundFile = new File("sound.wav");
        WavFile wavFile = WavFile.newWavFile(soundFile, 1, i, 16, sampleRate);
        wavFile.writeFrames(audio, i);
        wavFile.close();
    }

    // Displaying waveform after loading or recording of audio signal 
    public void signalAcquring_end() {

        signalReset_end();

        Zoomchart = RecordAudioWaveForm(file, 1);
        xAxis = (NumberAxis) Zoomchart.getXAxis();
        xAxis.setUpperBound(slider.getHighValue());
        zoomBox.getChildren().clear();
        zoomBox.getChildren().add(Zoomchart);
        Zoomchart.setCache(true);
        Zoomchart.setCacheShape(true);
        Zoomchart.setCacheHint(CacheHint.SPEED);
        Zoomchart.setMaxWidth(width + 10);
        Zoomchart.setMinWidth(width + 10);
        Zoomchart.setMaxHeight(200);
        Zoomchart.setMinHeight(200);
        sliderDrag();

        Label label = new Label();
        Popup popup = new Popup();
        popup.getContent().add(label);

    }

    public void signalReset_end() {
        Audiochart = RecordAudioWaveForm(file, 1);
        chartHolder = new Pane();
        chartHolder.setMaxWidth(chartBox.getMaxWidth());
        chartHolder.setMinWidth(chartBox.getMaxWidth());
        initial_plot = 1;
        chartHolder.getChildren().add(Audiochart);
        xAxis = (NumberAxis) Audiochart.getXAxis();
        yAxis = (NumberAxis) Audiochart.getYAxis();
        double temp_val = xAxis.upperBoundProperty().doubleValue();
        lowlabel.setText("0.0");
        if (temp_val > 4.8) {
            slider = new RangeSlider(0, 4.8, 0, 4.8);
            slider.setId("cursor2");
            String val = String.valueOf(xAxis.upperBoundProperty().doubleValue());
            highlabel.setText("4.8");
        } else {
            slider = new RangeSlider(0, xAxis.upperBoundProperty().doubleValue(), 0, xAxis.upperBoundProperty().doubleValue());
            slider.setId("cursor2");
            String val = String.valueOf(xAxis.upperBoundProperty().doubleValue());
            highlabel.setText("  " + val.substring(0, 4));
        }
        slider.setHighValue(time);
        slider_cont.getChildren().clear();
        slider_cont.getChildren().addAll(slider);
        slider.minProperty().bind(xAxis.lowerBoundProperty());
        slider.maxProperty().bind(xAxis.upperBoundProperty());
        DoubleProperty lineX = new SimpleDoubleProperty();
        lineX.bind(slider.lowValueProperty());
        DoubleProperty lineY = new SimpleDoubleProperty();
        lineY.bind(slider.highValueProperty());
        line1 = createVerticalLine(Audiochart, xAxis, yAxis, chartHolder, lineX, 1);
        line2 = createVerticalLine(Audiochart, xAxis, yAxis, chartHolder, lineY, 0);
        chartHolder.getChildren().addAll(line1, line2);
        slider.setMaxWidth(width - 75);
        slider.setMinWidth(width - 75);
        chartBox.getChildren().clear();
        chartBox.getChildren().add(chartHolder);
        Audiochart.setCache(true);
        Audiochart.setCacheShape(true);
        Audiochart.setCacheHint(CacheHint.SPEED);
        Audiochart.setMaxWidth(width + 10);
        Audiochart.setMinWidth(width + 10);
        Audiochart.setMaxHeight(200);
        Audiochart.setMinHeight(200);
        double val = Double.valueOf(highlabel.getText()) - Double.valueOf(lowlabel.getText());
        String selects;
        if (String.valueOf(val).length() > 5) {
            selects = String.valueOf(val).substring(0, 4);
        } else {
            selects = String.valueOf(val);
        }
        sliderDrag();
        start_time.setText("Start Time: " + lowlabel.getText().trim() + "s\t");
        end_time.setText("End Time: " + highlabel.getText().trim() + "s\t");
        diff_time.setText("Selected Duration: " + selects + "s");
    }

    public void sliderDrag() {
        // Controlling slider movement 
        slider.lowValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (slider.getLowValue() < 0.01) {
                    slider.setLowValue(0.0);
                }
                double low = slider.getLowValue();
                double high = slider.getHighValue();
                double t = low;
                if ((high - t) < 0.3) {
                    slider.setLowValue(high - 0.3);
                }
                if ((high - t) > 4.8) {
                    slider.setLowValue(low + (high - low - 4.8));
                }
            }
        });
        slider.highValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double low = slider.getLowValue();
                double high = slider.getHighValue();
                double t = low;
                if ((high - t) < 0.3) {
                    slider.setHighValue(low + 0.3);
                }
                if ((high - t) > 4.8) {
                    slider.setHighValue(high - (high - low - 4.8));
                }
            }
        });
        slider.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (active == 1) {
                    Zoom.setDisable(false);
                    lowlabel.setPrefWidth(30);
                    highlabel.setPrefWidth(30);
                    final NumberAxis xAxis = (NumberAxis) Zoomchart.getXAxis();
                    final NumberAxis yAxis = (NumberAxis) Audiochart.getYAxis();
                    Point2D yAxisInScene = yAxis.localToScene(0, 0);
                    final NumberAxis temp = (NumberAxis) Audiochart.getXAxis();
                    double xAxisScale = temp.getScale();
                    double xOffset = line1.getStartX() - yAxisInScene.getX() - 75;
                    double xOffset1;
                    String val;
                    if ((0 + xOffset / xAxisScale) < 0) {
                        val = "0.0000";
                    } else {
                        val = String.valueOf(xOffset / xAxisScale);
                    }
                    if (val.length() > 4) {
                        lowlabel.setText(val.substring(0, 4));
                    } else {
                        lowlabel.setText(val.substring(0, 3));
                    }
                    if (line2.getStartX() == 0) {
                        val = String.valueOf(slider.getHighValue()) + 0.000;
                    } else {
                        xOffset1 = line2.getStartX() - yAxisInScene.getX() - 75;
                        val = String.valueOf((xOffset1 / xAxisScale));
                    }
                    highlabel.setText(val.substring(0, 4));
                }
                double val = Double.valueOf(highlabel.getText()) - Double.valueOf(lowlabel.getText());
                String selects;
                if (String.valueOf(val).length() > 5) {
                    selects = String.valueOf(val).substring(0, 4);
                } else {
                    selects = String.valueOf(val);
                }
                start_time.setText("Start Time: " + lowlabel.getText().trim() + "s\t");
                end_time.setText("End Time: " + highlabel.getText().trim() + "s\t");
                diff_time.setText("Selected Duration: " + selects + "s");
            }
        });
    }

    public void recordEnd() {
        try {
            initialSound = 0;
            Play_before.setDisable(false);
            progressBar.progressProperty().unbind();
            Zoom.setDisable(false);
            Stop.setDisable(true);
            timeline.stop();
            sound.stop();
            sound.save(file);
            signalAcquring_end();
            if (initialSound == 0) {
                file_generation(0);
                initialSound++;
            }
            sound(soundBefore);
            message.setEditable(false);
            speakerInfo.getChildren().clear();
            speakerInfo.getChildren().addAll(fileSave, name, nameTxt, age, ageTxt, date_stamp);

        } catch (IOException ex) {
            new newLogging("SEVERE", WaveLoader.class.getName(), "load()", ex);

        } catch (Exception ex) {
            new newLogging("SEVERE", WaveLoader.class.getName(), "load()", ex);
        }
    }

    // Drawing waveform chart of audio signal
    public LineChart RecordAudioWaveForm(File file, int x) {
        try {
            time = 0.0;
            int i = 0;
            ArrayList<Double> data = new ArrayList<>();
            ObservableList<XYChart.Data> list = FXCollections.observableArrayList();
            if (x == 1) {
                Waveform getY = new Waveform();
                Double[] yValue = getY.Loadmatrix(file);
                duration = 1 / 10000.0;
                while (i < yValue.length && time < 10.0) {
                    data.add(time);
                    time = time + duration;
                    i = i + 1;
                }
                i = 0;
                while (i < yValue.length && data.size() > i) {
                    list.add(new XYChart.Data(data.get(i), yValue[i]));
                    i = i + 4;
                }
                if (time == 9.99) {
                    time = 10.0;
                }
            } else {
                duration = 1 / 100.0;
                time = 0.0;
                while (time <= 10) {
                    list.add(new XYChart.Data(time, 0));
                    time = time + duration;
                    i++;
                }
            }
            final NumberAxis xAxis_Val = new NumberAxis();
            xAxis_Val.tickLabelFontProperty().set(Font.font(12));
            final NumberAxis yAxis_Val = new NumberAxis();
            yAxis_Val.tickLabelFontProperty().set(Font.font(12));
            LineChart<Number, Number> Linechart = new LineChart<>(xAxis_Val, yAxis_Val);
            Linechart.setCache(true);
            Linechart.setCacheHint(CacheHint.SPEED);
            XYChart.Series series = new XYChart.Series();
            xAxis_Val.setLabel("Time (s)");
            yAxis_Val.setLabel("Sig. (norm.)");
            series.setData(list);
            Linechart.setId("line");
            Linechart.getData().add(series);
            labelConverter lConv;
            lConv = new labelConverter<>();
            unitConverter uConv;
            uConv = new unitConverter<>();
            xAxis_Val.setTickLabelFormatter(uConv);
            yAxis_Val.setTickLabelFormatter(lConv);
            Linechart.setLegendVisible(false);
            Linechart.setCreateSymbols(false);
            xAxis_Val.setAutoRanging(false);
            xAxis_Val.setLowerBound(0);
            xAxis_Val.setUpperBound(time);
            xAxis_Val.setTickUnit(time / 3);
            yAxis_Val.setAutoRanging(false);
            yAxis_Val.setLowerBound(-1);
            yAxis_Val.setUpperBound(1);
            yAxis_Val.setTickUnit(0.5);
            yAxis_Val.setMinorTickCount(2);
            return Linechart;

        } catch (Exception ex) {
            new newLogging("SEVERE", WaveLoader.class.getName(), "RecordAudioWaveform()", ex);
        }
        return null;
    }

    // For playing audio file
    public void sound(File file) {
        try {
            AudioInputStream stream;
            AudioFormat format;
            DataLine.Info info;
            Clip clip;
            stream = AudioSystem.getAudioInputStream(file);
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            new newLogging("SEVERE", WaveLoader.class.getName(), "sound()", ex);
        }
    }

    private Line createVerticalLine(XYChart<Number, Number> chart, NumberAxis xAxis, NumberAxis yAxis, Pane container, ObservableDoubleValue x, int color) {
        Line line = new Line();
        line.setStroke(Color.valueOf("#EE411B"));
        line.startXProperty().bind(Bindings.createDoubleBinding(() -> {
            double xInAxis = xAxis.getDisplayPosition(x.get());
            Point2D pointInScene = xAxis.localToScene(xInAxis, 0);
            double xInContainer = chart.sceneToLocal(pointInScene).getX();
            return xInContainer;
        }, x, chart.boundsInParentProperty(), xAxis.lowerBoundProperty(), xAxis.upperBoundProperty()));
        line.endXProperty().bind(line.startXProperty());
        line.startYProperty().bind(Bindings.createDoubleBinding(() -> {
            double upperY = yAxis.getDisplayPosition(yAxis.getUpperBound());
            Point2D pointInScene = yAxis.localToScene(0, upperY);
            double yInContainer = container.sceneToLocal(pointInScene).getY() + 18;
            return yInContainer;
        }, chart.boundsInParentProperty(), yAxis.lowerBoundProperty()));
        line.endYProperty().bind(Bindings.createDoubleBinding(() -> {
            double lowerY = yAxis.getDisplayPosition(yAxis.getUpperBound());
            Point2D pointInScene = yAxis.localToScene(0, lowerY);
            double yInContainer = container.sceneToLocal(pointInScene).getY() + chart.getMaxHeight() - 60;
            return yInContainer;
        }, chart.boundsInParentProperty(), yAxis.lowerBoundProperty()));
        line.visibleProperty().bind(Bindings.lessThan(x, xAxis.lowerBoundProperty()).and(Bindings.greaterThan(x, xAxis.upperBoundProperty())).not());
        return line;
    }

    public void save(int x) throws Exception {
        int i = 0, c, numSamples;
        double[] ymatrix;
        Waveform getY = new Waveform();
        Double[] yValue = getY.Loadmatrix(file);
        NumberAxis play_xAxis = (NumberAxis) Zoomchart.getXAxis();
        double x_minCord = play_xAxis.getLowerBound();
        double x_maxCord = play_xAxis.getUpperBound();
        int waveformLength = (int) (x_maxCord * 10000) - (int) (x_minCord * 10000);
        try {
            // For calulating the duration in multiples of 0.5 according to the selected segments
            if (x == 1) {
                double pure_signal = 0.2 + (x_maxCord - x_minCord);
                double diff = pure_signal - (int) pure_signal;
                double duration;
                if (diff == 0) {
                    duration = (int) pure_signal;
                } else {
                    if (diff <= 0.5) {
                        duration = (int) pure_signal + 0.5;
                    } else {
                        duration = (int) pure_signal + 1;
                    }
                }
                double period = duration * 10000;
                int length = (int) period;
                ymatrix = new double[length];
                numSamples = length;
                int m = 0;
                i = 0;
                if (x_minCord < 0.02) {
                    x_minCord = 0.02;
                }

                // For padding with zeros as prefix of 0.1 seconds and suffix as upto length
                if (pure_signal <= (max_signal + 0.2)) {
                    for (i = 0; i < 1000; i++) {
                        ymatrix[i] = 0;
                    }

                    for (c = (int) (x_minCord * 10000); c < (int) (x_maxCord * 10000); i++, c++) {
                        ymatrix[i] = yValue[c];
                    }
                    for (; i < length; i++) {
                        ymatrix[i] = 0;
                    }
                } else {
                    for (c = (int) (x_minCord * 10000); m < ymatrix.length; i++, c++, m++) {
                        ymatrix[i] = yValue[c];
                    }
                }
            } else {
                ymatrix = new double[waveformLength];
                numSamples = waveformLength;
                for (i = 0, c = (int) (x_minCord * 10000); c < (int) (x_maxCord * 10000); i++, c++) {
                    ymatrix[i] = yValue[c];
                }
            }
            WavFile wavFile = WavFile.newWavFile(soundFile, 1, numSamples, 16, sampleRate);
            wavFile.writeFrames(ymatrix, numSamples);
            wavFile.close();
        } catch (Exception ee) {
            new newLogging("SEVERE", WaveLoader.class.getName(), "save()", ee);
        }
    }

    // Initial settings of graph
    private void initialGraph() {
        active = 0;
        Save.setDisable(true);
        Proceed.setDisable(true);
        Zoom.setDisable(true);
        Play.setDisable(true);
        speakerInfo.setVisible(false);
        TitleLbl.setVisible(false);
        FileName.setVisible(false);
        slider.setDisable(false);
        Audiochart = RecordAudioWaveForm(file, 0);
        chartHolder = new Pane();
        chartHolder.setMaxWidth(chartBox.getMaxWidth() + 20);
        chartHolder.setMinWidth(chartBox.getMaxWidth() + 20);
        chartHolder.getChildren().add(Audiochart);
        xAxis = (NumberAxis) Audiochart.getXAxis();
        yAxis = (NumberAxis) Audiochart.getYAxis();
        slider.minProperty().bind(xAxis.lowerBoundProperty());
        slider.maxProperty().bind(xAxis.upperBoundProperty());
        DoubleProperty lineX = new SimpleDoubleProperty();
        lineX.bind(slider.lowValueProperty());
        DoubleProperty lineY = new SimpleDoubleProperty();
        lineY.bind(slider.highValueProperty());

        line1 = createVerticalLine(Audiochart, xAxis, yAxis, chartHolder, lineX, 1);
        line2 = createVerticalLine(Audiochart, xAxis, yAxis, chartHolder, lineY, 0);

        chartHolder.getChildren().addAll(line1, line2);
        slider.setMaxWidth(width - 75);
        slider.setMinWidth(width - 75);
        chartBox.getChildren().clear();
        chartBox.getChildren().add(chartHolder);
        Zoomchart = RecordAudioWaveForm(file, 0);
        zoomBox.getChildren().clear();
        zoomBox.getChildren().add(Zoomchart);
        Audiochart.setCache(true);
        Zoomchart.setCache(true);
        Audiochart.setMaxWidth(width + 10);
        Audiochart.setMinWidth(width + 10);
        Zoomchart.setMaxWidth(width + 10);
        Zoomchart.setMinWidth(width + 10);
        Audiochart.setMaxHeight(200);
        Audiochart.setMinHeight(200);
        Zoomchart.setMaxHeight(200);
        Zoomchart.setMinHeight(200);
    }

    private void zoomAction() {
        try {
            final NumberAxis xAxis = (NumberAxis) Zoomchart.getXAxis();
            final NumberAxis yAxis = (NumberAxis) Audiochart.getYAxis();
            Point2D yAxisInScene = yAxis.localToScene(0, 0);
            final NumberAxis temp = (NumberAxis) Audiochart.getXAxis();
            double xAxisScale = temp.getScale();
            double xOffset = line1.getStartX() - yAxisInScene.getX() - 75;  // Calculating position on graph from initial slider cursor
            double xOffset1 = line2.getStartX() - yAxisInScene.getX() - 75; // Calculating position on graph from final slider cursor

            if ((xOffset / xAxisScale) == (xOffset1 / xAxisScale)) {
                xOffset1 = xOffset;
            } else {
                if (line2.getStartX() == 0) {
                    if (temp.getUpperBound() < 4.8) {
                        xAxis.setUpperBound(temp.getUpperBound());
                    } else {
                        xAxis.setUpperBound(4.8);
                    }

                } else {
                    if (xOffset > xOffset1) {
                        xAxis.setUpperBound(0.3 + xOffset / xAxisScale);
                    } else {
                        xAxis.setUpperBound(0 + xOffset1 / xAxisScale);
                    }
                }
            }
            if ((0 + xOffset / xAxisScale) < 0) {
                xAxis.setLowerBound(0);
            } else {
                xAxis.setLowerBound(0 + xOffset / xAxisScale);
            }
            if ((xAxis.getUpperBound() - xAxis.getLowerBound()) > 4.8) {
                xAxis.setUpperBound(xAxis.getUpperBound() - (xAxis.getUpperBound() - xAxis.getLowerBound() - 4.8));
            }
            xAxis.setUpperBound(Double.valueOf(highlabel.getText()));
            xAxis.setLowerBound(Double.valueOf(lowlabel.getText()));
            xAxis.setTickUnit((xAxis.getUpperBound() - xAxis.getLowerBound()) / 3);
            xAxis.setMinorTickCount(2);
            file_generation(1);
            double val = Double.valueOf(highlabel.getText()) - Double.valueOf(lowlabel.getText());
            String selects;
            if (String.valueOf(val).length() > 5) {
                selects = String.valueOf(val).substring(0, 4);
            } else {
                selects = String.valueOf(val);
            }

            start_time.setText("Start Time: " + lowlabel.getText().trim() + "s\t");
            end_time.setText("End Time: " + highlabel.getText().trim() + "s\t");
            diff_time.setText("Selected Duration: " + selects + "s");
            Date date = new Date();
            String month = date.toString();
            StringTokenizer token = new StringTokenizer(month, " ");
            token.nextToken();

            DecimalFormat mFormat = new DecimalFormat("00");
            DateFormat df = new SimpleDateFormat("ddMMyy");
            speakerInfo.getChildren().clear();
            date_time = df.format(new Date()) + "_" + mFormat.format(Double.valueOf(date.getHours())) + "" + mFormat.format(Double.valueOf(date.getMinutes())) + "" + mFormat.format(Double.valueOf(date.getSeconds()));
            date_stamp.setText(date_time);
            speakerInfo.getChildren().addAll(fileSave, name, nameTxt, age, ageTxt, date_stamp);

        } catch (Exception ex) {
            new newLogging("SEVERE", WaveLoader.class.getName(), "load()", ex);
        }
    }

    //For adding space on Y-axis label
    private class labelConverter<T> extends StringConverter<T> {

        @Override
        public String toString(T t) {
            Double tb = (Double) t;
            String inString = tb.toString();
            String outString = "";
            for (int i = 0; i < 6 - inString.length(); i++) {
                outString = outString + " ";
            }
            outString = outString + inString;
            return outString;
        }

        @Override
        public T fromString(String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private class unitConverter<T> extends StringConverter<T> {

        @Override
        public String toString(T t) {
            Double tb = (Double) t;
            Double ta = tb.doubleValue();
            String inString = ta.toString();
            String outString = "";
            if (inString.length() > 4) {
                outString = inString.substring(0, 4);
            } else {
                outString = inString;
            }
            return outString;
        }

        @Override
        public T fromString(String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    class ProgressIndicatorBar extends StackPane {

        final private ReadOnlyDoubleProperty timeElpased;
        final private double totalTime;
        final private ProgressBar bar = new ProgressBar();
        final private Text text = new Text();
        final private String labelFormatSpecifier;

        ProgressIndicatorBar(final ReadOnlyDoubleProperty timeElpased, final double totalTime, final String labelFormatSpecifier) {
            this.timeElpased = timeElpased;
            this.totalTime = totalTime;
            this.labelFormatSpecifier = labelFormatSpecifier;
            stamp = new Label("");
            stamp.setStyle("-fx-padding: 8 0 0 0;-fx-wrap-text:true; -fx-text-alignment: center; -fx-alignment: CENTER; -fx-font-size: 10pt; -fx-font-family: \"Segoe UI Semibold\"; -fx-text-fill: #FFFFFF    ;");
            syncProgress();
            timeElpased.addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                    syncProgress();
                }
            });
            bar.setMaxWidth(Double.MAX_VALUE); // allows the progress bar to expand to fill available horizontal space.
            getChildren().setAll(bar);
        }

        // synchronizes the progress indicated with the work done.
        private void syncProgress() {
            text.setVisible(false);
            if (count_text == 0) {
                text.setText("");
                stamp.setText("");
                bar.setProgress(0);
                count_text++;

            } else {
                stamp.setText(String.format(labelFormatSpecifier, 10 - Math.ceil(timeElpased.get())));
                text.setText(String.format(labelFormatSpecifier, 10 - Math.ceil(timeElpased.get())));
                bar.setProgress(1 - timeElpased.get() / totalTime);
            }
            bar.setMinHeight(10);
            bar.setMaxHeight(10);
            bar.setMaxWidth(width - 73);
            bar.setMinWidth(width - 73);
        }
    }
}
