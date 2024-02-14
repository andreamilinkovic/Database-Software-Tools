/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrea
 */
public class ma190084_DB {

    private static final String username = "sa";
    private static final String password = "123";
    private static final String database = "OnlineShop02";
    private static final int port = 1433;
    private static final String server = "localhost";

    private static final String connectionUrl
            = "jdbc:sqlserver://" + server + ":" + port
            + ";databaseName=" + database
            + ";encrypt=true"
            + ";trustServerCertificate=true";
    
     /*private static final String connectionUrl = 
             "jdbc:sqlserver://" + server + ":" + port + ";databaseName=" + database + ";user=" + username + ";password=" + password;*/

   
    private Connection connection;
    private static ma190084_DB db = null;
   
    private ma190084_DB(){
        try {
            connection = DriverManager.getConnection(connectionUrl, username, password);
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    public static ma190084_DB getInstance(){
        if(db == null){
            db = new ma190084_DB();
        }
        return db;
    }
    
    public Connection getConnection(){
        return connection;
    }
}
