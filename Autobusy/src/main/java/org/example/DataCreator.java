package org.example;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

public class DataCreator {

    private Random rnd;



    public DataCreator(){
        this.rnd = new Random();
    }

    private int randInt(int a, int b){
        //zwraca liczbe typu int z przedzialu <a;b>
        return a + abs(rnd.nextInt() % (b-a+1));
    }

    public List<BusStop> createBusStops(){
        List<BusStop> result = new ArrayList<>();

        String[] stopNames = new String[]{"Kopiec", "Zamek", "Kościół", "AGH", "Miasteczko",
                "Kopalnia", "UJ", "ZOO", "Wisła", "Błonia", "Akademik",
                "Dworzec PKP", "Dworzec PKS", "Lotnisko", "Rynek", "Centrum Handlowe",
                "Lodowisko", "Park", "Stadion", "Fort", "Rondo", "Zajezdnia", "Osiedle",
                "Pizzeria", "Las", "Muzeum", "Obserwatorium astronomiczne"};

        for(int i=0;i<stopNames.length;i++){
            result.add(new BusStop(stopNames[i]));
        }

        return result;
    }

    public List<Line> createLines(int n, List<BusStop> busStops){
        List<Line> result = new ArrayList<>();

        for(int i=0;i<n;i++){
            result.add(new Line());
        }
        
        return  result;
    }


    public List<StopOnLine> addStopsToLines(List<BusStop> stops, List<Line> lines){
        List<StopOnLine> result = new ArrayList<>();
        for(Line line : lines){
            int nOfStops = this.randInt(5, 25);
            for(int j=0;j<nOfStops;j++){
                BusStop stop = stops.get(this.randInt(0, stops.size()-1));
                LocalTime time = LocalTime.of(0, 0);
                if(j > 0){
                    time = LocalTime.of(0, this.randInt(1, 15));
                }

                int dist = this.randInt(1, 5);

                result.add(line.addStopToLine(stop, time, dist));
            }
        }

        return result;

    }

    public List<TicketType> createTicketTypes(){
        List<TicketType> result = new ArrayList<>();

        //ulgowy 20 min
        result.add(new TicketType(20, "ULGOWY 20 minutowy", false, 2));

        //ulgowy 60 min
        result.add(new TicketType(60, "ULGOWY 60 minutowy", false, 3));

        //ulgowy 60 min lub cala linia
        result.add(new TicketType(60, "ULGOWY 60 minutowy lub na całą linie", true, 4));

        //normalny 20 min
        result.add(new TicketType(20, "NORMALNY 20 minutowy", false, 3));

        //normalny 60 min
        result.add(new TicketType(60, "NORMALNY 60 minutowy", false, 4));

        //normalny 60 min lub cala linia
        result.add(new TicketType(60, "NORMALNY 60 minutowy lub na całą linie", true, 5));

        return result;
    }

    public List<Bus> createBuses(int n, List<Line> lines){
        List<Bus> result = new ArrayList<>();

        int nOfLines = lines.size();

        for(int i=0;i<n;i++){
            result.add(new Bus(randInt(10, 60), lines.get(i % nOfLines)));
        }

        return result;
    }

    public List<Course> createCourses(int n, List<Bus> buses, LocalDateTime actualTime){

        LocalDate date = actualTime.toLocalDate();
        LocalTime now = actualTime.toLocalTime();

        List<Course> result = new ArrayList<>();

        int nOfBuses = buses.size();

        int actHour = now.getHour();

        int hourDelta = 2;

        for(int i=0;i<n;i++){
            int hour1 = randInt((actHour-hourDelta+24)%24, (actHour-1+24)%24);
            int minute = randInt(0, 59);

            LocalTime courseStartTime = LocalTime.of(hour1, minute);
            LocalDateTime cst = courseStartTime.atDate(date);

            Course newCourse = new Course(cst, buses.get(i%nOfBuses));

            //sprawdzanie kiedy kurs sie zakonczyl/ma sie zakonczyc
            LocalTime courseTime = newCourse.getBus().getLine().getTimeLength();

            LocalTime courseEndTime = courseStartTime.plusHours(courseTime.getHour());
            courseEndTime = courseEndTime.plusMinutes(courseTime.getMinute());


            LocalDateTime cet = courseEndTime.atDate(date);

            //jesli kurs przeszedl przez polnoc to zmieniam dzien konca kursu na kolejny
            if(cst.isAfter(cet)){
                cet = cet.plusDays(1);
            }

            //sprawdzam czy kurs sie juz zakonczyl
            if(cet.isBefore(actualTime)){
                newCourse.endCourse(cet);
            }

            //dodaje kurs do wynikowej listy
            result.add(newCourse);
        }

        return result;
    }

    public BoughtTicket buyTicket(TicketType tt, Course course){
        return new BoughtTicket(LocalTime.now(), course, tt);
    }

}
