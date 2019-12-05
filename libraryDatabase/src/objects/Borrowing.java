package objects;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class Borrowing implements Table{

    private String borrow_date;
    private String return_date;
    private int id_reader;
    private int id_book;
    private int id;

    public int getId() {
        return id;
    }

    public String getBorrow_date() {
        return borrow_date;
    }

    public String getReturn_date() {
        return return_date;
    }

    public int getId_reader() {
        return id_reader;
    }

    public int getId_book() {
        return id_book;
    }

    public Borrowing(int id, String borrow_date, String return_date, int id_reader, int id_book) {
        this.id = id;
        this.borrow_date = borrow_date;
        this.return_date = return_date;
        this.id_reader = id_reader;
        this.id_book = id_book;
    }
    public Borrowing(ResultSet results) throws SQLException {
        this(results.getInt(1), results.getString(4), results.getString(5),
                results.getInt(2), results.getInt(3));
    }

    public BigDecimal countFine() throws ParseException {
        Date d1 = new SimpleDateFormat("dd-MM-yyyy").parse(borrow_date);
        Date d2 = ("".equals(return_date)) ? new Date() : new SimpleDateFormat("dd-MM-yyyy").parse(return_date);


        long days = ChronoUnit.DAYS.between(d1.toInstant(), d2.toInstant());

        return BigDecimal.valueOf((days < 30) ? 0.00 : (days - 30)*0.2);
    }

}
