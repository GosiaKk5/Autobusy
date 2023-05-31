package org.example;

import jakarta.persistence.*;

@Entity
public class BusStop {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int busStopID;
    private String name;

    public BusStop() {
    }

    public BusStop(String name){
        this.name = name;
    }

    public int getId(){
        return this.busStopID;
    }

    public String getName(){
        return this.name;
    }

}
