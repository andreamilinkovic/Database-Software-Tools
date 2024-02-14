/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.TransactionOperations;

/**
 *
 * @author Andrea
 */
public class ma190084_TransactionOperations implements TransactionOperations{
    
    private Connection conn;
    
    public ma190084_TransactionOperations(){
        conn = ma190084_DB.getInstance().getConnection();
    }

    @Override
    public BigDecimal getBuyerTransactionsAmmount(int buyerId) {
        String sql = "select sum(Amount) from [Transaction] where IdBuy = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, buyerId);
            
            try(ResultSet rs = ps.executeQuery();){
                if(rs.next() && rs.getBigDecimal(1) != null)
                    return rs.getBigDecimal(1);
                else
                    return new BigDecimal("0").setScale(3);
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new BigDecimal("-1").setScale(3);
    }

    @Override
    public BigDecimal getShopTransactionsAmmount(int shopId) {
        String sql = "select sum(SP.Amount) from Shop_Profit SP join [Order] O on SP.IdOrd = O.IdOrd where SP.IdSho = ? and O.[State] = 'arrived'";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, shopId);
            
            try(ResultSet rs = ps.executeQuery();){
                if(rs.next() && rs.getBigDecimal(1) != null)
                    return rs.getBigDecimal(1).setScale(3);
                else
                    return new BigDecimal("0").setScale(3);
                
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new BigDecimal("-1").setScale(3);
    }

    @Override
    public List<Integer> getTransationsForBuyer(int buyerId) {
        List<Integer> transactions = new ArrayList<Integer>();
        
        String sql = "select IdTra from [Transaction] where IdBuy = ? ";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, buyerId);
            
            try (ResultSet rs = ps.executeQuery();){
                while(rs.next())
                    transactions.add(rs.getInt("IdTra"));
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return (transactions.size() == 0 ? null : transactions);
    }

    @Override
    public int getTransactionForBuyersOrder(int orderId) {        
        String sql = "select IdTra from [Transaction] where IdOrd = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, orderId);
            
            try (ResultSet rs = ps.executeQuery();){
                if(rs.next())
                    return rs.getInt("IdTra");
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }

    @Override
    public int getTransactionForShopAndOrder(int orderId, int shopId) {
        String sql = "select IdTra from Shop_Profit SP join [Order] O on SP.IdOrd = O.IdOrd where SP.IdOrd = ? and SP.IdSho = ? and O.[State] = 'arrived'";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, orderId);
            ps.setInt(2, shopId);
            
            try (ResultSet rs = ps.executeQuery();){
                if(rs.next())
                    return rs.getInt("IdTra");
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }

    @Override
    public List<Integer> getTransationsForShop(int shopId) {
        List<Integer> transactions = new ArrayList<Integer>();
        
        String sql = "select IdTra from Shop_Profit SP join [Order] O on SP.IdOrd = O.IdOrd where IdSho = ? and O.[State] = 'arrived'";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, shopId);
            
            try (ResultSet rs = ps.executeQuery();){
                while(rs.next())
                    transactions.add(rs.getInt("IdTra"));
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return (transactions.size() == 0 ? null : transactions);
    }

    @Override
    public Calendar getTimeOfExecution(int transactionId) {
        String sql = "select ExecutionTime from [Transaction] where IdTra = ?";
        
        Calendar time = Calendar.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, transactionId);
          
            try (ResultSet rs = ps.executeQuery();){
                if(rs.next()){
                    Date date = rs.getDate("ExecutionTime");
                    if (date != null) {
			time.setTimeInMillis(date.getTime());
                        return time;
                    }
                }                
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    @Override
    public BigDecimal getAmmountThatBuyerPayedForOrder(int orderId) {
        String sql = "select Amount from [Transaction] where IdOrd = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, orderId);
            
            try(ResultSet rs = ps.executeQuery();){
                if(rs.next() && rs.getBigDecimal(1) != null)
                    return rs.getBigDecimal(1);
                else
                    return new BigDecimal("0").setScale(3);
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new BigDecimal("-1").setScale(3);
    }

    @Override
    public BigDecimal getAmmountThatShopRecievedForOrder(int shopId, int orderId) {
        String sql = "select Amount from Shop_Profit SP join [Order] O on SP.IdOrd = O.IdOrd where IdOrd = ? and IdSho = ? and O.[State] = 'arrived'";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, orderId);
            ps.setInt(2, shopId);
            
            try(ResultSet rs = ps.executeQuery();){
                if(rs.next() && rs.getBigDecimal(1) != null)
                    return rs.getBigDecimal(1);
                else
                    return new BigDecimal("0").setScale(3);
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new BigDecimal("-1").setScale(3);
    }

    @Override
    public BigDecimal getTransactionAmount(int transactionId) {
        String sql = "select Amount from [Transaction] where IdTra = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, transactionId);
            
            try(ResultSet rs = ps.executeQuery();){
                if(rs.next() && rs.getBigDecimal(1) != null)
                    return rs.getBigDecimal(1);
                else
                    return new BigDecimal("0").setScale(3);
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new BigDecimal("-1").setScale(3);
    }

    @Override
    public BigDecimal getSystemProfit() {
        String sql = "select sum(Amount) from System_Profit SP join [Order] O on SP.IdOrd = O.IdOrd "
                + "where IdSys = (select IdSys from [System] where Name = 'System') and O.[State] = 'arrived'";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            try(ResultSet rs = ps.executeQuery();){
                if(rs.next() && rs.getBigDecimal(1) != null)
                    return rs.getBigDecimal(1);
                else
                    return new BigDecimal("0").setScale(3);
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new BigDecimal("-1").setScale(3);
    }
    
}
