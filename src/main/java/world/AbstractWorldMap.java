package world;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class AbstractWorldMap implements IPositionChangeObserver{
    public final int width;
    public final int height;
    public LinkedList<Animal> animalList;
    public Map<Vector2d, Grass> grasses;
    public Map<Vector2d, Grass> jungle;
    public Map<Vector2d, AnimalsAt> animals;
    public Vector2d[] jungleCorners;
    public Vector2d[] corners;
    public int animalsAlive;
    public int grassesAlive;
    public int sumDeadLifespan;
    public int deadCount;
    public AbstractWorldMap(int width, int height, double jungleRatio){
        this.width = width;
        this.height = height;
        int jw = (int) (jungleRatio * width / 2);
        int jh = (int) (jungleRatio * height / 2);
        if(jw == 0){
            jw = 1;
        }
        if(jh == 0){
            jh = 1;
        }
        this.jungleCorners = new Vector2d[]{new Vector2d(width / 2 - jw, height / 2 - jh), new Vector2d(width / 2 + jw, height / 2 + jh)};
        this.corners = new Vector2d[]{new Vector2d(0,0), new Vector2d(width, height)};
        this.animalList = new LinkedList<>();
        this.grasses = new LinkedHashMap<>();
        this.animals = new LinkedHashMap<>();
        this.jungle = new LinkedHashMap<>();
        this.animalsAlive = 0;
        this.grassesAlive = 0;
        this.sumDeadLifespan = 0;
        this.deadCount = 0;
    }
    public void addAnimal(Animal toAdd){
        this.animalList.add(toAdd);
        if(animals.containsKey(toAdd.getPosition())){
            animals.get(toAdd.getPosition()).addAnimal(toAdd);
        }
        else{
            this.animals.put(toAdd.getPosition(), new AnimalsAt(toAdd));
        }
    }
    public void removeAnimal(Animal animal){
        this.animals.get(animal.getPosition()).removeAnimal(animal);
        if(this.animals.get(animal.getPosition()).animals.size() == 0){ //cos nie gra
            this.animals.remove(animal.getPosition());
        }
        this.animalList.remove(animal);

    }
    public void addGrass(Vector2d location, int plantEnergy){
        this.grasses.put(location, new Grass(location, plantEnergy));
    }
    public void removeGrass(Grass grass){
        this.grasses.remove(grass.getPosition());
    }
    public void addJungle(Vector2d location, int plantEnergy){
        this.jungle.put(location, new Grass(location, plantEnergy));
    }
    public void removeJungle(Grass grass){
        this.jungle.remove(grass.getPosition());
    }
    public boolean isOccupied(Vector2d location){
        return animals.containsKey(location) || grasses.containsKey(location) || jungle.containsKey(location);
    }
    public Object objectAt(Vector2d location){
        if(animals.containsKey(location)){
            return animals.get(location);
        }
        else if(grasses.containsKey(location)){
            return grasses.get(location);
        }
        else if(jungle.containsKey(location)){
            return jungle.get(location);
        }
        return null;
    }
    public void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition){
        if(this.animals.get(oldPosition).removeAnimal(animal)){
            this.animals.remove(oldPosition);
        }
        if(animals.containsKey(newPosition)){
            animals.get(newPosition).addAnimal(animal);
        }
        else{
            this.animals.put(newPosition, new AnimalsAt(animal));
        }
    }
    public boolean isJungle(Vector2d location){
        return location.follows(jungleCorners[0]) && location.precedes(jungleCorners[1]);
    }
    public boolean canMoveTo(Vector2d location){
        return true;
    }
    public void moveForward(Animal animal){
        //must be overwritten
    }
    public void moveBackward(Animal animal){
        //must be overwritten
    }
}