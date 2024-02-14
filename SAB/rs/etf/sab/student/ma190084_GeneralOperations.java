/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.GeneralOperations;

/**
 *
 * @author Andrea
 */
public class ma190084_GeneralOperations implements GeneralOperations{
    
    private Connection conn;
    public static Calendar curr_time;
    
    public ma190084_GeneralOperations(){
        conn = ma190084_DB.getInstance().getConnection();
    }

    @Override
    public void setInitialTime(Calendar time) {
        curr_time = Calendar.getInstance();
        curr_time.clear();
        curr_time.set(time.get(Calendar.YEAR), time.get(Calendar.MONTH), time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public Calendar time(int days) {
        curr_time.add(Calendar.DATE, days);
        int year = curr_time.get(Calendar.YEAR);
        int month = curr_time.get(Calendar.MONTH);
        int day = curr_time.get(Calendar.DAY_OF_MONTH);

        // RECEIVE ORDER
        String sql1 = "select IdOrd from [Order] where [State] = 'sent'";
        try (Statement stmt1 = conn.createStatement();
            ResultSet rs1 = stmt1.executeQuery(sql1);){
            int idOrd = -1;
            while(rs1.next()){
                idOrd = rs1.getInt("IdOrd");
                String sql2 = "select top 1 P.IdCit from [Path] P join [Order] O on P.IdOrd = O.IdOrd "
                + "where datediff(day, O.SentTime, (select datefromparts(?, ?, ?))) < P.NumDays and P.IdOrd = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(sql2);){
                    ps2.setInt(1, year);
                    ps2.setInt(2, month + 1);
                    ps2.setInt(3, day);
                    ps2.setInt(4, idOrd);
                    try (ResultSet rs2 = ps2.executeQuery();){
                        if(rs2.next()){
                            // change location
                            int idCit = rs2.getInt("IdCit");
                            String sql3 = "update [Order] set Location = ?";
                            try (PreparedStatement ps3 = conn.prepareStatement(sql3);){
                                ps3.setInt(1, idCit);
                            ps3.executeUpdate();
                            } catch (SQLException ex) {
                                Logger.getLogger(ma190084_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            // change location and set ReceivedTime
                            // get dest_city
                            int dest_city = -1;
                            String sql4 = "select B.IdCit from [Order] O join Buyer B on O.IdBuy = B.IdBuy where IdOrd = ?";
                            try (PreparedStatement ps4 = conn.prepareStatement(sql4);){
                                ps4.setInt(1, idOrd);
                                try (ResultSet rs4 = ps4.executeQuery();){
                                    if(rs4.next())
                                        dest_city = rs4.getInt("IdCit");
                                } catch (SQLException ex) {
                                    Logger.getLogger(ma190084_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(ma190084_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            // change location
                            String sql5 = "update [Order] set Location = ?";
                            try (PreparedStatement ps5 = conn.prepareStatement(sql5);){
                                ps5.setInt(1, dest_city);
                                ps5.executeUpdate();
                            } catch (SQLException ex) {
                                Logger.getLogger(ma190084_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            // set ReceivedTime
                            String sql6 = "select top 1 NumDays from [Path] where IdOrd = ? order by IdPat desc";
                            try (PreparedStatement ps6 = conn.prepareStatement(sql6);){
                                ps6.setInt(1, idOrd);
                                try (ResultSet rs6 = ps6.executeQuery()){
                                    if(rs6.next()){
                                        int dist = rs6.getInt("NumDays");
                                        Calendar sentTime = Calendar.getInstance();
                                        sentTime.clear();
                                        try (PreparedStatement ps9 = conn.prepareStatement("select SentTime from [Order] where IdOrd = ?");){
                                            ps9.setInt(1, idOrd);
                                            try (ResultSet rs9 = ps9.executeQuery();){
                                                if(rs9.next()) sentTime.setTime(rs9.getDate("SentTime"));
                                            } catch (SQLException ex) {
                                                Logger.getLogger(ma190084_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        } catch (SQLException ex) {
                                            Logger.getLogger(ma190084_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        sentTime.add(Calendar.DATE, dist);
                                        // set ReceivedTime
                                        String sql7 = "update [Order] set ReceivedTime = ?, [State] = 'arrived' where IdOrd = ?";
                                        try (PreparedStatement ps7 = conn.prepareStatement(sql7);){
                                            ps7.setDate(1, new Date(sentTime.getTimeInMillis()));
                                            ps7.setInt(2, idOrd);
                                            ps7.executeUpdate();
                                        } catch (SQLException ex) {
                                            Logger.getLogger(ma190084_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        
                                        // set ExecutionTime
                                        String sql8 = "update [Transaction] set ExecutionTime = ? where IdOrd = ?";
                                        try (PreparedStatement ps8 = conn.prepareStatement(sql8);){
                                            ps8.setDate(1, new Date(sentTime.getTimeInMillis()));
                                            ps8.setInt(2, idOrd);
                                            ps8.executeUpdate();
                                        } catch (SQLException ex) {
                                            Logger.getLogger(ma190084_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                } catch (SQLException ex) {
                                    Logger.getLogger(ma190084_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(ma190084_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(ma190084_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
                    }                   
                } catch (SQLException ex) {
                    Logger.getLogger(ma190084_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(ma190084_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return curr_time;
    }

    @Override
    public Calendar getCurrentTime() {
        return curr_time;
    }

    @Override
    public void eraseAll() {
        String sql1 = "delete from Line";
        String sql2 = "delete from Item";
        String sql3 = "delete from System_Profit";
	String sql4 = "delete from Shop_Profit";
        String sql5 = "delete from [Transaction]";
        String sql6 = "delete from [System]";
        String sql7 = "delete from Article";
        String sql8 = "delete from [Path]";
        String sql9 = "delete from [Order]";
        String sql10 = "delete from Buyer";
	String sql11 = "delete from Shop";
        String sql12 = "delete from City";

	try (Statement stmt = conn.createStatement();
             CallableStatement call = conn.prepareCall("exec dbo.SP_RESEED_ID");) {

            stmt.addBatch(sql1);
            stmt.addBatch(sql2);
            stmt.addBatch(sql3);
            stmt.addBatch(sql4);
            stmt.addBatch(sql5);
            stmt.addBatch(sql6);
            stmt.addBatch(sql7);
            stmt.addBatch(sql8);
            stmt.addBatch(sql9);
            stmt.addBatch(sql10);
            stmt.addBatch(sql11);
            stmt.addBatch(sql12);

            int[] n = stmt.executeBatch();
            
            call.execute();
            
        } catch (SQLException e) {
            e.printStackTrace();
	}
    }
    
}
