/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vspeech;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

//For creating buttons of vertical Menu Bar icons
public class paneButton extends HBox {

    Label buttonLabel;
    DropShadow shadow;
    ImageView imageView;
    private Button button;
    Analyzer parent;
    String label;

    /*
    Input : 
        Label as String for Button label
        Tooltip as String for tooltip message 
        ImagePath as String for path of active image icon 
        DisableImagePath as String for path of inactive image icon 
        IsLeft as boolean for specifying butoons orientation for left or right panel
        Analyzer main content window 
     */
    public paneButton(String label, String msg, String imagePath, String disableimagePath, boolean isLeft, Analyzer parent) {
        buttonLabel = new Label("");
        buttonLabel.setStyle("-fx-wrap-text:true; -fx-text-alignment: left; -fx-alignment: center-left; -fx-font-size: 11pt; -fx-font-family: \"Segoe UI Semibold\"; -fx-text-fill:" + ColorConstants.label.trim() + ";-fx-padding: 0 0 0 0;");
        buttonLabel.setPrefWidth(5);
        setPrefHeight(40);
        setPadding(new Insets(10, 5, 0, -5));
        setStyle("-fx-border-width: 0 0 0 0 ;-fx-border-color: #373737;");
        getStyleClass().addAll("HBox");
        imageView = new ImageView(new Image(disableimagePath));
        this.parent = parent;
        this.label = label;
        //Action on activation or deactivation of icons
        disabledProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            if (t1) {
                getChildren().clear();

                imageView = new ImageView(new Image(disableimagePath));
                button = new Button(label, imageView);
                button.setPrefWidth(500);

                button.setContentDisplay(ContentDisplay.TOP);
                if (isLeft) {
                    getChildren().addAll(button, buttonLabel);
                } else {
                    getChildren().addAll(button, buttonLabel);
                }

            } else {
                getChildren().clear();
                imageView = new ImageView(new Image(imagePath));
                button = new Button(label, imageView);
                button.setPrefWidth(500);

                button.setContentDisplay(ContentDisplay.TOP);
                if (isLeft) {
                    getChildren().addAll(button, buttonLabel);
                } else {
                    getChildren().addAll(button, buttonLabel);
                }

            }
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    try {
                        action();
                    } catch (IOException ex) {
                        new newLogging("SEVERE", paneButton.class.getName(), "action()", ex);
                    }
                }
            });
        });
        button = new Button(label, imageView);
        button.setMaxWidth(105);
        button.setMinWidth(105);
        button.setContentDisplay(ContentDisplay.TOP);
        if (isLeft) {
            getChildren().addAll(button, buttonLabel);
        } else {
            getChildren().addAll(button, buttonLabel);
        }

        shadow = new DropShadow();
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    action();
                } catch (IOException ex) {
                    new newLogging("SEVERE", paneButton.class.getName(), "action()", ex);
                }
            }
        });

        addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                setEffect(shadow);
                Tooltip tip = new Tooltip();
                tip.setText(msg);
                Tooltip.install(button, tip);

            }
        });

        addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                setStyle("-fx-background-color:transparent;");
                setEffect(null);
            }
        });
    }

    public void action() throws IOException {
        if (label.contains("Message")) {
            parent.mesaagePane();
        }
        if (label.contains("Clear")) {
            parent.resetPane();
        }
        if (label.contains("Signal")) {
            parent.LoadFile();
        }
        if (label.contains("Analysis")) {
            parent.displayAnalysis();
        }
        if (label.contains("Animation")) {
            try {
                parent.displayAnimation();
            } catch (FileNotFoundException ex) {
                new newLogging("SEVERE", paneButton.class.getName(), "action()", ex);
            }

        }
    }
}
