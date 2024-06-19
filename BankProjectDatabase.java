/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Bank_Project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 *  @author Khululiwe
 */
public class BankProjectDatabase {
    
    private int accNumber=0;

    public int getAccNumber() {
        return accNumber;
    }

    public void setAccNumber(int accNumber) {
        this.accNumber = accNumber;
    }
    
    
    
    
    
    
    Connection con=null;
    public boolean connect(String url,String username,String password)
    { boolean flag=false;
        try{
        Class.forName("com.mysql.cj.jdbc.Driver");
        con=DriverManager.getConnection(url,username,password);
        if(!con.isClosed())
        {
            flag=true;
        }
        } catch (ClassNotFoundException ex) {
            System.out.println("Could not connect to database");
            flag=false;
        } catch (SQLException ex) {
            System.out.println("Information not available at the moment");                  
        }
        return flag;
    }
    
     
     
  public boolean accountCreation(String userName, String firstName, String lastName, double balance, String pin) {
      boolean success=true;
              try {
        LocalDate creationDate = LocalDate.now();

        if (balance < 20.00) {
            System.out.println("Your balance is below minimum");
            success= false; // Return false when balance is below minimum
        } else {
            String sql = "INSERT INTO bankaccounts (username, holderfirstname, holderlastname, balance, transactionid, pin, creationdate, closingdate) " +
                    "VALUES ('" + userName + "','" + firstName + "', '" + lastName + "', " + balance + ",  0 , '" + pin + "', '" + creationDate + "', 'yyyy-mm-dd')";

            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:4306/bankproject", "root", "");
            Statement p = con.createStatement();
          p.execute(sql); // Use boolean to check if the query executed successfully

            // Close the connection and statement after use
            p.close();
            con.close();

           // Return true if the query executed successfully
        }
    } catch (SQLException ex) {
        Logger.getLogger(BankProjectDatabase.class.getName()).log(Level.SEVERE, null, ex);
       success=false; // Return false in case of an exception
    }
        return success;
}

    public void bankTransfer(int firstAccount,int secondAccount,double amount)
    {
        withdrawMoney(amount,firstAccount);
        depositCash(amount,secondAccount);
        
    }
    
    public double withdrawMoney(double amount,int accountNumber)
    {
        double trans=amount;
        LocalDate creationDate = LocalDate.now();
        LocalTime creationTime = LocalTime.now();
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:4306/bankproject", "root", "");
            String selectSQL = "SELECT balance FROM bankaccounts where accountnumber="+accountNumber+"";
            PreparedStatement selectStatement = con.prepareStatement(selectSQL);
            ResultSet resultSet = selectStatement.executeQuery();
             if (resultSet.next()) {
                double x = resultSet.getDouble("balance");
                
                if(amount>x || amount<0)
                {
                    JOptionPane.showMessageDialog(null, "Do not have enough credit to withdraw amount",
                    "Error", JOptionPane.ERROR_MESSAGE);
                }else
                {
                 x-=amount;
                amount=x;
                String updateSQL = "UPDATE bankaccounts SET balance = ? where accountnumber="+accountNumber+"";
                PreparedStatement updateStatement = con.prepareStatement(updateSQL);
                JOptionPane.showMessageDialog (null, "Successfully withdrawn", "Cash Withdrawn", JOptionPane.INFORMATION_MESSAGE);
                updateStatement.setDouble(1, amount);
                updateStatement.executeUpdate();
                updateStatement.close();
                selectStatement.close();
            String sql = "INSERT INTO transaction (accountnumber, transactionname, amount, date, time) " +
            "VALUES ('" + accountNumber + "', 'Withdraw', " + trans + ", '" + creationDate + "', '" + creationTime + "')";
            Statement p = con.createStatement();
            p.execute(sql);
            
            // Close the connection and statement after use
            p.close();
            con.close();
                }
                
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(BankProjectDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        return amount;
        
        
    }
    
    public void depositCash(double amount,int accountNumber)
    {
        double trans=amount;
         LocalDate creationDate = LocalDate.now();
        LocalTime creationTime = LocalTime.now();
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:4306/bankproject", "root", "");
            String selectSQL = "SELECT balance FROM bankaccounts where accountnumber="+accountNumber+"";
            PreparedStatement selectStatement = con.prepareStatement(selectSQL);
            ResultSet resultSet = selectStatement.executeQuery();
             if(amount<=0)
                {
                     JOptionPane.showMessageDialog(null, "Deposit cannot be less than zero",
                "Error", JOptionPane.ERROR_MESSAGE);
                }else{
             if (resultSet.next()) {
                double x = resultSet.getDouble("balance");
                
               
                x+=amount;
                amount=x;
            }
            String updateSQL = "UPDATE bankaccounts SET balance = ? where accountnumber="+accountNumber+"";
            PreparedStatement updateStatement = con.prepareStatement(updateSQL);
            updateStatement.setDouble(1, amount);
            updateStatement.executeUpdate();
            JOptionPane.showMessageDialog (null, "Successfully deposited", "Cash Deposit", JOptionPane.INFORMATION_MESSAGE);
               
            updateStatement.close();
            selectStatement.close();
            String sql = "INSERT INTO transaction (accountnumber, transactionname, amount, date, time) " +
            "VALUES ('" + accountNumber + "', 'Deposit', " + trans + ", '" + creationDate + "', '" + creationTime + "')";
            Statement p = con.createStatement();
            p.execute(sql);
            
            // Close the connection and statement after use
            p.close();
             }
        } catch (SQLException ex) {
            Logger.getLogger(BankProjectDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
              
        
    }
    public void deleteAccount(int accountNumber)
    {
        try {
           LocalDate closingDate = LocalDate.now();
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:4306/bankproject", "root", "");
            String updateSQL = "Delete from bankaccounts where accountnumber="+accountNumber+"";
            PreparedStatement updateStatement = con.prepareStatement(updateSQL);
             
            updateStatement.executeUpdate();   
            updateStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(BankProjectDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
     public void profileUpdate(int accountNumber,String userName,String name,String lastname)
    {
        try {
           
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/bankproject", "root", "");
            String updateSQL = "UPDATE bankaccounts SET username = '"+userName+"',holderfirstname = '"+name+"',holderlastname = '"+lastname+"'where accountnumber="+accountNumber+"";
            PreparedStatement updateStatement = con.prepareStatement(updateSQL);
             
            updateStatement.executeUpdate();   
            updateStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(BankProjectDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
     public void changePin(int accountNumber,String newPin)
    {
        try {
           
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:4306/bankproject", "root", "");
            String updateSQL = "UPDATE bankaccounts SET pin = '"+newPin+"' where accountnumber="+accountNumber+"";
            PreparedStatement updateStatement = con.prepareStatement(updateSQL);
             
            updateStatement.executeUpdate();   
            updateStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(BankProjectDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
     
     
     public int getAccountId(String userName)
     {
         int getId=0;
        try {
            int id=0;
            
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:4306/bankproject", "root", "");
            String selectSQL = "SELECT accountnumber FROM bankaccounts where username='"+userName+"'";
            PreparedStatement selectStatement = con.prepareStatement(selectSQL);
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
               id = resultSet.getInt("accountnumber");
               
            }
            
            
             getId=id;     
        } catch (SQLException ex) {
            Logger.getLogger(BankProjectDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         return getId;
     }
     
     
     public void setBalance(int accNumber,JLabel balance)
     {
          try {
            // TODO add your handling code here:
            Connection con = null;
            con = DriverManager.getConnection("jdbc:mysql://localhost:4306/bankproject", "root", "");
            PreparedStatement os = con.prepareStatement("select balance from bankaccounts where accountnumber=?");
            os.setInt(1, accNumber);
            ResultSet res = os.executeQuery();

            if (res.next()) {
                
                balance.setText(res.getString("balance"));
                
                        }
        } catch (SQLException ex) {
           Logger.getLogger(BankProjectDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
     
    public void displayUsername(JComboBox<String> dropdown) {
    if (!isDataLoaded) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:4306/bankproject", "root", "");
            String selectSQL = "SELECT username FROM bankaccounts";
            PreparedStatement selectStatement = con.prepareStatement(selectSQL);
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                dropdown.addItem(resultSet.getString("username"));
            }

            if (resultSet != null) {
                resultSet.close();
            }
            if (selectStatement != null) {
                selectStatement.close();
            }
            if (con != null) {
                con.close();
            }

            // Set the flag to true to indicate that data has been loaded
            isDataLoaded = true;
        } catch (SQLException ex) {
            Logger.getLogger(BankProjectDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
    
    

// Declare the isDataLoaded flag in your class
private boolean isDataLoaded = false;

public void transactions(int accountNumber, DefaultTableModel tableModel) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:4306/bankproject", "root", "");
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT transactionid, transactionname, amount, date, time FROM transaction where accountnumber=" + accountNumber);

            tableModel.setRowCount(0); // Clear the existing data

            while (resultSet.next()) {
                int id = resultSet.getInt("transactionid");
                String name = resultSet.getString("transactionname");
                double amount = resultSet.getDouble("amount");
                String date = resultSet.getString("date");
                String time = resultSet.getString("time");

                // Add data to the DefaultTableModel
                tableModel.addRow(new Object[]{id, name, amount, date, time});
            }

            resultSet.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

public static boolean loginToAccount(String username, String password) {
    try {
        // Connect to the SQL database file
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:4306/bankproject", "root", "");

        // Prepare the SQL statement to check if the user exists
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM bankaccounts WHERE username = ? AND pin = ?");
        stmt.setString(1, username);
        stmt.setString(2, password);

        // Execute the SQL statement and get the result set
        ResultSet rs = stmt.executeQuery();

        // Check if the result set has any rows
        boolean userExists = rs.next();

        // Close the database connection and return the result
        conn.close();
        return userExists; // Return whether the user exists
    } catch (SQLException e) {
        // Log the error and return false
        System.err.println("Error checking login: " + e.getMessage());
        return false;
    }
}


}
