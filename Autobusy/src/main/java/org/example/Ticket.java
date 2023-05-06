package org.example;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int boughtTicketID;
    public LocalTime boughtTime;

    public int courseID;
    @ManyToOne
    public TicketType ticket;

    public Ticket() {}

    public Ticket(int boughtTicketID, LocalTime boughtTime, int courseID, TicketType ticket){

        this.boughtTicketID=boughtTicketID;
        this.boughtTime=boughtTime;
        this.courseID=courseID;
        this.ticket=ticket;

    }
}
