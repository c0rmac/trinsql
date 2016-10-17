package config;

import com.trinitcore.sql.Configuration;
import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by cormacpjkinsella on 10/9/16.
 */
public class Sqlite extends Configuration{
    public boolean release = false;
    public String name = "cormac_hackbook";

    public Sqlite() {
        super(true);
        SQLiteDataSource dataSource = new SQLiteDataSource();
        if (release){
            dataSource.setUrl("jdbc:sqlite:/usr/share/tomcat8/webapps/db/identifier.sqlite");
        }else {
            if (name.equals("oisin"))
                dataSource.setUrl("jdbc:sqlite:/home/oisin/Desktop/Programming/Git Directories/project-trade-server/web/identifier.sqlite");
            else if(name.equals("cormac"))
                dataSource.setUrl("jdbc:sqlite:C:\\Users\\Cormac\\Documents\\Projects\\project-trade-server\\web\\identifier.sqlite");
            else if(name.equals("cormac_hackbook")){
                dataSource.setUrl("jdbc:sqlite:/Users/cormacpjkinsella/Documents/Projects/project-trade-server/web/identifier.sqlite");
            }
        }
        try {
            setConnection(dataSource.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
