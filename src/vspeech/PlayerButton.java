/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vspeech;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

// For creating Bottom icon bar buttons 

public class PlayerButton extends VBox {

    DropShadow shadow;

    public PlayerButton(String EnableImagePath, String DisableImagePath, String msg) {
        shadow = new DropShadow();
        setMinWidth(LayoutConstants.fullWidth/25);
        setMinHeight(40);
        setStyle("-fx-border-width: 0 0 0 0; -fx-background-color: #E9E8E8;-fx-background-radius: 30;-fx-background-insets: 0,1,1;");
        
       
        disabledProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            if(t1)          // when icon gets activated  
                        setStyle("-fx-background-image: url(" + EnableImagePath + ");    -fx-background-position: center;-fx-background-repeat: no-repeat;-fx-border-width: 0 0 0 0; -fx-background-color: #E9E8E8;-fx-background-radius: 30;-fx-background-insets: 0,1,1;");

            else        // when icon gets activated 
                    setStyle("-fx-background-image: url(" + DisableImagePath + ");    -fx-background-position: center;-fx-border-width: 0 0 0 0;-fx-background-repeat: no-repeat; -fx-background-color: #E9E8E8;-fx-background-radius: 30;-fx-background-insets: 0,1,1;");

        });
        //For shadowing effect
        addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            setEffect(shadow);
              Tooltip tip = new Tooltip();
            tip.setText(msg);
            Tooltip.install(this, tip);
        });
        addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            setEffect(null);
        });
    }
}
