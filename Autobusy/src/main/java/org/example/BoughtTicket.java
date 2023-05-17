package org.example;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
public class BoughtTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int boughtTicketID;
    public LocalTime boughtTime;

    @ManyToOne
    public Course course;
    @ManyToOne
    public TicketType ticket;

    public BoughtTicket() {}

    public BoughtTicket(LocalTime boughtTime, Course course, TicketType ticket){

        this.boughtTime=boughtTime;
        this.course = course;
        this.ticket=ticket;

    }

    public int getId(){
        return this.boughtTicketID;
    }



}
