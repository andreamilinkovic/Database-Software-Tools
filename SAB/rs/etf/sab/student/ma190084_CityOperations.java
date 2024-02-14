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
import rs.etf.sab.operations.CityOperations;

/**
 *
 * @author Andrea
 */
public class ma190084_CityOperations implements CityOperations{
    
    private Connection conn;
    
    public ma190084_CityOperations(){
        conn = ma190084_DB.getInstance().getConnection();
    }

    @Override
    public int createCity(String name) {
        String sql = "insert into City(Name) values(?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){
            
            ps.setString(1, name);
            
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next())
                    return rs.getInt(1);
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_CityOperations.class.getName()).log(Level.ALL.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }

    @Override
    public List<Integer> getCities() {
        List<Integer> cities = new ArrayList<Integer>();
        
        String sql = "select IdCit from City";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery();){
            
            while(rs.next())
                cities.add(rs.getInt("IdCit"));
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return (cities.size() == 0 ? null : cities);
    }

    @Override
    public int connectCities(int cityId1, int cityId2, int distance) {
        String sql1 = "select IdLin from Line where (IdCit1 = ? and IdCit2 = ?) or (IdCit1 = ? and IdCit2 = ?)";
        String sql2 = "insert into Line(IdCit1, IdCit2, Distance) values(?, ?, ?)";
        
        try (PreparedStatement ps1 = conn.prepareStatement(sql1);
             PreparedStatement ps2 = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);){
           
            ps1.setInt(1, cityId1);
            ps1.setInt(2, cityId2);
            ps1.setInt(3, cityId2);
            ps1.setInt(4, cityId1);
            
            try (ResultSet rs1 = ps1.executeQuery()){
                 
                if(rs1.next() == false) {// no line
                    ps2.setInt(1, cityId1);
                    ps2.setInt(2, cityId2);
                    ps2.setInt(3, distance);
                    
                    ps2.executeUpdate();                  
                    try (ResultSet rs2 = ps2.getGeneratedKeys()){
                        if(rs2.next())
                            return rs2.getInt(1);
                    } catch (SQLException ex) {
                       Logger.getLogger(ma190084_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
             
            } catch (SQLException ex) {
               Logger.getLogger(ma190084_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }

    @Override
    public List<Integer> getConnectedCities(int cityId) {
        List<Integer> cities = new ArrayList<Integer>();
        
        String sql1 = "select IdCit1 from Line where IdCit2 = ?";
        String sql2 = "select IdCit2 from Line where IdCit1 = ?";
        
        try (PreparedStatement ps1 = conn.prepareStatement(sql1);
             PreparedStatement ps2 = conn.prepareStatement(sql2);){
            
            ps1.setInt(1, cityId);
            ps2.setInt(1, cityId);
            
            try (ResultSet rs1 = ps1.executeQuery();
                 ResultSet rs2 = ps2.executeQuery();){
                
                while(rs1.next())
                    cities.add(rs1.getInt("IdCit1"));
                
                while(rs2.next())
                    cities.add(rs2.getInt("IdCit2"));
                
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return (cities.size() == 0 ? null : cities);
    }

    @Override
    public List<Integer> getShops(int cityId) {
        List<Integer> shops = new ArrayList<Integer>();
        
        String sql = "select IdSho from Shop where IdCit = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, cityId);
            
            try (ResultSet rs = ps.executeQuery();){
                while(rs.next())
                    shops.add(rs.getInt("IdSho"));
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            } 
            
        } catch (SQLException ex) {
                Logger.getLogger(ma190084_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return (shops.size() == 0 ? null : shops);
    }
    
}
