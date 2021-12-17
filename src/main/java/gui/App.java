package gui;

import CSV.CSVReader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import world.*;

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
    public GridPane borderedMap = new GridPane();
    public GridPane borderlessMap = new GridPane();
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
        for(int i = 0; i<this.spawnAnimals; i++){
            Animal toAdd = new Animal(new Vector2d(getRandomNumber(0, this.width), getRandomNumber(0, this.height)), this.startEnergy);
            toAdd.addObserver(bMap);
            this.bMap.addAnimal(toAdd);
            this.bMap.animalsAlive = this.spawnAnimals;
        }
        for(int i = 0; i<this.spawnAnimals; i++){
            Animal toAdd = new Animal(new Vector2d(getRandomNumber(0, this.width), getRandomNumber(0, this.height)), this.startEnergy);
            toAdd.addObserver(lMap);
            this.lMap.addAnimal(toAdd);
            this.lMap.animalsAlive = this.spawnAnimals;
        }
    }
    public void start(Stage primaryStage) throws Exception{
        borderedMap.setGridLinesVisible(true);
        borderlessMap.setGridLinesVisible(true);
        for(int col = 0; col <= bMap.width; col++){
            borderedMap.getColumnConstraints().add(new ColumnConstraints(12));
        }
        for(int row = 0; row <= bMap.height; row++){
            borderedMap.getRowConstraints().add(new RowConstraints(12));
        }
        for(int row = 0; row <= bMap.height; row++){
            for(int col = 0; col <= bMap.width; col++){
                Label toAdd = new Label(" ");
                borderedMap.add(toAdd, col, row);
            }
        }
        for(int col = 0; col <= lMap.width; col++){
            borderlessMap.getColumnConstraints().add(new ColumnConstraints(12));
        }
        for(int row = 0; row <= lMap.height; row++){
            borderlessMap.getRowConstraints().add(new RowConstraints(12));
        }
        for(int row = 0; row <= lMap.height; row++){
            for(int col = 0; col <= lMap.width; col++){
                Label toAdd = new Label(" ");
                borderlessMap.add(toAdd, col, row);
            }
        }
        primaryStage.setTitle("Evolution");
        Button start = new Button("press to start!");
        start.setOnAction(action -> {
            SimulationEngine engine = new SimulationEngine(this.bMap, this.refresh, this.width, this.height, this.days, this.startEnergy, this.moveEnergy, this.plantEnergy, this.spawnGrass, this);
            Thread engineThread = new Thread(engine);
            engineThread.start();
        });
        VBox chosenParams = new VBox(new Text("Current parameters:"),
                new Text("refresh rate : " + this.parameters[0]),
                new Text("width: " + this.parameters[1]),
                new Text("height: " + this.parameters[2]),
                new Text("days: " + this.parameters[3]),
                new Text("start energy: " + this.parameters[4]),
                new Text("move energy: " + this.parameters[5]),
                new Text("plant energy: " + this.parameters[6]),
                new Text("animals: " + this.parameters[7]),
                new Text("plants: " + this.parameters[8]),
                new Text("jungle ratio: " + this.parameters[9]),
                start);
        Scene scene = new Scene(new HBox(chosenParams, borderedMap, borderlessMap), 1000, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void drawMap(){
        Platform.runLater(() -> {
            borderedMap.getChildren().clear();
            Set<Vector2d> keys = bMap.grasses.keySet();
            for(Vector2d location : keys){
                Label toAdd = new Label();
                GuiElementBox grass = new GuiElementBox("src/main/resources/grass.png");
                toAdd.setGraphic(grass.imageView);
                borderedMap.add(toAdd, location.x, location.y);
            }
            keys = bMap.jungle.keySet();
            for(Vector2d location : keys){
                Label toAdd = new Label();
                GuiElementBox grass = new GuiElementBox("src/main/resources/grass.png");
                toAdd.setGraphic(grass.imageView);
                borderedMap.add(toAdd, location.x, location.y);
            }
            keys = bMap.animals.keySet();
            for(Vector2d location : keys){
                if(bMap.animals.get(location).getMaxEnergy() > 5*startEnergy){
                    Label toAdd = new Label();
                    GuiElementBox strong = new GuiElementBox("src/main/resources/red.png");
                    toAdd.setGraphic(strong.imageView);
                    borderedMap.add(toAdd, location.x, location.y);
                }
                else{
                    Label toAdd = new Label();
                    GuiElementBox weak = new GuiElementBox("src/main/resources/blue.png");
                    toAdd.setGraphic(weak.imageView);
                    borderedMap.add(toAdd, location.x, location.y);
                }
            }
            //zmiana
            borderlessMap.getChildren().clear();
            keys = lMap.grasses.keySet();
            for(Vector2d location : keys){
                Label toAdd = new Label();
                GuiElementBox grass = new GuiElementBox("src/main/resources/grass.png");
                toAdd.setGraphic(grass.imageView);
                borderlessMap.add(toAdd, location.x, location.y);
            }
            keys = lMap.jungle.keySet();
            for(Vector2d location : keys){
                Label toAdd = new Label();
                GuiElementBox grass = new GuiElementBox("src/main/resources/grass.png");
                toAdd.setGraphic(grass.imageView);
                borderlessMap.add(toAdd, location.x, location.y);
            }
            keys = lMap.animals.keySet();
            for(Vector2d location : keys){
                if(lMap.animals.get(location).getMaxEnergy() > 5*startEnergy){
                    Label toAdd = new Label();
                    GuiElementBox strong = new GuiElementBox("src/main/resources/red.png");
                    toAdd.setGraphic(strong.imageView);
                    borderlessMap.add(toAdd, location.x, location.y);
                }
                else{
                    Label toAdd = new Label();
                    GuiElementBox weak = new GuiElementBox("src/main/resources/blue.png");
                    toAdd.setGraphic(weak.imageView);
                    borderlessMap.add(toAdd, location.x, location.y);
                }
            }
        });
    }
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}