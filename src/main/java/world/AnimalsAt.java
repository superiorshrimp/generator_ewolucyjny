package world;

import java.util.ArrayList;

public class AnimalsAt{
    public ArrayList<Animal> animals;
    public Animal strongest;
    public AnimalsAt(Animal animal){
        this.animals = new ArrayList<>();
        this.animals.add(animal);
        this.strongest = animal;
    }
    public int getMaxEnergy(){
        return this.strongest.getEnergy();
    }
    public void addAnimal(Animal animal){
        this.animals.add(animal);
        if(animal.getEnergy() > this.strongest.getEnergy()){
            this.strongest = animal;
        }
    }
    public boolean removeAnimal(Animal animal){
        if(animal == this.strongest){
            if(this.animals.size() == 1){
                this.animals.remove(animal);
                return true;
            }
            else{
                this.strongest = get2Strongest().get(1);
                this.animals.remove(animal);
                return false;
            }
        }
        else{
            this.animals.remove(animal);
            return this.animals.size() == 0;
        }
    }
    public ArrayList<Animal> get2Strongest(){
        ArrayList<Animal> ret = new ArrayList<>(2);
        ret.add(this.strongest);
        int lookingFor = 0;
        Animal rem = this.strongest; //must be filled
        for(Animal animal : animals){
            if(animal != this.strongest){
                if(animal.getEnergy() == getMaxEnergy()){
                    ret.add(animal);
                    return ret;
                }
                else if(animal.getEnergy() > lookingFor){
                    rem = animal;
                    lookingFor = animal.getEnergy();
                }
            }
        }
        ret.add(rem);
        return ret;
    }
    public ArrayList<Animal> getAllStrongest(){
        ArrayList<Animal> ret = new ArrayList<>();
        ret.add(this.strongest);
        for(Animal animal : animals){
            if(animal.getEnergy() == getMaxEnergy() && animal != this.strongest){
                ret.add(animal);
            }
        }
        return ret;
    }
    public void updateStrongest(){
        int max = this.strongest.getEnergy();
        for(Animal animal : this.animals){
            if(animal.getEnergy()>max){
                this.strongest = animal;
                max = animal.getEnergy();
            }
        }
    }
}