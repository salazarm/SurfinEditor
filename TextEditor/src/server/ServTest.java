package server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.junit.Test;

/**
 * Grammar: COMMAND ::= NEW | INSERT | DELETE | GET 
 * NEW ::= "NEW" NAME 
 * NAME ::= [.]+ 
 * DELETE ::= "DELETE" ID INDEX 
 * INSERT ::= "INSERT" ID INDEX LETTER
 * GET ::= "GET" ID 
 * ID ::= [0-9]+ 
 * INDEX ::= [0-9]+ 
 * LETTER ::= [.]
 * CONNECT ::= "CONNECT"
 * 
 */

public class ServTest {

    @SuppressWarnings("deprecation")
    @Test
    public void launchTest() throws IOException {
        Thread server = new Thread(new Runnable() {
            public void run() {
                try {
                    Server.main(new String[] {});
                } catch (IOException e) {
                    System.out
                            .println("Tried to make server");
                    e.printStackTrace();
                }
            }
        });
        server.start();
        
        Server s;
        int docID = 1;
        
        
        
        
        // client 1 connect
        Socket s1 = new Socket("localhost", 4444);
        BufferedReader br1 = new BufferedReader(new InputStreamReader(
                s1.getInputStream()));
        PrintWriter p1 = new PrintWriter(s1.getOutputStream(), true);
        
        // client 2 connect
        Socket s2 = new Socket("localhost", 4444);
        BufferedReader br2 = new BufferedReader(new InputStreamReader(
                s2.getInputStream()));
        PrintWriter p2 = new PrintWriter(s2.getOutputStream(), true);
        
        // client 3 connect
        Socket s3 = new Socket("localhost", 4444);
        BufferedReader br3 = new BufferedReader(new InputStreamReader(
                s3.getInputStream()));
        PrintWriter p3 = new PrintWriter(s3.getOutputStream(), true);
        
        
        //client 1 makes a new Document
        p1.println("NEW " + "sampleDoc");
        
        //check to see if clients 2 and 3 see this new Document
        p2.println("GET " + docID);
        p3.println("GET " + docID);
        
        
        
        //check Insert command
        p1.println("INSERT " + "1 " + "0 " + "a");
        
        //check CommandQueue
        p2.println("DELETE " + "1 " + "1");
        
        
    }
    
    
    
}