package world;

import gui.App;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class SimulationEngine implements Runnable{
    public AbstractWorldMap map;
    public int refresh;
    public int width;
    public int height;
    public int days;
    public int startEnergy;
    public int moveEnergy;
    public int plantEnergy;
    public int spawnGrass;
    public App application;
    public SimulationEngine(AbstractWorldMap map, int refresh, int width, int height, int days, int startEnergy, int moveEnergy, int plantEnergy, int spawnGrass, App application){
        this.map = map;
        this.refresh = refresh;
        this.width = width;
        this.height = height;
        this.days = days;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;
        this.spawnGrass = spawnGrass;
        this.map.grassesAlive = spawnGrass;
        this.application = application;
    }
    public void run(){
        for(int day = 0; day < this.days; day++){
            removeDeadAnimals();
            moveAnimals();
            eat();
            spawnNewAnimals();
            plantGrass();
            dayPassed();
            try {
                Thread.sleep(this.refresh);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void removeDeadAnimals(){
        ArrayList<Animal> toRemove = new ArrayList<>();
        for (Animal animal : this.map.animalList) {
            if (animal.getEnergy() - this.moveEnergy < 0) {
                toRemove.add(animal);
            }
        }
        this.map.animalsAlive -= toRemove.size();
        Iterator<Animal> rmv = toRemove.iterator();
        while (rmv.hasNext()) {
            this.map.removeAnimal(rmv.next());
        }
    }
    public void moveAnimals(){
        for(Animal animal : this.map.animalList){
            int randRotation = animal.getGenotype().get(getRandomNumber(0,32));
            if(randRotation == 0){
                this.map.moveForward(animal);
            }
            else if(randRotation == 4){
                this.map.moveBackward(animal);
            }
            else{
                animal.rotate(randRotation);
            }
        }
    }
    public void eat(){
        Set<Vector2d> keys = this.map.animals.keySet();
        ArrayList<Vector2d> toRemove = new ArrayList<>();
        for(Vector2d location : keys) {
            if (!this.map.isJungle(location)) {
                if (this.map.grasses.containsKey(location)) {
                    ArrayList<Animal> toFeed = this.map.animals.get(location).getAllStrongest();
                    int count = toFeed.size();
                    for (Animal animal : toFeed) {
                        animal.increaseEnergy(this.map.grasses.get(location).getPlantEnergy() / count);
                    }
                    toRemove.add(location);
                }
            } else {
                if (this.map.jungle.containsKey(location)) {
                    ArrayList<Animal> toFeed = this.map.animals.get(location).getAllStrongest();
                    int count = toFeed.size();
                    for (Animal animal : toFeed) {
                        animal.increaseEnergy(this.map.jungle.get(location).getPlantEnergy() / count);
                    }
                    toRemove.add(location);
                }
            }
        }
        this.map.grassesAlive -= toRemove.size();
        Iterator<Vector2d> rmv = toRemove.iterator();
        while(rmv.hasNext()){
            Vector2d location = rmv.next();
            if(!this.map.isJungle(location)){
                this.map.grasses.remove(location);
            }
            else{
                this.map.jungle.remove(location);
            }
        }
    }
    public void spawnNewAnimals(){
        Set<Vector2d> keys = this.map.animals.keySet();
        for(Vector2d location : keys){
            if(this.map.animals.get(location).animals.size()>=2){
                ArrayList<Animal> parents = this.map.animals.get(location).get2Strongest();
                Animal a0 = parents.get(0);
                Animal a1 = parents.get(1);
                if(a0.getEnergy() >= this.startEnergy/2 && a1.getEnergy() >= this.startEnergy/2){
                    Animal baby = new Animal(location, (a0.getEnergy() + a1.getEnergy())/4, a0.getGenotype(), a1.getGenotype(), a0.getEnergy(), a1.getEnergy());
                    a0.decreaseEnergy(a0.getEnergy()/4); //not too clean, but works
                    a1.decreaseEnergy(a1.getEnergy()/4);
                    this.map.addAnimal(baby);
                    this.map.animalsAlive += 1;
                    this.map.animals.get(location).updateStrongest();
                    baby.addObserver(this.map);
                }
            }
        }
    }
    public void plantGrass(){ //to check if works properly
        for(int i = 0; i<this.spawnGrass/2; i++){
            Vector2d toTry = new Vector2d(getRandomNumber(0, this.width)+1, getRandomNumber(0, this.height)+1);
            int tries = this.width*this.height;
            while((this.map.isJungle(toTry) || this.map.isOccupied(toTry)) && (tries > 0)){
                tries--;
                toTry = new Vector2d(getRandomNumber(0, this.width+1), getRandomNumber(0, this.height+1));
            }
            if(tries == 0){
                outerloop:
                for(int r = 0; r<=this.height+1; r++){
                    for(int c = 0; c<=this.width+1; c++){
                        toTry = new Vector2d(c,r);
                        if(!(this.map.isOccupied(toTry) && this.map.isJungle(toTry))){
                            this.map.addGrass(toTry, this.plantEnergy);
                            break outerloop;
                        }
                    }
                }
            }
            else{
                this.map.addGrass(toTry, this.plantEnergy);
            }
        }
        for(int i = 0; i<this.spawnGrass/2; i++){
            Vector2d toTry = new Vector2d(getRandomNumber(this.map.jungleCorners[0].x, this.map.jungleCorners[1].x+1), getRandomNumber(this.map.jungleCorners[0].y, this.map.jungleCorners[1].y+1));
            int tries = (this.map.jungleCorners[1].x - this.map.jungleCorners[0].x)*(this.map.jungleCorners[1].y - this.map.jungleCorners[0].y);
            while((!this.map.isJungle(toTry) || this.map.isOccupied(toTry)) && (tries > 0)){
                tries--;
                toTry = new Vector2d(getRandomNumber(this.map.jungleCorners[0].x, this.map.jungleCorners[1].x), getRandomNumber(this.map.jungleCorners[0].y, this.map.jungleCorners[1].y));
            }
            if(tries == 0){
                outerloop:
                for(int r = this.map.jungleCorners[0].y; r<=this.map.jungleCorners[1].y; r++){
                    for(int c = this.map.jungleCorners[0].x; c<=this.map.jungleCorners[1].x; c++){
                        toTry = new Vector2d(c, r);
                        if(!this.map.isOccupied(toTry)){
                            this.map.addJungle(toTry, this.plantEnergy);
                            break outerloop;
                        }
                    }
                }
            }
            else{
                this.map.addJungle(toTry, this.plantEnergy);
            }
        }
    }
    public void dayPassed(){
        this.application.drawMap();
    }
    public int getRandomNumber(int min, int max) {
        return new Random().nextInt(max) + min;
    }
}