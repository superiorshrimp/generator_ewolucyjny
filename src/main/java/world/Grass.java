package world;

public class Grass{
    private final int plantEnergy;
    private Vector2d position;
    public Grass(Vector2d location, int plantEnergy){
        this.position = location;
        this.plantEnergy = plantEnergy;
    }
    public int getPlantEnergy(){
        return this.plantEnergy;
    }
    public Vector2d getPosition(){
        return this.position;
    }
}
