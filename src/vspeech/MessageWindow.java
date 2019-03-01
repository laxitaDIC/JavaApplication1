/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vspeech;

import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 *
 * @author abc
 */
public class MessageWindow extends Application {

    private final Stage pStage;
    private final Analyzer pAnalyzer;
    private static final String[][] template = {
        {"❀", "✈", "✏", "☎", "?", "!", "_", "҈"},
        {"✍", "♛", "⤫", "⧗", "✔", "♫", "★", "⚘"},
        {"☺", "☻", "✌", "☹", "♥", "❃", "❁", "☀",},};

    private final Map<String, Button> accelerators = new HashMap<>();

    private StringProperty value = new SimpleStringProperty();
    private TextField screen;

    private VBox root;
    private VBox button_sym;
    private GridPane grid;
    private HBox lang_button;
    boolean isOn = Toolkit.getDefaultToolkit().getLockingKeyState(java.awt.event.KeyEvent.VK_CAPS_LOCK);
    int LIMIT = 40;
    private ImageView imageView;
    int initial = 1;
    private Stage primaryStage;

    public MessageWindow(Stage parentStage, Analyzer parentAnalyzer) {
        pStage = parentStage;
        pAnalyzer = parentAnalyzer;

    }

    public void displayMessage() {
        grid = new GridPane();
        initial = 1;

        if (pAnalyzer.isLeft) {
            if (pAnalyzer.getPlayBar().studMsg.equals("")) {
                value.set("");
            } else {
                value.set("" + pAnalyzer.getPlayBar().studMsg.getText());
            }
        } else {
            if (pAnalyzer.getPlayBar().refMsg.equals("")) {
                value.set("");
            } else {
                value.set("" + pAnalyzer.getPlayBar().refMsg.getText());
            }
        }
        String previous = value.get();

        final Screen capture = Screen.getPrimary();

        screen = createScreen();
        VBox buttons;

        buttons = createButtons();
        buttons.setPadding(new Insets(20, 0, 0, 0));

        primaryStage = new Stage();
        primaryStage.initOwner(pStage);
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        if (pAnalyzer.isLeft) {
            primaryStage.setY(pAnalyzer.content.getMaxHeight() / 3);
            primaryStage.setX(pAnalyzer.content.getMaxWidth() / 2);
        } else {
            primaryStage.setY(pAnalyzer.content.getMaxHeight() / 3);
            primaryStage.setX(pAnalyzer.content.getMaxWidth() * 1.5);

        }
        lang_button = new HBox();

        Button Eng = new Button("A");
        Button Dev = new Button();
        Dev.setText("" + (char) 2309);
        Button Graphics1 = new Button("★");
        Button Graphics2 = new Button("G");
        Eng.setStyle("-fx-font-size: 16px;");
        Dev.setStyle("-fx-font-size: 16px;");
        Graphics1.setStyle("-fx-font-size: 16px;");
        Graphics2.setStyle("-fx-font-size: 16px;");

        Eng.setMaxWidth(40);
        Eng.setMinWidth(40);
        Eng.setMaxHeight(40);
        Eng.setMinHeight(40);

        Dev.setMaxWidth(40);
        Dev.setMinWidth(40);
        Dev.setMaxHeight(40);
        Dev.setMinHeight(40);

        Graphics1.setMaxWidth(40);
        Graphics1.setMinWidth(40);
        Graphics1.setMaxHeight(40);
        Graphics1.setMinHeight(40);

        Graphics2.setMaxWidth(40);
        Graphics2.setMinWidth(40);
        Graphics2.setMaxHeight(40);
        Graphics2.setMinHeight(40);

        lang_button.setAlignment(Pos.BOTTOM_CENTER);
        lang_button.getChildren().addAll(Eng, Dev, Graphics1, Graphics2);
        lang_button.setSpacing(20);
        lang_button.setStyle("-fx-background-color: #FFFFFF; ");

        root = new VBox();
        button_sym = createLayout(screen, buttons);
        button_sym.setMaxHeight(470);
        button_sym.setMinHeight(470);

        root.getChildren().addAll(button_sym, lang_button);

        Eng.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                grid.getChildren().clear();
                VBox buttons = createButtons();
                buttons.setPadding(new Insets(20, 0, 0, 0));

                root.getChildren().clear();
                button_sym = createLayout(screen, buttons);
                button_sym.setMaxHeight(470);
                button_sym.setMinHeight(470);

                root.getChildren().addAll(button_sym, lang_button);
            }
        });
        Dev.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                grid.getChildren().clear();

                VBox buttons = createHindilButtons();
                buttons.setPadding(new Insets(20, 0, 0, 0));

                root.getChildren().clear();
                button_sym = createLayout(screen, buttons);
                button_sym.setMaxHeight(470);
                button_sym.setMinHeight(470);

                root.getChildren().addAll(button_sym, lang_button);
            }
        });

        Graphics1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                grid.getChildren().clear();

                VBox buttons = createGraphButtons();
                buttons.setPadding(new Insets(20, 0, 0, 0));

                root.getChildren().clear();
                button_sym = createLayout(screen, buttons);
                button_sym.setMaxHeight(470);
                button_sym.setMinHeight(470);

                root.getChildren().addAll(button_sym, lang_button);
            }
        });
        primaryStage.setOnCloseRequest((WindowEvent we) -> {
            value.set(previous);
            if (pAnalyzer.isLeft) {
                pAnalyzer.getPlayBar().studMsg.setText(value.getValue());
            } else {
                pAnalyzer.getPlayBar().refMsg.setText(value.getValue());
            }
            primaryStage.close();

        });
        Scene scene = new Scene(root, 450, 510);
        root.setStyle("-fx-background-color: #FFFFFF; ");

        primaryStage.setTitle("Message Window ; use mouse to type text");
        primaryStage.setFullScreen(false);
        primaryStage.setScene(scene);
        primaryStage.showAndWait();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private VBox createLayout(TextField screen, VBox buttons) {
        final VBox layout = new VBox();
        layout.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 20; -fx-font-size: 16;");
        layout.getChildren().setAll(screen, buttons);
        handleAccelerators(layout);
        screen.prefWidthProperty().bind(buttons.widthProperty());
        return layout;
    }

    private void handleAccelerators(VBox layout) {
        layout.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {

            }
        });
    }

    private TextField createScreen() {
        final TextField screen = new TextField();
        screen.setStyle("-fx-background-color: #000000;-fx-text-fill:#FFFFFF");
        screen.setEditable(false);
        screen.textProperty().bind(value);
        screen.lengthProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable,
                    Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()) {
                    // Check if the new character is greater than LIMIT
                    int len = value.getValue().length();
                    if (len >= LIMIT) {

                        value.set(value.getValue().substring(0, len - 1));

                    }
                }
            }

        });

        return screen;
    }

    private VBox createButtons() {
        VBox buttons = new VBox();

        grid.setHgap(10);
        grid.setVgap(5);

        int letter = 48;

        for (int i = 2; i < 9; i++) {
            for (int j = 0; j < 8; j++) {
                if (letter < 58) {
                    grid.add(createButton("" + (char) letter), j, i);
                    letter++;

                }
            }

        }
        letter = 65;
        int j = 0;
        for (int i = 10; i < 18; i++) {
            for (j = 0; j < 8 && letter < 91; j++) {
                grid.add(createButton("" + (char) letter), j, i);
                letter++;
            }

        }
        j = 3;
        //   grid.add(createButton("@"), j++, 18);
        // grid.add(createButton("←"), j++, 18);
        //  grid.add(createButton("⊂"), j++, 18);

        HBox extra_button = new HBox();
        extra_button.setSpacing(15);
        extra_button.setPadding(new Insets(0, 0, 0, 40));

        extra_button.getChildren().addAll(createButton("Backspace"), createButton("Space"), createButton("Delete All"), createButton("Enter"));

        VBox eng_buton = new VBox();
        eng_buton.setSpacing(60);
        eng_buton.getChildren().addAll(grid, extra_button);

        buttons.getChildren().add(eng_buton);

        return buttons;
    }

    private VBox createGraphButtons() {
        VBox buttons = new VBox();

        grid.setHgap(10);
        grid.setVgap(5);
        int j = 0;
        for (int i = 0; i < 3; i++) {
            for (j = 0; j < 8; j++) {
                grid.add(createButton(template[i][j]), j, i);
            }

        }
        j = 0;
        /*grid.add(createButton("@"), j++, 3);
        grid.add(createButton("←"), j++, 3);
        grid.add(createButton("⊂"), j++, 3*/
        HBox extra_button = new HBox();
        extra_button.setSpacing(15);
        extra_button.setPadding(new Insets(0, 0, 0, 40));

        extra_button.getChildren().addAll(createButton("Backspace"), createButton("Space"), createButton("Delete All"), createButton("Enter"));

        VBox graph_buton = new VBox();
        graph_buton.setSpacing(120);
        graph_buton.setPadding(new Insets(50, 0, 0, 0));

        graph_buton.getChildren().addAll(grid, extra_button);

        buttons.getChildren().add(graph_buton);

        return buttons;
    }

    private VBox createHindilButtons() {

        VBox buttons = new VBox();
        grid.setPadding(new Insets(10, 0, 0, 0));

        grid.setHgap(10);
        grid.setVgap(5);
        int letter = 2309;
        int j = 0;
        for (int i = 0; i < 7; i++) {
            for (j = 0; j < 8 && letter < 2364; j++) {
                if (letter == 2316) {
                    j--;
                } else if (letter == 2356) {
                    j--;
                } else if (letter == 2318) {
                    j--;
                } else {
                    grid.add(createButton("" + (char) letter), j, i);
                }
                letter++;
            }

        }
        j = 4;

        grid.add(createButton("" + (char) 2363), j++, 6);
        grid.add(createButton("" + (char) 2372), j++, 6);
        grid.add(createButton("" + (char) 2366), j++, 6);
        grid.add(createButton("" + (char) 2367), j++, 6);

        grid.add(createButton("" + (char) 2368), 0, 7);
        grid.add(createButton("" + (char) 2369), 1, 7);
        grid.add(createButton("" + (char) 2370), 2, 7);
        grid.add(createButton("" + (char) 2371), 3, 7);
        grid.add(createButton("" + (char) 2373), 4, 7);
        grid.add(createButton("" + (char) 2375), 5, 7);
        grid.add(createButton("" + (char) 2376), 6, 7);
        grid.add(createButton("" + (char) 2377), 7, 7);

        grid.add(createButton("" + (char) 2379), 0, 8);
        grid.add(createButton("" + (char) 2380), 1, 8);
        grid.add(createButton("" + (char) 2305), 2, 8);
        grid.add(createButton("" + (char) 2306), 3, 8);
        grid.add(createButton("" + (char) 2307), 4, 8);
        /* grid.add(createButton("@"), 5, 8);
        grid.add(createButton("←"), 6, 8);
        grid.add(createButton("⊂"), 7, 8);*/
        HBox extra_button = new HBox();
        extra_button.setSpacing(15);
        extra_button.setPadding(new Insets(0, 0, 0, 40));

        extra_button.getChildren().addAll(createButton("Backspace"), createButton("Space"), createButton("Delete All"), createButton("Enter"));

        VBox hin_buton = new VBox();
        hin_buton.setSpacing(20);
        hin_buton.getChildren().addAll(grid, extra_button);

        buttons.getChildren().add(hin_buton);

        return buttons;
    }

    private Button createButton(final String s) {
        Button button;
        if (s.equals("@")) {
            button = makeStandardButton("");
        } else if (s.contains("Enter")) {
            imageView = new ImageView(new Image("/images/enter.png"));

            button = new Button(s, imageView);
            button.setContentDisplay(ContentDisplay.TOP);
            button.setStyle("-fx-base: beige;-fx-font-size:12px");

            button.setMinWidth(75);
            button.setMaxWidth(75);
            button.setMinHeight(50);
            button.setMaxHeight(50);
        } else if (s.contains("Space")) {
            imageView = new ImageView(new Image("/images/space.png"));

            button = new Button(s, imageView);
            button.setContentDisplay(ContentDisplay.TOP);
            button.setStyle("-fx-base: beige;-fx-font-size:12px");

            button.setMinWidth(75);
            button.setMaxWidth(75);
            button.setMinHeight(50);
            button.setMaxHeight(50);
        } else if (s.contains("Back")) {
            imageView = new ImageView(new Image("/images/backspace.png"));

            button = new Button(s, imageView);
            button.setContentDisplay(ContentDisplay.TOP);
            button.setStyle("-fx-base: beige;-fx-font-size:12px");

            button.setMinWidth(75);
            button.setMaxWidth(75);
            button.setMinHeight(50);
            button.setMaxHeight(50);
        } else if (s.contains("Delete")) {
            imageView = new ImageView(new Image("/images/delete all.png"));

            button = new Button(s, imageView);
            button.setContentDisplay(ContentDisplay.TOP);
            button.setStyle("-fx-base: beige;-fx-font-size:12px");

            button.setMinWidth(75);
            button.setMaxWidth(75);
            button.setMinHeight(50);
            button.setMaxHeight(50);
        } else {
            button = makeStandardButton(s);
        }
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (!s.contains("Enter")) {
                    if (initial == 1) {
                        value.set("");
                        initial++;
                    }
                }
                char[] ascii1 = s.toCharArray();
                if (s.equals("Space")) {
                    value.set(value.get() + " ");

                } else if (s.contains("Enter")) {
                    primaryStage.close();

                } else if (s.contains("Backspace")) {
                    int len = value.getValue().length();
                    value.set(value.getValue().substring(0, len - 1));

                } else if (s.contains("Delete")) {
                    value.set("");
                    if (pAnalyzer.isLeft) {
                        pAnalyzer.getPlayBar().studMsg.setText("");
                    } else {
                        pAnalyzer.getPlayBar().refMsg.setText("");
                    }
                } else {
                    for (char ch : ascii1) {
                        if ((int) ch == 7842) {
                            grid.getChildren().clear();
                            VBox buttons = createButtons();
                            buttons.setPadding(new Insets(20, 0, 0, 0));

                            root.getChildren().clear();
                            button_sym = createLayout(screen, buttons);
                            button_sym.setMaxHeight(410);
                            button_sym.setMinHeight(410);
                            root.getChildren().addAll(button_sym, lang_button);

                        } else {
                            value.set(value.get() + s);
                        }

                    }
                }
                if (pAnalyzer.isLeft) {
                    pAnalyzer.getPlayBar().studMsg.setText(value.getValue());
                } else {
                    pAnalyzer.getPlayBar().refMsg.setText(value.getValue());
                }

            }
        });
        return button;
    }

    private Button makeStandardButton(String s) {

        Button button = new Button(s);
        button.setStyle("-fx-base: beige;");
        accelerators.put(s, button);
        if (s.equals("Space")) {
            button.setStyle("-fx-base: beige;-fx-font-size:12px");

            button.setMinWidth(75);
            button.setMaxWidth(75);
            button.setMinHeight(50);
            button.setMaxHeight(50);

        } else if (s.contains("Enter")) {
            button.setStyle("-fx-base: beige;-fx-font-size:12px");

            button.setMinWidth(75);
            button.setMaxWidth(75);
            button.setMinHeight(50);
            button.setMaxHeight(50);

        } else if (s.contains("Backspace")) {
            button.setStyle("-fx-base: beige;-fx-font-size:12px");

            button.setMinWidth(75);
            button.setMaxWidth(75);
            button.setMinHeight(50);
            button.setMaxHeight(50);

        } else if (s.contains("Delete")) {
            button.setStyle("-fx-base: beige;-fx-font-size:12px");

            button.setMinWidth(75);
            button.setMaxWidth(75);
            button.setMinHeight(50);
            button.setMaxHeight(50);

        } else {
            button.setStyle("-fx-base: beige;-fx-font-size:14px");

            button.setMinWidth(40);
            button.setMaxWidth(40);
        }

        return button;
    }
}
