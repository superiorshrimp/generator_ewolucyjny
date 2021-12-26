package world;

import gui.App;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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
    public int sumAliveLifespan;
    public AtomicInteger running;
    public StringBuilder sb;
    public double avg1 = 0;
    public double avg2 = 0;
    public double avg3 = 0;
    public double avg4 = 0;
    public double avg5 = 0;
    public int children = 0;
    public int descendants = 0;
    public Animal lastTracked = null;
    public SimulationEngine(AbstractWorldMap map, int refresh, int width, int height, int days, int startEnergy, int moveEnergy, int plantEnergy, int spawnGrass, App application, AtomicInteger running, Animal lastTracked){
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
        this.sumAliveLifespan = 0;
        this.running = running;
        this.sb = new StringBuilder();
        sb.append("animals alive,grasses alive,average lifespan of dead animals,average lifespan of alive animals,average count of children for living animals\n");
    }
    public void run(){
        for(int day = 0; day < this.days; day++){
            while(running.intValue() == 0){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(this.lastTracked != this.application.bTracked && this.lastTracked != this.application.lTracked){
                this.children = 0;
                this.descendants = 0;
                if(this.map instanceof WorldMapBordered) {
                    this.lastTracked = this.application.bTracked;
                }
                else{
                    this.lastTracked = this.application.lTracked;
                }
            }
            this.sumAliveLifespan = 0;
            removeDeadAnimals(map, day);
            moveAnimals(map);
            eat(map);
            spawnNewAnimals(map, day);
            plantGrass(map);
            if(this.map instanceof WorldMapBordered){
                bDayPassed(day, this.map.avgChildren());
            }
            else{
                lDayPassed(day, this.map.avgChildren());
            }
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
                animal.decreaseEnergy(this.moveEnergy);
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
                    Animal baby;
                    if(a0 == this.application.bTracked || a1 == this.application.bTracked || a0 == this.application.lTracked || a1 == this.application.lTracked){
                        baby = new Animal(location, (a0.getEnergy() + a1.getEnergy())/4, a0.getGenotype(), a1.getGenotype(), a0.getEnergy(), a1.getEnergy(), day, true, a0, a1);
                        this.children++;
                        this.descendants++;
                    }
                    else if(a0.isDescendant || a1.isDescendant){
                        baby = new Animal(location, (a0.getEnergy() + a1.getEnergy())/4, a0.getGenotype(), a1.getGenotype(), a0.getEnergy(), a1.getEnergy(), day, true, a0, a1);
                        this.descendants++;
                    }
                    else{
                        baby = new Animal(location, (a0.getEnergy() + a1.getEnergy())/4, a0.getGenotype(), a1.getGenotype(), a0.getEnergy(), a1.getEnergy(), day, false, a0, a1);
                    }
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
                for(int r = 0; r<=this.height; r++){
                    for(int c = 0; c<=this.width; c++){
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
    public void bDayPassed(int day, double val){
        if(this.application.bTracked!=null){
            this.application.bUpdateTracked(this.children, this.descendants, day);
        }
        this.application.bUpdateMode(this.map.modeOfGenotypes());
        this.application.drawBorderedMap();
        this.avg1 += this.map.animalsAlive;
        this.avg2 += this.map.grassesAlive;
        if(this.map.deadCount != 0){
            this.avg3 += this.map.sumDeadLifespan/this.map.deadCount;
        }
        if(this.map.animalsAlive != 0) {
            this.avg4 += sumAliveLifespan/this.map.animalsAlive;
        }
        this.avg5 += val;
        this.application.drawBorderedGraph(day, this.sumAliveLifespan, this.sb, this.avg1, this.avg2, this.avg3, this.avg4, this.avg5, val);
    }
    public void lDayPassed(int day, double val){
        if(this.application.lTracked!=null){
            this.application.lUpdateTracked(this.children, this.descendants, day);
        }
        this.application.lUpdateMode(this.map.modeOfGenotypes());
        this.application.drawBorderlessMap();
        this.avg1 += this.map.animalsAlive;
        this.avg2 += this.map.grassesAlive;
        if(this.map.deadCount != 0){
            this.avg3 += this.map.sumDeadLifespan/this.map.deadCount;
        }
        if(this.map.animalsAlive != 0) {
            this.avg4 += sumAliveLifespan/this.map.animalsAlive;
        }
        this.avg5 += val;
        this.application.drawBorderlessGraph(day, this.sumAliveLifespan, this.sb, this.avg1, this.avg2, this.avg3, this.avg4, this.avg5, val);
    }
    public int getRandomNumber(int min, int max) {
        return new Random().nextInt(max) + min;
    }
}