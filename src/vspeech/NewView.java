/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vspeech;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import static com.sun.jna.platform.win32.WinUser.GWL_STYLE;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;  
import javafx.stage.StageStyle;

/**
 *
 * Main Program window for creating two different panels and bottom icon bar
 */
public class NewView extends Application {

    private Visual objVisual;
    private VisualAction objVisualAction;
    public static Stage visualStage;
    private Scene scene;
    private VBox root;
    private GridPane sections;
    public static Analyzer studTemp, refTemp;
    public PlayerBar playBar;
    private TitleBar topContainer;
    public static DoubleProperty fontSize = new SimpleDoubleProperty();
    private static double xOffset = 0;
    private static double yOffset = 0;
    private String temp;
    public static Path path;

    public NewView() throws IOException, URISyntaxException, InterruptedException {
        ColorConstants color = new ColorConstants();

        Vspeech vsts = new Vspeech();
        visualStage = new Stage();
        visualStage.getIcons().add(new Image("/images/vsts_logo.png"));

        objVisual = new Visual();
        objVisualAction = new VisualAction(objVisual);

        //For dragging main screen window
        root = dragPanel(visualStage);

        root.setStyle("-fx-background-color:rgba(87,87,87,1);");
        scene = new Scene(root, LayoutConstants.minWidth, LayoutConstants.minHeight, Color.DARKGRAY);
        scene.getStylesheets().add(NewView.class.getResource("/css/style.css").toExternalForm());
        LayoutConstants.lineWidth = LayoutConstants.minWidth / 50;

        //For setting default font size of text 
        fontSize.bind(scene.widthProperty().add(scene.heightProperty()).divide(150));

        topContainer = new TitleBar("VSTS (Visual Speech Training System) v.1.7", visualStage, this);

        //for creating left side panel
        studTemp = new Analyzer(true, visualStage, fontSize);

        //For creating right side panel
        refTemp = new Analyzer(false, visualStage, fontSize);

        sections = new GridPane();
        sections.add(studTemp, 0, 0);
        sections.add(refTemp, 1, 0);

        //For displaying Player bar controls
        playBar = new PlayerBar(studTemp, refTemp, LayoutConstants.fullHeight, LayoutConstants.fullWidth, studTemp.getControl(), refTemp.getControl());

        //For creating left side player controls
        studTemp.setPlayBar(playBar);

        //For creating right side player controls
        refTemp.setPlayBar(playBar);

        root.getChildren().addAll(topContainer, sections, playBar);
        visualStage.setScene(scene);
        visualStage.setTitle("VSTS_(Visual Speech Training System)v.0.1 \u00A9 Media Lab Asia");
        visualStage.initStyle(StageStyle.UNDECORATED);
        visualStage.initModality(Modality.APPLICATION_MODAL);
        visualStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        visualStage.show();
        long lhwnd = com.sun.glass.ui.Window.getWindows().get(0).getNativeWindow();
        Pointer lpVoid = new Pointer(lhwnd);
        HWND hwnd = new HWND(lpVoid);
        final User32 user32 = User32.INSTANCE;
        int oldStyle = user32.GetWindowLong(hwnd, GWL_STYLE);
        int newStyle = oldStyle | 0x00020000;
        user32.SetWindowLong(hwnd, GWL_STYLE, newStyle);
        final Class<?> referenceClass = NewView.class;
        final URL url = referenceClass.getProtectionDomain().getCodeSource().getLocation();

        //For creating session folder for storing saved sounds
        Date date = new Date();
        DecimalFormat mFormat = new DecimalFormat("00");
        DateFormat df = new SimpleDateFormat("ddMMyy");
        temp = "rec_" + df.format(new Date()) + "_" + mFormat.format(Double.valueOf(date.getHours())) + "" + mFormat.format(Double.valueOf(date.getMinutes())) + "" + mFormat.format(Double.valueOf(date.getSeconds()));
        final File jarPath = new File(url.toURI()).getParentFile();

        if (vsts.jar) {
            path = Paths.get(jarPath.getAbsolutePath() + "/recorded_sound/" + temp);
        } else {
            path = Paths.get("/recorded_sound/" + temp);
        }

        Files.createDirectories(path);
       studTemp.initial_display();
        refTemp.initial_display();
        visualStage.getIcons().add(new Image("/images/vsts_logo.png"));

    }

    public static VBox dragPanel(final Stage primaryStage) {
        VBox bp = new VBox();
        bp.setPrefSize(900, 500);
        bp.setMaxSize(900, 500);
        HBox thb = new HBox(10); // Set spacing between each child into the HBox
        thb.setPadding(new Insets(15, 15, 15, 15));
        HBox bhb = new HBox(10); // Set spacing between each child into the HBox
        bhb.setPadding(new Insets(15, 15, 15, 15));
        bp.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        bp.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            }
        });
        return bp;
    }

    public void windowResized() throws FileNotFoundException {
        studTemp.windowResized(visualStage.isFullScreen());
        refTemp.windowResized(visualStage.isFullScreen());
    }

    private NewView(Stage primaryStage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void start(Stage primaryStage) {
        new NewView(primaryStage);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static void main(String[] args) {
        launch(args);
    }

}
