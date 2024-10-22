package domain.api;

import domain.connection.PetConnection;


import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.Statement;

public class PetController {

    PetConnection connection =  new PetConnection();
    Connection con;
    ResultSet result;
    public void displayAllPets(){
        try{
            connection.connect();
            String query="SELECT * FROM  pets";
            Statement statement = con.createStatement();
            result = statement.executeQuery(query);

            while (result.next()){
            int Id=result.getInt("id");
            String petName=result.getString("name");
            int typeId = result.getInt("Type_id");

            System.out.println(Id+":"+petName+":"+typeId);
            
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
