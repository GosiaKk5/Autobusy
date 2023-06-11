package org.example;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class BusStop {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int busStopID;
    private String name;

    @OneToMany
    private List<StopOnLine> linesBelongs;

    public BusStop() {
        this.linesBelongs = new ArrayList<StopOnLine>();
    }

    public BusStop(String name){
        this.name = name;
        this.linesBelongs = new ArrayList<StopOnLine>();
    }

    public List<StopOnLine> getLines(){
        return this.linesBelongs;
    }

    public int getId(){
        return this.busStopID;
    }

    public String getName(){
        return this.name;
    }

}
