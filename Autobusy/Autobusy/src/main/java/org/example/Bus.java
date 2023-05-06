package org.example;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int busID;
    public int busCapacity;
    public Line line;

    public Bus() {
    }

    public Bus(int busCapacity){
        this.busCapacity = busCapacity;
    }


}
