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
    private int time;

    private String name;
    private boolean isAllLine;
    private int price;

    public TicketType() {}

    public TicketType(int time, String name, boolean isAllLine, int price){

        this.time=time;
        this.name=name;
        this.isAllLine=isAllLine;
        this.price=price;

    }

    public int getId(){
        return this.ticketTypeID;
    }

    public int getTime() {
        return time;
    }


    public String getName() {
        return name;
    }


    public boolean isAllLine() {
        return isAllLine;
    }


    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
