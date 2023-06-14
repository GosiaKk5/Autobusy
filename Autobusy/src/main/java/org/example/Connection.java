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
        System.out.println("ZACZYNAM"+this.busStops.size()+this.busStops.get(0).getId()+busStops.get(1).getId()+lines.get(0).getId());

        if(this.lines.size()==1){
            BusStop start = this.busStops.get(0);
            StopOnLine lineStop = this.lines.get(0).getBusStop(0);
            int j=1;
            while(lineStop.getBusStop().getId()!=start.getId()){
                lineStop=this.lines.get(0).getBusStop(j);
                j+=1;
            }
            //linestop jest teraz tym na ktorym wsiadam
//            System.out.println("wsiadam"+lineStop.getBusStop().getId());
//            System.out.println(this.lines.get(0).getNoStops());
            while(lineStop.getBusStop().getId()!=this.busStops.get(1).getId()){
//                System.out.println("przystanek"+lineStop.getBusStop().getId()+j);
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
//            System.out.println(lineStop);
//            BusStop stop2 = this.busStops.get(i+1);
//            boolean shouldAdd = false;
            //do startu
            while (lineStop.getBusStop().getId() != start.getId()) {
                j += 1;
                lineStop = this.lines.get(i).getBusStop(j);
            }
//            System.out.println("to tez moze byc null "+this.busStops.get(i + 1).getId());
//            System.out.println("to moze byc null "+lineStop.getBusStop().getId());
            while (lineStop.getBusStop().getId() != this.busStops.get(i + 1).getId()) {
                j += 1;
                result = result.plusHours(lineStop.getDeltaTime().getHour());
                result = result.plusMinutes(lineStop.getDeltaTime().getMinute());
                lineStop = this.lines.get(i).getBusStop(j);
//                System.out.println(lineStop);
            }
        }


            //iteruje po przystankach na linii
//            for(int j=0;j<line.getNoStops();i++){
//                StopOnLine currStop = line.getBusStop(j);
//                if(shouldAdd){
//                    //dodaje czas
//                    result = result.plusHours(currStop.getDeltaTime().getHour());
//                    result = result.plusMinutes(currStop.getDeltaTime().getMinute());
//
//                    //sprawdzam czy sie nie przesiadam na kolejna linie
//                    if(stop2.getId() == currStop.getBusStop().getId()){
//                        break;
//                    }
//                }
            //sprawdzam czy od kolejnego przystanku nie bede mogl juz dodawc czasu
//                else if(stop1.getId() == currStop.getBusStop().getId()) {
//                    shouldAdd = true;
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
