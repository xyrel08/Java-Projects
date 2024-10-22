package domain.connection;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class PetConnection {
    //url:host+port+database_name
    private String url="jdbc:mysql://localhost:3307/dbname";
    private String username="root";
    private String password="";
    Connection connection;
    public void connect(){
        try{
        connection = DriverManager.getConnection(url,username,password);
        System.out.println("Connected successfully");
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }
}
