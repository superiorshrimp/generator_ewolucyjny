package world;

import gui.App;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class SimulationEngine implements Runnable{
    public AbstractWorldMap bMap;
    public AbstractWorldMap lMap;
    public int refresh;
    public int width;
    public int height;
    public int days;
    public int startEnergy;
    public int moveEnergy;
    public int plantEnergy;
    public int spawnGrass;
    public App application;
    public int sumAliveLifespan;
    public SimulationEngine(AbstractWorldMap bMap, AbstractWorldMap lMap, int refresh, int width, int height, int days, int startEnergy, int moveEnergy, int plantEnergy, int spawnGrass, App application){
        this.bMap = bMap;
        this.lMap = lMap;
        this.refresh = refresh;
        this.width = width;
        this.height = height;
        this.days = days;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;
        this.spawnGrass = spawnGrass;
        this.bMap.grassesAlive = spawnGrass;
        this.application = application;
        this.sumAliveLifespan = 0;
    }
    public void run(){
        for(int day = 0; day < this.days; day++){
            while(this.application.running == 0){
                try {
                    Thread.sleep(this.refresh);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.sumAliveLifespan = 0;
            removeDeadAnimals(bMap, day);
            removeDeadAnimals(lMap, day);
            moveAnimals(bMap);
            moveAnimals(lMap);
            eat(bMap);
            eat(lMap);
            spawnNewAnimals(bMap, day);
            spawnNewAnimals(lMap, day);
            plantGrass(bMap);
            plantGrass(lMap);
            dayPassed(day);
            try {
                Thread.sleep(this.refresh);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void removeDeadAnimals(AbstractWorldMap map, int day){
        ArrayList<Animal> toRemove = new ArrayList<>();
        for (Animal animal : map.animalList) {
            if (animal.getEnergy() - this.moveEnergy < 0){
                toRemove.add(animal);
            }
            else{
                this.sumAliveLifespan += day - animal.dayOfBirth;
                animal.decreaseEnergy(this.moveEnergy);
            }
        }
        map.animalsAlive -= toRemove.size();
        Iterator<Animal> rmv = toRemove.iterator();
        while(rmv.hasNext()){
            Animal tormv = rmv.next();
            map.sumDeadLifespan += day - tormv.dayOfBirth;
            map.deadCount += 1;
            map.removeAnimal(tormv);
        }
    }
    public void moveAnimals(AbstractWorldMap map){
        for(Animal animal : map.animalList){
            int randRotation = animal.getGenotype().get(getRandomNumber(0,32));
            if(randRotation == 0){
                map.moveForward(animal);
            }
            else if(randRotation == 4){
                map.moveBackward(animal);
            }
            else{
                animal.rotate(randRotation);
            }
        }
    }
    public void eat(AbstractWorldMap map){
        Set<Vector2d> keys = map.animals.keySet();
        ArrayList<Vector2d> toRemove = new ArrayList<>();
        for(Vector2d location : keys) {
            if (!map.isJungle(location)) {
                if (map.grasses.containsKey(location)) {
                    ArrayList<Animal> toFeed = map.animals.get(location).getAllStrongest();
                    int count = toFeed.size();
                    for (Animal animal : toFeed) {
                        animal.increaseEnergy(map.grasses.get(location).getPlantEnergy() / count);
                    }
                    toRemove.add(location);
                }
            } else {
                if (map.jungle.containsKey(location)) {
                    ArrayList<Animal> toFeed = map.animals.get(location).getAllStrongest();
                    int count = toFeed.size();
                    for (Animal animal : toFeed) {
                        animal.increaseEnergy(map.jungle.get(location).getPlantEnergy() / count);
                    }
                    toRemove.add(location);
                }
            }
        }
        map.grassesAlive -= toRemove.size();
        Iterator<Vector2d> rmv = toRemove.iterator();
        while(rmv.hasNext()){
            Vector2d location = rmv.next();
            if(!map.isJungle(location)){
                map.grasses.remove(location);
            }
            else{
                map.jungle.remove(location);
            }
        }
    }
    public void spawnNewAnimals(AbstractWorldMap map, int day){
        Set<Vector2d> keys = map.animals.keySet();
        for(Vector2d location : keys){
            if(map.animals.get(location).animals.size()>=2){
                ArrayList<Animal> parents = map.animals.get(location).get2Strongest();
                Animal a0 = parents.get(0);
                Animal a1 = parents.get(1);
                if(a0.getEnergy() >= this.startEnergy/2 && a1.getEnergy() >= this.startEnergy/2){
                    Animal baby = new Animal(location, (a0.getEnergy() + a1.getEnergy())/4, a0.getGenotype(), a1.getGenotype(), a0.getEnergy(), a1.getEnergy(), day);
                    a0.decreaseEnergy(a0.getEnergy()/4); //not too clean, but works
                    a1.decreaseEnergy(a1.getEnergy()/4);
                    map.addAnimal(baby);
                    map.animalsAlive += 1;
                    map.animals.get(location).updateStrongest();
                    baby.addObserver(map);
                }
            }
        }
    }
    public void plantGrass(AbstractWorldMap map){ //to check if works properly
        for(int i = 0; i<this.spawnGrass/2; i++){
            Vector2d toTry = new Vector2d(getRandomNumber(0, this.width)+1, getRandomNumber(0, this.height)+1);
            int tries = this.width*this.height;
            while((map.isJungle(toTry) || map.isOccupied(toTry)) && (tries > 0)){
                tries--;
                toTry = new Vector2d(getRandomNumber(0, this.width+1), getRandomNumber(0, this.height+1));
            }
            if(tries == 0){
                outerloop:
                for(int r = 0; r<=this.height+1; r++){
                    for(int c = 0; c<=this.width+1; c++){
                        toTry = new Vector2d(c,r);
                        if(!(map.isOccupied(toTry) && map.isJungle(toTry))){
                            map.addGrass(toTry, this.plantEnergy);
                            map.grassesAlive++;
                            break outerloop;
                        }
                    }
                }
            }
            else{
                map.addGrass(toTry, this.plantEnergy);
                map.grassesAlive++;
            }
        }
        for(int i = 0; i<this.spawnGrass/2; i++){
            Vector2d toTry = new Vector2d(getRandomNumber(map.jungleCorners[0].x, map.jungleCorners[1].x+1), getRandomNumber(map.jungleCorners[0].y, map.jungleCorners[1].y+1));
            int tries = (map.jungleCorners[1].x - map.jungleCorners[0].x)*(map.jungleCorners[1].y - map.jungleCorners[0].y);
            while((!map.isJungle(toTry) || map.isOccupied(toTry)) && (tries > 0)){
                tries--;
                toTry = new Vector2d(getRandomNumber(map.jungleCorners[0].x, map.jungleCorners[1].x), getRandomNumber(map.jungleCorners[0].y, map.jungleCorners[1].y));
            }
            if(tries == 0){
                outerloop:
                for(int r = map.jungleCorners[0].y; r<=map.jungleCorners[1].y; r++){
                    for(int c = map.jungleCorners[0].x; c<=map.jungleCorners[1].x; c++){
                        toTry = new Vector2d(c, r);
                        if(!map.isOccupied(toTry)){
                            map.addJungle(toTry, this.plantEnergy);
                            map.grassesAlive++;
                            break outerloop;
                        }
                    }
                }
            }
            else{
                map.addJungle(toTry, this.plantEnergy);
                map.grassesAlive++;
            }
        }
    }
    public void dayPassed(int day){
        this.application.drawMap();
        this.application.drawGraph(day, this.sumAliveLifespan);
    }
    public int getRandomNumber(int min, int max) {
        return new Random().nextInt(max) + min;
    }
}