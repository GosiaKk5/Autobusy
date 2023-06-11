package org.example;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int courseID;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @ManyToOne
    private Bus bus;

    public Course() {}

    public Course(LocalDateTime startTime, Bus bus){

        this.startTime=startTime;
        this.endTime=null;
        this.bus=bus;

    }

    public void endCourse(LocalDateTime end){
        this.endTime = end;
    }

    public int getId(){
        return this.courseID;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Bus getBus() {
        return bus;
    }
}
