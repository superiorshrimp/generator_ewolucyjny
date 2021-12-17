package world;

import java.util.Set;

public class WorldMapBordered extends AbstractWorldMap{
    public WorldMapBordered(int width, int height, double jungleRatio){
        super(width, height, jungleRatio);
    }
    @Override
    public boolean canMoveTo(Vector2d location){
        return location.follows(corners[0]) && location.precedes(corners[1]);
    }
    public void moveForward(Animal animal){
        Vector2d oldPos = animal.getPosition();
        Vector2d newPos = oldPos.add(animal.directionToVector());
        if(this.canMoveTo(newPos)){
            animal.positionChanged(animal, oldPos, newPos);
            animal.setPosition(newPos);
        }
    }
    public void moveBackward(Animal animal){
        Vector2d oldPos = animal.getPosition();
        Vector2d newPos = oldPos.subtract(animal.directionToVector());
        if(this.canMoveTo(newPos)){
            animal.positionChanged(animal, oldPos, newPos);
            animal.setPosition(newPos);
        }
    }
}
