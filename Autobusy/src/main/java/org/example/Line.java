package org.example;

import jakarta.persistence.*;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int lineID;
    private Integer length;
    private String start;
    private String finish;

    public Integer getLength() {
        return length;
    }


    public String getStart() {
        return start;
    }


    public String getFinish() {
        return finish;
    }


    public Line() {
    }

    public Line(Integer length, String start, String finish){

        this.length = length;
        this.start = start;
        this.finish = finish;
    }

    public int getId(){
        return this.lineID;
    }

}
