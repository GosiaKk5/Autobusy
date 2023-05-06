package org.example;



@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int lineID;
    public int length;
    public Line line;

    public Line() {
    }

    public Line(int busCapacity){
        this.busCapacity = busCapacity;
    }
}
