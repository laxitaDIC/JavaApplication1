package vspeech;

import java.io.FileNotFoundException;
import java.util.Set;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import org.controlsfx.control.RangeSlider;

// For creating animation panel

public class AnimationDisplay {

    private final GridPane parent;
    private Pane chartHolder;           //Place holder for storing waveform graph alongwith vertical line
    public double graphWidth;
    private double graphHeight; 
    public Polygon polygon;             // For drwaing jaw shape
    public ImageView baseImg;           // For reading Background base image on which polygon is drwan
    public double imgWidth;             //Base Image width as corresponds to screen resolution
    public double imgHeight;            //Base Image height as corresponds to screen resolution
    public HBox Box3;
    public VBox barBox;
    public HBox animatePane;
    public Circle circle;               //Place of articulation circle
    private Pane energychartHolder;     //Place holder for storing energy graph alongwith vertical line
    public Pane pitchchartHolder;       //Place holder for storing pitch graph alongwith vertical line
    private VBox Box1;
    private NumberAxis xAxis;
    private NumberAxis yAxis;
    private SimpleDoubleProperty lineX;
    public RangeSlider slider;                  //To show information as how animation moves
    private Line line1;
    private Line line2;
    private Line line3;
    public ProgressBar energyBar;           //Energy Bar
    public ProgressBar pitchBar;            //Pich Bar
    public Text energybarTxt;
    public Text pitchbarTxt;
    private Analyzer analyzer;
      
    public AnimationDisplay(GridPane parent) {
        this.parent = parent;
    }

    public void display(LineChart<Number, Number> waveform, LineChart<Number, Number> energyWaveform, LineChart<Number, Number> pitchWaveform, RangeSlider slider, ProgressBar energyBar, ProgressBar pitchBar, DoubleProperty fontSize, boolean isLeft, Analyzer analyzer) throws FileNotFoundException {
        this.energyBar = energyBar;
        this.pitchBar = pitchBar;
        this.analyzer = analyzer;

        parent.getChildren().clear();
        this.slider = slider;
        Box1 = new VBox();
        Box1.setPrefHeight((parent.getMaxHeight()) * 0.34);
        Box1.setPrefWidth(LayoutConstants.fullWidth);
        Box1.setStyle("-fx-background-color: rgba(53,53,53,1);-fx-border-width: 2 2 0 2;-fx-border-color:#FFFFFF;");
        graphWidth = parent.getMaxWidth() * 0.9;
        graphHeight = Box1.getPrefHeight() * 0.36;

        HBox waveformBox = new HBox();
        waveform.setCache(true);
        waveform.setStyle("CHART_COLOR_1: #C0AD03;");
        waveform.setMaxWidth(graphWidth);
        waveform.setMinWidth(graphWidth);
        waveform.setMaxHeight(graphHeight);
        waveform.setMinHeight(graphHeight);
        waveform.setPadding(new Insets(0, 0, 0, 0));
        waveform.getYAxis().setLabel("Sig. (nrm.)");
        Set<Node> axisNode = waveform.lookupAll(".axis");
        int i = 0;
        for (final Node axis : axisNode) {
            if (i != 0) {
                axis.setStyle("-fx-padding: 0 0 0 0;-fx-font-size: " + fontSize.get() + ";");
            } else {
                axis.setStyle("-fx-padding: 0 0 0 0;-fx-font-size: " + fontSize.get() + ";");
            }
            i++;
        }
        chartHolder = new Pane();
        chartHolder.setMaxWidth(parent.getMaxWidth() * 0.81);
        chartHolder.setMinWidth(parent.getMaxWidth() * 0.81);
        chartHolder.setMaxHeight(Box1.getPrefHeight() * 0.35);
        chartHolder.setMinHeight(Box1.getPrefHeight() * 0.35);

        chartHolder.getChildren().add(waveform);

        xAxis = (NumberAxis) waveform.getXAxis();
        yAxis = (NumberAxis) waveform.getYAxis();
        slider.minProperty().bind(xAxis.lowerBoundProperty());
        slider.maxProperty().bind(xAxis.upperBoundProperty());

        lineX = new SimpleDoubleProperty();
        lineX.bind(slider.lowValueProperty());

        DoubleProperty lineY = new SimpleDoubleProperty();
        lineY.bind(slider.highValueProperty());

        
        line1 = createVerticalLine(waveform, xAxis, yAxis, chartHolder, lineX);
        line2 = createVerticalLine(waveform, xAxis, yAxis, chartHolder, lineX);
        line3 = createVerticalLine(waveform, xAxis, yAxis, chartHolder, lineX);

        chartHolder.getChildren().addAll(line1);
        waveformBox.getChildren().addAll(chartHolder);

        Label xAxisLbl = new Label("Time (s)");
        xAxisLbl.setStyle("-fx-wrap-text:true; -fx-text-alignment: left; -fx-alignment: center-left; -fx-font-size: " + fontSize.subtract(50) + "; -fx-font-family: \"Segoe UI Semibold\"; -fx-text-fill: #FFFFFF;");
        xAxisLbl.setPadding(new Insets(0, 0, 0, waveform.getMaxWidth() / 2));

        HBox energychartBox = new HBox();
        energychartHolder = new Pane();
        energychartHolder.setMaxWidth(parent.getMaxWidth() * 0.81);
        energychartHolder.setMinWidth(parent.getMaxWidth() * 0.81);
        energychartHolder.setMaxHeight(Box1.getPrefHeight() * 0.35);
        energychartHolder.setMinHeight(Box1.getPrefHeight() * 0.35);
        energyPlot(energyWaveform);
        energychartBox.getChildren().addAll(energychartHolder);

        HBox pitchchartBox = new HBox();
        pitchchartHolder = new Pane();
        pitchchartHolder.setMaxWidth(parent.getMaxWidth() * 0.81);
        pitchchartHolder.setMinWidth(parent.getMaxWidth() * 0.81);
        pitchchartHolder.setMaxHeight(Box1.getPrefHeight() * 0.35);
        pitchchartHolder.setMinHeight(Box1.getPrefHeight() * 0.35);

        pitchPlot(pitchWaveform);
        pitchchartBox.getChildren().addAll(pitchchartHolder);
        Box1.getChildren().addAll(waveformBox, xAxisLbl, energychartBox, pitchchartBox);    //For adding waveform, energy chart, x-axis label and pitch chart
        
        
        VBox Box2 = new VBox();
        Box2.setPrefHeight((parent.getMaxHeight()) * 0.06);
        slider.setMaxWidth(graphWidth);
        slider.setMinWidth(graphWidth);
        slider.setPadding(new Insets(0, 5, 10, LayoutConstants.imagePadding-15));
        Box2.setStyle("-fx-background-color: rgba(53,53,53,1);-fx-border-width: 0 2 2 2;-fx-border-color:#FFFFFF;");
        Box2.getChildren().add(slider);                                     

        Box3 = new HBox();
        Box3.setPrefHeight((parent.getMaxHeight()) * 0.55);
        Box3.setPrefWidth(LayoutConstants.fullWidth);
        Box3.setStyle("-fx-background-color:#D3D3D3;-fx-border-width: 2 2 2 2;-fx-border-color:#FFFFFF;");

        Image base = analyzer.face;
        baseImg = new ImageView(base);
        baseImg.setFitHeight(graphWidth*0.65);
        baseImg.setFitWidth(graphWidth*0.67);
        
        imgWidth =graphWidth*0.67;
        imgHeight =graphWidth*0.65;

        polygon = new Polygon();
        polygon.setStroke(Color.BLACK);
        polygon.setStrokeWidth(3);
        polygon.setFill(Color.valueOf("#FE9D6F"));
        barBox = new VBox();
        
        if (analyzer.isBar) {
            barBox.setVisible(true);

            if (analyzer.isHorizontal) {
                barVertical();
            } else {
                barHorizontal();
            }
        } else {
            barBox.getChildren().clear();
            Label temp = new Label();
            temp.setMaxWidth(Box3.getPrefWidth() * 0.06);
            temp.setMinWidth(Box3.getPrefWidth() * 0.06);
            barBox.getChildren().add(temp);
        }
        analyzer.play(analyzer.control.frameVal);
        Box3.setScaleX(analyzer.scale);
        if (!analyzer.isImgLeft) {

            Label temp = new Label();
            temp.setMaxWidth(Box3.getPrefWidth() * 0.05);
            temp.setMinWidth(Box3.getPrefWidth() * 0.05);
            Box3.getChildren().addAll(barBox, temp, analyzer.newBox);
        } else {
            Label temp = new Label();
            temp.setMaxWidth(Box3.getPrefWidth() * 0.02);
            temp.setMinWidth(Box3.getPrefWidth() * 0.02);
            Box3.getChildren().addAll(temp, analyzer.newBox, barBox);
        }
        if (Box3.getScaleX() == -1) {
            Box3.getChildren().clear();
            Label temp = new Label();
            temp.setMaxWidth(Box3.getPrefWidth() * 0.05);
            temp.setMinWidth(Box3.getPrefWidth() * 0.05);
            Box3.getChildren().addAll(barBox, temp, analyzer.newBox);
        }
      
        parent.add(Box1, 0, 0);
        parent.add(Box2, 0, 1);
        parent.add(Box3, 0, 2);
    }
    
    // For drwaing vertical energy and pitch bar
    public void barVertical() {
       barBox.setMaxWidth(Box3.getPrefWidth() * 0.067);
        barBox.setMinWidth(Box3.getPrefWidth() * 0.067);

        barBox.setPrefWidth(Box3.getPrefWidth() * 0.067);
        barBox.setMaxHeight(Box3.getPrefHeight()*0.8);
        barBox.setMinHeight(Box3.getPrefHeight()*0.8);
        energyBar.setMaxWidth(Box3.getPrefHeight() * 0.2);
        energyBar.setMinWidth(Box3.getPrefHeight() * 0.2);
        energyBar.getTransforms().setAll(new Translate(0, 200.0 * imgHeight / 450.0), new Rotate(-90, 0, 0));
        energyBar.setTranslateX(barBox.getPrefWidth() / 1.2);
        energyBar.setMaxHeight(10);
        energyBar.setMinHeight(10);
        energyBar.setStyle("-fx-base: #353535; -fx-accent: #FE7D0B;-fx-control-inner-background: #353535; ");

        // energyBar.setStyle("-fx-base: #D3D3D3; -fx-accent: #FE7D0B;-fx-control-inner-background: #D3D3D3;-fx-border-width: 2 2 2 2;-fx-border-color:#000000;-fx-padding: -1px;");
        energybarTxt = new Text("L");
        energybarTxt.setStyle("-fx-font: 18px Tahoma;-fx-fill: #FE7D0B;-fx-stroke: black;-fx-stroke-width: 1;");
        energybarTxt.setTranslateY(60.0 * imgHeight / 450.0);
        energybarTxt.setTranslateX(energyBar.getTranslateX());
        VBox energyBox = new VBox();
        energyBox.getChildren().addAll(energybarTxt, energyBar);

        pitchBar.setMaxWidth(Box3.getPrefHeight() * 0.2);
        pitchBar.setMinWidth(Box3.getPrefHeight() * 0.2);
        pitchBar.setMaxHeight(10);
        pitchBar.setMinHeight(10);

        pitchBar.getTransforms().setAll(new Translate(0, 360.0 * imgHeight / 450.0), new Rotate(-90, 0, 0));
        pitchBar.setStyle("-fx-base: #353535; -fx-accent: #0BFAFE;-fx-control-inner-background: #353535; ");
        pitchBar.setTranslateX(energyBar.getTranslateX());
        pitchbarTxt = new Text("P");
        pitchbarTxt.setStyle("-fx-font: 18px Tahoma;-fx-fill: #0BFAFE;-fx-stroke: black;-fx-stroke-width: 1;");
        pitchbarTxt.setTranslateY(220.0 * imgHeight / 450.0);
        pitchbarTxt.setTranslateX(energyBar.getTranslateX());
        if (!analyzer.isImgLeft) {
            energybarTxt.setScaleX(1);
            pitchbarTxt.setScaleX(1);
        } else {
            energybarTxt.setScaleX(-1);
            pitchbarTxt.setScaleX(-1);
        }
        VBox pitchBox = new VBox();
        pitchBox.getChildren().addAll(pitchbarTxt, pitchBar);
        barBox.getChildren().addAll(energyBox, pitchBox);
    }
    
    // For Drawing horizontal energy and pitch bar
    public void barHorizontal() {
         barBox.setMaxWidth(Box3.getPrefWidth() * 0.067);
        barBox.setMinWidth(Box3.getPrefWidth() * 0.067);

        barBox.setPrefWidth(Box3.getPrefWidth() * 0.067);
        energyBar.getTransforms().setAll(new Translate(imgWidth / 6, imgHeight * 0.6), new Rotate(180, 0, 0));
        energyBar.setMaxHeight(10);
        energyBar.setMinHeight(10);

        //energyBar.setStyle("-fx-accent: #FE7D0B ;-fx-control-inner-background: rgba(10,10,10,0);");
        energyBar.setStyle("-fx-base: #353535; -fx-accent: #FE7D0B;-fx-control-inner-background: #353535; ");

        energybarTxt = new Text("L");
        energybarTxt.setStyle("-fx-font: 22px Tahoma;-fx-fill: #FE7D0B;-fx-stroke: black;-fx-stroke-width: 1;");
        energybarTxt.setTranslateX(imgWidth / 15);
        energybarTxt.setTranslateY(imgHeight * 0.53);
        HBox energyBox = new HBox();
        energyBox.getChildren().addAll(energybarTxt, energyBar);

        pitchBar.getTransforms().setAll(new Translate(imgWidth / 2, imgHeight * 0.88), new Rotate(180, 0, 0));

        pitchBar.setStyle("-fx-base: #353535; -fx-accent: #0BFAFE;-fx-control-inner-background: #353535; ");
        pitchBar.setMaxHeight(10);
        pitchBar.setMinHeight(10);

        pitchbarTxt = new Text("P");
        pitchbarTxt.setStyle("-fx-font: 22px Tahoma;-fx-fill: #0BFAFE;-fx-stroke: black;-fx-stroke-width: 1;");
        pitchbarTxt.setTranslateX(imgWidth / 2.4);
        pitchbarTxt.setTranslateY(imgHeight * 0.82);

        if (!analyzer.isImgLeft) {
            energybarTxt.setScaleX(1);
            pitchbarTxt.setScaleX(1);
        } else {
            energybarTxt.setScaleX(-1);
            pitchbarTxt.setScaleX(-1);
        }
        HBox pitchBox = new HBox();
        pitchBox.getChildren().addAll(pitchbarTxt, pitchBar);

        barBox.getChildren().addAll(energyBox, pitchBox);

    }
    
    // For creating vertical line over graph

    private Line createVerticalLine(XYChart<Number, Number> chart, NumberAxis xAxis, NumberAxis yAxis, Pane container, ObservableDoubleValue x) {
        Line line = new Line();
        line.setStroke(Color.WHITE);
        line.startXProperty().bind(Bindings.createDoubleBinding(() -> {
            double xInAxis = xAxis.getDisplayPosition(x.get());
            Point2D pointInScene = xAxis.localToScene(xInAxis, 0);
            double xInContainer = chart.sceneToLocal(pointInScene).getX();
            return xInContainer;
        }, x, chart.boundsInParentProperty(), xAxis.lowerBoundProperty(), xAxis.upperBoundProperty()));
        line.endXProperty().bind(line.startXProperty());
        line.startYProperty().bind(Bindings.createDoubleBinding(() -> {
            double upperY = yAxis.getDisplayPosition(yAxis.getUpperBound());
            Point2D pointInScene = yAxis.localToScene(0, upperY);
            double yInContainer = container.sceneToLocal(pointInScene).getY();
            return yInContainer;
        }, chart.boundsInParentProperty(), yAxis.lowerBoundProperty()));
        line.endYProperty().bind(Bindings.createDoubleBinding(() -> {
            double lowerY = yAxis.getDisplayPosition(yAxis.getUpperBound());
            Point2D pointInScene = yAxis.localToScene(0, lowerY);
            double yInContainer = container.sceneToLocal(pointInScene).getY() + chart.getMaxWidth() / 12 + 12;
            return yInContainer;
        }, chart.boundsInParentProperty(), yAxis.lowerBoundProperty()));
        line.visibleProperty().bind(Bindings.lessThan(x, xAxis.lowerBoundProperty()).and(Bindings.greaterThan(x, xAxis.upperBoundProperty())).not());
        return line;

    }
    // For Pitch graph
    public void pitchPlot(LineChart pitchWaveform) {
        pitchchartHolder.getChildren().clear();
        pitchWaveform.setStyle("CHART_COLOR_1: #0BFAFE;");
        pitchWaveform.setId("stroke_line");
        pitchWaveform.setMaxWidth(graphWidth);
        pitchWaveform.setMinWidth(graphWidth);
        pitchWaveform.setMaxHeight(graphHeight);
        pitchWaveform.setMinHeight(graphHeight);
        pitchWaveform.setPadding(new Insets(0, 0, 0, 0));
        pitchWaveform.getYAxis().setLabel("Pitch (Hz)");
        Set<Node> axisNode = pitchWaveform.lookupAll(".axis");
        int i = 0;
        for (final Node axis : axisNode) {
            if (i != 0) {
                axis.setStyle("-fx-padding: 0 0 0 0;-fx-font-size: 9;");
                break;
            }
            i++;
        }
        pitchchartHolder.getChildren().add(pitchWaveform);
        pitchchartHolder.getChildren().add(line2);
   }
    // For energy Graph
    public void energyPlot(LineChart<Number, Number> energyWaveform) {
        energychartHolder.getChildren().clear();
        energyWaveform.setStyle("CHART_COLOR_1: #FE7D0B;");
        energyWaveform.setId("stroke_line");
        energyWaveform.setMaxWidth(graphWidth);
        energyWaveform.setMinWidth(graphWidth);
        energyWaveform.setMaxHeight(graphHeight);
        energyWaveform.setMinHeight(graphHeight);
        energyWaveform.setPadding(new Insets(0, 0, 0, 0));
        energyWaveform.getYAxis().setLabel("Level (dB)");
        Set<Node> axisNode = energyWaveform.lookupAll(".axis");
        int i = 0;
        for (final Node axis : axisNode) {
            if (i != 0) {
                axis.setStyle("-fx-padding: 0 0 0 0;-fx-font-size: 9;");
                break;
            }
            i++;
        }
        energychartHolder.getChildren().add(energyWaveform);
        energychartHolder.getChildren().add(line3);
    }
}