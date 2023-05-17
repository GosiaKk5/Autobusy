package org.example;

import jakarta.persistence.*;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int lineID;
    public Integer length;
    public String start;
    public String finish;

    public Line() {
    }

    public Line(Integer length, String start, String finish){

        this.length = length;
        this.start = start;
        this.finish = finish;
    }

}
