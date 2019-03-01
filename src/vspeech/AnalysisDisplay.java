package vspeech;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.DoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;

// For Analysis Window 
public class AnalysisDisplay {

    private final GridPane parent;
    private double graphWidth;
    private double graphHeight;
    private double graphPadding;
    private Set<Node> axisNode;
    private Set<Node> axisLabel;
    double max_X;

    public AnalysisDisplay(GridPane parent) {
        this.parent = parent;
    }

    /*
    Input: 
        Analyzer content window
        Waveform chart
        Energy chart 
        Pitch chart
        Spectrogram as BufferedImage
        Areagram as BufferedImage
     */
    public void display(Analyzer analyzer, Stage parentStage, LineChart<Number, Number> waveform, LineChart<Number, Number> energyWaveform, LineChart<Number, Number> pitchWaveform, BufferedImage spectrogram, BufferedImage areagram, Label label, DoubleProperty fontSize) {
        parent.getChildren().clear();
        VBox Box1 = new VBox();
        Box1.setPrefHeight((parent.getMaxHeight()) * 0.41);
        Box1.setPrefWidth(LayoutConstants.fullWidth);
        Box1.setStyle("-fx-background-color: rgba(53,53,53,1);-fx-border-width: 2 2 0 2;-fx-border-color:#FFFFFF;");
        graphWidth = parent.getMaxWidth() * 0.9;
        graphHeight = Box1.getPrefHeight() * 0.3;
        graphPadding = 0;
        max_X = analyzer.objVisual.getDataWave_x().size() / 10000.0;
        HBox waveformBox = new HBox();
        waveform.setStyle("CHART_COLOR_1: #C0AD03;");
        waveform.setMaxWidth(graphWidth);
        waveform.setMinWidth(graphWidth);
        waveform.setMaxHeight(graphHeight);
        waveform.setMinHeight(graphHeight);
        waveform.setPadding(new Insets(0, 0, 0, graphPadding));
        waveform.getYAxis().setLabel("Sig. (nrm)");
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
        waveformBox.getChildren().addAll(waveform);

        Label xAxisLbl = new Label("Time (s)");
        xAxisLbl.setStyle("-fx-wrap-text:true; -fx-text-alignment: left; -fx-alignment: center-left; -fx-font-size: " + fontSize.subtract(50) + "; -fx-font-family: \"Segoe UI Semibold\"; -fx-text-fill: #FFFFFF;");
        xAxisLbl.setPadding(new Insets(-5, 0, 0, waveform.getMaxWidth() / 2));

        HBox energychartBox = new HBox();
        energyWaveform.setStyle("CHART_COLOR_1: #FE7D0B;");
        energyWaveform.setId("stroke_line");
        energyWaveform.setMaxWidth(graphWidth);
        energyWaveform.setMinWidth(graphWidth);
        energyWaveform.setMaxHeight(graphHeight);
        energyWaveform.setMinHeight(graphHeight);
        energyWaveform.setPadding(new Insets(0, 0, 0, graphPadding));
        energyWaveform.getYAxis().setLabel("Level (dB)");
        axisNode = energyWaveform.lookupAll(".axis");
        i = 0;
        for (final Node axis : axisNode) {
            if (i != 0) {
                axis.setStyle("-fx-padding: 0 0 0 0;-fx-font-size: " + fontSize.get() + ";");
                break;
            }
            i++;
        }
        energychartBox.getChildren().addAll(energyWaveform);

        HBox pitchchartBox = new HBox();
        pitchWaveform.setStyle("CHART_COLOR_1: #0BFAFE;");
        pitchWaveform.setId("stroke_line");
        pitchWaveform.setMaxWidth(graphWidth);
        pitchWaveform.setMinWidth(graphWidth);
        pitchWaveform.setMaxHeight(graphHeight);
        pitchWaveform.setMinHeight(graphHeight);
        pitchWaveform.setPadding(new Insets(-10, 0, 0, graphPadding));
        pitchWaveform.getYAxis().setLabel("Pitch (Hz)");
        axisNode = pitchWaveform.lookupAll(".axis");
        i = 0;
        for (final Node axis : axisNode) {
            if (i != 0) {
                axis.setStyle("-fx-padding: 0 0 0 0;-fx-font-size: " + fontSize.get() + ";");
                break;
            }
            i++;
        }
        pitchchartBox.getChildren().addAll(pitchWaveform);
        Box1.getChildren().addAll(waveformBox, xAxisLbl, energychartBox, pitchchartBox); // vertical Box containing wavform chart, energy chart , x-axis label and pitch chart 

        VBox Box2 = new VBox();
        Box2.setPrefHeight((parent.getMaxHeight()) * 0.51);
        Box2.setPrefWidth(LayoutConstants.fullWidth);
        Box2.setStyle("-fx-background-color: rgba(255,255,255,1);-fx-border-width: 2 2 2 2;-fx-border-color:#FFFFFF;");
        Box2.setPadding(new Insets(0, 0, 0, 0));
        double X = 0;
        if (waveform.getYAxis().getWidth() > 0) {
            X = waveform.getYAxis().getWidth() + waveform.getYAxis().getTickLength();
            LayoutConstants.imagePadding = X;
        }
        // For spectrogram 

        LineChart<Number, Number> chart;
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        chart = new LineChart<>(xAxis, yAxis);
        chart.setStyle("-fx-background-color: transparent;");
        final Node chartBackground = chart.lookup(".chart-plot-background");
        for (Node n : chartBackground.getParent().getChildrenUnmodifiable()) {
            n.setStyle("-fx-background-color: transparent; ");
        }
        chart.setAlternativeRowFillVisible(false);
        chart.setAlternativeColumnFillVisible(false);
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(max_X);
        xAxis.setTickUnit(0.25);
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(0.5);
        yAxis.setTickUnit(0.1);
        yAxis.setMinorTickVisible(false);
        yAxis.setLabel("Freq. (norm)");
        labelConverter lConv = new labelConverter<>();
        yAxis.setTickLabelFormatter(lConv);
        axisNode = chart.lookupAll(".axis");
        Set<Node> axisLabel = chart.lookupAll(".axis-label");
        i = 0;
        for (final Node axis : axisNode) {
            if (i != 0) {
                axis.setStyle("-fx-tick-label-fill: #000000;-fx-font-size:" + fontSize.subtract(50) + ";");
                break;
            }
            i++;
        }
        i = 0;
        for (final Node axis : axisLabel) {
            if (i != 0) {
                axis.setStyle("-fx-text-fill: #000000;-fx-font-size: " + fontSize.subtract(50) + ";-fx-opacity: 1.0;");
                break;
            }
            i++;
        }
        StackPane specPane = new StackPane();
        Image specImage = SwingFXUtils.toFXImage(spectrogram, null);

        ImageView spectrumView = new ImageView();
        spectrumView.setImage(specImage);
        spectrumView.setFitHeight(Box2.getPrefHeight() * 0.5);
        spectrumView.setFitWidth(graphWidth - LayoutConstants.imagePadding - 7);

        BufferedImage specbar = new BufferedImage(spectrogram.getHeight(), 255, BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < 255; x++) {
            for (int y = 0; y < spectrogram.getHeight(); y++) {
                int value = 255 - x;
                value = ((value << 16) | (value << 8) | value);
                specbar.setRGB(y, 255 - x - 1, value);
            }
        }

        Image specbarImg = SwingFXUtils.toFXImage(specbar, null);
        ImageView specbarView = new ImageView();
        specbarView.setImage(specbarImg);
        specbarView.setFitHeight(Box2.getPrefHeight() * 0.5);
        specbarView.setFitWidth(15);

        Slider barSlider = new Slider();
        barSlider.setId("bar");
        barSlider.setMin(0);
        barSlider.setMax(80);
        barSlider.setShowTickMarks(true);
        barSlider.setShowTickLabels(true);
        barSlider.setMajorTickUnit(20);
        barSlider.setMinorTickCount(0);
        barSlider.setOrientation(Orientation.VERTICAL);
        barSlider.setMinHeight(0);
        barSlider.setMaxHeight(Box2.getPrefHeight() * 0.5);
        barSlider.setPadding(new Insets(5, 0, 0, 0));

        HBox specBox = new HBox();
        specBox.setSpacing(10);
        specBox.setPadding(new Insets(0, 0, 0, LayoutConstants.imagePadding));

        HBox specBarbox = new HBox();
        specBarbox.setPrefHeight(Box2.getPrefHeight() * 0.5);
        specBarbox.getChildren().addAll(specbarView, barSlider);

        HBox specViewbox = new HBox();
        specViewbox.setPadding(new Insets(2, 0, 0, 0));
        specViewbox.getChildren().add(spectrumView);

        specBox.getChildren().addAll(specViewbox, specBarbox);

        specBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        enlargeSpectro(parentStage, spectrogram);
                    }
                }
            }
        });
        if (chart.getXAxis().getWidth() > 0) {
            X = chart.getXAxis().getWidth() + chart.getXAxis().getTickLength() + 5;
            LayoutConstants.graphpadding = X;
        }
        HBox specChart = new HBox();
        chart.setMaxHeight(spectrumView.getFitHeight() + LayoutConstants.graphpadding + 7);
        chart.setMinHeight(spectrumView.getFitHeight() + LayoutConstants.graphpadding + 7);
        chart.setMaxWidth(spectrumView.getFitWidth() + LayoutConstants.imagePadding + 15);
        chart.setMinWidth(spectrumView.getFitWidth() + LayoutConstants.imagePadding + 15);

        specChart.setPadding(new Insets(-7, 0, 0, -15));
        specChart.getChildren().addAll(chart);
        spectrumView.setOnMouseMoved(event -> {
            {
                Point2D pointInScene = new Point2D(event.getSceneX(), event.getSceneY());
                Axis<Number> xAxis1 = chart.getXAxis();
                Axis<Number> yAxis1 = chart.getYAxis();
                double xPosInAxis = xAxis1.sceneToLocal(new Point2D(pointInScene.getX(), 0)).getX();
                double yPosInAxis = yAxis1.sceneToLocal(new Point2D(0, pointInScene.getY())).getY();
                Number x1 = xAxis1.getValueForDisplay(xPosInAxis).floatValue();
                Number y1 = yAxis1.getValueForDisplay(yPosInAxis).floatValue();
                String xString = x1.toString();
                if (xString.length() > 5) {
                    xString = xString.substring(0, 5);
                }
                String yString = y1.toString();
                if (yString.length() > 5) {
                    yString = yString.substring(0, 5);
                }
                Robot robot = null;
                try {
                    robot = new Robot();
                } catch (AWTException ex) {
                    new newLogging("SEVERE", AnalysisDisplay.class.getName(), "displayAnalysis()", ex);
                }
                Color color = robot.getPixelColor((int) event.getScreenX(), (int) event.getScreenY());
                int red = color.getRed();
                int blue = color.getBlue();
                int green = color.getGreen();
                int rgb = (red + green + blue) / 3;

                int value = (((255 - rgb) * 80)) / 255;
                if (x1.doubleValue() >= 0 && x1.doubleValue() <= max_X && y1.doubleValue() >= 0 && y1.doubleValue() <= 0.6) {
                    label.setText("X = " + xString + " , Y =  " + yString + " , Z = " + value);
                } else {
                    label.setText("X =   , Y =   ");
                }
            }
        });
        spectrumView.setOnMouseExited((MouseEvent e) -> {
            label.setText("X =   , Y = ");
        });
        specPane.getChildren().addAll(specBox, specChart); // Containing spectrogram along with side-bar
        specChart.setDisable(true);

        // For Areagram
        StackPane areaPane = new StackPane();
        LineChart<Number, Number> Areachart;
        final NumberAxis xArea = new NumberAxis();
        final NumberAxis yArea = new NumberAxis();
        Areachart = new LineChart<>(xArea, yArea);
        Areachart.setStyle("-fx-background-color: transparent;");
        final Node AreachartBackground = Areachart.lookup(".chart-plot-background");
        for (Node n : AreachartBackground.getParent().getChildrenUnmodifiable()) {
            n.setStyle("-fx-background-color: transparent;");
        }
        Areachart.setHorizontalGridLinesVisible(false);
        Areachart.setVerticalGridLinesVisible(false);
        xArea.setTickLabelsVisible(false);
        xArea.setTickMarkVisible(false);
        xArea.setMinorTickVisible(false);
        xArea.setAutoRanging(false);
        xArea.setLowerBound(0);
        xArea.setUpperBound(max_X);
        xArea.setTickUnit(0.25);
        yArea.setAutoRanging(false);
        yArea.setLowerBound(0);
        yArea.setUpperBound(1);
        yArea.setTickUnit(0.2);
        yArea.setMinorTickVisible(false);
        yArea.setLabel("Dis t. (norm)");
        yArea.setTickLabelFormatter(lConv);
        axisNode = Areachart.lookupAll(".axis");
        axisLabel = Areachart.lookupAll(".axis-label");
        i = 0;
        for (final Node axis : axisNode) {
            if (i != 0) {
                axis.setStyle("-fx-tick-label-fill: #000000;-fx-font-size:" + fontSize.subtract(50) + ";");
                break;
            }
            i++;
        }
        i = 0;
        for (final Node axis : axisLabel) {
            if (i != 0) {
                axis.setStyle("-fx-text-fill: #000000;-fx-font-size: " + fontSize.subtract(50) + ";-fx-opacity: 1.0;");
                break;
            }
            i++;
        }
        Image areaImage = SwingFXUtils.toFXImage(areagram, null);
        ImageView areagramView = new ImageView();
        areagramView.setImage(areaImage);
        areagramView.setFitHeight(Box2.getPrefHeight() * 0.5);
        areagramView.setFitWidth(graphWidth - LayoutConstants.imagePadding - 7);
        BufferedImage areabar = new BufferedImage(areagram.getHeight(), 255, BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < 255; x++) {
            for (int y = 0; y < areagram.getHeight(); y++) {
                int value = x;
                value = ((value << 16) | (value << 8) | value);
                areabar.setRGB(y, 255 - x - 1, value);
            }
        }
        Image areabarImg = SwingFXUtils.toFXImage(areabar, null);
        ImageView areabarView = new ImageView();
        areabarView.setImage(areabarImg);
        areabarView.setFitHeight(Box2.getPrefHeight() * 0.5);
        areabarView.setFitWidth(15);

        Slider areaSlider = new Slider();
        areaSlider.setId("bar");
        areaSlider.setMin(0);
        areaSlider.setMax(1);
        areaSlider.setShowTickMarks(true);
        areaSlider.setShowTickLabels(true);
        areaSlider.setMajorTickUnit(0.25);
        areaSlider.setMinorTickCount(0);
        areaSlider.setOrientation(Orientation.VERTICAL);
        areaSlider.setMinHeight(0);
        areaSlider.setMaxHeight(Box2.getPrefHeight() * 0.49);
        areaSlider.setPadding(new Insets(5, 0, 0, 0));

        HBox areachartBox = new HBox();
        Areachart.setMaxWidth(areagramView.getFitWidth() + LayoutConstants.imagePadding + 15);
        Areachart.setMinWidth(areagramView.getFitWidth() + LayoutConstants.imagePadding + 15);
        Areachart.setMaxHeight(areagramView.getFitHeight() + LayoutConstants.graphpadding + 12);
        Areachart.setMinHeight(areagramView.getFitHeight() + LayoutConstants.graphpadding + 12);
        areachartBox.setPadding(new Insets(-12, 0, 0, -15));
        areachartBox.getChildren().addAll(Areachart);
        HBox areaBox = new HBox();

        areaBox.setSpacing(10);
        areaBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        enlargeArea(parentStage, areagram);
                    }
                }
            }
        });
        HBox areaBarbox = new HBox();
        areaBarbox.setPrefHeight(Box2.getPrefHeight() * 0.5);
        areaBarbox.setPadding(new Insets(0, 0, 0, 0));
        areaBarbox.getChildren().addAll(areabarView, areaSlider);
        areaBox.getChildren().addAll(areagramView, areaBarbox);
        areaBox.setPadding(new Insets(0, 0, 0, LayoutConstants.imagePadding));
        areaPane.getChildren().addAll(areaBox, areachartBox);
        areachartBox.setDisable(true);
        areagramView.setOnMouseMoved(event -> {
            {
                Point2D pointInScene = new Point2D(event.getSceneX(), event.getSceneY());
                Axis<Number> xAxis1 = Areachart.getXAxis();
                Axis<Number> yAxis1 = Areachart.getYAxis();
                double xPosInAxis = xAxis1.sceneToLocal(new Point2D(pointInScene.getX(), 0)).getX();
                double yPosInAxis = yAxis1.sceneToLocal(new Point2D(0, pointInScene.getY())).getY();
                Number x1 = xAxis1.getValueForDisplay(xPosInAxis).floatValue();
                Number y1 = yAxis1.getValueForDisplay(yPosInAxis).floatValue();
                String xString = x1.toString();
                if (xString.length() > 5) {
                    xString = xString.substring(0, 5);
                }
                String yString = y1.toString();
                if (yString.length() > 5) {
                    yString = yString.substring(0, 5);
                }
                Robot robot = null;
                try {
                    robot = new Robot();
                } catch (AWTException ex) {
                    new newLogging("SEVERE", AnalysisDisplay.class.getName(), "displayAnalysis()", ex);
                }
                Color color = robot.getPixelColor((int) event.getScreenX(), (int) event.getScreenY());
                int red = color.getRed();
                int blue = color.getBlue();
                int green = color.getGreen();
                int rgb = (red + green + blue) / 3;
                double value = (((rgb) * 1)) / 255.0;
                NumberFormat formatter = new DecimalFormat("#0.00");
                if (x1.doubleValue() >= 0 && x1.doubleValue() <= max_X && y1.doubleValue() >= 0 && y1.doubleValue() <= 1.1) {
                    label.setText("X = " + xString + " , Y =  " + yString + " , Z = " + formatter.format(value));
                } else {
                    label.setText("X =   , Y =   ");
                }

            }
        });
        areagramView.setOnMouseExited((MouseEvent e) -> {
            label.setText("X =   , Y = ");
        });
        Box2.getChildren().addAll(specPane, areaPane);
        VBox Box3 = new VBox();
        Box3.setPrefHeight((parent.getMaxHeight()) * 0.02);
        Box3.setPrefWidth(LayoutConstants.fullWidth);
        Box3.setStyle("-fx-background-color: rgba(0,0,0,1);-fx-border-width: 2 2 2 2;-fx-border-color:#FFFFFF;");
        label.setStyle(" -fx-text-fill: #FFFFFF;-fx-font-size:14px; -fx-padding: 5 0 0 15");
        Box3.getChildren().add(label); // For adding label that shows cursor movement values
      
        parent.add(Box1, 0, 0);
        parent.add(Box3, 0, 1);
        parent.add(Box2, 0, 2);
    }

    public void enlargeSpectro(Stage stage, BufferedImage spectrogram) {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Spectrogram");
        primaryStage.initOwner(stage);
        int height = 500;
        int width = 600;
        Text label = new Text("");
        label.setStyle("-fx-font: 24px Times New Roman;-fx-fill: #FFFFFF;");
        StackPane root = new StackPane();

        StackPane specPane = new StackPane();
        Image specImage = SwingFXUtils.toFXImage(spectrogram, null);
        ImageView spectrogramView = new ImageView();
        spectrogramView.setImage(specImage);
        spectrogramView.setFitHeight(height + 10);
        spectrogramView.setFitWidth(width);
        BufferedImage specbar = new BufferedImage(spectrogram.getHeight(), 255, BufferedImage.TYPE_BYTE_GRAY);
        int k = 0;
        for (int x = 0; x < 255; x++) {
            for (int y = 0; y < spectrogram.getHeight(); y++) {
                int value = 255 - x;
                value = ((value << 16) | (value << 8) | value);
                specbar.setRGB(y, 255 - x - 1, value);
            }
        }

        HBox specBox = new HBox();

        specBox.setSpacing(10);
        specBox.getChildren().addAll(spectrogramView);
        specBox.setPadding(new Insets(12, 0, 0, LayoutConstants.imagePadding + 16));
        LineChart<Number, Number> chart;
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        chart = new LineChart<>(xAxis, yAxis);
        chart.setStyle("-fx-background-color: transparent;");
        final Node chartBackground = chart.lookup(".chart-plot-background");
        for (Node n : chartBackground.getParent().getChildrenUnmodifiable()) {
            n.setStyle("-fx-background-color: transparent; ");
        }
        chart.setAlternativeRowFillVisible(false);
        chart.setAlternativeColumnFillVisible(false);
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(max_X);
        xAxis.setTickUnit(0.25);
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(0.5);
        yAxis.setTickUnit(0.1);
        yAxis.setMinorTickVisible(false);
        yAxis.setLabel("Freq. (norm)");
        labelConverter lConv = new labelConverter<>();
        yAxis.setTickLabelFormatter(lConv);
        Set<Node> axisNode = chart.lookupAll(".axis");
        Set<Node> axisLabel = chart.lookupAll(".axis-label");
        int i = 0;
        for (final Node axis : axisNode) {
            axis.setStyle("-fx-tick-label-fill: #000000;-fx-font-size: 12;");

            i++;
        }
        i = 0;
        for (final Node axis : axisLabel) {
            axis.setStyle("-fx-text-fill: #000000;-fx-font-size: 12 ;-fx-opacity: 1.0;");
            i++;
        }
        chart.setMaxHeight(spectrogramView.getFitHeight() + LayoutConstants.graphpadding + 10);
        chart.setMinHeight(spectrogramView.getFitHeight() + LayoutConstants.graphpadding + 10);
        chart.setMaxWidth(spectrogramView.getFitWidth() + LayoutConstants.imagePadding + 15);
        chart.setMinWidth(spectrogramView.getFitWidth() + LayoutConstants.imagePadding + 15);

        spectrogramView.setOnMouseMoved(event -> {
            {
                Point2D pointInScene = new Point2D(event.getSceneX(), event.getSceneY());
                Axis<Number> xAxis1 = chart.getXAxis();
                Axis<Number> yAxis1 = chart.getYAxis();
                double xPosInAxis = xAxis1.sceneToLocal(new Point2D(pointInScene.getX(), 0)).getX();
                double yPosInAxis = yAxis1.sceneToLocal(new Point2D(0, pointInScene.getY())).getY();
                Number x1 = xAxis1.getValueForDisplay(xPosInAxis).floatValue();
                Number y1 = yAxis1.getValueForDisplay(yPosInAxis).floatValue();
                String xString = x1.toString();
                if (xString.length() > 5) {
                    xString = xString.substring(0, 5);
                }
                String yString = y1.toString();
                if (yString.length() > 5) {
                    yString = yString.substring(0, 5);
                }
                Robot robot = null;
                try {
                    robot = new Robot();
                } catch (AWTException ex) {
                    Logger.getLogger(AnalysisDisplay.class.getName()).log(Level.SEVERE, null, ex);
                }
                Color color = robot.getPixelColor((int) event.getScreenX(), (int) event.getScreenY());
                int red = color.getRed();
                int blue = color.getBlue();
                int green = color.getGreen();
                int rgb = (red + green + blue) / 3;

                int value = (((255 - rgb) * 80)) / 255;
                if (x1.doubleValue() >= 0 && x1.doubleValue() <= max_X && y1.doubleValue() >= 0 && y1.doubleValue() <= 0.6) {
                    label.setText("\tX = " + xString + " , Y =  " + yString + " , Z = " + value);
                } else {
                    label.setText("\tX =   , Y =   ");
                }
            }
        });
        spectrogramView.setOnMouseExited((MouseEvent e) -> {
            label.setText("\tX =   , Y = ");
        });
        chart.setDisable(true);

        specPane.getChildren().addAll(specBox, chart);

        Image specbarImg = SwingFXUtils.toFXImage(specbar, null);

        ImageView specbarView = new ImageView();
        specbarView.setImage(specbarImg);
        specbarView.setFitHeight(height + 12);
        specbarView.setFitWidth(15);

        Slider barSlider = new Slider();
        barSlider.setId("bar");
        barSlider.setMin(0);
        barSlider.setMax(80);
        barSlider.setShowTickMarks(true);
        barSlider.setShowTickLabels(true);
        barSlider.setMajorTickUnit(20);
        barSlider.setMinorTickCount(0);
        barSlider.setOrientation(Orientation.VERTICAL);
        barSlider.setMinHeight(0);
        barSlider.setMaxHeight(height + 12);
        barSlider.setPadding(new Insets(10, 0, 0, 0));

        VBox Labelcont = new VBox();
        Labelcont.setStyle(" -fx-background-color: #000000;");
        Labelcont.setMaxHeight(30);
        Labelcont.setMinHeight(30);
        Labelcont.setMinWidth(width + 200);
        Labelcont.setMaxWidth(width + 200);

        Labelcont.getChildren().add(label);
        HBox specBarbox = new HBox();
        specBarbox.setPrefHeight(height);
        specBarbox.setPadding(new Insets(12, 0, 0, 30));
        specBarbox.getChildren().addAll(specbarView, barSlider);

        HBox specComb = new HBox();
        specComb.getChildren().addAll(specPane, specBarbox);

        VBox cont = new VBox();
        cont.getChildren().addAll(specComb, Labelcont);
        root.getChildren().add(cont);

        Scene scene = new Scene(root, LayoutConstants.minWidth, LayoutConstants.minHeight);
        scene.getStylesheets().add(Vspeech.class.getResource("/css/style.css").toExternalForm());

        primaryStage.setResizable(false);
        primaryStage.setWidth(width + 200);
        primaryStage.setHeight(height + 100);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void enlargeArea(Stage stage, BufferedImage areagram) {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Spectrogram");
        primaryStage.initOwner(stage);
        int height = 500;
        int width = 600;
        Text label = new Text("");
        label.setStyle("-fx-font: 24px Times New Roman;-fx-fill: #FFFFFF;");
        StackPane root = new StackPane();

        StackPane areaPane = new StackPane();

        Image areaImage = SwingFXUtils.toFXImage(areagram, null);
        ImageView areagramView = new ImageView();
        areagramView.setImage(areaImage);
        areagramView.setFitHeight(height + 10);
        areagramView.setFitWidth(width);
        BufferedImage areabar = new BufferedImage(areagram.getHeight(), 255, BufferedImage.TYPE_BYTE_GRAY);
        int k = 0;
        for (int x = 0; x < 255; x++) {
            for (int y = 0; y < areagram.getHeight(); y++) {
                int value = x;
                value = ((value << 16) | (value << 8) | value);
                areabar.setRGB(y, 255 - x - 1, value);
            }
        }

        HBox areaBox = new HBox();

        areaBox.setSpacing(10);
        areaBox.getChildren().addAll(areagramView);
        areaBox.setPadding(new Insets(12, 0, 0, LayoutConstants.imagePadding + 5));

        LineChart<Number, Number> chart;
        final NumberAxis xArea = new NumberAxis();
        final NumberAxis yArea = new NumberAxis();
        chart = new LineChart<>(xArea, yArea);

        chart.setStyle("-fx-background-color: transparent;");
        final Node AreachartBackground = chart.lookup(".chart-plot-background");
        for (Node n : AreachartBackground.getParent().getChildrenUnmodifiable()) {
            n.setStyle("-fx-background-color: transparent;");
        }
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        xArea.setTickLabelsVisible(false);
        xArea.setTickMarkVisible(false);
        xArea.setMinorTickVisible(false);
        xArea.setAutoRanging(false);
        xArea.setLowerBound(0);
        xArea.setUpperBound(max_X);
        xArea.setTickUnit(0.25);
        yArea.setAutoRanging(false);
        yArea.setLowerBound(0);
        yArea.setUpperBound(1);
        yArea.setTickUnit(0.2);
        yArea.setMinorTickVisible(false);
        yArea.setLabel("Dis t. (norm)");
        labelConverter lConv = new labelConverter<>();
        yArea.setTickLabelFormatter(lConv);
        axisNode = chart.lookupAll(".axis");
        axisLabel = chart.lookupAll(".axis-label");
        int i = 0;
        for (final Node axis : axisNode) {
            if (i != 0) {
                axis.setStyle("-fx-tick-label-fill: #000000;-fx-font-size: 12;");
                break;
            }
            i++;
        }
        i = 0;
        for (final Node axis : axisLabel) {
            if (i != 0) {
                axis.setStyle("-fx-text-fill: #000000;-fx-font-size: 12;-fx-opacity: 1.0;");
                break;
            }
            i++;
        }
        chart.setMaxHeight(areagramView.getFitHeight() + LayoutConstants.graphpadding + 10);
        chart.setMinHeight(areagramView.getFitHeight() + LayoutConstants.graphpadding + 10);
        chart.setMaxWidth(areagramView.getFitWidth() + LayoutConstants.imagePadding + 15);
        chart.setMinWidth(areagramView.getFitWidth() + LayoutConstants.imagePadding + 15);

        areagramView.setOnMouseMoved(event -> {
            {
                Point2D pointInScene = new Point2D(event.getSceneX(), event.getSceneY());
                Axis<Number> xAxis1 = chart.getXAxis();
                Axis<Number> yAxis1 = chart.getYAxis();
                double xPosInAxis = xAxis1.sceneToLocal(new Point2D(pointInScene.getX(), 0)).getX();
                double yPosInAxis = yAxis1.sceneToLocal(new Point2D(0, pointInScene.getY())).getY();
                Number x1 = xAxis1.getValueForDisplay(xPosInAxis).floatValue();
                Number y1 = yAxis1.getValueForDisplay(yPosInAxis).floatValue();
                String xString = x1.toString();
                if (xString.length() > 5) {
                    xString = xString.substring(0, 5);
                }
                String yString = y1.toString();
                if (yString.length() > 5) {
                    yString = yString.substring(0, 5);
                }
                Robot robot = null;
                try {
                    robot = new Robot();
                } catch (AWTException ex) {
                    Logger.getLogger(AnalysisDisplay.class.getName()).log(Level.SEVERE, null, ex);
                }
                Color color = robot.getPixelColor((int) event.getScreenX(), (int) event.getScreenY());
                int red = color.getRed();
                int blue = color.getBlue();
                int green = color.getGreen();
                int rgb = (red + green + blue) / 3;
                double value = (((rgb) * 1)) / 255.0;
                NumberFormat formatter = new DecimalFormat("#0.00");
                if (x1.doubleValue() >= 0 && x1.doubleValue() <= max_X && y1.doubleValue() >= 0 && y1.doubleValue() <= 1.1) {
                    label.setText("\tX = " + xString + " , Y =  " + yString + " , Z = " + formatter.format(value));
                } else {
                    label.setText("\tX =   , Y =   ");
                }
            }
        });
        areagramView.setOnMouseExited((MouseEvent e) -> {
            label.setText("\tX =   , Y = ");
        });
        chart.setDisable(true);

        areaPane.getChildren().addAll(areaBox, chart);

        Image specbarImg = SwingFXUtils.toFXImage(areabar, null);

        ImageView areabarView = new ImageView();
        areabarView.setImage(specbarImg);
        areabarView.setFitHeight(height + 12);
        areabarView.setFitWidth(15);

        Slider barSlider = new Slider();
        barSlider.setId("bar");
        barSlider.setMin(0);
        barSlider.setMax(1);
        barSlider.setShowTickMarks(true);
        barSlider.setShowTickLabels(true);
        barSlider.setMajorTickUnit(0.25);
        barSlider.setMinorTickCount(0);
        barSlider.setOrientation(Orientation.VERTICAL);
        barSlider.setMinHeight(0);
        barSlider.setMaxHeight(height + 12);
        barSlider.setPadding(new Insets(6, 0, 0, 0));

        VBox Labelcont = new VBox();
        Labelcont.setStyle(" -fx-background-color: #000000;");
        Labelcont.setMaxHeight(30);
        Labelcont.setMinHeight(30);
        Labelcont.setMinWidth(width + 200);
        Labelcont.setMaxWidth(width + 200);

        Labelcont.getChildren().add(label);
        HBox areaBarbox = new HBox();
        areaBarbox.setPrefHeight(height);
        areaBarbox.setPadding(new Insets(12, 0, 0, 30));
        areaBarbox.getChildren().addAll(areabarView, barSlider);

        HBox areaComb = new HBox();
        areaComb.getChildren().addAll(areaPane, areaBarbox);

        VBox cont = new VBox();
        cont.getChildren().addAll(areaComb, Labelcont);
        root.getChildren().add(cont);

        Scene scene = new Scene(root, LayoutConstants.minWidth, LayoutConstants.minHeight);
        scene.getStylesheets().add(Vspeech.class.getResource("/css/style.css").toExternalForm());

        primaryStage.setResizable(false);
        primaryStage.setWidth(width + 200);
        primaryStage.setHeight(height + 100);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private class labelConverter<T> extends StringConverter<T> {

        @Override
        public String toString(T t) {
            Double tb = (Double) t;
            Float ta = tb.floatValue();
            String inString = ta.toString();
            String outString = "";
            for (int i = 0; i < 6 - inString.length(); i++) {
                outString = outString + " ";
            }
            outString = outString + inString;
            return outString;
        }

        @Override
        public T fromString(String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
