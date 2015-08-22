import java.io.*;
import java.net.*;
import com.example.tutorial.*;
import com.example.tutorial.Test.test;
import com.example.tutorial.AddressBookProtos.Person;
import com.example.tutorial.AddressBookProtos.PersonOrBuilder;
import java.util.ArrayList;
import java.util.List;

public class TCPServer {
    public static void server() throws Exception {
        String clientSentence;
        String capitalizedSentence;
        ServerSocket welcomeSocket = new ServerSocket(6787);
        
        while(true) {
          //  System.out.println("waiting");
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            clientSentence = inFromClient.readLine();
            String ans="null";
            getDatabase get=new getDatabase();
            DataOutputStream os = new DataOutputStream(connectionSocket.getOutputStream());           
            Person per=null;
            if(clientSentence.charAt(0)=='i' || clientSentence.charAt(0)=='p'){
                per=get.getData(Long.parseLong(clientSentence.substring(1)), clientSentence.charAt(0));
                per.writeTo(os);
                os.flush();
                os.close();
            }
            // x is for password change
            else if(clientSentence.charAt(0)=='x'){
                String up[]=(clientSentence.substring(1)).split(" ");
                BufferedReader br = new BufferedReader(new FileReader("/home/arnav/Desktop/pass.txt"));
                List<String> lines = new ArrayList<String>();
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                String qry=up[0]+" "+up[1];
                while (line != null) {
                    if(line.contains(qry)){
                        line=line.replace(up[1],up[2] );
                    lines.add(line);
                    }
                }
                br.close();    
                BufferedWriter out=new BufferedWriter(new FileWriter("/home/arnav/Desktop/pass.txt"));
                for(String s : lines)
                    out.write(s);
                out.flush();
                out.close();
            }
            // s is for login
            else if(clientSentence.charAt(0)=='s'){
                BufferedReader br = new BufferedReader(new FileReader("/home/arnav/Desktop/pass.txt"));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                ans="incorrect";
                String qry=clientSentence.substring(1);
                while (line != null) {
                    if(line.contains(qry) || line.equals(qry)){
                        ans="correct";
                        break;
                    }
                    line = br.readLine();
                }
                br.close();
                os.writeBytes(ans + '\n');  
            }
            //m is for meter reading
            else if(clientSentence.charAt(0)=='m'){
                ans=clientSentence.substring(1);
                
                String ins[]=ans.split(" ");
                get.updateMeterReading(Integer.parseInt(ins[0]), Integer.parseInt(ins[1]));       
            }
            // a is for updating dues
            else if(clientSentence.charAt(0)=='a'){
                ans=clientSentence.substring(1);
                String ins[]=ans.split("=");
                System.out.print(ans+"   "+ ins.length);
               // get.updateArrearDue(ins[0], Integer.parseInt(ins[1]), Integer.parseInt(ins[2]));            }
            }else
            {
                BufferedReader br = new BufferedReader(new FileReader("/home/arnav/Desktop/Values.txt"));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
             
                String qry=clientSentence.substring(1);
                while (line != null) {
                    if(line.contains(qry)){
            //            System.out.print("jsek:");
                        ans=line.replace(qry, "");
                        break;
                    }
                    line = br.readLine();
                }
                br.close();
                os.writeBytes(ans + '\n');
            }
        }
    }
}