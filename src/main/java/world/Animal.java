package world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Animal implements IPositionChangeObserver{
    public boolean isDescendant;
    private int energy;
    private final ArrayList<Integer> genotype;
    private int facing;
    private Vector2d position;
    public ArrayList<IPositionChangeObserver> observers;
    public int dayOfBirth;
    public int sumOfGenotype;
    public ArrayList <Integer> mapOfGenotype;
    public Animal(Vector2d location, int energy){
        this.position = location;
        this.observers = new ArrayList<>();
        this.energy = energy;
        this.dayOfBirth = 0;
        this.mapOfGenotype = new ArrayList<>(8);
        for(int i = 0; i<8; i++){
            this.mapOfGenotype.add(-1);
        }
        this.genotype = createRandomGenotype();
        this.facing = getRandomNumber(0,8);
        this.dayOfBirth = 0;
        this.isDescendant = false;
    }
    public Animal(Vector2d location, int energy, ArrayList<Integer> genotype1, ArrayList<Integer> genotype2, int energy1, int energy2, int day, boolean desc){
        this.position = location;
        this.observers = new ArrayList<>();
        this.energy = energy;
        this.dayOfBirth = 0;
        this.mapOfGenotype = new ArrayList<>(8);
        for(int i = 0; i<8; i++){
            this.mapOfGenotype.add(-1);
        }
        this.genotype = createGenotype(genotype1, genotype2, energy1, energy2);
        this.facing = getRandomNumber(0,8);
        this.dayOfBirth = day;
        this.isDescendant = desc;
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
            case 0: res = new Vector2d(0,1); break;//N
            case 1: res = new Vector2d(1,1); break;//NE
            case 2: res = new Vector2d(1,0); break;//E
            case 3: res = new Vector2d(1,-1); break;//SE
            case 4: res = new Vector2d(0,-1); break;//S
            case 5: res = new Vector2d(-1,-1); break;//SW
            case 6: res = new Vector2d(-1,0); break;//W
            default: res = new Vector2d(-1,1); break;//NW
        };
        return res;
    }
    public int getFacing(){
        return this.facing;
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
                    this.sumOfGenotype += genotype1.get(i);
                }
                for(int i = div; i<32; i++){
                    ret.add(genotype2.get(i));
                    this.sumOfGenotype += genotype2.get(i);
                }
            }
            else{
                div = 32-(int) (32 * ((double) energy1/(double) sum));
                for(int i = 0; i<div; i++){
                    ret.add(genotype2.get(i));
                    this.sumOfGenotype += genotype2.get(i);
                }
                for(int i = div; i<32; i++){
                    ret.add(genotype1.get(i));
                    this.sumOfGenotype += genotype1.get(i);
                }
            }
        }
        else{
            if(side == 0){
                div = (int) (32 * ((double) energy2/(double) sum));
                for(int i = 0; i<div; i++){
                    ret.add(genotype2.get(i));
                    this.sumOfGenotype += genotype2.get(i);
                }
                for(int i = div; i<32; i++){
                    ret.add(genotype1.get(i));
                    this.sumOfGenotype += genotype1.get(i);
                }
            }
            else{
                div = 32-(int) (32 * ((double) energy2/(double) sum));
                for(int i = 0; i<div; i++){
                    ret.add(genotype1.get(i));
                    this.sumOfGenotype += genotype1.get(i);
                }
                for(int i = div; i<32; i++){
                    ret.add(genotype2.get(i));
                    this.sumOfGenotype += genotype2.get(i);
                }
            }
        }
        Collections.sort(ret);
        for(int gene = 0; gene<32; gene++){
            if(this.mapOfGenotype.get(ret.get(gene)) == -1){
                this.mapOfGenotype.add(ret.get(gene), gene);
            }
        }
        return ret;
    }
    public ArrayList<Integer> createRandomGenotype(){
        ArrayList<Integer> ret = new ArrayList<>(32);
        for(int i = 0; i<32; i++){
            int rand = getRandomNumber(0,8);
            ret.add(rand);
            this.sumOfGenotype += rand;
        }
        Collections.sort(ret);
        for(int gene = 0; gene<32; gene++){
            if(this.mapOfGenotype.get(ret.get(gene)) == -1){
                this.mapOfGenotype.add(ret.get(gene), gene);
            }
        }
        return ret;
    }
    public void decreaseEnergy(int moveEnergy){
        this.energy -= moveEnergy;
    }
    public ArrayList<Integer> getGenotype(){
        return this.genotype;
    }
    public int getRandomNumber(int min, int max) {
        return new Random().nextInt(max) + min;
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
    public int getSumOfGenotype(){
        return this.sumOfGenotype;
    }
    public ArrayList <Integer> getMapOfGenotype(){
        return this.mapOfGenotype;
    }
}