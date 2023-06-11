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
    private int stopOnLineID;

    private Integer orderOnLine;

    private LocalTime deltaTime;

    private int distance;

    @ManyToOne
    private BusStop busStop;

    @ManyToOne
    private Line line;


    public StopOnLine() {
    }

    public StopOnLine(Line line, BusStop busStop, Integer order, LocalTime deltaTime, int distance) {
        this.orderOnLine = order;
        this.line = line;
        this.busStop = busStop;
        this.deltaTime = deltaTime;
        this.distance = distance;
    }


    public int getId(){
        return this.stopOnLineID;
    }

    public int getOrderOnLine(){
        return this.orderOnLine;
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
