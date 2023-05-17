package org.example;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

public class DataCreator {

    private Random rnd;

    private String[] stopNames;


    public DataCreator(){
        this.rnd = new Random();

        this.stopNames = new String[]{"Kopiec", "Zamek", "Kościół", "AGH", "Miasteczko",
                                    "Kopalnia", "UJ", "ZOO", "Wisła", "Błonia", "Akademik",
                                    "Dworzec PKP", "Dworzec PKS", "Lotnisko", "Rynek", "Centrum Handlowe"};


    }

    private int randInt(int a, int b){
        //zwraca liczbe typu int z przedzialu <a;b>
        return a + abs(rnd.nextInt() % (b-a+1));
    }

    public List<Line> createLines(int n){
        List<Line> result = new ArrayList<>();

        for(int i=0;i<n;i++){
            String start = this.stopNames[randInt(0, this.stopNames.length-1)];
            String end = this.stopNames[randInt(0, this.stopNames.length-1)];
            int len = randInt(1, 60);

            result.add(new Line(len, start, end));

        }
        
        return  result;
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

    public List<Course> createCourses(int n, List<Bus> buses, LocalTime actualTime){
        List<Course> result = new ArrayList<>();

        int nOfBuses = buses.size();

        int actHour = actualTime.getHour();

        int hourDelta = 2;

        for(int i=0;i<n;i++){
            int hour1 = randInt((actHour-hourDelta+24)%24, (actHour-1+24)%24);
            int minute = randInt(0, 59);
            LocalTime courseStartTime = LocalTime.of(hour1, minute);
            Course newCourse = new Course(courseStartTime, buses.get(i%nOfBuses));
            //niektore kursy beda juz zakonczone
            if(randInt(0, 10) < 5){
                while (true){
                    int hour2 = randInt(hour1, (actHour-1+24)%24);
                    minute = randInt(0, 59);
                    int seconds = randInt(0, 59);
                    LocalTime courseEndTime = LocalTime.of(hour2, minute, seconds);
                    if(courseEndTime.isAfter(courseStartTime)){
                        newCourse.endCourse(courseEndTime);
                        break;
                    }
                }

            }
            result.add(newCourse);
        }

        return result;
    }

    public BoughtTicket buyTicket(TicketType tt, Course course){
        return new BoughtTicket(LocalTime.now(), course, tt);
    }

}
