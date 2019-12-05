package objects;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Reader implements Table{

    private int id;
    private String surname;
    private String name;
    private String password;
    private String city;
    private String address;

    public Reader(int id, String surname, String name, String password, String city, String address) {
        this.id = id;
        this.surname = surname;
        this.name = name;
        this.password = password;
        this.city = city;
        this.address = address;
    }
    public Reader(ResultSet results) throws SQLException {
        this(results.getInt(1), results.getString(2), results.getString(3),
                results.getString(4), results.getString(5), results.getString(6));
    }

    public int getId() {
        return id;
    }

    public String getSurname() {
        return surname;
    }

    public String getName() {
        return name;
    }


    public String getPassword() {
        return password;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Reader{" +
                "id=" + id +
                ", surname='" + surname  +
                ", name='" + name  +
                ", password='" + password  +
                ", city='" + city  +
                ", address='" + address  +
                '}';
    }
}
