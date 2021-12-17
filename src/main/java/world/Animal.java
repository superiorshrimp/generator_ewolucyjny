package world;

import java.util.ArrayList;
import java.util.Collections;

public class Animal implements IPositionChangeObserver{
    private int energy;
    private final ArrayList<Integer> genotype;
    private int facing;
    private Vector2d position;
    public ArrayList<IPositionChangeObserver> observers;
    public Animal(Vector2d location, int energy){
        this.position = location;
        this.observers = new ArrayList<>();
        this.energy = energy;
        this.genotype = createRandomGenotype();
        this.facing = getRandomNumber(0,8);
    }
    public Animal(Vector2d location, int energy, ArrayList<Integer> genotype1, ArrayList<Integer> genotype2, int energy1, int energy2){
        this.position = location;
        this.observers = new ArrayList<>();
        this.energy = energy;
        this.genotype = createGenotype(genotype1, genotype2, energy1, energy2);
        this.facing = getRandomNumber(0,8);
    }
    public void increaseEnergy(int plantEnergy){
        this.energy += plantEnergy;
    }
    public int getEnergy(){
        return this.energy;
    }
    public void rotate(int val){
        this.facing = (this.facing + val)%8;
    }
    public Vector2d directionToVector(){
        Vector2d res;
        switch(this.facing){
            case 0: res = new Vector2d(0,1); //N
            case 1: res = new Vector2d(1,1); //NE
            case 2: res = new Vector2d(1,0); //E
            case 3: res = new Vector2d(1,-1); //SE
            case 4: res = new Vector2d(0,-1); //S
            case 5: res = new Vector2d(-1,-1); //SW
            case 6: res = new Vector2d(-1,0); //W
            default: res = new Vector2d(-1,1); //NW
        };
        return res;
    }
    public Vector2d getFacing(){
        return this.directionToVector();
    } //czy potrzebne?
    public ArrayList<Integer> createGenotype(ArrayList<Integer> genotype1, ArrayList<Integer> genotype2, int energy1, int energy2){
        ArrayList<Integer> ret = new ArrayList<>(32);
        int side = getRandomNumber(0,2);
        int sum = energy1 + energy2;
        int div;
        if(energy1>energy2){
            if(side == 0){
                div = (int) (32 * ((double) energy1/(double) sum));
                for(int i = 0; i<div; i++){
                    ret.add(genotype1.get(i));
                }
                for(int i = div; i<32; i++){
                    ret.add(genotype2.get(i));
                }
            }
            else{
                div = 32-(int) (32 * ((double) energy1/(double) sum));
                for(int i = 0; i<div; i++){
                    ret.add(genotype2.get(i));
                }
                for(int i = div; i<32; i++){
                    ret.add(genotype1.get(i));
                }
            }
        }
        else{
            if(side == 0){
                div = (int) (32 * ((double) energy2/(double) sum));
                for(int i = 0; i<div; i++){
                    ret.add(genotype2.get(i));
                }
                for(int i = div; i<32; i++){
                    ret.add(genotype1.get(i));
                }
            }
            else{
                div = 32-(int) (32 * ((double) energy2/(double) sum));
                for(int i = 0; i<div; i++){
                    ret.add(genotype1.get(i));
                }
                for(int i = div; i<32; i++){
                    ret.add(genotype2.get(i));
                }
            }
        }
        Collections.sort(ret);
        return ret;
    }
    public ArrayList<Integer> createRandomGenotype(){
        ArrayList<Integer> ret = new ArrayList<>(32);
        for(int i = 0; i<32; i++){
            ret.add(getRandomNumber(0,8));
        }
        Collections.sort(ret);
        return ret;
    }
    public void decreaseEnergy(int moveEnergy){
        this.energy -= moveEnergy;
    }
    public ArrayList<Integer> getGenotype(){
        return this.genotype;
    }
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    public Vector2d getPosition(){
        return this.position;
    }
    public void addObserver(IPositionChangeObserver observer){
        this.observers.add(observer);
    }
    public void positionChanged(Animal animal, Vector2d oldPosition, Vector2d newPosition){
        for(IPositionChangeObserver observer : observers){
            observer.positionChanged(this, oldPosition, newPosition);
        }
    }
    public void setPosition(Vector2d location){
        this.position = location;
    }
}