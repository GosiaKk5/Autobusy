package org.example;


import jakarta.persistence.*;

@Entity
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int busID;
    public int busCapacity;

    @ManyToOne
    public Line line;   

    public Bus() {
    }

    public Bus(int busCapacity){
        this.busCapacity = busCapacity;
    }


}
