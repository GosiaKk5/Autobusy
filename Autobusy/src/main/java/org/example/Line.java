package org.example;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int lineID;

    @OneToMany
    private Set<StopOnLine> stops;

    public int getLength() {
        int length = 0;
        for(StopOnLine stop : this.stops){
            length += stop.getDistance();
        }
        return length;
    }

    public LocalTime getTimeLength() {
        LocalTime time = LocalTime.MIN;
        for(StopOnLine stop : this.stops){
            time = time.plusHours(stop.getDeltaTime().getHour());
            time = time.plusMinutes(stop.getDeltaTime().getMinute());
        }
        return time;
    }

    public int getNoStops() {
        return this.stops.size();
    }

    public StopOnLine getBusStop(int i){
        for(StopOnLine stop : this.stops){
            if(stop.getOrderOnLine() == i){
                return stop;
            }
        }
        return null;
    }

    public String getStart(){
        return this.getBusStop(0).getBusStop().getName();
    }

    public String getFinish(){
        return this.getBusStop(this.getNoStops()-1).getBusStop().getName();
    }

    public StopOnLine addStopToLine(BusStop stop, LocalTime deltaTime, int distance){
        StopOnLine newStop = new StopOnLine(this, stop, this.getNoStops(), deltaTime, distance);
        this.stops.add(newStop);
        stop.getLines().add(newStop);
        return  newStop;
    }


    public Line() {
        this.stops = new HashSet<StopOnLine>();
    }

    public int getId(){
        return this.lineID;
    }

}
