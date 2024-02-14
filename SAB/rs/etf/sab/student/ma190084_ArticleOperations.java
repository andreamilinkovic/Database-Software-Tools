/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.ArticleOperations;

/**
 *
 * @author Andrea
 */
public class ma190084_ArticleOperations implements ArticleOperations{
    
    private Connection conn;
    
    public ma190084_ArticleOperations(){
        conn = ma190084_DB.getInstance().getConnection();
    }

    @Override
    public int createArticle(int shopId, String articleName, int articlePrice) {
        String sql = "insert into Article(Name, Price, Amount, IdSho) values(?, ?, 0, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){
            
            ps.setString(1, articleName);
            ps.setInt(2, articlePrice);
            ps.setInt(3, shopId);
            
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys();){
                if(rs.next())
                    return rs.getInt(1);
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }
    
}
