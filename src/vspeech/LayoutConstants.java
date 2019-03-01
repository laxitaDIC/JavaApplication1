package vspeech;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;


/*
For specifying height and width of the application's different components such as Title Bar, Bottom Icon Bar,Border etc.

*/
public class LayoutConstants {

    public static final Screen screen = Screen.getPrimary(); // for reading system's device configuration such as monitor height and width

    public static final Rectangle2D fullScreenBounds = screen.getVisualBounds();
    public static final Rectangle2D minimizeBounds = new Rectangle2D(fullScreenBounds.getMinX() + fullScreenBounds.getWidth() / 8, fullScreenBounds.getMinY() + fullScreenBounds.getHeight() / 8, fullScreenBounds.getWidth() * 0.92, fullScreenBounds.getHeight() * 0.8);

    public static final double fullWidth = fullScreenBounds.getWidth();
    public static final double fullHeight = fullScreenBounds.getHeight();
    public static final double minWidth = minimizeBounds.getWidth()+20;
    public static final double minHeight = minimizeBounds.getHeight()+120;

    public static double titleBarHeight = 40.0;
    public static double playBarHeight = 70.0;
    public static double menuBarWidth = 120.0;
    public static double playerControlWidth=620;
    
    public static double imagePadding = 64.0;
    public static double graphpadding = 16.0;
    public static double yaxisWidth = 61.0;
    
    public static double lineWidth=0.0;

}
