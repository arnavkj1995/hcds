import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.*;      
import java.awt.event.*; 
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.example.tutorial.AddressBookProtos.Person;
import com.example.tutorial.AddressBookProtos.PersonOrBuilder;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class getDatabase {
    //JDBC name and database URL
    static final String JDBC_DRIVER="com.mysql.jdbc.Driver";
    static final String DB_URL= "jdbc:mysql://localhost/arnav";

    //Databas credentials
    static final String USER="root";
    static final String PASS= "arnav";

    static Connection conn=null;
        
    public static String getValue(String s) throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader("/home/arnav/Desktop/Values.txt"));
        StringBuilder sb = new StringBuilder();
        String ans="";
        String line = br.readLine();
        while (line != null) {
            if(line.contains(s)){
                ans=line.replace(s, "");
                    break;
                }
                line = br.readLine();
            }
        br.close();
        return ans;
    }
    
    public static double getFineLR(String x, double pa){
        String v[]=x.split(" ");
        Double values[]=new Double[v.length];
        for(int i=0;i<values.length;i++)
            values[i]=Double.parseDouble(v[i]);
        Double toAdd=0.0;
        for(int i=0; i <=4;i++){
            if(i==4){
                toAdd+=values[i]*toAdd;
            }
            else{
                toAdd+=values[i]*pa;
            }
        }
        return toAdd;
    }
    
    public static double getFineWC(String x){
              String v[]=x.split(" ");
        Double values[]=new Double[v.length];
        for(int i=0;i<values.length;i++)
            values[i]=Double.parseDouble(v[i]);
        Double toAdd=0.0;
        for(int i=0; i <=2;i++){
            if(i==2){
                toAdd+=values[i]*toAdd;
            }
            else{
                toAdd+=values[i];
            }
        }
        System.out.println(toAdd+"toadd");
        return toAdd; 
    } 
    
    public static double getFineEC(String x){
        String v[]=x.split(" ");
        Double values[]=new Double[v.length];
        for(int i=0;i<values.length;i++)
            values[i]=Double.parseDouble(v[i]);
        Double toAdd=0.0;
        for(int i=0; i <=2;i++){
            if(i==2){
                toAdd+=values[i]*toAdd;
            }
            else{
                toAdd+=values[i];
            }
        }
        return toAdd; 
    }        
    
    public static int isSafe(String s, String cat){
        int ans=0;
             if(s.equals("Land Rental")){
                 if(cat.equals("Member"))
                     ans=1;
            }
            else if(s.equals("Water Charges")){
                if(cat.equals("Member") || cat.equals("Non=Member"))
                    ans=1;
            }
            else if(s.equals("Conservancy")){

            }
            
            else if(s.equals("License Fee")){
                if(cat.equals("Shopkeeper"))
                    ans=1;
            }
            
            else if(s.equals("Electricity Charges")){
                if(cat.equals("Shopkeeper"))
                    ans=1;
            }   
        return ans;     
    }
    
    public static void updateDues(String s){
        Statement stmt=null;
        Connection conn=null;
        System.out.print(s);
        try {
            Class.forName("com.mysql.jdbc.Driver");

            System.out.println("Connecting to databse...");
            conn=DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            String sql="";
            
            //getting queries to be updated
            if(s.equals("Land Rental")){
                sql="select * from HCDData3 where Paid_LR=0";
            }
            else if(s.equals("Water Charges")){
                sql="select * from HCDData3 where Paid_WC=0";
            }
            else if(s.equals("Conservancy")){
                sql="select * from HCDData3 where Paid_C=0";
            }
            else if(s.equals("License Fee")){
                sql="select * from HCDData3 where Paid_LF=0";
            }
            else if(s.equals("Electricity Charges")){
                sql="select * from HCDData3 where Paid_EC=0";
            }
            int check=0;
            
            ResultSet rs = stmt.executeQuery(sql);
            String mouza="";
            String Cat="";
            String x="", update="";
            while(rs.next()){
                x="";
                stmt = conn.createStatement();
                mouza=rs.getString("Mouza");
                Cat=rs.getString("Category");
                if(isSafe(s, Cat)==0)
                    continue;
                double pa=rs.getDouble("Plot_Area");
                x=Cat + " "+s+" ";
                if(s.equals("Land Rental"))
                    x=x+mouza+" ";
                x=getValue(x);
                double due=0, fine=0;
                if(s.equals("Land Rental")){
                    System.out.println("here");
                    due=rs.getInt("Land_Rent_Dues");
                    fine= getFineLR(x,pa);
                    update="UPDATE HCDData3 "+"SET Land_Rent_Dues="+((int)(due+fine+0.5))+", Paid_LR=0 WHERE MemberID="+rs.getLong("MemberID")+";";
                }
                
                else if(s.equals("Water Charges")){
                    System.out.println("here");
                    due=rs.getInt("Water_Charges_Dues");
                    fine= getFineWC(x);
                    update="UPDATE HCDData3 "+"SET Water_Charges_Dues="+((int)(due+fine+0.5))+", Paid_WC=0 WHERE MemberID="+rs.getLong("MemberID")+";";                   
                }
                
                else if(s.equals("Consercancy")){
                    due=rs.getInt("Conservance_Dues");
                    fine=Double.parseDouble(x);
                    update="UPDATE HCDData3 "+"SET Conservance_Dues="+((int)(due+fine+0.5))+", Paid_C=0 WHERE MemberID="+rs.getLong("MemberID")+";";
                }
                
                else if(s.equals("License Fee")){
                    due=rs.getInt("License_Fee_Dues");
                    fine=Double.parseDouble(x);
                    update="UPDATE HCDData3 "+"SET License_Fee_Dues="+((int)(due+fine+0.5))+", Paid_LF=0 WHERE MemberID="+rs.getLong("MemberID")+";";
                }
                
                else if(s.equals("Electricity Charges")){
                   due=rs.getInt("Electricity_Charges_Dues");
                   fine=Double.parseDouble(x);
                   update="UPDATE HCDData3 "+"SET Electricity_Charges_Dues="+((int)(due+fine+0.5))+", Paid_EC=0, Current_Meter_Reading="+rs.getInt("Prev_Meter_Reading")+" WHERE MemberID="+rs.getLong("MemberID")+";";
                }
                int count =stmt.executeUpdate(update);
            }    
        }
        catch(Exception e){
            e.printStackTrace();
        } 
    }   
      
    public static void updateMeterReading(int meterReading, int id) throws ClassNotFoundException{
        
    Connection conn = null;
    Statement stmt = null;
    try{
      //STEP 2: Register JDBC driver
      Class.forName("com.mysql.jdbc.Driver");

      //STEP 3: Open a connection
      System.out.println("Connecting to a selected database...");
      conn = DriverManager.getConnection(DB_URL, USER, PASS);
      System.out.println("Connected database successfully...");
      
      //STEP 4: Execute a query
      System.out.println("Creating statement...");
      stmt = conn.createStatement();
      String sql = "UPDATE HCDData3 "+"SET Current_Meter_Reading="+meterReading+", Date_of_Meter_Reading=CURDATE()"+" WHERE MemberID="+id+";";
      stmt.executeUpdate(sql);
        }
      catch(Exception e){
            e.printStackTrace();
        }
    }    
  
    public static void updateWaterChargesDues(String x){
        Connection conn = null;
        Statement stmt = null; 
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS); 
            stmt = conn.createStatement();
            String sql="";
            String v[]=x.split("=");
            int values[]=new int[v.length];
            for(int i=0;i<v.length;i++){
                values[i]=Integer.parseInt(v[i]);
            }
            sql="UPDATE HCDData3 SET Water_Charges_Dues="+values[1]+
                " , WC_Charge_Dues="+values[2]+   
                " , WC_DevTax_Dues="+values[3]+ 
                " , WC_Interest_Dues="+values[4]+     
                " WHERE MemberID="+values[0]+";";
            if(!sql.equals(""))
              stmt.executeUpdate(sql);    
        }
        catch(Exception e){
            e.printStackTrace();
        }
    } 
    
    public static void updateLicenseFeeDues(String x){
        
        Connection conn = null;
        Statement stmt = null; 
        
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS); 
            stmt = conn.createStatement();
            String sql="";
            String v[]=x.split("=");
            int values[]=new int[v.length];
            for(int i=0;i<v.length;i++){
                values[i]=Integer.parseInt(v[i]);
            }
            sql="UPDATE HCDData3 SET License_Fee_Dues="+values[1]+
                " , LF_MonthlyLF="+values[2]+
                " , LF_Interest="+values[3]+
                " , LF_Rebate="+values[4]+
                " WHERE MemberID="+values[0]+";";
            
            if(!sql.equals(""))
              stmt.executeUpdate(sql);    
        
        }
        
        catch(Exception e){
            e.printStackTrace();
        }        
    }
    
    public static void updateLandRentDues(String x){
        Connection conn = null;
        Statement stmt = null; 
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS); 
            stmt = conn.createStatement();
            String sql="";
            String v[]=x.split("=");
            int values[]=new int[v.length];
            for(int i=0;i<v.length;i++){
                values[i]=Integer.parseInt(v[i]);
            }
            sql="UPDATE HCDData3 SET Land_Rent_Dues="+values[1]+
                " , LR_Rent_Dues="+values[2]+   
                " , LR_StreetLight_Dues="+values[3]+ 
                " , LR_DevTax_Dues="+values[4]+ 
                " , LR_ExDevTax_Dues="+values[5]+
                " , LR_Interest="+values[6]+
                " , LR_Rebate="+values[7]+
                " WHERE MemberID="+values[0]+";";
            if(!sql.equals(""))
              stmt.executeUpdate(sql);    
        }
        catch(Exception e){
            e.printStackTrace();
        }
    } 
    
    public static void updateElectricityChargesDues(String x){
        Connection conn = null;
        Statement stmt = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS); 
            stmt = conn.createStatement();
            String sql="";
            String v[]=x.split("=");
            int values[]=new int[v.length];
            for(int i=0;i<v.length;i++){
                values[i]=Integer.parseInt(v[i]);
            }
            sql="UPDATE HCDData3 SET Electricity_Charge_Dues="+values[1]+
                " , Date_of_Meter_Reading=CURDATE(), EC_Rent_Dues="+values[2]+   
                " , EC_MeterRent_Dues="+values[3]+ 
                " , Current_Meter_Reading="+values[4]+ 
                " , EC_FuseCall_Dues="+values[5]+
                " , EC_Interest="+values[6]+    
                " WHERE MemberID="+values[0]+";";
            updateMeterReading(values[7], values[0]);
            if(!sql.equals(""))
              stmt.executeUpdate(sql);    
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }    
    // this function is used to update the dues at the time of payment
    public static void updateArrearDue(String type ,String x) throws ClassNotFoundException{
        
    Connection conn = null;
    Statement stmt = null;
    try{
      //STEP 2: Register JDBC driver
      Class.forName("com.mysql.jdbc.Driver");

      //STEP 3: Open a connection
      conn = DriverManager.getConnection(DB_URL, USER, PASS);
      
      //STEP 4: Execute a query
      stmt = conn.createStatement();
      String sql="";
      if(type.contains("Land Rental")){
            updateLandRentDues(x);
      }
      else if(type.contains("Water Charges")){
            updateWaterChargesDues(x);
      }
      else if(type.contains("Conservanc")){
            //updateCon
      }
      else if(type.contains("License Fee")){
            updateLicenseFeeDues(x);
      }
      else if(type.contains("Electricity Charges")){
            updateElectricityChargesDues(x);
      }
     // String sql = "UPDATE HCDData3 "+"SET Current_Meter_Reading="+meterReading+" WHERE MemberID="+id+";";
      if(!sql.equals(""))
          System.out.print("here");
              stmt.executeUpdate(sql);
        }
      catch(Exception e){
            e.printStackTrace();
        }
    }       
    // below is the code to fetch data from the database
    public static Person getData(long input, char inputType) {        
    	Person.Builder person = Person.newBuilder();
    	
        Statement stmt=null;
        try {
            Class.forName("com.mysql.jdbc.Driver");

            //System.out.println("Connecting to databse...");
            conn=DriverManager.getConnection(DB_URL, USER, PASS);

//            System.out.println("Creating statement..."+name);
            stmt=conn.createStatement();
            String sql="";
            //int a=18;
            if(inputType=='i')
                sql="SELECT * FROM HCDData3"+" WHERE Members_ID="+input+";";//+name+";";
            else if(inputType=='p')
                sql="SELECT * FROM HCDData3"+" WHERE Contact_No="+input+";";//+name+";";
            System.out.println("here"+input);
            ResultSet rs = stmt.executeQuery(sql);
            
            if(rs==null && inputType=='p'){
                sql="SELECT * FROM HCDData3"+" WHERE Contact_No="+input+";";//+name+";";
            
            rs = stmt.executeQuery(sql);
            }
            int check=0;
           
            while(rs.next()){
                check=1;
                person.setID(rs.getLong("Members_ID"));
                person.setUserType(rs.getString("Category"));
                person.setAddress(rs.getString("Address"));
                person.setEmailID("abc@gmail.com");
                person.setNameGuardian(rs.getString("FH_Name"));
                person.setADElectricityCharges(0);
                person.setADLandRent(0);
                person.setLFFee(rs.getInt("LF_Fee_Dues"));
                person.setLRRent(rs.getInt("LR_Rent_Dues"));
                person.setWCCharges(rs.getInt("WC_Charges_Dues"));
                person.setADConservancy(rs.getInt("Conservance_Dues"));
                person.setName(rs.getString("Name"));
                person.setPlotArea(rs.getDouble("Plot_Area"));
                person.setPreviousMeterReading(rs.getInt("Current_Meter_Reading"));
                person.setTitle("Mr.");
                person.setMouza(rs.getString("Mouza"));
                person.setSocietyPlotNumber(rs.getString("Plot_No"));
                person.setShopNo(rs.getString("Shop_No"));
                person.setLFInterest(0);
                person.setLFRebate(rs.getInt("LF_Rebate_Dues"));
                person.setLRDevTax(rs.getInt("LR_DevTax_Dues"));
                person.setLRExDevTax(rs.getInt("LR_ExDevTax_Dues"));
                person.setLRInterest(rs.getInt("LR_Interest"));
                person.setLRRent(rs.getInt("LR_Rent_Dues"));
                person.setLRRebate(rs.getInt("LR_Rebate"));
                person.setLRStreetLight(rs.getInt("LR_StreetLight_Dues"));
                person.setECFuseCall(rs.getInt("EC_FuseCall_Dues"));
                person.setECInterest(rs.getInt("EC_Interest_Dues"));
                person.setECMeterRent(rs.getInt("EC_MeterRent_Dues"));
                person.setECRent(rs.getInt("EC_Rent_Dues"));
                person.setWCDevTax(rs.getInt("WC_DevTax_Dues"));
                person.setWCInterest(rs.getInt("EC_Interest_Dues"));
                person.setMonthlyLicenseFee(rs.getInt("Monthly_License_Fee"));
                person.setCurrentMeterReading(rs.getInt("Current_Meter_Reading"));
                System.out.print("here"+rs.getString("Category"));
                break;
            }
             if(check==0){
                System.out.print("here");
                person.setID(-1);
                person.setUserType("null");
                person.setAddress("null");
                person.setEmailID("null");
                person.setNameGuardian("null");
                person.setADElectricityCharges(0);
                person.setADLandRent(0);
                person.setLRRent(0);
                person.setWCCharges(0);
                person.setADConservancy(0);
                person.setName("null");
                person.setPlotArea(0);
                person.setPreviousMeterReading(0);
                person.setTitle("Mr.");
                person.setMouza("null");
                person.setSocietyPlotNumber("null");
                person.setShopNo("null");
                person.setLFInterest(0);
                person.setLFRebate(0);
                person.setLRDevTax(0);
                person.setLRExDevTax(0);
                person.setLRInterest(0);
                person.setLRRent(0);
                person.setLRRebate(0);
                person.setLRStreetLight(0);
                person.setECFuseCall(0);
                person.setECInterest(0);
                person.setECMeterRent(0);
                person.setECRent(0);
                person.setWCDevTax(0);
                person.setWCInterest(0);
                person.setLFFee(0);
                person.setCurrentMeterReading(0);
                person.setMonthlyLicenseFee(0);
             }
            
            rs.close();
            stmt.close();
            conn.close();
        //    System.out.println("vcjhsdhe");
        } catch (SQLException ex) {
            // handle the error
            ex.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                if(stmt!=null)
                    conn.close(); 
             //   System.out.println("fjbsdk2");
                return person.build();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }
}