/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vspeech;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

// For creating Bottom icon bar buttons 
public class SignalButton extends VBox {

    DropShadow shadow;
    private ImageView imageView;
    Label label;

    public SignalButton(String ShadowImagePath, String DisableImagePath, String EnableImagePath, String name,String msg) {
        shadow = new DropShadow();
        imageView = new ImageView(new Image(DisableImagePath));

        

        label = new Label(name);
        setAlignment(Pos.CENTER);
        label.setStyle("-fx-padding 0 0 0 5;-fx-wrap-text:true; -fx-text-alignment: center; -fx-alignment: CENTER; -fx-font-size: 10pt; -fx-font-family: \"Segoe UI Semibold\"; -fx-text-fill: #FFFFFF    ;");

        setMinWidth(40);
        //    setMinHeight(40);
        //  setStyle("-fx-border-width: 0 0 0 0; -fx-background-color: rgba(0,168,355,0);");
        getChildren().addAll(imageView, label);

        disabledProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            if (t1) // when icon gets activated  
            {
                getChildren().clear();
                imageView = new ImageView(new Image(EnableImagePath));
                getChildren().addAll(imageView, label);

                //setStyle("-fx-background-image: url(" + EnableImagePath + ");    -fx-background-position: center;-fx-background-repeat: no-repeat;-fx-border-width: 0 0 0 0; ");
            } else // when icon gets activated 
            {
                getChildren().clear();

                imageView = new ImageView(new Image(DisableImagePath));
                getChildren().addAll(imageView, label);

                // setStyle("-fx-background-image: url(" + DisableImagePath + ");    -fx-background-position: center;-fx-border-width: 0 0 0 0;-fx-background-repeat: no-repeat; ");
            }

        });

        //For shadowing effect
        addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            getChildren().clear();

            imageView = new ImageView(new Image(ShadowImagePath));
            getChildren().addAll(imageView, label);

           // setEffect(shadow);
             Tooltip tip = new Tooltip();
            //tip.set
             tip.setText(msg);
             Tooltip.install(this, tip);
        });
        addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            getChildren().clear();

            imageView = new ImageView(new Image(DisableImagePath));
            getChildren().addAll(imageView, label);

          //  setEffect(null);
        });
    }
}
