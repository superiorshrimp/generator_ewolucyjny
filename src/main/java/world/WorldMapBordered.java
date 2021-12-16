package world;

public class WorldMapBordered extends AbstractWorldMap{
    public WorldMapBordered(int width, int height, double jungleRatio){
        super(width, height, jungleRatio);
    }
    @Override
    public boolean canMoveTo(Vector2d location){
        return location.follows(corners[0]) && location.precedes(corners[1]);
    }
    public void moveForward(Animal animal){
        Vector2d newPos = animal.getPosition().add(animal.directionToVector());
        if(this.canMoveTo(newPos)){
            animal.positionChanged(animal, animal.getPosition(), newPos);
            animal.setPosition(newPos);
        }
    }
    public void moveBackward(Animal animal){
        Vector2d newPos = animal.getPosition().subtract(animal.directionToVector());
        if(this.canMoveTo(newPos)){
            animal.positionChanged(animal, animal.getPosition(), newPos);
            animal.setPosition(newPos);
        }
    }
}
