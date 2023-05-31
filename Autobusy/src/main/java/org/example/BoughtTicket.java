package org.example;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
public class BoughtTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int boughtTicketID;
    private LocalTime boughtTime;

    @ManyToOne
    private Course course;
    @ManyToOne
    private TicketType ticket;

    public BoughtTicket() {}

    public BoughtTicket(LocalTime boughtTime, Course course, TicketType ticket){

        this.boughtTime=boughtTime;
        this.course = course;
        this.ticket=ticket;

    }

    public int getId(){
        return this.boughtTicketID;
    }

    public LocalTime getBoughtTime() {
        return boughtTime;
    }

    public Course getCourse() {
        return course;
    }


    public TicketType getTicket() {
        return ticket;
    }

}
