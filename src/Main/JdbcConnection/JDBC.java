package Main.JdbcConnection;

import java.sql.Connection;
import java.sql.DriverManager;

public class JDBC {

    public static Connection databaseConnect() {
        Connection dbConnection = null;
        try {
            System.out.println("");
            Class.forName("org.postgresql.Driver");
            dbConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/AlchemDB",
                    "postgres", "123456");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbConnection;
    }

}
