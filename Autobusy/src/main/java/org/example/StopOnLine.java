package org.example;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class StopOnLine {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int stopID;

    private int order;

    private LocalTime deltaTime;

    private int distance;

    @ManyToOne
    private BusStop busStop;

    @ManyToOne
    private Line line;


    public StopOnLine() {
    }

    public StopOnLine(Line line, BusStop busStop, int order, LocalTime deltaTime, int distance) {
        this.order = order;
        this.line = line;
        this.busStop = busStop;
        this.deltaTime = deltaTime;
        this.distance = distance;
    }


    public int getId(){
        return this.stopID;
    }

    public int getOrder(){
        return this.order;
    }

    public Line getLine(){
        return this.line;
    }

    public BusStop getBusStop(){
        return this.busStop;
    }

    public LocalTime getDeltaTime(){
        return this.deltaTime;
    }

    public int getDistance(){
        return this.distance;
    }


}
