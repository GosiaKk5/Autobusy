package org.example;

import jakarta.persistence.*;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int lineID;
    public int length;
    public String start;
    public String end;

    public Line() {
    }

    public Line(int length, String start, String end){

        this.length=length;
        this.start=start;
        this.end=end;
    }

}
