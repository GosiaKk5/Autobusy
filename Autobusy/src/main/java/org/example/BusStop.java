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
    private Set<StopOnLine> linesBelongs;

    public BusStop() {
        this.linesBelongs = new HashSet<StopOnLine>();
    }

    public BusStop(String name){
        this.name = name;
        this.linesBelongs = new HashSet<StopOnLine>();
    }

    public Set<StopOnLine> getLines(){
        return this.linesBelongs;
    }

    public int getId(){
        return this.busStopID;
    }

    public String getName(){
        return this.name;
    }

}
