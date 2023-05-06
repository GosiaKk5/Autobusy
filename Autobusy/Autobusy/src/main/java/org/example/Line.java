package org.example;

<<<<<<< HEAD
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
=======

>>>>>>> parent of 4e89ab3 (nothing)

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int lineID;
    public int length;
    public Line line;

    public Line() {
    }

<<<<<<< HEAD
<<<<<<< Updated upstream
    public Line(int length, String start, String end){

        this.length=length;
        this.start=start;
        this.end=end;

=======
    public Line(int busCapacity){
        this.busCapacity = busCapacity;
>>>>>>> parent of 4e89ab3 (nothing)
    }
=======
>>>>>>> Stashed changes
}
