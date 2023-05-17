package org.example;

import org.apache.derby.iapi.db.Factory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

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

    private static void fillDatabase(Session session, DataCreator creator){
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
    }

    private static int parseInputAndExecute(List<String> input, Session session){
        //wyjscie
        if(input.get(0).equals("exit")){
            return -1;
        }

        //pomoc
        if(input.get(0).equals("help")){
            System.out.println("Dostępne polecenia:");
            System.out.println("+ exit");
            System.out.println("+ help");
            System.out.println("+ wypisz bilety");
            System.out.println("+ wypisz linie");
            System.out.println("+ wypisz kursy");
            System.out.println("+ sprawdz *id_biletu* *id_kursu_gdzie_bilet_jest_sprawdzany*");
            System.out.println("+ kup *id_typu_biletu* *id_kursu*");
            return 0;
        }

        //wypisywanie
        if(input.get(0).equals("wypisz")){
            //bilety
            if(input.get(1).equals("bilety")){
                List<TicketType> tts = session.createQuery("SELECT t FROM TicketType t", TicketType.class).getResultList();
                System.out.println("Dostepne typy biletów:");
                for(TicketType tt : tts){
                    System.out.println(tt.getId()+": "+tt.name+" => "+tt.price);
                }
                return 0;
            }

            //linie
            if(input.get(1).equals("linie")){
                List<Line> lines = session.createQuery("SELECT l FROM Line l", Line.class).getResultList();
                System.out.println("Linie autobusowe:");
                for(Line l : lines){
                    System.out.println(l.getId()+": "+l.start+" -> "+l.finish+" ("+l.length+")");
                }
                return 0;
            }

            //kursy
            if(input.get(1).equals("kursy")){
                List<Course> courses = session.createQuery("SELECT c FROM Course c WHERE c.endTime IS NOT NULL ", Course.class).getResultList();
                System.out.println("Aktualne kursy:");
                for(Course c : courses){
                    System.out.println(c.getId()+": "+c.bus.line.start+" -> "+c.bus.line.finish+" ("+c.bus.getId()+")");
                }
                return 0;
            }


            return 1;

        }

        //sprawdzenie poprawnosci biletu
        if(input.get(0).equals("sprawdz") && input.size() > 2){
            int ticketID = Integer.parseInt(input.get(1));
            int actualCurseID = Integer.parseInt(input.get(2));

            //sprawdzam czy jest taki bilet
            boolean correct = false;
            List<BoughtTicket> currTickets = session.createQuery("SELECT t FROM BoughtTicket t ", BoughtTicket.class).getResultList();
            for(BoughtTicket bt : currTickets){
                if(bt.getId() == ticketID){
                    correct = true;

                    //sprawdzilem ze istnieje/istnial taki bilet, teraz sprawdzam czy jest poprawny
                    LocalTime goodTo = bt.boughtTime.plusMinutes(bt.ticket.time);
                    if(goodTo.isBefore(LocalTime.now())){
                        correct = false;
                    }
                    if(bt.ticket.isAllLine && bt.course.getId()==actualCurseID){
                        correct = true;
                    }

                    break;

                }
            }

            //ostateczny komunikat
            if(correct){
                System.out.println("Bilet jest ważny!");
            }
            else{
                System.out.println("Bilet jest nieważny!");
            }

            return 0;
        }

        //zakup biletu
        if(input.get(0).equals("kup") && input.size() > 2){
            int ticketID = Integer.parseInt(input.get(1));
            int courseID = Integer.parseInt(input.get(2));

            //sprawdzam czy jest taki kurs
            Course myCourse = null;
            List<Course> courses = session.createQuery("SELECT c FROM Course c WHERE c.endTime IS NOT NULL ", Course.class).getResultList();
            for(Course course : courses){
                if(course.getId() == courseID){
                    myCourse = course;
                    break;
                }
            }
            if(myCourse == null){
                //jezeli nie znaleziono danego kursu
                System.out.println("Niepoprawne ID kursu!");
                return 1;
            }

            //sprawdzam czy jest taki typ biletu
            TicketType ticketType = null;
            List<TicketType> ticketTypes = session.createQuery("SELECT t FROM TicketType t ", TicketType.class).getResultList();
            for(TicketType tt : ticketTypes){
                if(tt.getId() == ticketID){
                    ticketType = tt;
                    break;
                }
            }
            if(ticketType == null){
                //jezeli nie znaleziono danego typu biletu
                System.out.println("Niepoprawne ID typu biletu!");
                return 1;
            }

            Transaction tx = session.beginTransaction();
            BoughtTicket newTicket = new DataCreator().buyTicket(ticketType, myCourse);
            session.save(newTicket);
            tx.commit();

            System.out.println("ID kupionego biletu: "+newTicket.getId());

            return 0;


        }

        //jesli zaden wzorzec nie pasowal
        return 1;

    }

    public static void main(final String[] args) throws Exception {
        final Session session = getSession();
        try {
            Transaction tx = session.beginTransaction();

            DataCreator creator = new DataCreator();

            //wypelniam baze danymi
            fillDatabase(session, creator);

            tx.commit();


            //czekanie na komunikaty
            Scanner scanner = new Scanner(System.in);
            System.out.println("\nWpisz polecenie (w razie problemów użyj komendy 'help'):");
            while(true){
                System.out.print(">>> ");
                List<String> com = Arrays.asList(scanner.nextLine().split(" "));

                int result = parseInputAndExecute(com, session);

                //wyjscie z programu
                if(result < 0){
                    break;
                } else if (result > 0) {
                    //komunikat bledu
                    System.out.println("Niepoprawne polecenie!");
                }
            }





        } finally {
            session.close();
        }
    }
}