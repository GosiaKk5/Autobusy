package org.example;

import jakarta.persistence.*;

import java.time.LocalTime;
@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int courseID;
    public LocalTime startTime;
    public LocalTime endTime;
    @ManyToOne
    public Bus bus;

    public Course() {}

    public Course(LocalTime startTime, LocalTime endTime, Bus bus){

        this.startTime=startTime;
        this.endTime=endTime;
        this.bus=bus;

    }
}
