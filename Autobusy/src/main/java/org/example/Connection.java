package org.example;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Connection {
    private List<Line> lines;
    private List<BusStop> busStops;
    private List<LocalTime> deltas;
    private LocalDateTime startTime;

    public Connection(BusStop startStop, LocalDateTime startTime){
        this.startTime = startTime;
        this.busStops = new ArrayList<>();
        this.busStops.add(startStop);
        this.lines = new ArrayList<>();
        this.deltas = new ArrayList<>();
    }

    public void addLine(Line line, BusStop endStop, LocalTime delta){
        this.lines.add(line);
        this.busStops.add(endStop);
        this.deltas.add(delta);
    }

    public int getNoLines(){
        return this.lines.size();
    }

    public LocalTime getDuration(){
        LocalTime result = LocalTime.MIN;

        //bez przesiadek
        if(this.lines.size() == 1){
            BusStop start = this.busStops.get(0);
            StopOnLine lineStop = this.lines.get(0).getBusStop(0);
            int j=1;
            while(lineStop.getBusStop().getId()!=start.getId()){
                lineStop=this.lines.get(0).getBusStop(j);
                j+=1;
            }
            //linestop jest teraz tym na ktorym wsiadam
            while(lineStop.getBusStop().getId()!=this.busStops.get(1).getId()){
                lineStop=this.lines.get(0).getBusStop(j);
                result=result.plusMinutes(lineStop.getDeltaTime().getMinute());
                result=result.plusHours(lineStop.getDeltaTime().getHour());
                j+=1;
            }
            return result;

        }

        //dodaje czasy przesiadek
        for(LocalTime delta : this.deltas){
            result = result.plusHours(delta.getHour());
            result = result.plusMinutes(delta.getMinute());
        }

        //czasy przejazdow
        for(int i=0;i<this.getNoLines();i++) {
            //zmienne pomocnicze
            int j = 0;
            BusStop start = this.busStops.get(i);
            StopOnLine lineStop = this.lines.get(i).getBusStop(j);
            //do startu
            while (lineStop.getBusStop().getId() != start.getId()) {
                j += 1;
                lineStop = this.lines.get(i).getBusStop(j);
            }
            //dodaje czasy
            while (lineStop.getBusStop().getId() != this.busStops.get(i + 1).getId()) {
                j += 1;
                result = result.plusHours(lineStop.getDeltaTime().getHour());
                result = result.plusMinutes(lineStop.getDeltaTime().getMinute());
                lineStop = this.lines.get(i).getBusStop(j);
            }
        }

        return result;
    }

    public LocalDateTime getEndTime(){
        LocalTime duration = this.getDuration();

        LocalDateTime result = this.startTime.plusHours(duration.getHour());
        result = result.plusMinutes(duration.getMinute());

        return result;
    }


    public String toString(){

        LocalDateTime endTime = this.getEndTime();

        String t1 = "";
        if(this.startTime.getMinute() < 10){
            t1 = this.startTime.getHour()+":0"+this.startTime.getMinute();
        }
        else{
            t1 = this.startTime.getHour()+":"+this.startTime.getMinute();
        }


        String t2 = "";
        if(endTime.getMinute() < 10){
            t2 = endTime.getHour()+":0"+endTime.getMinute();
        }
        else{
            t2 = endTime.getHour()+":"+endTime.getMinute();
        }

        if(this.getNoLines() == 1){
            return  "Mozesz jechac linia nr "+this.lines.get(0).getId()+" o godzinie "+t1+". Planowany przyjazd o godzinie: "+t2;
        }
        return "Mozesz jechac linia nr "+this.lines.get(0).getId()+" o godzinie "+t1+", z przesiadka na przystanku "+this.busStops.get(1).getName()+" na linie nr "+this.lines.get(1).getId()+". Planowany przyjazd o godzinie: "+t2;

    }
}
