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

//    @Override
//    public int hashCode() {
//        int result=1;
//        return result;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Connection))
            return false;

        Connection connection = (Connection) o;

        boolean endTime = this.getEndTime().equals(((Connection) o).getEndTime());
        boolean stop = this.busStops.equals(((Connection) o).getBusStops());
        boolean lines = this.lines.equals(((Connection) o).getLines());

        return endTime && stop && lines;
    }

    public void addLine(Line line, BusStop endStop, LocalTime delta){
        this.lines.add(line);
        this.busStops.add(endStop);
        this.deltas.add(delta);
    }

    public int getNoLines(){
        return this.lines.size();
    }

    public List<BusStop> getBusStops() {
        return busStops;
    }

    public List<Line> getLines() {
        return lines;
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

        //przechodze po kazdej linii
//        for(int i=0;i<this.getNoLines();i++){
//            Line line = this.lines.get(i);
//            boolean canAdd = false;
//            //dodaje czasy pomiedzy odpowiednimi przystankami na danej linii
//            for(int j=0;j<line.getNoStops();j++){
//                //jezeli moge dodawac czas to dodaje
//                if(canAdd){
//                    result = result.plusHours(line.getBusStop(j).getDeltaTime().getHour());
//                    result = result.plusMinutes(line.getBusStop(j).getDeltaTime().getMinute());
//
//                    //sprawdzam czy nie wysiadam
//                    if(line.getBusStop(j).getBusStop().getId() == this.busStops.get(i+1).getId()){
//                        break;
//                    }
//                }
//                //sprawdzam czy juz moge dodawac czasy
//                else if(line.getBusStop(j).getBusStop().getId() == this.busStops.get(i).getId()) {
//                    canAdd = true;
//                }
//            }
//        }

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
