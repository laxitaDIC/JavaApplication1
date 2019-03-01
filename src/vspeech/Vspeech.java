 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vspeech;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
/**
 *
 * 
 * Application Main class
 */
public class Vspeech extends Application {

    private VBox Start;
    private VBox Exit;
    private VBox Manual;
    private DropShadow shadow;
    public boolean jar = false  ; // true if want to execute application from jar file otherwise false

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image("/images/vsts_logo.png"));
        primaryStage.setTitle("VSTS (Visual Speech Training System) v.1.7");
        StackPane root = new StackPane();
        Image image = new Image("/images/welcome_screen_05.jpg");
        Scene scene = new Scene(root, LayoutConstants.minWidth, LayoutConstants.minHeight);
        scene.getStylesheets().add(Vspeech.class.getResource("/css/style.css").toExternalForm());
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(25, 25, 25, 25));

        Manual = new VBox();
        Manual.getChildren().addAll(new ImageView("/images/manual_1.png"));
        Manual.setPrefWidth(80);
        Manual.getStyleClass().addAll("VBox");
        grid.add(Manual, 9, 4);
        Start = new VBox();
        Start.getChildren().addAll(new ImageView("/images/start_1.png"));
        Start.setPrefWidth(80);
        Start.getStyleClass().addAll("VBox");
        grid.add(Start, 9, 8);

        Exit = new VBox();
        Exit.getChildren().addAll(new ImageView("/images/exit_1.png"));
        Exit.setPrefWidth(80);
        Exit.getStyleClass().addAll("VBox");
        grid.add(Exit, 9, 12);
        Start.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            try {
                primaryStage.close();
                NewView view = new NewView();
            } catch (Exception ex) {
                ex.printStackTrace();
                new newLogging("SEVERE", Vspeech.class.getName(), "exit()", ex);
            }
        });
        Exit.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            try {
                System.exit(0);
            } catch (Exception ex) {
                new newLogging("SEVERE", Vspeech.class.getName(), "exit()", ex);
            }
        });
        Manual.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            try {
                final Class<?> referenceClass = NewView.class;
                final URL url = referenceClass.getProtectionDomain().getCodeSource().getLocation();
                final File jarPath = new File(url.toURI()).getParentFile();
                WebView contentHelp = new WebView();
                contentHelp.setPrefSize(610, 610);
                String mypath = Paths.get("src").toAbsolutePath().normalize().toString();
                if (jar) {
                    contentHelp.getEngine().load("file:///" + jarPath.getAbsolutePath() + "/help/VSTS_ver1.6_User_Manual.html");
                } else {
                    contentHelp.getEngine().load("file:///" + mypath + "/help/VSTS_ver1.6_User_Manual.html");
                }
                Stage dialogStage = new Stage();
                dialogStage.initStyle(StageStyle.UTILITY);
                dialogStage.initOwner(primaryStage);
                dialogStage.setScene(new Scene(contentHelp));
                dialogStage.setTitle("User Manual");
                dialogStage.showAndWait();
            } catch (URISyntaxException ex) {
                System.out.println(ex);
                new newLogging("SEVERE", Vspeech.class.getName(), "manual()", ex);
            }
        });
        grid.setPadding(new Insets(-50, 0, 0, 909 / 1.8));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(909);
        imageView.setFitHeight(701);
        root.getChildren().addAll(imageView, grid);
        display(Start);
        display(Manual);
        display(Exit);
        primaryStage.setResizable(false);
        primaryStage.setWidth(909);
        primaryStage.setHeight(715);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void display(VBox box) {
        shadow = new DropShadow();
        box.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                box.setStyle("-fx-background-color:transparent;");
                box.setEffect(shadow);
            }
        });
        box.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                box.setStyle("-fx-background-color:transparent;");
                box.setEffect(null);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
