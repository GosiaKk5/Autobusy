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

        //dodaje czasy przesiadek
        for(LocalTime delta : this.deltas){
            result = result.plusHours(delta.getHour());
            result = result.plusMinutes(delta.getMinute());
        }

        //czasy przejazdow
        for(int i=0;i<this.getNoLines();i++){
            //zmienne pomocnicze
            Line line = this.lines.get(i);
            BusStop stop1 = this.busStops.get(i);
            BusStop stop2 = this.busStops.get(i+1);
            boolean shouldAdd = false;

            //iteruje po przystankach na linii
            for(int j=0;j<line.getNoStops();i++){
                StopOnLine currStop = line.getBusStop(j);
                if(shouldAdd){
                    //dodaje czas
                    result = result.plusHours(currStop.getDeltaTime().getHour());
                    result = result.plusMinutes(currStop.getDeltaTime().getMinute());

                    //sprawdzam czy sie nie przesiadam na kolejna linie
                    if(stop2.getId() == currStop.getBusStop().getId()){
                        break;
                    }
                }
                //sprawdzam czy od kolejnego przystanku nie bede mogl juz dodawc czasu
                else if(stop1.getId() == currStop.getBusStop().getId()) {
                    shouldAdd = true;
                }
            }
        }
        //return
        return result;
    }

    public LocalDateTime getEndTime(){
        LocalTime duration = this.getDuration();

        LocalDateTime result = this.startTime.plusHours(duration.getHour());
        result = result.plusMinutes(duration.getMinute());

        return result;
    }


    public String toString(){

        if(this.getNoLines() == 1){
            return  "Mozesz jechac linia nr "+this.lines.get(0).getId()+" o godzinie "+this.startTime.getHour()+":"+this.startTime.getMinute();
        }
        return "Mozesz jechac linia nr "+this.lines.get(0).getId()+" o godzinie "+this.startTime.getHour()+":"+this.startTime.getMinute()+", z przesiadka na przystanku "+this.busStops.get(1).getName()+" na linie nr "+this.lines.get(1).getId();

    }
}
