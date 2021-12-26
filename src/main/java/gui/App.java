package gui;

import CSV.CSVReader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import world.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import java.util.concurrent.atomic.AtomicInteger;

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
    public XYChart.Series<Number, Number> bASeries = new XYChart.Series<>();
    public XYChart.Series<Number, Number> bGSeries = new XYChart.Series<>();

    public final NumberAxis lAGxAx = new NumberAxis();
    public final NumberAxis lAGyAx = new NumberAxis();
    public final LineChart<Number, Number> lAGChart = new LineChart(lAGxAx, lAGyAx);
    public XYChart.Series<Number, Number> lASeries = new XYChart.Series<>();
    public XYChart.Series<Number, Number> lGSeries = new XYChart.Series<>();

    public ArrayList<GuiElementBox> grassImages;
    public ArrayList<GuiElementBox> strongAnimalImages;
    public ArrayList<GuiElementBox> weakAnimalImages;

    public final NumberAxis bLDxAx = new NumberAxis();
    public final NumberAxis bLDyAx = new NumberAxis();
    public final LineChart<Number, Number> bLDChart = new LineChart(bLDxAx, bLDyAx);
    public XYChart.Series<Number, Number> bDSeries = new XYChart.Series<>();
    public XYChart.Series<Number, Number> bAlSeries = new XYChart.Series<>();

    public final NumberAxis lLDxAx = new NumberAxis();
    public final NumberAxis lLDyAx = new NumberAxis();
    public final LineChart<Number, Number> lLDChart = new LineChart(lLDxAx, lLDyAx);
    public XYChart.Series<Number, Number> lDSeries = new XYChart.Series<>();
    public XYChart.Series<Number, Number> lAlSeries = new XYChart.Series<>();

    public AtomicInteger bRunning = new AtomicInteger(0);
    public AtomicInteger lRunning = new AtomicInteger(0);

    public Text bGenotypeMode = new Text("");
    public Text lGenotypeMode = new Text("");

    public Animal bTracked;
    public Animal lTracked;

    public Text bTrackedGenotype = new Text();
    public Text bTrackedIsAlive = new Text();
    public Text bChildrenCount = new Text();
    public Text bDescendantsCount = new Text();
    public Text lTrackedGenotype = new Text();
    public Text lTrackedIsAlive = new Text();
    public Text lChildrenCount = new Text();
    public Text lDescendantsCount = new Text();

    public PrintWriter bReport;
    {
        try {
            bReport = new PrintWriter(new File("bordered_map_report.csv")); //saves everything on last day
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public PrintWriter lReport;
    {
        try {
            lReport = new PrintWriter(new File("borderless_map_report.csv")); //saves everything on last day
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String bState = "";
    public String lState = "";
    public double bAvg1 = 0;
    public double bAvg2 = 0;
    public double bAvg3 = 0;
    public double bAvg4 = 0;
    public double lAvg1 = 0;
    public double lAvg2 = 0;
    public double lAvg3 = 0;
    public double lAvg4 = 0;
    public int bDay = 0;
    public int lDay = 0;

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
        this.bAGxAx.setLabel("day");
        this.bAGyAx.setLabel("count");
        this.bASeries.setName("animals");
        this.bGSeries.setName("grasses");
        this.bAGChart.getData().add(this.bASeries);
        this.bAGChart.getData().add(this.bGSeries);

        this.lAGxAx.setLabel("day");
        this.lAGyAx.setLabel("count");
        this.lASeries.setName("animals");
        this.lGSeries.setName("grasses");
        this.lAGChart.getData().add(this.lASeries);
        this.lAGChart.getData().add(this.lGSeries);

        this.bLDxAx.setLabel("day");
        this.bLDyAx.setLabel("average lifespan of animals");
        this.bDSeries.setName("dead");
        this.bAlSeries.setName("alive");
        this.bLDChart.getData().add(this.bDSeries);
        this.bLDChart.getData().add(this.bAlSeries);

        this.lLDxAx.setLabel("day");
        this.lLDyAx.setLabel("average lifespan of animals");
        this.lDSeries.setName("dead");
        this.lAlSeries.setName("alive");
        this.lLDChart.getData().add(this.lDSeries);
        this.lLDChart.getData().add(this.lAlSeries);

        this.bTracked = null;
        this.lTracked = null;

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
        Button bStop = new Button("press to stop bordered map");
        Button bResume = new Button("press to resume bordered map");
        Button lStop = new Button("press to stop borderless map");
        Button lResume = new Button("press to resume borderless map");

        Button bShowAllModeGenotype = new Button("animals with dominant genotype on bordered map");
        bShowAllModeGenotype.setOnAction(act -> {
            if(this.bRunning.intValue() == 0){
                System.out.println("animals with dominant genotype:");
                for(Animal animal : bMap.animalList){
                    if(animal.getGenotype().equals(bMap.modeOfGenotypes())){
                        System.out.println("b location: " + animal.getPosition() + ", energy: " + animal.getEnergy() + ", facing" + animal.getFacing());
                    }
                }
            }
        });

        Button lShowAllModeGenotype = new Button("animals with dominant genotype on borderless map");
        lShowAllModeGenotype.setOnAction(act -> {
            if(this.lRunning.intValue() == 0){
                System.out.println("animals with dominant genotype:");
                for(Animal animal : lMap.animalList){
                    if(animal.getGenotype().equals(lMap.modeOfGenotypes())){
                        System.out.println("l location: " + animal.getPosition() + ", energy: " + animal.getEnergy() + ", facing" + animal.getFacing());
                    }
                }
            }
        });

        Button bSave = new Button("save current stats (bordered)");
        Button lSave = new Button("save current stats (borderless)");

        bSave.setOnAction(act -> {
            if(this.bRunning.intValue() == 0){
                try {
                    PrintWriter bStateReport = new PrintWriter(new File("bordered_state_report.csv"));
                    bState += bAvg1/bDay + ',' + bAvg2/bDay + ',' + bAvg3/bDay + ',' + bAvg4/bDay;
                    bStateReport.write(bState);
                    bStateReport.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        lSave.setOnAction(act -> {
            if(this.lRunning.intValue() == 0){
                try {
                    PrintWriter lStateReport = new PrintWriter(new File("borderless_state_report.csv"));
                    lState += lAvg1/lDay + ',' + lAvg2/lDay + ',' + lAvg3/lDay + ',' + lAvg4/lDay;
                    lStateReport.write(lState);
                    lStateReport.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        ListView<String> bScrollableList = new ListView<String>();
        ObservableList<String> bScrollableListInfo = FXCollections.observableArrayList("pause to choose animal from bordered map to track");
        bScrollableList.setItems(bScrollableListInfo);
        ListView<String> lScrollableList = new ListView<String>();
        ObservableList<String> lScrollableListInfo = FXCollections.observableArrayList("pause to choose animal from borderless map to track");
        lScrollableList.setItems(lScrollableListInfo);

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
            for(int i = 0; i<=2*(width+1)*(height+1); i++){
                this.grassImages.add(new GuiElementBox("src/main/resources/grass.png"));
                this.strongAnimalImages.add(new GuiElementBox("src/main/resources/red.png"));
                this.weakAnimalImages.add(new GuiElementBox("src/main/resources/blue.png"));
            }
            this.bRunning.set(1);
            this.lRunning.set(1);
            SimulationEngine bEngine = new SimulationEngine(this.bMap, this.refresh, this.width, this.height, this.days, this.startEnergy, this.moveEnergy, this.plantEnergy, this.spawnGrass, this, bRunning, null);
            Thread bEngineThread = new Thread(bEngine);
            bEngineThread.start();
            SimulationEngine lEngine = new SimulationEngine(this.lMap, this.refresh, this.width, this.height, this.days, this.startEnergy, this.moveEnergy, this.plantEnergy, this.spawnGrass, this, lRunning, null);
            Thread lEngineThread = new Thread(lEngine);
            lEngineThread.start();
        });
        bStop.setOnAction(act -> {
            this.bRunning.set(0);
            bScrollableList.getItems().clear();
            Map<Integer, Animal> temp = new HashMap<>(this.bMap.animalList.size());
            Set<Vector2d> keys = this.bMap.animals.keySet();
            int p = 0;
            for(Vector2d loc : keys){
                for(Animal animal : this.bMap.animals.get(loc).animals){
                    temp.put(p, animal);
                    p++;
                    bScrollableList.getItems().add(loc + ", energy: " + animal.getEnergy() + ", facing: " + animal.getFacing());
                }
            }
            bScrollableList.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    int p = bScrollableList.getSelectionModel().getSelectedIndex();
                    bTracked = temp.get(p);
                    String t = "genotype of tracked animal from bordered map: ";
                    for(Integer gene : bTracked.getGenotype()){
                        t += gene.toString();
                        t += ";";
                    }
                    for(Animal animal : bMap.animalList){
                        animal.isDescendant = false;
                    }
                    bTrackedGenotype.setText(t);
                    bTrackedIsAlive.setText("alive, energy: " + temp.get(p).getEnergy());
                    bChildrenCount.setText("children: 0");
                    bDescendantsCount.setText("descendants: 0");
                }
            });
        });
        bResume.setOnAction(act -> {
            this.bRunning.set(1);
            bScrollableList.getItems().clear();
            bScrollableList.getItems().add("pause to choose animal from bordered map to track");
        });
        lStop.setOnAction(act -> {
            this.lRunning.set(0);
            lScrollableList.getItems().clear();
            Map<Integer, Animal> temp = new HashMap<>(this.lMap.animalList.size());
            Set<Vector2d> keys = this.lMap.animals.keySet();
            int p = 0;
            for(Vector2d loc : keys){
                for(Animal animal : this.lMap.animals.get(loc).animals){
                    temp.put(p, animal);
                    p++;
                    lScrollableList.getItems().add(loc + ", energy: " + animal.getEnergy() + ", facing: " + animal.getFacing());
                }
            }
            lScrollableList.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    int p = lScrollableList.getSelectionModel().getSelectedIndex();
                    lTracked = temp.get(p);
                    String t = "genotype of tracked animal from borderless map: ";
                    for(Integer gene : lTracked.getGenotype()){
                        t += gene.toString();
                        t += ";";
                    }
                    for(Animal animal : lMap.animalList){
                        animal.isDescendant = false;
                    }
                    lTrackedGenotype.setText(t);
                    lTrackedIsAlive.setText("alive, energy: " + temp.get(p).getEnergy());
                    lChildrenCount.setText("children: 0");
                    lDescendantsCount.setText("descendants: 0");
                }
            });
        });
        lResume.setOnAction(act -> {
            this.lRunning.set(1);
            lScrollableList.getItems().clear();
            lScrollableList.getItems().add("pause to choose animal from borderless map to track");
        });
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
                bStop,
                bResume,
                lStop,
                lResume);
        Scene scene = new Scene(new VBox(new HBox(chosenParams, borderedMap, borderlessMap), new HBox(bAGChart, lAGChart, bLDChart, lLDChart), new HBox(new Text("bordered map mode of genotype: "), this.bGenotypeMode, new Text("borderless map mode of genotype: "), this.lGenotypeMode), new HBox(bScrollableList, lScrollableList, new VBox(bTrackedGenotype, bTrackedIsAlive, bChildrenCount, bDescendantsCount, bShowAllModeGenotype, bSave), new VBox(lTrackedGenotype, lTrackedIsAlive, lChildrenCount, lDescendantsCount, lShowAllModeGenotype, lSave)) ), 1600, 900);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void drawBorderedMap(){
        Platform.runLater(() -> {
            int flag = 0;
            if(this.bRunning.intValue() == 1){
                flag = 1;
                this.bRunning.set(0);
            }
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
            if(flag == 1){
                this.bRunning.set(1);
            }
        });
    }
    public void drawBorderlessMap(){
        Platform.runLater(() -> {
            int flag = 0;
            if(this.lRunning.intValue() == 1){
                flag = 1;
                this.lRunning.set(0);
            }
            int g = (this.width+1)*(this.height+1);
            int sa = (this.width+1)*(this.height+1);
            int wa = (this.width+1)*(this.height+1);
            borderlessMap.getChildren().clear();
            Set<Vector2d> keys = lMap.grasses.keySet();
            for (Vector2d location : keys) {
                Label toAdd = new Label();
                toAdd.setGraphic(this.grassImages.get(g).imageView);
                borderlessMap.add(toAdd, location.x, location.y);
                g++;
            }
            keys = lMap.jungle.keySet();
            for (Vector2d location : keys) {
                Label toAdd = new Label();
                toAdd.setGraphic(this.grassImages.get(g).imageView);
                borderlessMap.add(toAdd, location.x, location.y);
                g++;
            }
            keys = lMap.animals.keySet();
            for (Vector2d location : keys) {
                if (lMap.animals.get(location).getMaxEnergy() < 5 * this.startEnergy) {
                    Label toAdd = new Label();
                    toAdd.setGraphic(this.weakAnimalImages.get(wa).imageView);
                    borderlessMap.add(toAdd, location.x, location.y);
                    wa++;
                } else {
                    Label toAdd = new Label();
                    toAdd.setGraphic(this.strongAnimalImages.get(sa).imageView);
                    borderlessMap.add(toAdd, location.x, location.y);
                    sa++;
                }
            }
            if(flag == 1){
                this.lRunning.set(1);
            }
        });
    }
    public void drawBorderedGraph(int day, int sumAliveLifespan, StringBuilder sb, double avg1, double avg2, double avg3, double avg4){
        Platform.runLater(() -> {
            bState = sb.toString();
            this.bAvg1 = avg1;
            this.bAvg2 = avg2;
            this.bAvg3 = avg3;
            this.bAvg4 = avg4;
            this.bDay = day;
            int flag = 0;
            if(this.bRunning.intValue() == 1){
                flag = 1;
                this.bRunning.set(0);
            }
            sb.append(this.bMap.animalsAlive).append(',');
            sb.append(this.bMap.grassesAlive).append(',');
            bASeries.getData().add(new XYChart.Data<>(day, this.bMap.animalsAlive));
            bGSeries.getData().add(new XYChart.Data<>(day, this.bMap.grassesAlive));

            if(this.bMap.deadCount == 0){
                bDSeries.getData().add(new XYChart.Data<>(day, 0));
                sb.append(0).append(',');
            }
            else{
                bDSeries.getData().add(new XYChart.Data<>(day, this.bMap.sumDeadLifespan/this.bMap.deadCount));
                sb.append(this.bMap.sumDeadLifespan / this.bMap.deadCount).append(',');
            }
            if(this.bMap.animalsAlive == 0){
                bAlSeries.getData().add(new XYChart.Data<>(day, 0));
                sb.append(0).append(',').append('\n');
            }
            else{
                bAlSeries.getData().add(new XYChart.Data<>(day, sumAliveLifespan/this.bMap.animalsAlive));
                sb.append(sumAliveLifespan / this.bMap.animalsAlive).append('\n');
            }
            if(day==this.days-1){
                sb.append(avg1/days).append(',').append(avg2/days).append(',').append(avg3/days).append(',').append(avg4/days);
                this.bReport.write(sb.toString());
                this.bReport.close();
            }
            if(flag == 1){
                this.bRunning.set(1);
            }
        });
    }
    public void drawBorderlessGraph(int day, int sumAliveLifespan, StringBuilder sb, double avg1, double avg2, double avg3, double avg4){
        Platform.runLater(() -> {
            lState = sb.toString();
            this.lAvg1 = avg1;
            this.lAvg2 = avg2;
            this.lAvg3 = avg3;
            this.lAvg4 = avg4;
            this.lDay = day;
            int flag = 0;
            if(this.lRunning.intValue() == 1){
                flag = 1;
                this.lRunning.set(0);
            }
            sb.append(this.lMap.animalsAlive).append(',');
            sb.append(this.lMap.grassesAlive).append(',');
            lASeries.getData().add(new XYChart.Data<>(day, this.lMap.animalsAlive));
            lGSeries.getData().add(new XYChart.Data<>(day, this.lMap.grassesAlive));

            if(this.lMap.deadCount == 0){
                lDSeries.getData().add(new XYChart.Data<>(day, 0));
                sb.append(0).append(',');
            }
            else{
                lDSeries.getData().add(new XYChart.Data<>(day, this.lMap.sumDeadLifespan/this.lMap.deadCount));
                sb.append(this.lMap.sumDeadLifespan / this.lMap.deadCount).append(',');
            }
            if(this.lMap.animalsAlive == 0){
                lAlSeries.getData().add(new XYChart.Data<>(day, 0));
                sb.append(0).append(',').append('\n');
            }
            else{
                lAlSeries.getData().add(new XYChart.Data<>(day, sumAliveLifespan/this.lMap.animalsAlive));
                sb.append(String.valueOf(sumAliveLifespan / this.lMap.animalsAlive)).append('\n');
            }
            if(day==this.days-1){
                sb.append(avg1/days).append(',').append(avg2/days).append(',').append(avg3/days).append(',').append(avg4/days);
                this.lReport.write(sb.toString());
                this.lReport.close();
            }
            if(flag == 1){
                this.lRunning.set(1);
            }
        });
    }
    public void bUpdateMode(ArrayList<Integer> mode){
        String res = "";
        if(this.bMap.animalList.size()==0){
            res = "there are no animals on the map ";
        }
        if(mode.get(0) == -2){
            res = "there is no dominant genotype ";
            this.bGenotypeMode.setText(res);
        }
        else{
            for(Integer el : mode){
                res += Integer.toString(el);
                res += ";";
            }
            this.bGenotypeMode.setText(res);
        }
    }
    public void lUpdateMode(ArrayList<Integer> mode){
        String res = "";
        if(this.bMap.animalList.size()==0){
            res = "there are no animals on the map ";
        }
        if(mode.get(0) == -2){
            res = "there is no dominant genotype ";
            this.lGenotypeMode.setText(res);
        }
        else{
            for(Integer el : mode){
                res += Integer.toString(el);
                res += ";";
            }
            this.lGenotypeMode.setText(res);
        }
    }
    public void bUpdateTracked(int children, int descendants, int day){
        if(this.bTracked.getEnergy() - this.moveEnergy > 0){
            this.bTrackedIsAlive.setText("alive, energy: " + this.bTracked.getEnergy());
        }
        else if(bTrackedIsAlive.getText().charAt(0)!='d'){
            bTrackedIsAlive.setText("dead :( " + String.valueOf(day));
        }
        bChildrenCount.setText("children: " + String.valueOf(children));
        bDescendantsCount.setText("descendants: " + String.valueOf(descendants));

    }
    public void lUpdateTracked(int children, int descendants, int day){
        if(this.lTracked.getEnergy() - this.moveEnergy > 0){
            this.lTrackedIsAlive.setText("alive, energy: " + this.lTracked.getEnergy());
        }
        else if(lTrackedIsAlive.getText().charAt(0)!='d'){
            lTrackedIsAlive.setText("dead :( " + String.valueOf(day));
        }
        lChildrenCount.setText("children: " + String.valueOf(children));
        lDescendantsCount.setText("descendants: " + String.valueOf(descendants));
    }
    public int getRandomNumber(int min, int max) {
        return new Random().nextInt(max) + min;
    }
}