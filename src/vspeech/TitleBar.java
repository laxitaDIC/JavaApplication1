/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vspeech;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

// For creating Title Bar

public class TitleBar extends HBox {

    private final Glow shadow;
    Screen screen = Screen.getPrimary();
    Rectangle2D bounds = screen.getVisualBounds();
    Vspeech vsts;

    public TitleBar(String titleString, Stage parent, NewView parentView) {
        vsts = new Vspeech();
        setStyle("-fx-background-color:" + ColorConstants.title + ";-fx-border-width: 4 3 5 3;-fx-border-color: " + ColorConstants.border + ";");
        Label title = new Label(titleString);
        title.setPadding(new Insets(0, 0, 0, 10));
        title.setStyle("-fx-wrap-text:true; -fx-text-alignment: left; -fx-alignment: center-left; -fx-font-size: 10pt; -fx-font-family: \"Segoe UI Semibold\"; -fx-text-fill: #FFFFFF;");
        title.setPrefWidth(LayoutConstants.fullWidth);
        GridPane title_pane = new GridPane();
        title_pane.add(title, 0, 0);
        HBox imageContainer = new HBox();
        imageContainer.setSpacing(2);
        VBox minButton = new VBox();                //Minimize button
        minButton.setStyle("-fx-background-color:rgb(0,0,0,1);-fx-border-width: 1 1 1 1;-fx-border-color: #FFFFFF;  -fx-border-radius: 0 0 0 5; ");
        minButton.getChildren().add(new ImageView("/images/minimize.jpg"));
        VBox maxButton = new VBox();               //Maximize Button
        maxButton.setStyle("-fx-background-color:rgb(0,0,0,1);-fx-border-width: 1 1 1 0;-fx-border-color: #FFFFFF;");
        maxButton.getChildren().add(new ImageView("/images/maximize_new.png"));
        VBox closeButton = new VBox();              //Close Button
        closeButton.setStyle("-fx-background-color:rgb(150,0,0,1);-fx-border-width: 1 1 1 0;-fx-border-color: #FFFFFF;-fx-border-radius: 0 0 5 0;");
        closeButton.getChildren().add(new ImageView("/images/close.png"));
        VBox colorButton = new VBox();              //Color Button
        colorButton.setStyle("-fx-border-width: 0 0 0 0;-fx-border-color: #FFFFFF;-fx-border-radius: 0 0 0 0; -fx-padding:0 10 0 0;");
        colorButton.getChildren().add(new ImageView("/images/color1.png"));

        VBox manualButton = new VBox();             //Manual Button
        manualButton.setStyle("-fx-border-width: 0 0 0 0;-fx-border-color: #FFFFFF;-fx-border-radius: 0 0 0 0; -fx-padding:0 5 0 0;");
        manualButton.getChildren().add(new ImageView("/images/book3.png"));

        imageContainer.getChildren().addAll(manualButton, colorButton, minButton, maxButton, closeButton);
        title_pane.add(imageContainer, 1, 0);

        manualButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            try {
                final Class<?> referenceClass = NewView.class;
                final URL url = referenceClass.getProtectionDomain().getCodeSource().getLocation();

                final File jarPath = new File(url.toURI()).getParentFile();

                WebView contentHelp = new WebView();
                contentHelp.setPrefSize(610, 610);
                String mypath = Paths.get("src").toAbsolutePath().normalize().toString();
                if (vsts.jar) {
                    contentHelp.getEngine().load("file:///" + jarPath.getAbsolutePath() + "/help/VSTS_ver1.6_User_Manual.html");
                } else {
                    contentHelp.getEngine().load("file:///" + mypath + "/help/VSTS_ver1.6_User_Manual.html");
                }
                Stage dialogStage = new Stage();
                dialogStage.initStyle(StageStyle.UTILITY);
                dialogStage.initOwner(NewView.visualStage);
                dialogStage.setScene(new Scene(contentHelp));
                dialogStage.setTitle("User Manual");
                dialogStage.showAndWait();
            } catch (URISyntaxException ex) {
                Logger.getLogger(TitleBar.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        minButton.setOnMouseClicked((MouseEvent me) -> {
            parent.setIconified(true);
        });

        maxButton.setOnMouseClicked((MouseEvent me) -> {
            try {
                parentView.windowResized();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TitleBar.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (!parent.isFullScreen()) {
                parent.setFullScreen(true);
            } else {
                parent.setFullScreen(false);
            }
            if (parent.isFullScreen()) {
                parent.setX(bounds.getMinX());
                parent.setY(bounds.getMinY());
                parent.setHeight(bounds.getHeight());
                parent.setWidth(bounds.getWidth());
            }
        });
        closeButton.setOnMouseClicked((MouseEvent me) -> {
            System.exit(0);
        });
        shadow = new Glow();
        shadow.setLevel(0.7);
        closeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                closeButton.setEffect(shadow);
            }
        });
        closeButton.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                closeButton.setEffect(null);
            }
        });

        // Color Pallete for assigning different color to different sections
        
        colorButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent e) {
                try {
                    Stage stage = new Stage();
                    stage.initOwner(NewView.visualStage);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setResizable(false);
                    stage.setTitle("Color Choice");
                    Scene scene = new Scene(new HBox(20), 400, 300);
                    HBox box = (HBox) scene.getRoot();
                    GridPane colorGrid = new GridPane();

                    // For changing vertical menu bar background color
                    
                    final ColorPicker verticalIconcolor = new ColorPicker();
                    verticalIconcolor.setValue(Color.valueOf(ColorConstants.menuBar));
                    Text verticalIcontext = new Text("Color picker : ");
                    verticalIcontext.setFill(verticalIconcolor.getValue());
                    String previous_menu = ColorConstants.menuBar;
                    verticalIconcolor.setOnAction((ActionEvent t) -> {
                        verticalIcontext.setFill(verticalIconcolor.getValue());
                        ColorConstants.menuBar = "#" + verticalIcontext.getFill().toString().substring(2, 8);
                        NewView.refTemp.getMenuBar().setStyle("-fx-background-color:" + ColorConstants.menuBar + "; -fx-border-width: 0 3 5 0; -fx-border-color: " + ColorConstants.border + ";-fx-padding: 30 0 0 10;");
                        NewView.studTemp.getMenuBar().setStyle("-fx-background-color:" + ColorConstants.menuBar + "; -fx-border-width: 0 0 5 3; -fx-border-color: " + ColorConstants.border + ";-fx-padding: 30 0 0 10;");
                    });
                    
                    // For changing panel background color
                    
                    final ColorPicker panelIconcolor = new ColorPicker();
                    panelIconcolor.setValue(Color.valueOf(ColorConstants.background));
                    Text paneltext = new Text("Color picker : ");
                    paneltext.setFill(panelIconcolor.getValue());
                    String previous_panel = ColorConstants.background;
                    panelIconcolor.setOnAction((ActionEvent t) -> {
                        paneltext.setFill(panelIconcolor.getValue());
                        ColorConstants.background = "#" + paneltext.getFill().toString().substring(2, 8);

                        NewView.studTemp.content.setStyle("-fx-background-color:" + ColorConstants.background + "; -fx-border-width: 0 5 8 9; -fx-border-color:" + ColorConstants.border + ";-fx-padding: 0 0 0 0;");
                        NewView.refTemp.content.setStyle("-fx-background-color:" + ColorConstants.background + "; -fx-border-width: 0 9 8 5 ; -fx-border-color:" + ColorConstants.border + ";-fx-padding: 0 0 0 0;");

                    });
                    
                    // For changing border color
                    
                    final ColorPicker borderIconcolor = new ColorPicker();
                    borderIconcolor.setValue(Color.valueOf(ColorConstants.border));
                    Text bordertext = new Text("Color picker : ");
                    bordertext.setFill(panelIconcolor.getValue());
                    String previous_border = ColorConstants.border;
                    borderIconcolor.setOnAction((ActionEvent t) -> {
                        bordertext.setFill(borderIconcolor.getValue());
                        ColorConstants.border = "#" + bordertext.getFill().toString().substring(2, 8);
                        NewView.studTemp.content.setStyle("-fx-background-color:" + ColorConstants.background + "; -fx-border-width: 0 5 8 9; -fx-border-color:" + ColorConstants.border + ";-fx-padding: 0 0 0 0;");
                        NewView.refTemp.content.setStyle("-fx-background-color:" + ColorConstants.background + "; -fx-border-width: 0 9 8 5 ; -fx-border-color:" + ColorConstants.border + ";-fx-padding: 0 0 0 0;");
                        NewView.refTemp.getMenuBar().setStyle("-fx-background-color:" + ColorConstants.menuBar + "; -fx-border-width: 0 3 5 0; -fx-border-color: " + ColorConstants.border + ";-fx-padding: 30 0 0 10;");
                        NewView.studTemp.getMenuBar().setStyle("-fx-background-color:" + ColorConstants.menuBar + "; -fx-border-width: 0 0 5 3; -fx-border-color: " + ColorConstants.border + ";-fx-padding: 30 0 0 10;");
                        NewView.studTemp.getPlayBar().setStyle("-fx-background-color:" + ColorConstants.bottom + ";-fx-border-width: 0 3 5 3; -fx-border-color: " + ColorConstants.border + ";-fx-scale-x: 1;-fx-padding:-7 0 7 0;");
                        NewView.refTemp.getPlayBar().setStyle("-fx-background-color:" + ColorConstants.bottom + ";-fx-border-width: 0 3 5 3; -fx-border-color: " + ColorConstants.border + ";-fx-scale-x: 1;-fx-padding:-7 0 7 0;");
                        setStyle("-fx-background-color:" + ColorConstants.title + ";-fx-border-width: 4 3 5 3;-fx-border-color: " + ColorConstants.border + ";");

                    });
                    
                    // For changing title bar background color
                    
                    final ColorPicker titleIconcolor = new ColorPicker();
                    titleIconcolor.setValue(Color.valueOf(ColorConstants.menuBar));
                    Text titleIcontext = new Text("Color picker : ");
                    titleIcontext.setFill(titleIconcolor.getValue());
                    String previous_title = ColorConstants.title;
                    titleIconcolor.setOnAction((ActionEvent t) -> {
                        titleIcontext.setFill(titleIconcolor.getValue());
                        ColorConstants.title = "#" + titleIcontext.getFill().toString().substring(2, 8);
                        setStyle("-fx-background-color:" + ColorConstants.title + ";-fx-border-width: 4 3 5 3;-fx-border-color: " + ColorConstants.border + ";");

                    });
                    // For changing bottom menu bar background color
                    
                    final ColorPicker bottomIconcolor = new ColorPicker();
                    bottomIconcolor.setValue(Color.valueOf(ColorConstants.bottom));
                    Text bottomIcontext = new Text("Color picker : ");
                    bottomIcontext.setFill(bottomIconcolor.getValue());
                    String previous_bottom = ColorConstants.bottom;
                    bottomIconcolor.setOnAction((ActionEvent t) -> {
                        bottomIcontext.setFill(bottomIconcolor.getValue());
                        ColorConstants.bottom = "#" + bottomIcontext.getFill().toString().substring(2, 8);
                        NewView.studTemp.getPlayBar().setStyle("-fx-background-color:" + ColorConstants.bottom + ";-fx-border-width: 0 3 5 3; -fx-border-color: " + ColorConstants.border + ";-fx-scale-x: 1;-fx-padding:-7 0 7 0;");
                        NewView.refTemp.getPlayBar().setStyle("-fx-background-color:" + ColorConstants.bottom + ";-fx-border-width: 0 3 5 3; -fx-border-color: " + ColorConstants.border + ";-fx-scale-x: 1;-fx-padding:-7 0 7 0;");
                    });
                    HBox box1 = new HBox();
                    box1.setSpacing(10);
                    box1.getChildren().addAll(verticalIconcolor);

                    HBox box2 = new HBox();
                    box2.setSpacing(10);
                    box2.getChildren().addAll(titleIconcolor);

                    HBox box3 = new HBox();
                    box3.setSpacing(10);
                    box3.getChildren().addAll(bottomIconcolor);

                    HBox box4 = new HBox();
                    box4.setSpacing(10);
                    box4.getChildren().addAll(panelIconcolor);

                    HBox box5 = new HBox();
                    box5.setSpacing(10);
                    box5.getChildren().addAll(borderIconcolor);

                    Button OK = new Button("OK");
                    Button Cancel = new Button("Cancel");
                    HBox control_box = new HBox();
                    control_box.setSpacing(10);
                    control_box.setPadding(new Insets(0, 0, 0, 10));
                    control_box.getChildren().addAll(OK, Cancel);

                    Label iconBar = new Label("Vertical Icon Bar");
                    iconBar.setFont(new Font("Arial", 14));
                    colorGrid.add(iconBar, 0, 0);
                    colorGrid.add(box1, 1, 0);

                    Label titleBar = new Label("Title Bar");
                    titleBar.setFont(new Font("Arial", 14));
                    colorGrid.add(titleBar, 0, 1);
                    colorGrid.add(box2, 1, 1);

                    Label bottomBar = new Label("Bottom Icon Bar");
                    bottomBar.setFont(new Font("Arial", 14));
                    colorGrid.add(bottomBar, 0, 2);
                    colorGrid.add(box3, 1, 2);

                    Label panelBar = new Label("Panels");
                    panelBar.setFont(new Font("Arial", 14));
                    colorGrid.add(panelBar, 0, 3);
                    colorGrid.add(box4, 1, 3);

                    Label borderBar = new Label("Borders ");
                    borderBar.setFont(new Font("Arial", 14));
                    colorGrid.add(borderBar, 0, 4);
                    colorGrid.add(box5, 1, 4);

                    colorGrid.setVgap(20);
                    colorGrid.setHgap(10);
                    VBox mainBox = new VBox();
                    mainBox.setSpacing(20);
                    mainBox.getChildren().addAll(colorGrid, control_box);
                    mainBox.setPadding(new Insets(10, 0, 0, 20));

                    box.getChildren().addAll(mainBox);

                    stage.setScene(scene);
                    stage.show();
                    stage.setOnCloseRequest((WindowEvent we) -> {
                        NewView.studTemp.content.setStyle("-fx-background-color:" + previous_panel + "; -fx-border-width: 0 5 8 9; -fx-border-color:" + previous_border + ";-fx-padding: 0 0 0 0;");
                        NewView.refTemp.content.setStyle("-fx-background-color:" + previous_panel + "; -fx-border-width: 0 9 8 5 ; -fx-border-color:" + previous_border + ";-fx-padding: 0 0 0 0;");
                        NewView.refTemp.getMenuBar().setStyle("-fx-background-color:" + previous_menu + "; -fx-border-width: 0 3 5 0; -fx-border-color: " + previous_border + ";-fx-padding: 30 0 0 10;");
                        NewView.studTemp.getMenuBar().setStyle("-fx-background-color:" + previous_menu + "; -fx-border-width: 0 0 5 3; -fx-border-color: " + previous_border + ";-fx-padding: 30 0 0 10;");
                        NewView.studTemp.getPlayBar().setStyle("-fx-background-color:" + previous_bottom + ";-fx-border-width: 0 3 5 3; -fx-border-color: " + previous_border + ";-fx-scale-x: 1;-fx-padding:-7 0 7 0;");
                        NewView.refTemp.getPlayBar().setStyle("-fx-background-color:" + previous_bottom + ";-fx-border-width: 0 3 5 3; -fx-border-color: " + previous_border + ";-fx-scale-x: 1;-fx-padding:-7 0 7 0;");
                        setStyle("-fx-background-color:" + previous_title + ";-fx-border-width: 4 3 5 3;-fx-border-color: " + previous_border + ";");

                        ColorConstants.menuBar = previous_menu;
                        ColorConstants.border = previous_border;
                        ColorConstants.background = previous_panel;
                        ColorConstants.bottom = previous_bottom;
                        ColorConstants.title = previous_title;
                    });
                    OK.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            try {
                                NewView.studTemp.content.setStyle("-fx-background-color:" + ColorConstants.background + "; -fx-border-width: 0 5 8 9; -fx-border-color:" + ColorConstants.border + ";-fx-padding: 0 0 0 0;");
                                NewView.refTemp.content.setStyle("-fx-background-color:" + ColorConstants.background + "; -fx-border-width: 0 9 8 5 ; -fx-border-color:" + ColorConstants.border + ";-fx-padding: 0 0 0 0;");
                                NewView.refTemp.getMenuBar().setStyle("-fx-background-color:" + ColorConstants.menuBar + "; -fx-border-width: 0 3 5 0; -fx-border-color: " + ColorConstants.border + ";-fx-padding: 30 0 0 10;");
                                NewView.studTemp.getMenuBar().setStyle("-fx-background-color:" + ColorConstants.menuBar + "; -fx-border-width: 0 0 5 3; -fx-border-color: " + ColorConstants.border + ";-fx-padding: 30 0 0 10;");
                                NewView.studTemp.getPlayBar().setStyle("-fx-background-color:" + ColorConstants.bottom + ";-fx-border-width: 0 3 5 3; -fx-border-color: " + ColorConstants.border + ";-fx-scale-x: 1;-fx-padding:-7 0 7 0;");
                                NewView.refTemp.getPlayBar().setStyle("-fx-background-color:" + ColorConstants.bottom + ";-fx-border-width: 0 3 5 3; -fx-border-color: " + ColorConstants.border + ";-fx-scale-x: 1;-fx-padding:-7 0 7 0;");
                                setStyle("-fx-background-color:" + ColorConstants.title + ";-fx-border-width: 4 3 5 3;-fx-border-color: " + ColorConstants.border + ";");
                                writeDraft();
                                stage.close();
                            } catch (Exception ex) {
                                new newLogging("SEVERE", TitleBar.class.getName(), "record()", ex);
                            }
                        }
                    });
                    Cancel.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            try {
                                NewView.studTemp.content.setStyle("-fx-background-color:" + previous_panel + "; -fx-border-width: 0 5 8 9; -fx-border-color:" + previous_border + ";-fx-padding: 0 0 0 0;");
                                NewView.refTemp.content.setStyle("-fx-background-color:" + previous_panel + "; -fx-border-width: 0 9 8 5 ; -fx-border-color:" + previous_border + ";-fx-padding: 0 0 0 0;");
                                NewView.refTemp.getMenuBar().setStyle("-fx-background-color:" + previous_menu + "; -fx-border-width: 0 3 5 0; -fx-border-color: " + previous_border + ";-fx-padding: 30 0 0 10;");
                                NewView.studTemp.getMenuBar().setStyle("-fx-background-color:" + previous_menu + "; -fx-border-width: 0 0 5 3; -fx-border-color: " + previous_border + ";-fx-padding: 30 0 0 10;");
                                NewView.studTemp.getPlayBar().setStyle("-fx-background-color:" + previous_bottom + ";-fx-border-width: 0 3 5 3; -fx-border-color: " + previous_border + ";-fx-scale-x: 1;-fx-padding:-7 0 7 0;");
                                NewView.refTemp.getPlayBar().setStyle("-fx-background-color:" + previous_bottom + ";-fx-border-width: 0 3 5 3; -fx-border-color: " + previous_border + ";-fx-scale-x: 1;-fx-padding:-7 0 7 0;");
                                setStyle("-fx-background-color:" + previous_title + ";-fx-border-width: 4 3 5 3;-fx-border-color: " + previous_border + ";");

                                ColorConstants.menuBar = previous_menu;
                                ColorConstants.border = previous_border;
                                ColorConstants.background = previous_panel;
                                ColorConstants.bottom = previous_bottom;
                                ColorConstants.title = previous_title;
                                stage.close();
                            } catch (Exception ex) {
                                new newLogging("SEVERE", TitleBar.class.getName(), "cancel()", ex);
                            }
                        }
                    });
                } catch (Exception ex) {
                    new newLogging("SEVERE", TitleBar.class.getName(), "titlebar()", ex);
                }
            }
        });

        getChildren().add(title_pane);
    }

    // For writing selected color to configuration setting file
    
    public void writeDraft() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("settings.txt", "UTF-8");
            String content = "MENUCOLOR=" + ColorConstants.menuBar + "\nBORDERCOLOR=" + ColorConstants.border + "\nPANELCOLOR=" + ColorConstants.background
                    + "\nTITLECOLOR=" + ColorConstants.title + "\nBOTTOMCOLOR=" + ColorConstants.bottom;
            writer.println(content);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            new newLogging("SEVERE", TitleBar.class.getName(), "writeDraft()", ex);
        } finally {
            writer.close();
        }
    }
}
