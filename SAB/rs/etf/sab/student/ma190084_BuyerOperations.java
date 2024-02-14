/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.BuyerOperations;

/**
 *
 * @author Andrea
 */
public class ma190084_BuyerOperations implements BuyerOperations{
    
    private Connection conn;
    
    public ma190084_BuyerOperations(){
        conn = ma190084_DB.getInstance().getConnection();
    }

    @Override
    public int createBuyer(String name, int cityId) {
        String sql = "insert into Buyer(Name, Credit, IdCit) values(?, 0, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){
            
            ps.setString(1, name);
            ps.setInt(2, cityId);
            
            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys();){
                if(rs.next())
                    return rs.getInt(1);
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }

    @Override
    public int setCity(int buyerId, int cityId) {
        String sql = "update Buyer set IdCit = ? where IdBuy = ?"; 
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, cityId);
            ps.setInt(2, buyerId);
            
            if(ps.executeUpdate() > 0)
                return 1;
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }

    @Override
    public int getCity(int buyerId) {
        String sql = "select IdCit from Buyer where IdBuy = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, buyerId);
            
            try (ResultSet rs = ps.executeQuery();){
                if(rs.next())
                    return rs.getInt("IdCit");
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }

    @Override
    public BigDecimal increaseCredit(int buyerId, BigDecimal credit) {
        String sql1 = "select Credit from Buyer where IdBuy = ?"; 
        String sql2 = "update Buyer set Credit = ? where IdBuy = ?";
        
        BigDecimal new_value = credit;
        
        try (PreparedStatement ps1 = conn.prepareStatement(sql1);
             PreparedStatement ps2 = conn.prepareStatement(sql2);){
            
            ps1.setInt(1, buyerId);
            
            try (ResultSet rs1 = ps1.executeQuery();){
                if(rs1.next())
                    new_value = new_value.add(rs1.getBigDecimal("Credit"));
                
                ps2.setBigDecimal(1, new_value);
                ps2.setInt(2, buyerId);
                
                if(ps2.executeUpdate() > 0)
                    return new_value;
            
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
                    
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    @Override
    public int createOrder(int buyerId) {
        String sql = "insert into [Order](IdBuy, State, Location) values(?, 'created', -1)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){
            
            ps.setInt(1, buyerId);
            
            ps.executeUpdate();                
            try(ResultSet rs = ps.getGeneratedKeys();){
                if(rs.next())
                    return rs.getInt(1);
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }

    @Override
    public List<Integer> getOrders(int buyerId) {
        List<Integer> orders = new ArrayList<Integer>();
        
        String sql = "select IdOrd from [Order] where IdBuy = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, buyerId);
            
            try (ResultSet rs = ps.executeQuery();){
                while(rs.next())
                    orders.add(rs.getInt("IdOrd"));
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return (orders.size() == 0 ? null : orders);
    }

    @Override
    public BigDecimal getCredit(int buyerId) {
        String sql = "select Credit from Buyer where IdBuy = ?"; 
         
        BigDecimal credit = new BigDecimal("0");
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, buyerId);
            
            try (ResultSet rs = ps.executeQuery();){
                if(rs.next()){
                    credit = rs.getBigDecimal("Credit");
                    return credit;
                }
            
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
}
