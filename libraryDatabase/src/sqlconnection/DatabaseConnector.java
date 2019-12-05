package sqlconnection;

import objects.*;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


/***
 * Database connector class
 * An object, which is instance of the class, allows manipulating the library database,
 * class uses JDBC API
 */
public class DatabaseConnector {

    public static final String DRIVER = "org.sqlite.JDBC";  /** string with database driver's name **/
    public static final String DB_URL = "jdbc:sqlite:library.db"; /** string with given name of database file **/

    private Connection conn;    /** object, which allows connecting to database **/
    private Statement stat;     /** object, which allows creating statements in database **/

    /**
     * Constructor of DatabaseConnector class
     */
    public DatabaseConnector(){
        try{
            Class.forName(DatabaseConnector.DRIVER);    /** creating JDBC engine **/
        }
        catch (ClassNotFoundException e){
            System.err.println("JDBC engine hasn't been found!");
            e.printStackTrace();
        }
        try{
            conn = DriverManager.getConnection(DB_URL); /** trying to connect */
        }
        catch (SQLException e){
            System.err.println("Problem after trying to connect!");
            e.printStackTrace();
        }
        createTables();  /** calling method, which creates tables in database **/
    }

    /**
     *  Method creates entities in database if they don't exist
     */
    private void createTables(){
        String query1 = "CREATE TABLE IF NOT EXISTS readers(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "surname VARCHAR(255)," +
                "name VARCHAR(255)," +
                "card_number VARCHAR(20) UNIQUE," +
                "password VARCHAR(255)," +
                "city VARCHAR(255)," +
                "address VARCHAR(255));";

         String query2 =       "CREATE TABLE IF NOT EXISTS borrowing(" +
                "id_reader INTEGER," +
                "id_book INTEGER," +
                "borrow_date DATE," +
                "return_date DATE," +
                 "to_return DATE," +
                "FOREIGN KEY(id_reader) REFERENCES readers(id)," +
                "FOREIGN KEY(id_book) REFERENCES books(id));";

          String query3 =      "CREATE TABLE IF NOT EXISTS books(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "genre VARCHAR(255)," +
                "author VARCHAR(255)," +
                "title VARCHAR(255)," +
                "publisher VARCHAR(255)," +
                "published DATE," +
                "status VARCHAR(255)," +
                  "image VARCHAR(255));";

        try{
            stat = conn.createStatement();
            stat.execute(query1);
            stat = conn.createStatement();
            stat.execute(query2);
            stat = conn.createStatement();
            stat.execute(query3);
        }
        catch (SQLException e){
            System.err.println("Something went wrong with creating tables in database!");
            e.printStackTrace();
        }
    }

    /**
     * Method inserts record to "books" table to database with attributes given as arguments of the method
     * @param genre  genre of a book
     * @param author author/writer of a book
     * @param title title of book
     * @param publisher publisher of a book
     * @param date date of book's publishing
     * @param status status of library's book, it can appear as "borrowed" or "free"
     * @param image path of file with a book's cover
     */
    public void insertBook(String genre, String author, String title, String publisher, String date, String status, String image){
        insertToTable("books", genre, author, title, publisher, date, status, image);
    }

    /**
     * Method inserts record to "borrowing" table to database with attributes given as arguments of the method
     * @param return_date date of returning a book by a reader
     * @param id_reader value of a reader's ID (card number)
     * @param id_book value of a book's ID
     * @return
     */
    public boolean insertBorrowing(String return_date, String id_reader, String id_book){

        if(isBookBorrowed(Integer.parseInt(id_book)) == false) {                                                         /** check if book is already borrowed by someone **/
            Date today = new Date();                                                /** creating a new Date object to gain today's date **/
            SimpleDateFormat form = new SimpleDateFormat("dd-MM-yyyy");     /** the indicated format of date **/
            String date = form.format(today);
            insertToTable("borrowing", id_reader, id_book, date, return_date);      /** adding record to database **/
            bookUpdate(Integer.parseInt(id_book), "borrowed");                          /** updating status of the book **/
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Method inserts record
     * @param surname a reader's surname
     * @param name a reader's name
     * @param password a reader's password
     * @param city a reader's city
     * @param address a reader's address
     */
    public void insertReader(String surname, String name, String password, String city, String address){
        insertToTable("readers", name, surname, password, city, address);
    }

    /**
     * Method updates a status of the book
     * @param id_book book's id
     * @param status status of the book we'd like to change, it can be "borrowed" or "free"
     */
    private void bookUpdate(int id_book, String status){
        this.doQuery("UPDATE books SET status=\"" + status + "\" WHERE id=\"" + id_book +"\";");
    }

    /**
     * Method updates data in books' record by given keys,
     * it changes status of a borrowed book to "free"
     * and adds date of the book's returning
     * @param id_book a book's ID
     * @param id_reader a reader's ID
     */
    public void returnBook(int id_book, int id_reader){
        try {
            Date today = new Date();                                                /** creating Date object to gain current date **/
            SimpleDateFormat form = new SimpleDateFormat("dd-MM-yyyy");     /** formatting current date properly **/
            String date = form.format(today);
            this.doQuery("UPDATE borrowing SET return_date=\"" + date + "\" WHERE id_reader=\"" + id_reader + "\" AND return_date=\"\";");  /** updating data **/
            bookUpdate(id_book, "free");        /** changing status of the book **/
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Method checks if book given by id is already borrowed
     * @param id a book's id
     * @return returns true if it's borrowed or false if isn't
     */
    private boolean isBookBorrowed(int id){
        return !this.listUsersQuery("SELECT * FROM books WHERE id=\"" + id + "\" AND status=\"borrowed\";", "Book").isEmpty();
    }

    /**
     * Method inserts record to a table given by name,
     * it uses the interface "Table" to create a proper query,
     * every class representing entity in the database extends "Table" interface
     * @param table_name name of a class, representing entity in database
     * @param args attributes of a proper entity
     */
    private void insertToTable(String table_name, String... args){
        try{
            PreparedStatement prepStat = conn.prepareStatement(Table.buildInsertQuery(table_name, args));  /** preparing statement **/
            prepStat.execute();             /** executing statement built in class **/
        }
        catch (Exception e){
            System.err.println("Something went wrong with adding a book!");
            e.printStackTrace();
        }
    }

    /**
     * Method deletes book from library database
     * @param id a book's id
     */
    public void deleteBookById(int id){
        try{
            PreparedStatement prepStat = conn.prepareStatement("DELETE FROM books WHERE id =\"" + id + "\"");  /** preparing query **/
            prepStat.execute();
        }
        catch (Exception e){
            System.err.println("Somethinf went wrong with deleting a record");
            e.printStackTrace();
        }
    }

    /**
     * Method adds all records from table given by name to "List" object
     * it uses reflection to find a proper class of entity
     * @param query query we'd like to execute
     * @param object_name name of class representing a proper entity in database
     * @return list with every element found in DB by given query
     */
    public List listUsersQuery(String query, String object_name){
        List<Table> users = new LinkedList<>();     /** creating "Table" list **/
        try{
            ResultSet results = stat.executeQuery(query);       /** result set of given query **/
            while(results.next()){
                users.add((Table) Class.forName("objects." + object_name).getConstructor(ResultSet.class).newInstance(results));    /** adding record to the list **/
            }
        }
        catch (Exception e){
            System.err.println("Something went wrong with listing table.");
            e.printStackTrace();
        }
        return users;       /** returning the list **/
    }

    /**
     * Method executes given query in DB
     * @param query given instruction Ex. "SELECT * FROM books"
     */
    public void doQuery(String query){
            try{
                PreparedStatement prepStat = conn.prepareStatement(query);
                prepStat.execute();
            }
            catch (Exception e){
                System.err.println("Something went wrogn with executing a query!");
                e.printStackTrace();
            }
    }

    /**
     * Method closes connection with library database
     */
    public void closeConnection(){
            try{
                conn.close();
            }
            catch (SQLException e){
                System.err.println("Something went wrong with closing connection!");
            }
    }

}
