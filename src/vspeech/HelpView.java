/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vspeech;

import java.util.Arrays;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.TextArea;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 *
 * @author acer
 */
public class HelpView extends Application {

    private Stage helpStage;
    TextArea contentTA;

    List<Help> objHelp = Arrays.<Help>asList(
            new Help("Configuration", "System Summary", "VSTS version 1.0 is a versatile clinical and teaching tool that provides information important for speech training and features including: \n" +
"1. Recording and playback \n" +
"2. Storing recorded sounds \n" +
"3. Loading pre-recorded sounds from PC \n" +
"4. Speech parameters – Energy & Pitch \n" +
"5. Validation tools – Spectrogram & Areagram \n" +
"6. Vocal tract shape animation from input speech \n" +
"7. Animation rate and playback controls \n" +
"8. Shape emphasizing place of articulation "),
            new Help("Requirement", "System Summary", "1. This software package is designed to run on Windows based PC, preferably with Windows 7 or higher, with preferably Intel 6th Generation processors (Core i3, i5 or i7) or equivalent."
            +"\n2. The memory requirement to run VSTS is minimum 2 GB of RAM. User computer must have a hard-disk with minimum of 1 GB of free space."
            +"\n3. User computer must have a Windows-compatible sound card that has a 16-bit resolution and a sampling frequency of 44100 Hz.  Most  of the sound cards  today will work with  the VSTS program. If the sound quality is not satisfactory, changing the sound card will generally work. If user is using the program on a laptop, then a configuration with separate audio input and output\n" +
"ports is required."
            +"\n4. User must have a good-quality unidirectional dynamic or unidirectional condenser microphone to receive the best assessment of user’s voice. While combined headset microphones also work with the system, use of separate dedicated microphones is recommended to ensure a good quality recording. Most types of speakers will work, but the better the quality the better the sound."),
            
     new Help("Reset","Functionality"," The resent button will reset the function of the system and shows the ideal stage of the VSTS. "),
    new Help("Load","Functionality","1. For loading the pre –recorded sound file  user has to click on Load Icon of that particular panel to load the sound file for analysis.\n2. After clicking on the Load Icon, the file open dialogue box will appear from where user has to select the .wav file from specific file location on his system. After selecting .wav file user has to \n" +
"click on Open button at bottom-right corner.\n3. After  click  on  Open  button  the  following  dialouge  box  will  appear  which  shows  the waveform of the audio file with two option buttons –  Proceed and  Repeat.\n" +
""),
     new Help("Record","Functionality","1.  For recording the new sound file user has to click on Record Icon of that particular panel to display the analysis of file. A recording of size 3 seconds is made, from which a section of 1 second  is  selected  for  analysis,  starting  from  the  point  where  voiced  speech  begins  as determined by the program. The speech portion, if longer than a second, is clipped and the displays and animation are generated only for a second of the speech."
             + "\n2.  After clicking on Record icon, dialogue box will appear which shows the progress bar with indication for the user when to start speaking. During red bar, user should remain silent, as the program estimates the background noise for determining the speech portion from the complete recording."
             + "\n3.  After red bar fills, a new green progress bar appears which indicates the user has started to speak into the microphone.  User can speak till green bar gets completely fills.\n" +
"4.  After green bar fills (indicates that  speech is now recorded) the following dialouge box will appear which shows the waveform of the audio file with two option buttons –   Proceed and Repeat."
  +"\n5.  Click on Repeat button will starts the recording of sound file again. While click on Proceed button will start analyzing  the audio file to  produce the resultant output.  The recording  is automatically stored with the current date and time of the system, in the format ‘rec_dd-month- yyyy-hh-mm’. The ‘Proceed’ button will navigate on the main screen again with enabling the Analysis and Animation Icon buttons."),
 
new Help("Analysis","Functionality","1.  After getting the analysis button active, user can click on the icon to get display of normalized waveform signal, energy, pitch, spectrogram and areagram. "
+"\n2.  After clicking on analysis button, two buttons on the bottom bar becomes active."
+"\n3.  User can view simultaneously both the analysis panel to make the comparison between the waveform, Energy graph, Pitch graph, spectrogram and areagram of two different audio signal."),

    new Help("Animation","Functionality","1.  User can listen the audio file which he is analysis by clicking on the sound icon bottom bar player by clicking sound icon." +"\n" +
"2.  User can flip the image either left side or right side by clicking on the flip icon bottom player. can even flip the image while animation by clicking flip icon.	\n" +
"3. User can display or hide the two progresses bar namely, energy and pitch progress bar by clicking bar icon. "+"\n"+
 "4.  User can display or hide the ball indicating the place of articulation, by clicking on ball icon.\n"+
  "5.  User can restart the animation by clicking on restart icon, after clicking on restart icon   vertical bar moving on the graph will move to the starting position in the graph.\n"+
   "6.  User can play the animation by clicking on the play icon in the bottom player.\n "+
   "7.  User can pause the animation by clicking on the pause button  in the bottom player."));

    
    TreeItem<String> rootNode = new TreeItem<String>("VSTS Help Center");

    public HelpView(final Stage primaryStage) {
    }

    public HelpView() {
        helpStage = new Stage();
        rootNode.setExpanded(true);
        for (Help help : objHelp) {
            TreeItem<String> empLeaf = new TreeItem<String>(help.getName());
            boolean found = false;
            for (TreeItem<String> depNode : rootNode.getChildren()) {
                if (depNode.getValue().contentEquals(help.getCategory())) {
                    depNode.getChildren().add(empLeaf);
                    found = true;
                    break;
                }
            }
            if (!found) {
                TreeItem<String> depNode = new TreeItem<String>(help.getCategory());
                rootNode.getChildren().add(depNode);
                depNode.getChildren().add(empLeaf);
            }
        }

        helpStage.setTitle("VSTS Help Index");
        HBox box = new HBox();
        final Scene scene = new Scene(box, 800, 400);
        scene.setFill(Color.LIGHTGRAY);

        TreeView<String> treeView = new TreeView<String>(rootNode);
        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                TreeItem<String> empLeaf = (TreeItem<String>) newValue;
                for (Help help : objHelp) {
                    if(help.getName().equalsIgnoreCase(empLeaf.getValue().toString())){
                    contentTA.setText("\n"+help.getDescription().toString());
                    }
                }
                // do what ever you want 
            }
        });
        contentTA = new TextArea();
        contentTA.setPrefWidth(666);
        box.getChildren().addAll(treeView, contentTA);
        helpStage.setScene(scene);
        helpStage.showAndWait();
    }

    public void start(Stage stage) throws Exception {
        // stage.initOwner(WindowView.fabricStage);
        new HelpView(stage);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static void main(String[] args) {
        launch(args);
    }
}
