package org.example;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class TicketType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int ticketTypeID;
    public int time;

    public String name;
    public boolean isAllLine;
    public int price;

    public TicketType() {}

    public TicketType(int time, String name, boolean isAllLine, int price){

        this.time=time;
        this.name=name;
        this.isAllLine=isAllLine;
        this.price=price;

    }
}
