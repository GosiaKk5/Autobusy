package org.example;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static java.lang.Integer.min;


class ConnectionsComparator implements Comparator<Connection> {

    @Override
    public int compare(Connection c1, Connection c2) {
        //czasy dojazdu do celu
        //c1 wczesniej niz c2
        if(c1.getEndTime().isBefore(c2.getEndTime())){
            return -1;
        }
        //c2 wczesniej niz c1
        if(c2.getEndTime().isBefore(c1.getEndTime())){
            return 1;
        }

        //ten sam czas dojazdu, ale rozne czasy przejazdu
        //c1 krotszy niz c2
        if(c1.getDuration().isBefore(c2.getDuration())){
            return -1;
        }
        //c2 krotszy niz c1
        if(c2.getDuration().isBefore(c1.getDuration())){
            return 1;
        }
        //gdy te same czasy przejazdu, to mneijsza liczba przesiadek jest lepsza
        return c1.getNoLines()-c2.getNoLines();
    }
}

public class Main {

    public static final String ANSI_CYAN = "\u001B[36m";
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
        //dodaje przystanki
        List<BusStop> stops = creator.createBusStops();
        for(BusStop stop : stops){
            session.save(stop);
        }

        //dodaje linie
        List<Line> lines = creator.createLines(20);
        for(Line line : lines){
            session.save(line);
        }

        //dodaje przystanki do linii
        List<StopOnLine> lineStops = creator.addStopsToLines(stops, lines);
        for(StopOnLine stop : lineStops){
            session.save(stop);
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
        List<Course> courses = creator.createCourses(50, buses, LocalDateTime.now());
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
            System.out.println("+ wypisz przystanki");
            System.out.println("+ wypisz kurs *id_kursu*");
            System.out.println("+ wypisz polaczenia *id_przystanku_startowego* *id_przystanku_koncowego*");
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
                    System.out.println(tt.getId()+": "+tt.getName()+" => "+tt.getPrice());
                }
                return 0;
            }

            //linie
            if(input.get(1).equals("linie")){
                List<Line> lines = session.createQuery("SELECT l FROM Line l", Line.class).getResultList();
                System.out.println("Linie autobusowe:");
                for(Line l : lines){
                    System.out.println(l.getId()+": "+l.getStart()+" -> "+l.getFinish()+" ("+l.getLength()+")");
                }
                return 0;
            }

            //kursy
            if(input.get(1).equals("kursy")){
                List<Course> courses = session.createQuery("SELECT c FROM Course c, Line l, Bus b WHERE c.endTime IS NULL and c.bus=b.busID and b.line=l.lineID ", Course.class).getResultList();
                System.out.println("Aktualne kursy:");
                for(Course c : courses){
                    System.out.println(c.getId()+": "+c.getBus().getLine().getStart()+" -> "+c.getBus().getLine().getFinish()+" ("+c.getBus().getId()+")");
                }
                return 0;
            }

            //przystanki
            if(input.get(1).equals("przystanki")){
                List<BusStop> busStops = session.createQuery("SELECT b FROM BusStop b", BusStop.class).getResultList();
                System.out.println("Wszystkie przystanki:");
                for(BusStop bs : busStops){
                    System.out.println(bs.getId()+": "+bs.getName());
                }
                return 0;
            }
            //dany kurs
            if(input.get(1).equals("kurs")){
                int courseID = Integer.parseInt(input.get(2));
                System.out.println(courseID);
                List<Course> courses = session.createQuery("SELECT c FROM Course c, Line l, Bus b WHERE c.endTime IS NULL and c.bus=b.busID and b.line=l.lineID", Course.class).getResultList();

                for(Course c : courses){
                    if(c.getId() == courseID){
                        System.out.println("Przystanki kursu nr. "+courseID+" ( "+c.getBus().getLine().getStart()+" -> "+c.getBus().getLine().getFinish()+" ):");
                        LocalDateTime sdt = c.getStartTime();
                        int distance = 0;
                        for(int i=0;i<c.getBus().getLine().getNoStops();i++){
                            //zbieram dane
                            StopOnLine sol = c.getBus().getLine().getBusStop(i);
                            sdt = sdt.plusHours(sol.getDeltaTime().getHour());
                            sdt = sdt.plusMinutes(sol.getDeltaTime().getMinute());
                            distance += sol.getDistance();
                            String stopName = sol.getBusStop().getName();

                            //print
                            System.out.println((i+1)+". "+stopName+",\t data: "+sdt.toLocalDate()+",\t godzina: "+sdt.toLocalTime()+",\t odległość: "+distance+" km");
                        }
                        return 0;
                    }
                }
                System.out.println("Nie znaleziono takiego kursu!");
                return 0;
            }

            //znajdowanie polaczen
            if(input.get(1).equals("polaczenia") && input.size() > 3){
                int startID = Integer.parseInt(input.get(2));
                int endID = Integer.parseInt(input.get(3));

                //sprawdzam czy istnieja takie przystanki
                boolean correct = false;
                List<BusStop> busStops = session.createQuery("SELECT bs FROM BusStop bs ", BusStop.class).getResultList();

                for(BusStop bsS : busStops){
                    if(bsS.getId() == startID){
                        for(BusStop bsE : busStops){
                            if(bsE.getId() == endID){
                                correct=true;
                                break;
                            }
                        }
                    }
                }
                //jezeli wszystko ok z przystankami
                if(correct){
                    //zapytania do bazy
                    List<Course> coursesWithStart =
                            session.createQuery("SELECT c FROM Course c, Line l, Bus b, StopOnLine so, BusStop bs WHERE c.bus=b.busID and " +
                                    "b.line=l.lineID and bs.busStopID=so.busStop and so.line=l.lineID and " +
                                    "bs.busStopID = :start", Course.class).setParameter("start", startID).getResultList();
                    List<Course> coursesWithEnd = session.createQuery("SELECT c FROM Course c, Line l, Bus b, StopOnLine so, BusStop bs " +
                            "WHERE c.bus=b.busID and b.line=l.lineID and bs.busStopID=so.busStop and so.line=l.lineID " +
                            "and bs.busStopID = :end", Course.class).setParameter("end", endID).getResultList();

                    //szukam instancji przystanku startowego i koncowego
                    BusStop startStop = new BusStop();
                    Line ln = coursesWithStart.get(0).getBus().getLine();
                    for(int i=0;i<ln.getNoStops();i++){
                        if(ln.getBusStop(i).getBusStop().getId() == startID){
                            startStop = ln.getBusStop(i).getBusStop();
                        }
                    }
                    BusStop endStop = new BusStop();
                    ln = coursesWithEnd.get(0).getBus().getLine();
                    for(int i=0;i<ln.getNoStops();i++){
                        if(ln.getBusStop(i).getBusStop().getId() == endID){
                            endStop = ln.getBusStop(i).getBusStop();
                        }
                    }

                    //lista polaczen
                    Set<Connection> connections = new TreeSet<>(new ConnectionsComparator());

                    for(Course c1 : coursesWithStart){
                        LocalDateTime sdtCourse1 = c1.getStartTime();
                        LocalDateTime start=sdtCourse1;

                        int n = c1.getBus().getLine().getNoStops();

                        boolean startBeforeTransfer = false;
                        //przechodze po wszystkich przystankach tego kursu
                        for(int i=0; i<n; i++){
                            StopOnLine sol1 = c1.getBus().getLine().getBusStop(i);
                            sdtCourse1 = sdtCourse1.plusHours(sol1.getDeltaTime().getHour());
                            sdtCourse1 = sdtCourse1.plusMinutes(sol1.getDeltaTime().getMinute());
                            if(sol1.getBusStop().getId() == startID){
                                startBeforeTransfer = true;
                                start = sdtCourse1;
                                //nie interesuje nas to co bylo
                                if(start.isBefore(LocalDateTime.now())){
                                    break;
                                }
                            }
                            //dla kazdego przystanku sprawdzam wszystkie kursy do koncowego
                            for(Course c2 : coursesWithEnd){
                                LocalDateTime sdtCourse2 = c2.getStartTime();
                                int n1 = c2.getBus().getLine().getNoStops();
                                for(int j=0; j<n1; j++) {
                                    StopOnLine sol2 = c2.getBus().getLine().getBusStop(j);
                                    sdtCourse2 = sdtCourse2.plusHours(sol2.getDeltaTime().getHour());
                                    sdtCourse2 = sdtCourse2.plusMinutes(sol2.getDeltaTime().getMinute());
                                    //jezeli doszedlem do przystanku docelowego to nie ma sensu dalej iterowac
                                    if(sol2.getBusStop().getId() == endID){
                                        break;
                                    }

                                    //kurs bezposredni
                                    if(c1.getId() == c2.getId()){
                                        //sprawdzam czy przystanek startowy jest przed docelowym
                                        Line line = c1.getBus().getLine();
                                        for(int k=0;k<line.getNoStops();k++){
                                            if(line.getBusStop(k).getBusStop().getId() == startID){
                                                Connection connection = new Connection(startStop, start);
                                                connection.addLine(line, endStop, LocalTime.MIN);

                                                if(connection.getEndTime().isAfter(LocalDateTime.now()) && !connections.contains(connection)) connections.add(connection);
                                                break;

                                            } else if (line.getBusStop(k).getBusStop().getId() == endID) {
                                                break;
                                            }
                                        }

                                    }

                                    //kurs z przesiadka
                                    if(sol1.getBusStop().getId() == sol2.getBusStop().getId() && sdtCourse2.isAfter(sdtCourse1) && startBeforeTransfer){
                                        //czas przesiadki
                                        LocalDateTime delta = sdtCourse2.minusHours(sdtCourse1.getHour());
                                        delta = delta.minusMinutes(sdtCourse1.getMinute());

                                        Connection connection = new Connection(startStop, start);
                                        connection.addLine(c1.getBus().getLine(), sol2.getBusStop(), delta.toLocalTime());
                                        connection.addLine(c2.getBus().getLine(), endStop, LocalTime.MIN);
                                        if(connection.getEndTime().isAfter(LocalDateTime.now()) && !connections.contains(connection)) connections.add(connection);

                                    }

                                }
                            }

                        }
                    }
                    if(connections.size() == 0) {
                        System.out.println("Nie ma połaczenia z maksymalnie jedną przesiadką pomiędzy tymi przystankami!");
                    }
                    else{
                        Iterator it = connections.iterator();
                        for(int i=0;i<min(15, connections.size());i++){
                            System.out.println(it.next());
                        }
                    }
                }
                //jezeli jest problem z przystankami
                else{
                    System.out.println("Nie ma takiego przystanku!");
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
                    LocalTime goodTo = bt. getBoughtTime().plusMinutes(bt.getTicket().getTime());
                    if(goodTo.isBefore(LocalTime.now())) correct = false;
                    if(bt.getTicket().isAllLine() && bt.getCourse().getId()==actualCurseID) correct = true;
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
            List<Course> courses = session.createQuery("SELECT c FROM Course c WHERE c.endTime IS NULL ", Course.class).getResultList();
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

        }finally {
            session.close();
        }
    }
}