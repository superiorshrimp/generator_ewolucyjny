package world;

public class WorldMapBorderless extends AbstractWorldMap{
    public WorldMapBorderless(int width, int height, double jungleRatio){
        super(width, height, jungleRatio);
    }
    public void moveForward(Animal animal){
        Vector2d newPos;
        if(animal.getPosition().x+animal.directionToVector().x >= 0){
            if(animal.getPosition().y+animal.directionToVector().y >= 0){
                newPos = new Vector2d((animal.getPosition().x+animal.directionToVector().x)%(this.width+1), (animal.getPosition().y+animal.directionToVector().y)%(this.height+1));
            }
            else{
                newPos = new Vector2d((animal.getPosition().x+animal.directionToVector().x)%(this.width+1), (-animal.getPosition().y-animal.directionToVector().y)%(this.height+1));
            }
        }
        else{
            if(animal.getPosition().y+animal.directionToVector().y >= 0){
                newPos = new Vector2d((-animal.getPosition().x-animal.directionToVector().x)%(this.width+1), (animal.getPosition().y+animal.directionToVector().y)%(this.height+1));
            }
            else{
                newPos = new Vector2d((-animal.getPosition().x-animal.directionToVector().x)%(this.width+1), (-animal.getPosition().y-animal.directionToVector().y)%(this.height+1));
            }
        }
        this.positionChanged(animal, animal.getPosition(), newPos);
        animal.setPosition(newPos);
    }
    public void moveBackward(Animal animal){
        Vector2d newPos;
        if(animal.getPosition().x-animal.directionToVector().x >= 0){
            if(animal.getPosition().y-animal.directionToVector().y >= 0){
                newPos = new Vector2d((animal.getPosition().x-animal.directionToVector().x)%(this.width+1), (animal.getPosition().y-animal.directionToVector().y)%(this.height+1));
            }
            else{
                newPos = new Vector2d((animal.getPosition().x-animal.directionToVector().x)%(this.width+1), (-animal.getPosition().y+animal.directionToVector().y)%(this.height+1));
            }
        }
        else{
            if(animal.getPosition().y-animal.directionToVector().y >= 0){
                newPos = new Vector2d((-animal.getPosition().x+animal.directionToVector().x)%(this.width+1), (animal.getPosition().y-animal.directionToVector().y)%(this.height+1));
            }
            else{
                newPos = new Vector2d((-animal.getPosition().x+animal.directionToVector().x)%(this.width+1), (-animal.getPosition().y+animal.directionToVector().y)%(this.height+1));
            }
        }
        this.positionChanged(animal, animal.getPosition(), newPos);
        animal.setPosition(newPos);
    }
}
