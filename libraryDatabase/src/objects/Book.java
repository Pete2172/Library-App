package objects;

import java.sql.ResultSet;
import java.sql.SQLException;


public class Book implements Table{

    private int id;
    private String genre;
    private String author;
    private String title;
    private String publisher;
    private String published;
    private String status;

    public Book(int id, String genre, String author, String title, String publisher, String published, String status) {
        this.id = id;
        this.genre = genre;
        this.author = author;
        this.title = title;
        this.publisher = publisher;
        this.published = published;
        this.status = status;
    }
    public Book(ResultSet results) throws SQLException {
        this(results.getInt(1), results.getString(2), results.getString(3),
                results.getString(4), results.getString(5), results.getString(6),
                results.getString(7));
    }

    public int getId() {
        return id;
    }

    public String getGenre() {
        return genre;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublished() {
        return published;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", genre='" + genre +
                ", author='" + author +
                ", title='" + title +
                ", publisher='" + publisher +
                ", published='" + published  +
                ", status='" + status +
                '}';
    }
}
