package com.trinitcore.sql;

import com.trinitcore.sql.queryObjects.QueryObject;
import com.trinitcore.sql.queryObjects.returnableQueries.Select;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cormacpjkinsella on 10/9/16.
 */
public class SQL {

    public String query;
    public List<Object> parameters = new ArrayList<Object>();
    public QueryObject queryObject;

    public ResultSet resultSet = null;
    public PreparedStatement preparedStmt = null;
    public static Configuration configuration;

    public static void setConfiguration(Configuration extendedConfiguration) {
        configuration = extendedConfiguration;
    }

    public SQL() {

    }

    public SQL(String query, List<Object> parameters) {
        this.query = query;
        this.parameters = parameters;
    }

    public SQL(Select selectQuery) {
        this.query = selectQuery.query;
        this.queryObject = selectQuery;

        System.out.println(query);
    }

    public SQL query(boolean returnable) {
        try {
            this.preparedStmt = configuration.connection.prepareStatement(query);
            int count = 1;
            for (Object value : this.parameters){
                this.preparedStmt.setObject(count,value);
                count++;
            }
            if (returnable) this.resultSet = this.preparedStmt.executeQuery();
            else this.preparedStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public SQL close() {
        try {
            if (preparedStmt.isClosed()) return this;
            this.preparedStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }


}
