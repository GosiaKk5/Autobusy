package org.example;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.time.LocalTime;
import java.util.List;

public class Main {
    private static final SessionFactory ourSessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();

            ourSessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }

    public static void main(final String[] args) throws Exception {
        final Session session = getSession();
        try {
            Transaction tx = session.beginTransaction();
            
            DataCreator creator = new DataCreator();

            //dodaje linie
            List<Line> lines = creator.createLines(10);
            for(Line line : lines){
                session.save(line);
            }


            //dodaje typy biletow
            List<TicketType> ticketTypes = creator.createTicketTypes();
            for(TicketType tt : ticketTypes){
                session.save(tt);
            }

            //dodaje autobusy
            List<Bus> buses = creator.createBuses(20, lines);
            for(Bus bus : buses){
                session.save(bus);
            }

            //dodaje kursy
            List<Course> courses = creator.createCourses(50, buses, LocalTime.now());
            for(Course course : courses){
                session.save(course);
            }




            tx.commit();

        } finally {
            session.close();
        }
    }
}