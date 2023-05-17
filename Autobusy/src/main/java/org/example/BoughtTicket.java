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

    public BoughtTicket(int boughtTicketID, LocalTime boughtTime, Course course, TicketType ticket){

        this.boughtTicketID=boughtTicketID;
        this.boughtTime=boughtTime;
        this.course = course;
        this.ticket=ticket;

    }
}
