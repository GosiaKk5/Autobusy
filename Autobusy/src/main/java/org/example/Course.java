package org.example;

import jakarta.persistence.*;

import java.time.LocalTime;
@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int courseID;
    private LocalTime startTime;
    private LocalTime endTime;
    @ManyToOne
    private Bus bus;

    public Course() {}

    public Course(LocalTime startTime, Bus bus){

        this.startTime=startTime;
        this.endTime=null;
        this.bus=bus;

    }

    public void endCourse(LocalTime end){
        this.endTime = end;
    }

    public int getId(){
        return this.courseID;
    }

    public LocalTime getStartTime() {
        return startTime;
    }
    public LocalTime getEndTime() {
        return endTime;
    }

    public Bus getBus() {
        return bus;
    }
}
