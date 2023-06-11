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
    private List<StopOnLine> stops;

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

    public StopOnLine getStop(int i){
        if(i >= this.getNoStops()){
            return null;
        }
        return this.stops.get(i);
    }

    public void addStopToLine(BusStop stop, LocalTime deltaTime, int distance){
        StopOnLine newStop = new StopOnLine(this, stop, this.getNoStops(), deltaTime, distance);
        this.stops.add(newStop);
        stop.getLines().add(newStop);
    }


    public Line() {
        this.stops = new ArrayList<StopOnLine>();
    }

    public int getId(){
        return this.lineID;
    }

}
