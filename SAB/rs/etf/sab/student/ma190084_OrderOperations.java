/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.OrderOperations;
import rs.etf.sab.operations.ShopOperations;
import student.dijkstra.Dijkstra;

/**
 *
 * @author Andrea
 */
public class ma190084_OrderOperations implements OrderOperations{
    
    private Connection conn;
    
    public ma190084_OrderOperations(){
        conn = ma190084_DB.getInstance().getConnection();
    }

    @Override
    public int addArticle(int orderId, int articleId, int count) {
        String sql1 = "select Amount from Article where IdArt = ? and Amount >= ?";
        String sql2 = "select IdIte, Amount from Item where IdOrd = ? and IdArt = ?";
        String sql3 = "insert into Item(Amount, IdOrd, IdArt) values(?, ?, ?)";
        String sql4 = "update Item set Amount = ? where IdIte = ?";
        
        int IdIte = -1;
                    
        try (PreparedStatement ps1 = conn.prepareStatement(sql1);
             PreparedStatement ps2 = conn.prepareStatement(sql2);
             PreparedStatement ps3 = conn.prepareStatement(sql3, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement ps4 = conn.prepareStatement(sql4);){
            
            ps1.setInt(1, articleId);
            ps1.setInt(2, count);
            
            ps2.setInt(1, orderId);
            ps2.setInt(2, articleId);

            try (ResultSet rs1 = ps1.executeQuery();
                 ResultSet rs2 = ps2.executeQuery();){
                if(rs1.next()){
                    //int new_amount = rs1.getInt("Amount") - count;
                    
                    if(rs2.next()){ //already exists
                        IdIte = rs2.getInt("IdIte");
                        int amount = rs2.getInt("Amount");
                        ps4.setInt(1, amount + count);
                        ps4.setInt(2, IdIte);
                        ps4.executeUpdate();
                    } else {
                        ps3.setInt(1, count);
                        ps3.setInt(2, orderId);
                        ps3.setInt(3, articleId);
                        if(ps3.executeUpdate() > 0){
                            try (ResultSet rs3 = ps3.getGeneratedKeys()){
                                if(rs3.next())
                                    IdIte = rs3.getInt(1);
                            } catch (SQLException ex) {
                                Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    // update Article
                    ShopOperations shop = new ma190084_ShopOperations();
                    shop.increaseArticleCount(articleId, -count);
                }
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return IdIte;
    }

    @Override
    public int removeArticle(int orderId, int articleId) {
        String sql1 = "select Amount from Item where IdOrd = ? and IdArt = ?";
        String sql2 = "delete from Item where IdOrd = ? and IdArt = ?";
        
        try (PreparedStatement ps1 = conn.prepareStatement(sql1);
             PreparedStatement ps2 = conn.prepareStatement(sql2);){
            
            ps1.setInt(1, orderId);
            ps1.setInt(2, articleId);
            
            ps2.setInt(1, orderId);
            ps2.setInt(2, articleId);
            
            try (ResultSet rs1 = ps1.executeQuery();){
                if(rs1.next()){
                    int amount = rs1.getInt("Amount");
                    if(ps2.executeUpdate() > 0) {
                        // update Article
                        ShopOperations shop = new ma190084_ShopOperations();
                        shop.increaseArticleCount(articleId, amount);
                        return 1;
                    }
                }                    
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }

    @Override
    public List<Integer> getItems(int orderId) {
        List<Integer> items = new ArrayList<Integer>();
        
        String sql = "select IdIte from Item where IdOrd = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, orderId);
            
            try (ResultSet rs = ps.executeQuery();){
                while(rs.next())
                    items.add(rs.getInt("IdIte"));
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return (items.size() == 0 ? null : items);
    }

    @Override
    public int completeOrder(int orderId) {
        String sql1 = "exec dbo.SP_FINAL_PRICE ?";
        String sql2 = "update [Order] set [State] = ?, SentTime = ? where IdOrd = ?";
        
        try (CallableStatement cs2 = conn.prepareCall(sql1);
             PreparedStatement ps1 = conn.prepareStatement(sql2);
             PreparedStatement ps = conn.prepareStatement("update [Transaction] set ExecutionTime = ? where IdOrd = ?")){
            
            cs2.setInt(1, orderId);
            cs2.execute();
            
            ps1.setString(1, "sent");
            ps1.setDate(2, new Date(ma190084_GeneralOperations.curr_time.getTimeInMillis()));
            ps1.setInt(3, orderId);
            ps1.executeUpdate();
            
            ps.setDate(1, new Date(ma190084_GeneralOperations.curr_time.getTimeInMillis()));
            ps.setInt(2, orderId);
            ps.executeUpdate();
            
            // ReceivedTime callucation TODO
            
            // destination_city
            String sql3 = "select B.IdCit from [Order] O join Buyer B on O.IdBuy = B.IdBuy where IdOrd = ?";
            int destination_city = -1;
            try (PreparedStatement ps3 = conn.prepareStatement(sql3);){
                ps3.setInt(1, orderId);
                try (ResultSet rs3 = ps3.executeQuery()){
                    if(rs3.next()) destination_city = rs3.getInt("IdCit");
                } catch (SQLException ex) {
                    Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // get num of cities
            int num_of_cities = 0;
            try (Statement stmt4 = conn.createStatement();
                 ResultSet rs4 = stmt4.executeQuery("select count(IdCit) from City")){
                    if (rs4.next()) num_of_cities = rs4.getInt(1);
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // create graph
            ArrayList<ArrayList<Dijkstra.AdjListNode> > graph  = new ArrayList<>();
            for (int i = 0; i < num_of_cities; i++) {
                graph.add(new ArrayList<>());
            }
            
            // fill graph
            try (Statement stmt5 = conn.createStatement();
                 ResultSet rs5 = stmt5.executeQuery("select IdCit1, IdCit2, Distance from Line");){
                while(rs5.next()){
                    int idCit1 = rs5.getInt("IdCit1");
                    int idCit2 = rs5.getInt("IdCit2");
                    int dist = rs5.getInt("Distance");
                    
                    graph.get(idCit1).add(new Dijkstra.AdjListNode(idCit2, dist));
                    graph.get(idCit2).add(new Dijkstra.AdjListNode(idCit1, dist));
                }
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            ArrayList<int[]> list = Dijkstra.dijkstra(num_of_cities, graph, destination_city);
            int[] distance = list.get(0);
            int[] parents = list.get(1);            
            
            // get middle city
            int middle_city = -1;
            try (Statement stmt6 = conn.createStatement();
                ResultSet rs6 = stmt6.executeQuery("select IdCit from Shop");){
                int min_dist = Integer.MAX_VALUE;
                int idCit;
                while(rs6.next()){
                    idCit = rs6.getInt(1);
                    if(distance[idCit] < min_dist){
                        middle_city = idCit;
                        min_dist = distance[idCit];
                    }
                }                
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // get path
            ArrayList<Integer> path = new ArrayList<>();
            Dijkstra.getPath(middle_city, parents, path);
            
            // get the furthest shop distance
            String sql7 = "select IdCit from [Item] I join Article A on I.IdArt = A.IdArt join Shop S on A.IdSho = S.IdSho where IdOrd = ?";
            int furthest_distance = -1;
            try (PreparedStatement ps7 = conn.prepareStatement(sql7);){
                ps7.setInt(1, orderId);
                try (ResultSet rs7 = ps7.executeQuery();){
                    while(rs7.next()){
                        int idCit = rs7.getInt("IdCit");
                        if(distance[idCit] > furthest_distance){
                            furthest_distance = distance[idCit];
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // insert into path
            String sql8 = "insert into [Path](IdOrd, IdCit, NumDays) values(?, ?, ?)";
            try (PreparedStatement ps8 = conn.prepareStatement(sql8);){
                for(int i =  path.size() - 1; i > 0; i--){
                    int idCit = path.get(i);
                    int days = furthest_distance - distance[parents[idCit]];
                    ps8.setInt(1, orderId);
                    ps8.setInt(2, idCit);
                    ps8.setInt(3, days);
                    ps8.executeUpdate();
                }
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // set location
            String sql9 = "update [Order] set Location = ? where IdOrd = ?";
            try (PreparedStatement ps9 = conn.prepareStatement(sql9);){
                ps9.setInt(1, middle_city);
                ps9.setInt(2, orderId);
                ps9.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }

    @Override
    public BigDecimal getFinalPrice(int orderId) {
        String sql = "select FinalPrice from [Order] where IdOrd = ? and State <> 'created'";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, orderId);
            
            try (ResultSet rs = ps.executeQuery();){
                if(rs.next())
                    return rs.getBigDecimal("FinalPrice");
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new BigDecimal("-1");
    }

    @Override
    public BigDecimal getDiscountSum(int orderId) {
        String sql = "select DiscountSum from [Order] where IdOrd = ? and State <> 'created'";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, orderId);
            
            try (ResultSet rs = ps.executeQuery();){
                if(rs.next())
                    return rs.getBigDecimal("DiscountSum");
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new BigDecimal("-1");
    }

    @Override
    public String getState(int orderId) {
        String sql = "select State from [Order] where IdOrd = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, orderId);
            
            try (ResultSet rs = ps.executeQuery();){
                if(rs.next())
                    return rs.getString("State");
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    @Override
    public Calendar getSentTime(int orderId) {
        String sql = "select SentTime from [Order] where IdOrd = ?";
        
        Calendar time = Calendar.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, orderId);
          
            try (ResultSet rs = ps.executeQuery();){
                if(rs.next()){
                    Date date = rs.getDate("SentTime");
                    if (date != null) {
			time.setTimeInMillis(date.getTime());
                        return time;
                    }
                }                
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    @Override
    public Calendar getRecievedTime(int orderId) {
        String sql = "select ReceivedTime from [Order] where IdOrd = ?";
        
        Calendar time = Calendar.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, orderId);
          
            try (ResultSet rs = ps.executeQuery();){
                if(rs.next()){
                    Date date = rs.getDate("ReceivedTime");
                    if (date != null) {
			time.setTimeInMillis(date.getTime());
                        return time;
                    }
                }                
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    @Override
    public int getBuyer(int orderId) {
        String sql = "select IdBuy from [Order] where IdOrd = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, orderId);
            
            try (ResultSet rs = ps.executeQuery();){
                if(rs.next())
                    return rs.getInt("IdBuy");
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }

    @Override
    public int getLocation(int orderId) {
        String sql = "select Location from [Order] where IdOrd = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            
            ps.setInt(1, orderId);
            
            try (ResultSet rs = ps.executeQuery();){
                if(rs.next())
                    return rs.getInt("Location");
            } catch (SQLException ex) {
                Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ma190084_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }
    
}
