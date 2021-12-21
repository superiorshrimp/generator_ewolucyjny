package gui;

import CSV.CSVReader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import world.*;

import java.util.ArrayList;
import java.util.Random;

import java.util.Set;

public class App extends Application{
    public String[] parameters;
    public AbstractWorldMap bMap;
    public AbstractWorldMap lMap;
    public int refresh;
    public int width;
    public int height;
    public int days;
    public int startEnergy;
    public int moveEnergy;
    public int plantEnergy;
    public int spawnAnimals;
    public int spawnGrass;
    public double jungleRatio;
    public int mode;
    public final GridPane borderedMap = new GridPane();
    public final GridPane borderlessMap = new GridPane();
    public final NumberAxis bAGxAx = new NumberAxis();
    public final NumberAxis bAGyAx = new NumberAxis();
    public final LineChart<Number, Number> bAGChart = new LineChart(bAGxAx, bAGyAx);
    public XYChart.Series<Number, Number> bAGSeries = new XYChart.Series<>();
    public final NumberAxis lAGxAx = new NumberAxis();
    public final NumberAxis lAGyAx = new NumberAxis();
    public final LineChart<Number, Number> lAGChart = new LineChart(lAGxAx, lAGyAx);
    public XYChart.Series<Number, Number> lAGSeries = new XYChart.Series<>();
    public ArrayList<GuiElementBox> grassImages;
    public ArrayList<GuiElementBox> strongAnimalImages;
    public ArrayList<GuiElementBox> weakAnimalImages;
    public int running = 0;
    public void init(){
        String[] args = getParameters().getRaw().toArray(new String[0]);
        String filePath = args[0];
        CSVReader csvreader = new CSVReader(filePath);
        String[] parameters = csvreader.read();
        this.parameters = parameters;
        this.refresh = Integer.parseInt(parameters[0]);
        this.width = Integer.parseInt(parameters[1]);
        this.height = Integer.parseInt(parameters[2]);
        this.days = Integer.parseInt(parameters[3]);
        this.startEnergy = Integer.parseInt(parameters[4]);
        this.moveEnergy = Integer.parseInt(parameters[5]);
        this.plantEnergy = Integer.parseInt(parameters[6]);
        this.spawnAnimals = Integer.parseInt(parameters[7]);
        this.spawnGrass = Integer.parseInt(parameters[8]);
        this.jungleRatio = Double.parseDouble(parameters[9]);
        this.mode = Integer.parseInt(parameters[10]);
        AbstractWorldMap bMap = new WorldMapBordered(this.width, this.height, this.jungleRatio);
        AbstractWorldMap lMap = new WorldMapBorderless(this.width, this.height, this.jungleRatio);
        this.bMap = bMap;
        this.lMap = lMap;
        this.strongAnimalImages = new ArrayList<>(2*(1+width)*(1+height));
        this.weakAnimalImages = new ArrayList<>(2*(1+width)*(1+height));
        this.grassImages = new ArrayList<>(2*(1+width)*(1+height));
        for(int i = 0; i<=2*width*height; i++){
            this.grassImages.add(new GuiElementBox("src/main/resources/grass.png"));
            this.strongAnimalImages.add(new GuiElementBox("src/main/resources/blue.png"));
            this.weakAnimalImages.add(new GuiElementBox("src/main/resources/red.png"));
        }
        this.bAGChart.getData().add(this.bAGSeries);
        this.lAGChart.getData().add(this.lAGSeries);
    }
    public void start(Stage primaryStage){
        TextField tfRefresh = new TextField(String.valueOf(this.refresh));
        tfRefresh.textProperty().addListener((observable, oldValue, newValue) -> this.refresh = Integer.parseInt(newValue));
        TextField tfWidth = new TextField(String.valueOf(this.width));
        tfWidth.textProperty().addListener((observable, oldValue, newValue) -> this.width = Integer.parseInt(newValue));
        TextField tfHeight = new TextField(String.valueOf(this.height));
        tfHeight.textProperty().addListener((observable, oldValue, newValue) -> this.height = Integer.parseInt(newValue));
        TextField tfDays = new TextField(String.valueOf(this.days));
        tfDays.textProperty().addListener((observable, oldValue, newValue) -> this.days = Integer.parseInt(newValue));
        TextField tfStartEnergy = new TextField(String.valueOf(this.startEnergy));
        tfStartEnergy.textProperty().addListener((observable, oldValue, newValue) -> this.startEnergy = Integer.parseInt(newValue));
        TextField tfMoveEnergy = new TextField(String.valueOf(this.moveEnergy));
        tfMoveEnergy.textProperty().addListener((observable, oldValue, newValue) -> this.moveEnergy = Integer.parseInt(newValue));
        TextField tfPlantEnergy = new TextField(String.valueOf(this.plantEnergy));
        tfPlantEnergy.textProperty().addListener((observable, oldValue, newValue) -> this.plantEnergy = Integer.parseInt(newValue));
        TextField tfAnimals = new TextField(String.valueOf(this.spawnAnimals));
        tfAnimals.textProperty().addListener((observable, oldValue, newValue) -> this.spawnAnimals = Integer.parseInt(newValue));
        TextField tfPlants = new TextField(String.valueOf(this.spawnGrass));
        tfPlants.textProperty().addListener((observable, oldValue, newValue) -> this.spawnGrass = Integer.parseInt(newValue));
        TextField tfJungleRatio = new TextField(String.valueOf(this.jungleRatio));
        tfJungleRatio.textProperty().addListener((observable, oldValue, newValue) -> this.jungleRatio = Double.parseDouble(newValue));
        TextField tfMode = new TextField(String.valueOf(this.mode));
        tfMode.textProperty().addListener((observable, oldValue, newValue) -> this.mode = Integer.parseInt(newValue));
        primaryStage.setTitle("Evolution");
        Button start = new Button("press to start");
        Button stop = new Button("press to stop");
        Button resume = new Button("press to resume");
        start.setOnAction(action -> {
            AbstractWorldMap bMap = new WorldMapBordered(this.width, this.height, this.jungleRatio);
            AbstractWorldMap lMap = new WorldMapBorderless(this.width, this.height, this.jungleRatio);
            this.bMap = bMap;
            this.lMap = lMap;
            for(int i = 0; i<this.spawnAnimals; i++){
                Animal toAdd = new Animal(new Vector2d(getRandomNumber(0, this.width+1), getRandomNumber(0, this.height+1)), this.startEnergy);
                toAdd.addObserver(bMap);
                this.bMap.addAnimal(toAdd);
                this.bMap.animalsAlive = this.spawnAnimals;
            }
            for(int i = 0; i<this.spawnAnimals; i++){
                Animal toAdd = new Animal(new Vector2d(getRandomNumber(0, this.width+1), getRandomNumber(0, this.height+1)), this.startEnergy);
                toAdd.addObserver(lMap);
                this.lMap.addAnimal(toAdd);
                this.lMap.animalsAlive = this.spawnAnimals;
            }
            for(int col = 0; col <= bMap.width; col++){
                borderedMap.getColumnConstraints().add(new ColumnConstraints(20));
                borderlessMap.getColumnConstraints().add(new ColumnConstraints(20));
            }//grid pane constraints
            for(int row = 0; row <= bMap.height; row++){
                borderedMap.getRowConstraints().add(new RowConstraints(20));
                borderlessMap.getRowConstraints().add(new RowConstraints(20));
            }//grid pane constraints
            for(int row = 0; row <= bMap.height; row++){
                for(int col = 0; col <= bMap.width; col++){
                    Label toAdd = new Label(" ");
                    borderedMap.add(toAdd, col, row);
                    Label toAddL = new Label(" ");
                    borderlessMap.add(toAddL, col, row);
                }
            }//grid pane constraints
            this.strongAnimalImages = new ArrayList<>(2*(1+width)*(1+height));
            this.weakAnimalImages = new ArrayList<>(2*(1+width)*(1+height));
            this.grassImages = new ArrayList<>(2*(1+width)*(1+height));
            for(int i = 0; i<=2*width*height; i++){
                this.grassImages.add(new GuiElementBox("src/main/resources/grass.png"));
                this.strongAnimalImages.add(new GuiElementBox("src/main/resources/red.png"));
                this.weakAnimalImages.add(new GuiElementBox("src/main/resources/blue.png"));
            }
            this.running = 1;
            SimulationEngine engine = new SimulationEngine(this.bMap, this.lMap, this.refresh, this.width, this.height, this.days, this.startEnergy, this.moveEnergy, this.plantEnergy, this.spawnGrass, this);
            Thread engineThread = new Thread(engine);
            engineThread.start();
        });
        stop.setOnAction(act -> {this.running = 0;});
        resume.setOnAction(act -> {this.running = 1;});
        VBox chosenParams = new VBox(new Text("Current parameters:"),
                new HBox(new Text("refresh rate: "), tfRefresh),
                new HBox(new Text("width: "), tfWidth),
                new HBox(new Text("height: "), tfHeight),
                new HBox(new Text("days: "), tfDays),
                new HBox(new Text("start energy: "), tfStartEnergy),
                new HBox(new Text("move energy: "), tfMoveEnergy),
                new HBox(new Text("plant energy: "), tfPlantEnergy),
                new HBox(new Text("animals: "), tfAnimals),
                new HBox(new Text("plants: "), tfPlants),
                new HBox(new Text("jungle ratio: "), tfJungleRatio),
                new HBox(new Text("mode: "), tfMode),
                start,
                stop,
                resume);
        Scene scene = new Scene(new VBox(new HBox(chosenParams, borderedMap, borderlessMap), new HBox(bAGChart, lAGChart)), 1600, 900);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void drawMap(){
        Platform.runLater(() -> {
            int g = 0;
            int sa = 0;
            int wa = 0;
            borderedMap.getChildren().clear();
            Set<Vector2d> keys = bMap.grasses.keySet();
            for(Vector2d location : keys){
                Label toAdd = new Label();
                toAdd.setGraphic(this.grassImages.get(g).imageView);
                borderedMap.add(toAdd, location.x, location.y);
                g++;
            }
            keys = bMap.jungle.keySet();
            for(Vector2d location : keys){
                Label toAdd = new Label();
                toAdd.setGraphic(this.grassImages.get(g).imageView);
                borderedMap.add(toAdd, location.x, location.y);
                g++;
            }
            keys = bMap.animals.keySet();
            for(Vector2d location : keys){
                if(bMap.animals.get(location).getMaxEnergy() < 5*startEnergy){
                    Label toAdd = new Label();
                    toAdd.setGraphic(this.weakAnimalImages.get(wa).imageView);
                    borderedMap.add(toAdd, location.x, location.y);
                    wa++;
                }
                else{
                    Label toAdd = new Label();
                    toAdd.setGraphic(this.strongAnimalImages.get(sa).imageView);
                    borderedMap.add(toAdd, location.x, location.y);
                    sa++;
                }
            }
            //drawing second map below
            borderlessMap.getChildren().clear();
            keys = lMap.grasses.keySet();
            for(Vector2d location : keys){
                Label toAdd = new Label();
                toAdd.setGraphic(this.grassImages.get(g).imageView);
                borderlessMap.add(toAdd, location.x, location.y);
                g++;
            }
            keys = lMap.jungle.keySet();
            for(Vector2d location : keys){
                Label toAdd = new Label();
                toAdd.setGraphic(this.grassImages.get(g).imageView);
                borderlessMap.add(toAdd, location.x, location.y);
                g++;
            }
            keys = lMap.animals.keySet();
            for(Vector2d location : keys){
                if(lMap.animals.get(location).getMaxEnergy() < 5*this.startEnergy){
                    Label toAdd = new Label();
                    toAdd.setGraphic(this.weakAnimalImages.get(wa).imageView);
                    borderlessMap.add(toAdd, location.x, location.y);
                    wa++;
                }
                else{
                    Label toAdd = new Label();
                    toAdd.setGraphic(this.strongAnimalImages.get(sa).imageView);
                    borderlessMap.add(toAdd, location.x, location.y);
                    sa++;
                }
            }
        });
    }
    public void drawGraph(int day){
        Platform.runLater(() -> {
            bAGSeries.getData().add(new XYChart.Data<>(day, this.bMap.animalsAlive));
            lAGSeries.getData().add(new XYChart.Data<>(day, this.lMap.animalsAlive));
        });
    }
    public int getRandomNumber(int min, int max) {
        return new Random().nextInt(max) + min;
    }
}