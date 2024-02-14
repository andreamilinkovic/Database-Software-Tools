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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.ShopOperations;

/**
 *
 * @author Andrea
 */
public class ma190084_ShopOperations implements ShopOperations{
    
    private Connection conn;
    
    public ma190084_ShopOperations(){
        conn = ma190084_DB.getInstance().getConnection();
    }

    @Override
    public int createShop(String name, String cityName) {
        String sql1 = "select IdCit from City where Name = ?";
        String sql2 = "insert into Shop(Name, Discount, IdCit) values(?, 0, ?)";
        
        try (PreparedStatement ps1 = conn.prepareStatement(sql1);
             PreparedStatement ps2 = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);){
            
            ps1.setString(1, cityName);
            
            try(ResultSet rs1 = ps1.executeQuery()){
                
                if(rs1.next()){
                    int idCit = rs1.getInt("IdCit");
                    
                    ps2.setString(1, name);
                    ps2.setInt(2, idCit);
                    
                    ps2.executeUpdate();
                    try (ResultSet rs2 = ps2.getGeneratedKeys();){
                        if(rs2.next())
                            return rs2.getInt(1);
                    } catch (SQLException ex) {
                        Logger.getLogger(ma190084_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                    
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }

    @Override
    public int setCity(int shopId, String cityName) {
        String sql1 = "select IdCit from City where Name = ?";
        String sql2 = "update Shop set IdCit = ? where IdSho = ?";
        
        try (PreparedStatement ps1 = conn.prepareStatement(sql1);
             PreparedStatement ps2 = conn.prepareStatement(sql2);){
            
            ps1.setString(1, cityName);
            
            try(ResultSet rs1 = ps1.executeQuery()){
                if(rs1.next()){
                    int idCit = rs1.getInt("IdCit");
                    
                    ps2.setInt(1, idCit);
                    ps2.setInt(2, shopId);
                    
                    if(ps2.executeUpdate() > 0)
                        return 1;
                }
                    
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }

    @Override
    public int getCity(int shopId) {
        String sql = "select IdCit from Shop where IdSho = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            ps.setInt(1, shopId);
            
            try (ResultSet rs = ps.executeQuery();){
                if(rs.next())
                    return rs.getInt("IdCit");
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }

    @Override
    public int setDiscount(int shopId, int discountPercentage) {
        String sql = "update Shop set Discount = ? where IdSho = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, discountPercentage);
            ps.setInt(2, shopId);

            if(ps.executeUpdate() > 0)
                return 1;

        } catch (SQLException ex) {
            Logger.getLogger(ma190084_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }

    @Override
    public int increaseArticleCount(int articleId, int increment) {
        String sql1 = "select Amount from Article where IdArt = ?"; 
        String sql2 = "update Article set Amount = ? where IdArt = ?";
        
        int new_value = increment;
        
        try (PreparedStatement ps1 = conn.prepareStatement(sql1);
             PreparedStatement ps2 = conn.prepareStatement(sql2);){
            
            ps1.setInt(1, articleId);
            
            try (ResultSet rs1 = ps1.executeQuery();){
                if(rs1.next())
                    new_value = new_value + rs1.getInt("Amount");
                
                ps2.setInt(1, new_value);
                ps2.setInt(2, articleId);
                
                if(ps2.executeUpdate() > 0)
                    return new_value;
            
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
                    
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }

    @Override
    public int getArticleCount(int articleId) {
        String sql = "select Amount from Article where IdArt = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, articleId);
            
            try (ResultSet rs = ps.executeQuery();){
                if(rs.next())
                    return rs.getInt("Amount");
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }

    @Override
    public List<Integer> getArticles(int shopId) {
        List<Integer> articles = new ArrayList<Integer>();
        
        String sql = "select IdArt from Article where IdSho = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, shopId);
            
            try (ResultSet rs = ps.executeQuery();){
                while(rs.next())
                    articles.add(rs.getInt("IdArt"));

            } catch (SQLException ex) {
                Logger.getLogger(ma190084_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return (articles.size() == 0 ? null : articles);
    }

    @Override
    public int getDiscount(int shopId) {
        String sql = "select Discount from Shop where IdSho = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, shopId);
            
            try (ResultSet rs = ps.executeQuery();){
                if(rs.next())
                    return rs.getInt("Discount");
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }
    
}
