package vspeech;

import backend.TimeScale;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import static com.sun.jna.platform.win32.WinUser.GWL_STYLE;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.stage.Stage;
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

// Panel Window
public class Analyzer extends GridPane {

    public MenuBar menuBar;
    public final GridPane content;
    private final Stage pStage;
    private final WaveLoader wLoader;
    private final AnalysisDisplay aDisplay;
    private final AnimationDisplay animDisplay;
    public File analysisFile;
    private BufferedImage spectrogramImage;
    private BufferedImage areagramImage;
    public ArrayList<Double> waveform_x, waveform_y, pitch_x, pitch_y, energy_x, energy_y, poa_x, poa_y;
    private LineChart<Number, Number> energyPlot;
    private LineChart<Number, Number> pitchPlot;
    private LineChart<Number, Number> waveform;
    private boolean energyZoom = false;
    private boolean pitchZoom = false;
    public boolean isAnalysisDisplayed = false;
    public boolean isImgLeft = false;
    public boolean isDisplay = false;
    public boolean isBar = true;
    public final PlayerControl control;
    public PlayerBar playBar;
    private final Label label = new Label();
    public RangeSlider slider;
    public double counter = 0;
    public double[][] data_jaw_x;
    public double[][] data_jaw_y;
    public Visual objVisual = new Visual();
    private final VisualAction objVisualAction = new VisualAction(objVisual);
    private boolean isAnimationDisplayed = false;
    public ProgressBar energyBar = new ProgressBar();
    public ProgressBar pitchBar = new ProgressBar();
    private DoubleProperty fontSize = new SimpleDoubleProperty();
    public HBox animatePane = new HBox();
    public VBox barBox = new VBox();
    public int pitchScale = 500;
    public int energyScale = 80;
    public boolean isLeft;
    public ImageDisplay img;
    public int analysisCount = 0;
    private AudioInputStream stream;
    private AudioFormat format;
    private DataLine.Info info;
    private Clip clip;
    public Image face;
    public static int faceCount = 0;
    public final Group newBox;
    private List<Double> max_minpoaX;
    private ObservableList<Anchor> anchors;
    private int initial;
    private ArrayList energy_data;
    private int point;
    private Timeline timeline;
    private static double slide_val_3;
    private static double slide_val_5;
    public static double slide_val_1;
    public ImageView baseImg;
    public boolean isHorizontal = true;
    public boolean isFlip = false;
    public int scale = 1;
    public boolean previous_isBar = true;
    public Vspeech vsts;
    private boolean isLeftclick;
    private boolean isRightclick;
    public static File pitchFile;

    public Analyzer(boolean isLeft, Stage parentStage, DoubleProperty fontSize) {
        menuBar = new MenuBar(this, isLeft);
        content = new GridPane();
        content.setMinSize((LayoutConstants.minWidth - (2 * LayoutConstants.menuBarWidth)) / 2, LayoutConstants.minHeight - (LayoutConstants.titleBarHeight + LayoutConstants.playBarHeight + 10) - 30);
        content.setMaxSize((LayoutConstants.minWidth - (2 * LayoutConstants.menuBarWidth)) / 2, LayoutConstants.minHeight - (LayoutConstants.titleBarHeight + LayoutConstants.playBarHeight + 10) - 30);
        this.isLeft = isLeft;

        if (isLeft) {
            content.setStyle("-fx-background-color:" + ColorConstants.background + ";-fx-border-width: 2 5 9 9 ;-fx-border-color:" + ColorConstants.border + ";");
        } else {
            content.setStyle("-fx-background-color:" + ColorConstants.background + ";-fx-border-width: 2 9 9 5 ;-fx-border-color: " + ColorConstants.border + ";");
        }
        pStage = parentStage;
        this.fontSize = fontSize;
        wLoader = new WaveLoader(pStage, this);
        aDisplay = new AnalysisDisplay(content);
        animDisplay = new AnimationDisplay(content);
        img = new ImageDisplay();
        energyBar.setProgress(0);
        pitchBar.setProgress(0);
        control = new PlayerControl(isLeft, this);
        if (isLeft) {
            add(menuBar, 0, 0);
            add(content, 1, 0);
        } else {
            add(menuBar, 1, 0);
            add(content, 0, 0);
        }
        if (isLeft) {
            face = new Image("/images/girl child1.png");
            Image base = face;
            baseImg = new ImageView(base);
            animDisplay.baseImg = baseImg;
            baseImg.setId("girl");

        } else {
            face = new Image("/images/female9.png");
            Image base = face;
            baseImg = new ImageView(base);
            animDisplay.baseImg = baseImg;
            baseImg.setId("women");

        }
        newBox = new Group();
        vsts = new Vspeech();
    }

    // Initial display when first time application invoke 
    public void initial_display() throws URISyntaxException, IOException, InterruptedException {

        File analysis_file;
        if (vsts.jar) {
            analysis_file = new File("aai.wav");
        } else {
            analysis_file = new File(".\\src\\praatchk\\aai.wav");
        }

        if (vsts.jar) {
            if (isLeft) {

                final Class<?> referenceClass = NewView.class;
                final URL url = referenceClass.getProtectionDomain().getCodeSource().getLocation();
                final File jarPath = new File(url.toURI()).getParentFile();

                File temp_file = new File(jarPath.getAbsolutePath() + "\\temp.wav");

                temp_file.createNewFile();
                FileInputStream instream = new FileInputStream(analysis_file);
                FileOutputStream outstream = new FileOutputStream(temp_file);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = instream.read(buffer)) > 0) {
                    outstream.write(buffer, 0, length);
                }
                instream.close();
                outstream.close();

                Files.deleteIfExists(Paths.get(jarPath.getAbsolutePath() + "\\pitch.txt"));

                String pFilesX86 = System.getenv("ProgramFiles(X86)");
                if (pFilesX86 != (null)) {
                    String commands[] = {"praatcon.exe", "extract_pitch.praat", temp_file.getName(), "pitch.txt", jarPath.getAbsolutePath() + "\\"};
                    Process pb = new ProcessBuilder(commands).start();
                    pb.waitFor();
                } else {
                    String commands[] = {"praat_32.exe", "extract_pitch.praat", temp_file.getName(), "pitch.txt", jarPath.getAbsolutePath() + "\\"};
                    Process pb = new ProcessBuilder(commands).start();
                    pb.waitFor();
                }
                pitchFile = new File(Paths.get("pitch.txt").toString());
            }
        } else {
            if (isLeft) {
                // Files.deleteIfExists(Paths.get(".\\src\\praatchk\\temp.wav"));

                // File temp_file = new File(".\\src\\praatchk\\temp.wav");
                Files.deleteIfExists(Paths.get(".\\src\\praatchk\\pitch.txt"));
                String commands[] = {"praatcon.exe", "extract_pitch.praat", analysis_file.getName(), "pitch.txt", ".\\src\\praatchk\\"};
                Process pb = new ProcessBuilder(commands).start();

                /*   for (int i = 0; i < 50000; i++) {

                        System.out.println("" + i);
                        if (i == 20000) {
                            process_graph.destroy();
                            break;
                        }

                    }*/
                pitchFile = new File(Paths.get("pitch.txt").toString());

            }
        }

        objVisualAction.readFile();
        spectrogramImage = img.img(objVisual.getSpecData(), 80, 6, 5, "Freq. (norm.)");
        areagramImage = img.img(objVisual.getAreaData(), 7, 6, 1, "Dist (norm.)");
        waveform_x = objVisual.getDataWave_x();
        waveform_y = objVisual.getDataWave_y();
        double maxAbs = Collections.max(waveform_y, new Comparator<Double>() {
            @Override
            public int compare(Double x, Double y) {
                return Math.abs(x) < Math.abs(y) ? -1 : 1;
            }
        });
        if (maxAbs < 0) {
            maxAbs = maxAbs * (-1);
        }
        for (int i = 0; i < waveform_y.size(); i++) {
            waveform_y.set(i, (waveform_y.get(i) * 0.8) / maxAbs);
        }

        energy_x = objVisual.getData_time();
        energy_y = objVisual.getData_int();
        energy_data = new ArrayList();
        int i = 0;
        while (i < energy_y.size()) {
            double x = energy_y.get(i);
            if (x < 20) {
                x = 20;
                energy_data.add(x);
            } else {
                energy_data.add(x);
            }
            i++;
        }

        pStage.show();
        long lhwnd = com.sun.glass.ui.Window.getWindows().get(0).getNativeWindow();
        Pointer lpVoid = new Pointer(lhwnd);
        WinDef.HWND hwnd = new WinDef.HWND(lpVoid);
        final User32 user32 = User32.INSTANCE;
        int oldStyle = user32.GetWindowLong(hwnd, GWL_STYLE);
        int newStyle = oldStyle | 0x00020000;
        user32.SetWindowLong(hwnd, GWL_STYLE, newStyle);
        energyPlot = plot(energy_x, energy_data, "energy", 20, energyScale, "Time (s)", "Level (dB)");

        pitch_x = objVisual.getData_pitch();
        pitch_y = objVisual.getData_pitchValue();

        pitchPlot = plot(pitch_x, pitch_y, "pitch", 0, pitchScale, "Time (s)", "Pitch (Hz)");
        data_jaw_x = objVisual.getMat_x();
        data_jaw_y = objVisual.getMat_y();
        poa_x = objVisual.getPoa_x();
        poa_y = objVisual.getPoa_y();
        waveform = plot(waveform_x, waveform_y, "waveform", -1, 1, "Time (s)", "Sig.(nrm)");
        counter = 0;
        if (control.timeline != null) {
            control.timeline.pause();
        }
        counter = 0;
        control.disableAllControls();
        initial = 1;
        slider = new RangeSlider(0, waveform_x.size() / 10000.0, 0, waveform_x.size() / 10000.0);

        slider.setLowValue(0);
        slider.setHighValue(waveform_x.size() / 10000.0);
        slide_val_1 = ((waveform_x.size()) / 10000.0) / ((data_jaw_x.length - 1));
        slide_val_3 = (waveform_x.size() / 10000.0) / ((data_jaw_x.length / 3.0));
        slide_val_5 = (waveform_x.size() / 10000.0) / ((data_jaw_x.length / 5.0));
        menuBar.enableAnalysis();
        menuBar.enableAnimation();
        displayAnimation();
        isBar = false;
        barDisplay();
        getPlayBar().studMsg.setDisable(false);
        getPlayBar().refMsg.setDisable(false);
        getPlayBar().studMsg.setText("AAI");
        getPlayBar().refMsg.setText("AAI");
        circleDisplay();
        TimeScale timeAudio = new TimeScale();
        timeAudio.audioTime();
        objVisual.setSoundWave_1(timeAudio.Y[0]);
        objVisual.setSoundWave_2(timeAudio.Y[1]);
        objVisual.setSoundWave_5(timeAudio.Y[2]);
        objVisual.setSoundWave_10(timeAudio.Y[3]);
        objVisual.setSoundWave_20(timeAudio.Y[4]);

    }

    // For clearing the panel 
    public void resetPane() {
        analysisFile = null;
        content.getChildren().clear();
        spectrogramImage = null;
        areagramImage = null;
        waveform_x = null;
        waveform_y = null;
        energy_x = null;
        energy_y = null;
        pitch_x = null;
        pitch_y = null;
        data_jaw_x = null;
        data_jaw_y = null;
        poa_x = null;
        poa_y = null;
        menuBar.disableAnalysis();
        menuBar.disableAnimation();
        control.disableAllControls();
        playBar.disablePlayControls();
        if (isLeft) {
            getPlayBar().studMsg.setDisable(true);
            getPlayBar().studMsg.setText("");
        } else {
            getPlayBar().refMsg.setDisable(true);
            getPlayBar().refMsg.setText("");
        }
    }

    public void LoadFile() throws IOException {
        analysisFile = wLoader.load(isLeft);
    }

    public PlayerControl getControl() {
        return control;
    }

    // Invoking the MATLAB executable and storing the values in variable from text file that generates after MATLAB processing
    public void fileAnalysis() {
        content.getChildren().clear();
        menuBar.disableAnimation();
        menuBar.disableAnalysis();
        try {
            control.getChildren().clear();
            if (isLeft) {
                control.add(control.MessageVB, 0, 0);
                control.add(control.PitchVB, 1, 0);
                control.add(control.EnergyVB, 2, 0);
                control.add(control.BarVB, 4, 0);
                control.add(control.CircleVB, 5, 0);
                if (baseImg.getId().contains("men")) {
                    control.add(control.ManFaceVB, 7, 0);
                }
                if (baseImg.getId().contains("women")) {
                    control.add(control.WomanFaceVB, 7, 0);
                }
                if (baseImg.getId().contains("boy")) {
                    control.add(control.BoyFaceVB, 7, 0);
                }
                if (baseImg.getId().contains("girl")) {
                    control.add(control.GirlFaceVB, 7, 0);
                }
                control.add(control.FlipVB, 8, 0);
//                control.add(control.SoundVB, 10, 0);
                control.add(control.PlayVB, 12, 0);
                control.add(control.RestartVB, 13, 0);

            } else {
                control.setAlignment(Pos.CENTER_RIGHT);
                control.add(control.MessageVB, 13, 0);
                control.add(control.PitchVB, 12, 0);
                control.add(control.EnergyVB, 11, 0);
                control.add(control.BarVB, 9, 0);
                control.add(control.CircleVB, 8, 0);
                if (baseImg.getId().contains("men")) {
                    control.add(control.ManFaceVB, 6, 0);
                }
                if (baseImg.getId().contains("women")) {
                    control.add(control.WomanFaceVB, 6, 0);
                }
                if (baseImg.getId().contains("boy")) {
                    control.add(control.BoyFaceVB, 6, 0);
                }
                if (baseImg.getId().contains("girl")) {
                    control.add(control.GirlFaceVB, 6, 0);
                }
                control.add(control.FlipVB, 5, 0);
                //  control.add(control.SoundVB, 3, 0);
                control.add(control.RestartVB, 1, 0);
                control.add(control.PlayVB, 0, 0);
            }
            final Class<?> referenceClass = NewView.class;
            final URL url = referenceClass.getProtectionDomain().getCodeSource().getLocation();
            final File jarPath = new File(url.toURI()).getParentFile();

            File temp_file;

            if (vsts.jar) {
                temp_file = new File(jarPath.getAbsolutePath() + "\\temp.wav");

            } else {
                temp_file = new File(".\\src\\praatchk\\temp.wav");
            }

            temp_file.createNewFile();
            FileInputStream instream = new FileInputStream(analysisFile);
            FileOutputStream outstream = new FileOutputStream(temp_file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = instream.read(buffer)) > 0) {
                outstream.write(buffer, 0, length);
            }
            instream.close();
            outstream.close();

            if (vsts.jar) {
                String pFilesX86 = System.getenv("ProgramFiles(X86)");
                if (pFilesX86 != (null)) {
                    Files.deleteIfExists(Paths.get(jarPath.getAbsolutePath() + "\\pitch.txt"));

                    String commands[] = {"praatcon.exe", "extract_pitch.praat", temp_file.getName(), "pitch.txt", jarPath.getAbsolutePath() + "\\"};
                    Process pb = new ProcessBuilder(commands).start();
                    pb.waitFor();
                    Process process_graph = new ProcessBuilder("process.exe").start();

                    //   Process process_graph = new ProcessBuilder("plots_1.exe", temp_file.getAbsolutePath(), "pitch.txt").start();
                    //    process_graph.waitFor();
                } else {
                    String commands[] = {"praat_32.exe", "extract_pitch.praat", temp_file.getName(), "pitch.txt", jarPath.getAbsolutePath() + "\\"};
                    Process pb = new ProcessBuilder(commands).start();
                    pb.waitFor();
                    Process process_graph = new ProcessBuilder("process.exe").start();

                    // Process process_graph = new ProcessBuilder("plots_32.exe", temp_file.getAbsolutePath(), "pitch.txt").start();
                    // process_graph.waitFor();
                }
                pitchFile = new File(Paths.get("pitch.txt").toString());
                /* ArrayList<Double> Temp_1 = new ArrayList<Double>();
                ArrayList<Double> Temp_2 = new ArrayList<Double>();

                StringBuilder sb = new StringBuilder();
                String line = " ";
                BufferedReader br = new BufferedReader(new FileReader(pitchFile));

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    Temp_1.add(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                    Temp_2.add(Double.parseDouble(line.substring(line.indexOf(" ") + 1, line.length())));

                }
                System.out.println("Temp= " + Temp_1);
                System.exit(0);*/

            } else {
                Files.deleteIfExists(Paths.get(".\\src\\praatchk\\pitch.txt"));
                String commands[] = {"praatcon.exe", "extract_pitch.praat", temp_file.getName(), "pitch.txt", ".\\src\\praatchk\\"};
                Process pb = new ProcessBuilder(commands).start();
                Process process_graph = new ProcessBuilder("process.exe").start();
                pitchFile = new File(Paths.get("pitch.txt").toString());

                //  Process process_graph = new ProcessBuilder("plots_1", temp_file.getAbsolutePath(), ".\\src\\praatchk\\pitch.txt").start();
                // process_graph.waitFor();
            }
            objVisualAction.readFile();

            objVisual.setStudentFile(analysisFile);

            waveform_x = objVisual.getDataWave_x();
            waveform_y = objVisual.getDataWave_y();

            spectrogramImage = img.img(objVisual.getAreaData(), 80, 6, 5, "Freq. (norm.)");
            areagramImage = img.img(objVisual.getAreaData(), 7, 6, 1, "Dist (norm.)");
            double maxAbs = Collections.max(waveform_y, new Comparator<Double>() {
                @Override
                public int compare(Double x, Double y) {
                    return Math.abs(x) < Math.abs(y) ? -1 : 1;
                }
            });
            if (maxAbs < 0) {
                maxAbs = maxAbs * (-1);
            }
            for (int i = 0; i < waveform_y.size(); i++) {
                waveform_y.set(i, (waveform_y.get(i) * 0.8) / maxAbs);
            }
            energy_x = objVisual.getData_time();
            energy_y = objVisual.getData_int();
            energy_data = new ArrayList();
            int i = 0;
            while (i < energy_y.size()) {
                double x = energy_y.get(i);
                if (x < 20) {
                    x = 20;
                    energy_data.add(x);
                } else {
                    energy_data.add(x);
                }
                i++;
            }

            energyPlot = plot(energy_x, energy_data, "energy", 20, energyScale, "Time (s)", "Level  (dB)");

            pitch_x = objVisual.getData_pitch();
            pitch_y = objVisual.getData_pitchValue();

            pitchPlot = plot(pitch_x, pitch_y, "pitch", 0, pitchScale, "Time (s)", "Pitch (Hz)");
            data_jaw_x = objVisual.getMat_x();
            data_jaw_y = objVisual.getMat_y();

            data_jaw_x = objVisual.getMat_x();
            data_jaw_y = objVisual.getMat_y();
            poa_x = objVisual.getPoa_x();
            poa_y = objVisual.getPoa_y();

            waveform = plot(waveform_x, waveform_y, "waveform", -1, 1, "Time (s)", "Sig. (nrm)");
            counter = 0;
            if (control.timeline != null) {
                control.timeline.pause();
            }
            counter = 0;
            control.disableAllControls();
            initial = 1;
            slider = new RangeSlider(0, waveform_x.size() / 10000.0, 0, waveform_x.size() / 10000.0);

            slider.setLowValue(0);
            slider.setHighValue(waveform_x.size() / 10000.0);
            slide_val_1 = ((waveform_x.size()) / 10000.0) / ((data_jaw_x.length - 1));
            slide_val_3 = (waveform_x.size() / 10000.0) / ((data_jaw_x.length / 3.0));
            slide_val_5 = (waveform_x.size() / 10000.0) / ((data_jaw_x.length / 5.0));

            instream.close();
            outstream.close();
            File f;
            if (!vsts.jar) {
                f = new File(".\\src\\praatchk\\");
                for (File file : f.listFiles()) {
                    if (file.isFile() && file.getName().endsWith(".txt")) {
                        //    file.delete();
                    }
                }
            } else {
                f = new File(".");
                for (File file : f.listFiles()) {
                    if (file.isFile() && file.getName().endsWith(".txt") && !file.getName().contains("settings")) {
                        //  file.delete();
                    }
                }
            }
            f = new File("save.wav");
            f.delete();
            f = new File("sound.wav");
            f.delete();
            menuBar.enableAnalysis();
            menuBar.enableAnimation();
            displayAnimation();
            isBar = false;
            isHorizontal = true;
            barDisplay();
            if (isLeft) {
                getPlayBar().studMsg.setDisable(true);
                getPlayBar().studMsg.setText("");
            } else {
                getPlayBar().refMsg.setDisable(true);
                getPlayBar().refMsg.setText("");
            }
            isLeftclick = false;
            isRightclick = false;

        } catch (Exception ex) {
            menuBar.disableAnalysis();
            menuBar.disableAnimation();
        }
    }

    // For Message Window
    public void mesaagePane() {
        if (isLeft) {
            getPlayBar().studMsg.setDisable(false);
        } else {
            getPlayBar().refMsg.setDisable(false);
        }
        MessageWindow msgWindow = new MessageWindow(pStage, this);
        msgWindow.displayMessage();
    }

    // For activating Analysis window
    public void displayAnalysis() {
        counter = 0;
        if (control.timeline != null) {
            control.timeline.stop();
        }
        energyBar.setProgress(0);
        pitchBar.setProgress(0);
        control.disableAllControls();
        control.enableAnalysisControls();
        isAnalysisDisplayed = true;
        isAnimationDisplayed = false;
        aDisplay.display(this, pStage, waveform, energyPlot, pitchPlot, spectrogramImage, areagramImage, label, fontSize);
        playBar.disablePlayControls();
        displayControls();
    }

    // For activating Animation window
    public void displayAnimation() throws FileNotFoundException {
        if (initial == 1) {
            initial++;
            if (isLeft) {
                scale = -1;
            }
        }
        animatePane = new HBox();
        control.disableAllControls();
        control.enableAnimationControls();
        isAnalysisDisplayed = false;
        isAnimationDisplayed = true;
        animDisplay.display(waveform, energyPlot, pitchPlot, slider, energyBar, pitchBar, fontSize, isLeft, this);
        if (isLeft) {
            if (scale == -1) {
                animDisplay.energybarTxt.setScaleX(-1);
                animDisplay.pitchbarTxt.setScaleX(-1);
            }
        }
        playBar.enablePlayControls();
        control.playControl();
        slider.setLowValue(0);
        counter = 0;
    }

    // For drawing Waveform,Energy and Pitch chart
    private LineChart<Number, Number> plot(ArrayList x, ArrayList y, String name, double minY, double maxY, String xlabel, String ylabel) {
        LineChart<Number, Number> chart;
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        chart = new LineChart<>(xAxis, yAxis);
        xAxis.setTickLabelsVisible(true);
        xAxis.setTickMarkVisible(true);
        xAxis.setMinorTickVisible(true);
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(Collections.max(waveform_x));
        xAxis.setTickUnit((xAxis.getUpperBound() + xAxis.getLowerBound()) / 5);
        xAxis.setTickLabelFont(Font.font(10));
        xAxis.setStyle("-fx-tick-label-fill: #FFFFFF");
        if (!name.equalsIgnoreCase("waveform")) {
            xAxis.setMinorTickVisible(false);
            xAxis.setTickMarkVisible(true);
            Set<Node> axisNode = chart.lookupAll(".axis");
            Set<Node> axisLabel = chart.lookupAll(".axis-tick-mark");
            for (final Node axis : axisNode) {
                axis.setStyle("-fx-tick-label-fill: #353535;");
                break;
            }
            for (final Node axis : axisLabel) {
                axis.setStyle("-fx-stroke: #353535;");
                break;
            }
        }
        chart.setCreateSymbols(false);
        chart.setLegendVisible(false);
        chart.setId(name);
        chart.setAnimated(false);
        chart.setCreateSymbols(false);
        chart.setAlternativeRowFillVisible(false);
        chart.setPrefSize(LayoutConstants.fullWidth, LayoutConstants.fullHeight);
        chart.setMinSize(0, 0);
        XYChart.Series series = new XYChart.Series();
        yAxis.setAutoRanging(false);
        chart.getYAxis().setTickLabelRotation(-90);
        chart.getYAxis().lookup(".axis-label").setStyle("-fx-label-padding: -10 0 10 0;");
//chart.getYAxis().lookup(".axis").setStyle("-fx-label-padding: 10 0 0 0;");
        yAxis.setLowerBound(minY);
        yAxis.setUpperBound(maxY);
        yAxis.setTickUnit((maxY - minY) / 2);
        yAxis.setMinorTickCount(2);
        yAxis.setTickLabelFont(Font.font("Monospaced", 10));
        labelConverter lConv;
        lConv = new labelConverter<>();
        //  yAxis.setTickLabelFormatter(lConv);
        int incre = (waveform_x.size() / 10000) + 1;
        for (int i = 0; i < x.size();) {
            if (!name.equalsIgnoreCase("waveform")) {
                series.getData().add(new XYChart.Data(x.get(i), y.get(i)));
                i++;
            } else {
                series.getData().add(new XYChart.Data(x.get(i), y.get(i)));
                i = i + incre;
            }
        }
        chart.getData().add(series);

        // For displaying cusror position values on mouse movement on graph
        chart.setOnMouseMoved((MouseEvent event) -> {
            Point2D pointInScene = new Point2D(event.getSceneX(), event.getSceneY());
            Axis<Number> xAxis1 = chart.getXAxis();
            Axis<Number> yAxis1 = chart.getYAxis();
            double xPosInAxis = xAxis1.sceneToLocal(new Point2D(pointInScene.getX(), 0)).getX();
            double yPosInAxis = yAxis1.sceneToLocal(new Point2D(0, pointInScene.getY())).getY();
            Number x1 = xAxis1.getValueForDisplay(xPosInAxis).floatValue();
            Number y1 = yAxis1.getValueForDisplay(yPosInAxis).floatValue();
            String xString = x1.toString();
            if (xString.length() > 5) {
                xString = xString.substring(0, 5);
            }
            String yString = y1.toString();
            if (yString.length() > 5) {
                yString = yString.substring(0, 5);
            }
            if (x1.doubleValue() >= 0 && x1.doubleValue() <= (waveform_x.size() / 10000.0) && y1.doubleValue() >= minY && y1.doubleValue() <= maxY) {
                label.setText("X =  " + xString + " , Y =  " + yString);
            } else {
                label.setText("X =   , Y =  ");
            }
        });
        chart.setOnMouseExited((MouseEvent e) -> {
            label.setText("X =   ,  Y =  ");
        });
        return chart;
    }

    // For flipping image left or right
    public void imgFlip() {
        isFlip = true;
        if (isImgLeft) {
            animDisplay.Box3.setScaleX(1);
            isImgLeft = false;
            animDisplay.energybarTxt.setScaleX(1);
            animDisplay.pitchbarTxt.setScaleX(1);
            scale = 1;
        } else {
            animDisplay.Box3.setScaleX(-1);
            animDisplay.energybarTxt.setScaleX(-1);
            animDisplay.pitchbarTxt.setScaleX(-1);
            scale = -1;
            isImgLeft = true;
        }
    }

    // For toggling energy scale from 20-60dB to 20-80dB 
    public void scaleEnergy() throws FileNotFoundException {
        ArrayList energy_data = new ArrayList();
        int i = 0;
        while (i < energy_y.size()) {
            double x = energy_y.get(i);
            if (x < 20) {
                x = 20;
                energy_data.add(x);
            } else {
                energy_data.add(x);
            }
            i++;
        }
        if (energyZoom) {
            energyScale = 80;
            energyPlot = plot(energy_x, energy_data, "energy", 20, energyScale, "Time (s)", "Level  (dB)");
            energyZoom = false;
        } else {
            energyScale = 60;
            energyPlot = plot(energy_x, energy_data, "energy", 20, energyScale, "Time (s)", "Level  (dB)");
            energyZoom = true;
        }
        if (isAnalysisDisplayed) {
            displayAnalysis();
        } else {
            animDisplay.energyPlot(energyPlot);
        }
        double energyValue = energy_y.get((int) counter);
        energyBar.setProgress(energyValue / energyScale);
    }

    // For showing or hidding place of articulation 
    public void circleDisplay() {
        if (isDisplay) {
            isDisplay = false;
        } else {
            isDisplay = true;
        }
        newBox.getChildren().clear();
        newBox.getChildren().add(animDisplay.baseImg);
        newBox.getChildren().add(animDisplay.polygon);
        newBox.getChildren().addAll(createControlAnchorsFor(animDisplay.polygon.getPoints()));
    }

    // For toggling energy and pitch bar display from vertical, horizontal and hidding 
    public void barDisplay() {
        previous_isBar = isBar;
        if (isBar) {
            animDisplay.barBox.setVisible(false);
            isBar = false;
        } else {
            animDisplay.barBox.setVisible(true);
            animDisplay.barBox.getChildren().clear();
            if (isHorizontal) {
                isHorizontal = false;
                isBar = true;
                animDisplay.barHorizontal();
            } else {
                isHorizontal = true;
                animDisplay.barVertical();
            }
            if (isLeft) {
                if (!isFlip) {
                    animDisplay.energybarTxt.setScaleX(-1);
                    animDisplay.pitchbarTxt.setScaleX(-1);
                }
            }
        }
    }

    // For toggling energy scale from 0-250Hz to 0-500Hz 
    public void scalePitch() throws FileNotFoundException {
        if (pitchZoom) {
            pitchScale = 250;
            pitchPlot = plot(pitch_x, pitch_y, "pitch", 0, pitchScale, "Time (s)", "Pitch  (Hz)");
            pitchZoom = false;
        } else {
            pitchScale = 500;
            pitchPlot = plot(pitch_x, pitch_y, "pitch", 0, pitchScale, "Time (s)", "Pitch  (Hz)");
            pitchZoom = true;
        }
        double pitchValue = pitch_y.get((int) counter / 2);
        pitchBar.setProgress(pitchValue / pitchScale);
        if (isAnalysisDisplayed) {
            displayAnalysis();
        } else {
            animDisplay.pitchPlot(pitchPlot);
        }
    }

    // For changing base image cyclically from man,woman,boy or girl 
    public void imgFace() throws FileNotFoundException {
        isLeftclick = true;
        isRightclick = true;
        if (faceCount == 0) {
            displayControls();
            face = new Image("/images/male1.png");
            Image base = face;
            baseImg = new ImageView(base);
            animDisplay.baseImg = baseImg;
            baseImg.setId("men");

        }
        if (faceCount == 1) {
            displayControls();
            face = new Image("/images/female9.png");
            Image base = face;
            baseImg = new ImageView(base);
            animDisplay.baseImg = baseImg;
            baseImg.setId("women");

        }
        if (faceCount == 2) {
            displayControls();
            face = new Image("/images/male child.png");
            Image base = face;
            baseImg = new ImageView(base);
            animDisplay.baseImg = baseImg;
            baseImg.setId("boy");

        }
        if (faceCount == 3) {
            displayControls();
            face = new Image("/images/girl child1.png");
            faceCount = 0;
            Image base = face;
            baseImg = new ImageView(base);
            animDisplay.baseImg = baseImg;
            baseImg.setId("girl");

        } else {
            faceCount++;
        }
        baseImg.setFitHeight(animDisplay.graphWidth * 0.65);
        baseImg.setFitWidth(animDisplay.graphWidth * 0.67);
        newBox.getChildren().clear();
        if (!isDisplay) {
            newBox.getChildren().add(baseImg);
            newBox.getChildren().add(animDisplay.polygon);
        } else {
            newBox.getChildren().add(animDisplay.baseImg);
            newBox.getChildren().add(animDisplay.polygon);
            newBox.getChildren().addAll(createControlAnchorsFor(animDisplay.polygon.getPoints()));
        }
    }

    public void displayControls() {
        control.getChildren().clear();
        if (isLeft) {
            control.add(control.MessageVB, 0, 0);
            control.add(control.PitchVB, 1, 0);
            control.add(control.EnergyVB, 2, 0);
            control.add(control.BarVB, 4, 0);
            control.add(control.CircleVB, 5, 0);
            if (isLeftclick) {
                if (faceCount == 0) {
                    control.add(control.ManFaceVB, 7, 0);
                }
                if (faceCount == 1) {
                    control.add(control.WomanFaceVB, 7, 0);
                }
                if (faceCount == 2) {
                    control.add(control.BoyFaceVB, 7, 0);
                }
                if (faceCount == 3) {
                    control.add(control.GirlFaceVB, 7, 0);
                }
            } else {
                control.add(control.GirlFaceVB, 7, 0);
            }

            control.add(control.FlipVB, 8, 0);
//            control.add(control.SoundVB, 10, 0);
            control.add(control.PlayVB, 12, 0);
            control.add(control.RestartVB, 13, 0);
        } else {
            control.setAlignment(Pos.CENTER_RIGHT);
            control.add(control.MessageVB, 13, 0);
            control.add(control.PitchVB, 12, 0);
            control.add(control.EnergyVB, 11, 0);
            control.add(control.BarVB, 9, 0);
            control.add(control.CircleVB, 8, 0);
            if (isRightclick) {

                if (faceCount == 0) {
                    control.add(control.ManFaceVB, 6, 0);
                }
                if (faceCount == 1) {
                    control.add(control.WomanFaceVB, 6, 0);
                }
                if (faceCount == 2) {
                    control.add(control.BoyFaceVB, 6, 0);
                }
                if (faceCount == 3) {
                    control.add(control.GirlFaceVB, 6, 0);
                }
            } else {
                control.add(control.WomanFaceVB, 6, 0);
            }

            control.add(control.FlipVB, 5, 0);
            //   control.add(control.SoundVB, 3, 0);
            control.add(control.RestartVB, 1, 0);
            control.add(control.PlayVB, 0, 0);
        }
    }

    public void enableButtons() {
        //     control.SoundVB.setDisable(false);
        control.FlipVB.setDisable(false);
        control.EnergyVB.setDisable(false);
        control.PitchVB.setDisable(false);
        control.CircleVB.setDisable(false);
        control.BarVB.setDisable(false);
        control.ManFaceVB.setDisable(false);
        control.WomanFaceVB.setDisable(false);
        control.BoyFaceVB.setDisable(false);
        control.GirlFaceVB.setDisable(false);

        control.PlayVB.setDisable(true);
        control.RestartVB.setDisable(false);
        control.MessageVB.setDisable(false);
    }

    public void playControls() {
        control.getChildren().clear();

        if (isLeft) {
            control.add(control.MessageVB, 0, 0);
            control.add(control.PitchVB, 1, 0);
            control.add(control.EnergyVB, 2, 0);
            control.add(control.BarVB, 4, 0);
            control.add(control.CircleVB, 5, 0);

            if (baseImg.getId().contains("men")) {
                control.add(control.ManFaceVB, 7, 0);
            }
            if (baseImg.getId().contains("women")) {
                control.add(control.WomanFaceVB, 7, 0);
            }
            if (baseImg.getId().contains("boy")) {
                control.add(control.BoyFaceVB, 7, 0);
            }
            if (baseImg.getId().contains("girl")) {
                control.add(control.GirlFaceVB, 7, 0);
            }

            control.add(control.FlipVB, 8, 0);
//            control.add(control.SoundVB, 10, 0);
            control.add(control.PlayVB, 12, 0);
            control.add(control.RestartVB, 13, 0);

        } else {

            control.setAlignment(Pos.CENTER_RIGHT);
            control.add(control.MessageVB, 13, 0);
            control.add(control.PitchVB, 12, 0);
            control.add(control.EnergyVB, 11, 0);
            control.add(control.BarVB, 9, 0);
            control.add(control.CircleVB, 8, 0);

            if (baseImg.getId().contains("men")) {
                control.add(control.ManFaceVB, 6, 0);
            }
            if (baseImg.getId().contains("women")) {
                control.add(control.WomanFaceVB, 6, 0);
            }
            if (baseImg.getId().contains("boy")) {
                control.add(control.BoyFaceVB, 6, 0);
            }
            if (baseImg.getId().contains("girl")) {
                control.add(control.GirlFaceVB, 6, 0);

            }

            control.add(control.FlipVB, 5, 0);
            //   control.add(control.SoundVB, 3, 0);
            control.add(control.RestartVB, 1, 0);
            control.add(control.PlayVB, 0, 0);
        }
    }

    // For playing animation 
    public void play(int val) {
        newBox.getChildren().clear();
        int max_frame = data_jaw_x.length;
        double max_time = (objVisual.getDataWave_x().size() / 10000.0);
        double low = slider.getLowValue() + 0.1;
        double high = slider.getHighValue();
        playBar.commonPlayVB.setDisable(true);
        playBar.commonRestartVB.setDisable(true);
        if (playBar.studAnalyzer.control.timeline != null && playBar.refAnalyzer.control.timeline != null) {
            playBar.commonRestartVB.setDisable(false);
            playBar.commonPlayVB.setDisable(false);

        }
        if (low == high) {
            if ((high + 0.1) >= max_time && control.timeline != null) {
                if (control.clip != null) {
                    control.clip.stop();
                }
                control.timeline.pause();
                control.timeline = null;
            }
            if (control.timeline != null) {
                control.timeline.pause();
            }
            double studAnalyzer_low = playBar.studAnalyzer.slider.getLowValue();
            double studAnalyzer_high = playBar.studAnalyzer.slider.getHighValue();
            double refAnalyzer_low = playBar.refAnalyzer.slider.getLowValue();
            double refAnalyzer_high = playBar.refAnalyzer.slider.getHighValue();

            double stud_diff = studAnalyzer_high - studAnalyzer_low;
            double ref_diff = refAnalyzer_high - refAnalyzer_low;

            DecimalFormat df = new DecimalFormat("#.###");

            if (df.format(stud_diff).equals("0.1") && df.format(ref_diff).equals("0.1")) {
                playBar.commonPlayVB.setDisable(false);
                playBar.commonRestartVB.setDisable(false);
            }
            if (playBar.studAnalyzer.control.timeline == null && playBar.refAnalyzer.control.timeline == null) {
                playBar.specialPane.getChildren().clear();
                HBox common = new HBox();
                common.setSpacing(3);
                common.getChildren().addAll(playBar.commonPlayVB, playBar.commonRestartVB);
                playBar.specialPane.add(playBar.studControl, 0, 1);
                playBar.specialPane.add(common, 7, 1);
                playBar.specialPane.add(playBar.refControl, 12, 1);
            }
            enableButtons();
            playControls();
        }
        animDisplay.polygon.getPoints().clear();
        List<Double> values = new ArrayList<>();
        max_minpoaX = new ArrayList<>();
        if (counter == max_frame - 1) {
            counter = 0;
        }
        counter = Math.round(counter);

        //Playing animation at diffreent frame rate in normal speed and slow speed
        if (val == 0) {
            if (max_frame - 1 == counter) {
                control.timeline.stop();
                counter = 0;
                displayControls();
                enableButtons();
            }
            if (counter == 0.0) {
                slider.setLowValue(0.0);
            }
            for (int x = 0; x < 34; x++) {
                values.add(data_jaw_x[(int) Math.round(counter)][x] * animDisplay.imgWidth / 425.0);
                values.add(data_jaw_y[(int) Math.round(counter)][x] * animDisplay.imgHeight / 450.0);
                max_minpoaX.add(data_jaw_x[(int) Math.round(counter)][x] * animDisplay.imgWidth / 425.0);
            }
            animDisplay.polygon.getPoints().clear();
            animDisplay.polygon.getPoints().setAll(values);
            newBox.getChildren().add(animDisplay.baseImg);
            newBox.getChildren().add(animDisplay.polygon);
            newBox.getChildren().addAll(createControlAnchorsFor(animDisplay.polygon.getPoints()));
            slider.setLowValue(slider.getLowValue() + slide_val_1);
            counter = slider.getLowValue() * (objVisual.getMat_x().length / (objVisual.getDataWave_x().size() / 10000.0));
        }
        // Playing animation at frame rate of 25frame/sec and averaging 3 frames to play at faster speed
        if (val == 2) {
            if (max_frame - 3 <= counter) {
                control.timeline.stop();
                displayControls();
                counter = 0.0;
                enableButtons();
            }
            if (counter == 0.0) {
                slider.setLowValue(0.0);
            }
            for (int z = 0; z < 34; z++) {
                double sum_x = 0, sum_y = 0;
                for (int y = (int) counter; y <= counter + 2; y++) {
                    sum_x = sum_x + (data_jaw_x[y][z] * animDisplay.imgWidth / 425.0);
                    sum_y = sum_y + (data_jaw_y[y][z] * animDisplay.imgHeight / 450.0);
                }
                values.add(sum_x / 3.0);
                values.add(sum_y / 3.0);
                max_minpoaX.add(sum_x / 3.0);
            }
            animDisplay.polygon.getPoints().clear();
            animDisplay.polygon.getPoints().setAll(values);
            newBox.getChildren().add(animDisplay.baseImg);
            newBox.getChildren().add(animDisplay.polygon);
            newBox.getChildren().addAll(createControlAnchorsFor(animDisplay.polygon.getPoints()));
            slider.setLowValue(slider.getLowValue() + slide_val_3);
            counter = slider.getLowValue() * (objVisual.getMat_x().length / (objVisual.getDataWave_x().size() / 10000.0));
        }
        // Playing animation at frame rate of 25frame/sec and averaging 5 frames to play at faster speed
        if (val == 4) {
            if (max_frame - 5 <= counter) {
                control.timeline.stop();
                counter = 0;
                displayControls();
                enableButtons();
            }
            if (counter == 0.0) {
                slider.setLowValue(0.0);
            }
            for (int z = 0; z < 34; z++) {
                double sum_x = 0, sum_y = 0;
                for (int y = (int) counter; y <= counter + 4 && y < max_frame; y++) {
                    sum_x = sum_x + (data_jaw_x[y][z] * animDisplay.imgWidth / 425.0);
                    sum_y = sum_y + (data_jaw_y[y][z] * animDisplay.imgHeight / 450.0);
                }
                values.add(sum_x / 5.0);
                values.add(sum_y / 5.0);
                max_minpoaX.add(sum_x / 5.0);

            }
            animDisplay.polygon.getPoints().clear();
            animDisplay.polygon.getPoints().setAll(values);
            newBox.getChildren().add(animDisplay.baseImg);
            newBox.getChildren().add(animDisplay.polygon);
            newBox.getChildren().addAll(createControlAnchorsFor(animDisplay.polygon.getPoints()));
            slider.setLowValue(slider.getLowValue() + slide_val_5);
            counter = slider.getLowValue() * (objVisual.getMat_x().length / (objVisual.getDataWave_x().size() / 10000.0));
        }
        if (energy_y.size() > counter) {
            double energyValue = energy_y.get((int) counter);
            energyBar.setProgress(energyValue / energyScale);
        }
        if (pitch_y.size() > counter / 2) {
            double pitchValue = pitch_y.get((int) counter / 2);
            pitchBar.setProgress(pitchValue / pitchScale);
        }
    }

    // For placing small red circle on ploygon as place of articulation
    private ObservableList<Anchor> createControlAnchorsFor(final ObservableList<Double> points) {
        anchors = FXCollections.observableArrayList();
        double xCord = poa_x.get((int) counter) * animDisplay.imgWidth / 425.0;
        double Xdistance = Math.abs((max_minpoaX.get(0)) - xCord);
        int idx = 0;
        for (int c = 19; c < 34; c++) {
            double cdistance = Math.abs((max_minpoaX.get(c)) - xCord);
            if (cdistance < Xdistance) {
                idx = c;
                Xdistance = cdistance;
            }
        }
        double newXcord = (max_minpoaX.get(idx));
        for (int i = 0; i < points.size(); i += 2) {
            if (points.get(i) == newXcord) {
                DoubleProperty xProperty = new SimpleDoubleProperty(points.get(i));
                DoubleProperty yProperty = new SimpleDoubleProperty(points.get(i + 1));
                anchors.add(new Anchor(xProperty, yProperty));
                break;
            }
        }
        return anchors;
    }

    class Anchor extends Circle {

        private final DoubleProperty x, y;

        Anchor(DoubleProperty x, DoubleProperty y) {
            super(x.get(), y.get() + 8.0, 10);
            if (isDisplay) {
                setRadius(animDisplay.imgWidth / 60);
            } else {
                setRadius(0);
            }
            setFill(Color.BROWN);
            setStrokeWidth(2);
            setStrokeType(StrokeType.OUTSIDE);
            this.x = x;
            this.y = y;
            x.bind(centerXProperty());
            y.bind(centerYProperty());
        }
    }

    // For playing animation alongwith audio 
    public void sound() throws UnsupportedAudioFileException, IOException, LineUnavailableException, Exception {
        if (timeline != null) {
            timeline.stop();
        }
        if (control.timeline != null) {
            control.timeline.stop();
            displayControls();
        }
        control.disableAllControls();
        point = 0;
        ArrayList<Double> temp = objVisual.getDataWave_y();
        double[] tempSound = new double[temp.size()];
        int i = 0;
        for (i = 0; i < temp.size(); i++) {
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
        clip.start();
        timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(25), ae -> animate()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();
    }

    public void animate() {
        int max_frame = data_jaw_x.length;
        if (point == 0) {
            clip.start();
        }
        List<Double> values = new ArrayList<>();
        max_minpoaX = new ArrayList<>();
        for (int z = 0; z < 34; z++) {
            double sum_x = 0, sum_y = 0;
            for (int y = (int) point; y <= point + 4; y++) {
                sum_x = sum_x + (data_jaw_x[y][z] * animDisplay.imgWidth / 425.0);
                sum_y = sum_y + (data_jaw_y[y][z] * animDisplay.imgHeight / 450.0);
            }
            values.add(sum_x / 5.0);
            values.add(sum_y / 5.0);
            max_minpoaX.add(sum_x / 5.0);
        }
        animDisplay.polygon.getPoints().clear();
        animDisplay.polygon.getPoints().setAll(values);
        newBox.getChildren().clear();
        newBox.getChildren().add(animDisplay.baseImg);
        newBox.getChildren().add(animDisplay.polygon);

        newBox.getChildren().addAll(createControlAnchorsSound(animDisplay.polygon.getPoints()));
        point = point + 5;
        if (max_frame - 1 <= point + 5) {
            point = 0;
            timeline.stop();
            control.enableAnimationControls();
            control.enableAnalysisControls();
        }
    }

    private ObservableList<Anchor> createControlAnchorsSound(final ObservableList<Double> points) {
        anchors = FXCollections.observableArrayList();
        double xCord = poa_x.get((int) point) * animDisplay.imgWidth / 425.0;
        double Xdistance = Math.abs((max_minpoaX.get(0)) - xCord);
        int idx = 0;
        for (int c = 19; c < 34; c++) {
            double cdistance = Math.abs((max_minpoaX.get(c)) - xCord);
            if (cdistance < Xdistance) {
                idx = c;
                Xdistance = cdistance;
            }
        }
        double newXcord = (max_minpoaX.get(idx));
        for (int i = 0; i < points.size(); i += 2) {
            if (points.get(i) == newXcord) {
                DoubleProperty xProperty = new SimpleDoubleProperty(points.get(i));
                DoubleProperty yProperty = new SimpleDoubleProperty(points.get(i + 1));
                anchors.add(new Anchor(xProperty, yProperty));
                break;
            }
        }
        return anchors;
    }

    // For chaning height and width of components as screen gets mazimized or minimized
    public void windowResized(boolean isFullScreen) throws FileNotFoundException {
        if (isFullScreen) {
            content.setMinSize((LayoutConstants.minWidth - (2 * LayoutConstants.menuBarWidth)) / 2, LayoutConstants.minHeight - (LayoutConstants.titleBarHeight + LayoutConstants.playBarHeight + 10) - 30);
            content.setMaxSize((LayoutConstants.minWidth - (2 * LayoutConstants.menuBarWidth)) / 2, LayoutConstants.minHeight - (LayoutConstants.titleBarHeight + LayoutConstants.playBarHeight + 10) - 30);
            content.setPrefSize((LayoutConstants.minWidth - (2 * LayoutConstants.menuBarWidth)) / 2, LayoutConstants.minHeight - (LayoutConstants.titleBarHeight + LayoutConstants.playBarHeight + 10) - 30);
            LayoutConstants.lineWidth = LayoutConstants.lineWidth * 2;
            control.PlayVB.setMinWidth(LayoutConstants.fullWidth / 25);
            control.PlayVB.setMaxWidth(LayoutConstants.fullWidth / 25);
            control.PauseVB.setMinWidth(LayoutConstants.fullWidth / 25);
            control.PauseVB.setMaxWidth(LayoutConstants.fullWidth / 25);
            control.RestartVB.setMinWidth(LayoutConstants.fullWidth / 25);
            control.RestartVB.setMaxWidth(LayoutConstants.fullWidth / 25);
            control.BarVB.setMinWidth(LayoutConstants.fullWidth / 25);
            control.BarVB.setMaxWidth(LayoutConstants.fullWidth / 25);
//            control.SoundVB.setMinWidth(LayoutConstants.fullWidth / 28);
            //   control.SoundVB.setMaxWidth(LayoutConstants.fullWidth / 28);
            control.FlipVB.setMinWidth(LayoutConstants.fullWidth / 25);
            control.FlipVB.setMaxWidth(LayoutConstants.fullWidth / 25);
            control.EnergyVB.setMinWidth(LayoutConstants.fullWidth / 25);
            control.EnergyVB.setMaxWidth(LayoutConstants.fullWidth / 25);
            control.PitchVB.setMinWidth(LayoutConstants.fullWidth / 25);
            control.PitchVB.setMaxWidth(LayoutConstants.fullWidth / 25);
            control.CircleVB.setMinWidth(LayoutConstants.fullWidth / 25);
            control.CircleVB.setMaxWidth(LayoutConstants.fullWidth / 25);
            control.WomanFaceVB.setMinWidth(LayoutConstants.fullWidth / 25);
            control.WomanFaceVB.setMaxWidth(LayoutConstants.fullWidth / 25);
            control.ManFaceVB.setMaxWidth(LayoutConstants.fullWidth / 25);
            control.ManFaceVB.setMinWidth(LayoutConstants.fullWidth / 25);
            control.ManFaceVB.setPrefWidth(LayoutConstants.fullWidth / 25);

            control.BoyFaceVB.setMaxWidth(LayoutConstants.fullWidth / 25);
            control.BoyFaceVB.setMinWidth(LayoutConstants.fullWidth / 25);

            control.GirlFaceVB.setMaxWidth(LayoutConstants.fullWidth / 25);
            control.GirlFaceVB.setMinWidth(LayoutConstants.fullWidth / 25);

            control.MessageVB.setMinWidth(LayoutConstants.fullWidth / 25);
            control.MessageVB.setMaxWidth(LayoutConstants.fullWidth / 25);
            playBar.commonPlayVB.setMinWidth(LayoutConstants.fullWidth / 25);
            playBar.commonPlayVB.setMaxWidth(LayoutConstants.fullWidth / 25);
            playBar.commonPauseVB.setMinWidth(LayoutConstants.fullWidth / 25);
            playBar.commonPauseVB.setMaxWidth(LayoutConstants.fullWidth / 25);
            playBar.commonRestartVB.setMinWidth(LayoutConstants.fullWidth / 25);
            playBar.commonRestartVB.setMaxWidth(LayoutConstants.fullWidth / 25);

        } else {
            content.setMinSize((LayoutConstants.fullWidth - (2 * LayoutConstants.menuBarWidth)) / 2, LayoutConstants.fullHeight - (LayoutConstants.titleBarHeight + LayoutConstants.playBarHeight * 1.5));
            content.setMaxSize((LayoutConstants.fullWidth - (2 * LayoutConstants.menuBarWidth)) / 2, LayoutConstants.fullHeight - (LayoutConstants.titleBarHeight + LayoutConstants.playBarHeight * 1.5));
            content.setPrefSize((LayoutConstants.fullWidth - (2 * LayoutConstants.menuBarWidth)) / 2, LayoutConstants.fullHeight - (LayoutConstants.titleBarHeight + LayoutConstants.playBarHeight * 1.5));
            LayoutConstants.lineWidth = LayoutConstants.minWidth / 50;
            control.PlayVB.setMinWidth(content.getMaxWidth() / 11);
            control.PlayVB.setMaxWidth(content.getMaxWidth() / 11);
            control.PauseVB.setMinWidth(content.getMaxWidth() / 11);
            control.PauseVB.setMaxWidth(content.getMaxWidth() / 11);
            control.RestartVB.setMinWidth(content.getMaxWidth() / 11);
            control.RestartVB.setMaxWidth(content.getMaxWidth() / 11);
            control.BarVB.setMinWidth(content.getMaxWidth() / 11);
            control.BarVB.setMaxWidth(content.getMaxWidth() / 11);
            //  control.SoundVB.setMinWidth(content.getMaxWidth() / 11);
            // control.SoundVB.setMaxWidth(content.getMaxWidth() / 11);
            control.FlipVB.setMinWidth(content.getMaxWidth() / 11);
            control.FlipVB.setMaxWidth(content.getMaxWidth() / 11);
            control.EnergyVB.setMinWidth(content.getMaxWidth() / 11);
            control.EnergyVB.setMaxWidth(content.getMaxWidth() / 11);
            control.PitchVB.setMinWidth(content.getMaxWidth() / 11);
            control.PitchVB.setMaxWidth(content.getMaxWidth() / 11);
            control.CircleVB.setMinWidth(content.getMaxWidth() / 11);
            control.CircleVB.setMaxWidth(content.getMaxWidth() / 11);
            control.ManFaceVB.setMinWidth(content.getMaxWidth() / 11);
            control.ManFaceVB.setMaxWidth(content.getMaxWidth() / 11);

            control.WomanFaceVB.setMaxWidth(content.getMaxWidth() / 11);
            control.WomanFaceVB.setMinWidth(content.getMaxWidth() / 11);

            control.GirlFaceVB.setMaxWidth(content.getMaxWidth() / 11);
            control.GirlFaceVB.setMinWidth(content.getMaxWidth() / 11);

            control.BoyFaceVB.setMaxWidth(content.getMaxWidth() / 11);
            control.BoyFaceVB.setMinWidth(content.getMaxWidth() / 11);

            control.MessageVB.setMinWidth(content.getMaxWidth() / 11);
            control.MessageVB.setMaxWidth(content.getMaxWidth() / 11);
            playBar.commonPlayVB.setMinWidth(content.getMaxWidth() / 11);
            playBar.commonPlayVB.setMaxWidth(content.getMaxWidth() / 11);
            playBar.commonPauseVB.setMinWidth(content.getMaxWidth() / 11);
            playBar.commonPauseVB.setMaxWidth(content.getMaxWidth() / 11);
            playBar.commonRestartVB.setMinWidth(content.getMaxWidth() / 11);
            playBar.commonRestartVB.setMaxWidth(content.getMaxWidth() / 11);
        }
        if (isAnalysisDisplayed) {
            displayAnalysis();
        }
        if (isAnimationDisplayed) {
            displayAnimation();
        }
    }

    public void setPlayBar(PlayerBar playBar) {
        this.playBar = playBar;
    }

    public PlayerBar getPlayBar() {
        return playBar;
    }

    public void setMenuBar(MenuBar menuBar) {
        this.menuBar = menuBar;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }
//For adding spaces on y-axis label on  chart

    private class labelConverter<T> extends StringConverter<T> {

        @Override
        public String toString(T t) {
            Double tb = (Double) t;
            Integer ta = tb.intValue();
            String inString = ta.toString();
            String outString = "";
            for (int i = 0; i < 4 - inString.length(); i++) {
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
}
