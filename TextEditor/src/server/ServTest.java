package server;

import static org.junit.Assert.assertTrue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
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

/**
 * Currently a race condition in the testing, will figure out better way to test.
 * @author Marco Salazar
 *
 */
public class ServTest {

    @Test(timeout=20000)
    public void launchTest() throws IOException, InterruptedException {
    	
		final Server server = new Server(new ServerSocket(1337));
		server.build();
    	
        Thread serv = new Thread(new Runnable() {
            public void run() {
				try {
					server.serve();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
                }
        });
        serv.start();
        
        // client 1 connect
        Socket s1 = new Socket("localhost", 1337);
        BufferedReader br1 = new BufferedReader(new InputStreamReader(
                s1.getInputStream()));
        PrintWriter p1 = new PrintWriter(s1.getOutputStream(), true);
        
        // client 2 connect
        Socket s2 = new Socket("localhost", 1337);
        BufferedReader br2 = new BufferedReader(new InputStreamReader(
                s2.getInputStream()));
        PrintWriter p2 = new PrintWriter(s2.getOutputStream(), true);
        
        // client 3 connect
        Socket s3 = new Socket("localhost", 1337);
        BufferedReader br3 = new BufferedReader(new InputStreamReader(
                s3.getInputStream()));
        PrintWriter p3 = new PrintWriter(s3.getOutputStream(), true);
        
        p1.println("CONNECT");
        p2.println("CONNECT");
        p3.println("CONNECT");
        
        //client 1 makes a new Document
        p1.println("NEW sampleDoc");
        
        //check to see if clients 2 and 3 see this new Document
        String o1 = null; String o2 = null; String o3 = null;
        while (o1 == null)
        	o1 = br1.readLine();
        while (o2 == null)
        	o2 = br2.readLine();
        while (o3 == null)
        	o3 = br3.readLine();
        System.out.println(o1+o2+o3);
        assertTrue(o1.equals(o2) && o1.equals(o3) && o1.equals("0%sampleDoc%"));

        p1.println("GET 0");
        p2.println("GET 0");
        p3.println("GET 0");
        
        //check Insert command
        p1.println("INSERT 0 0 a");
        o1 = null; o2 = null; o3 = null;
        while (o1 == null)
        	o1 = br1.readLine();
        while (o2 == null)
        	o2 = br2.readLine();
        while (o3 == null)
        	o3 = br3.readLine();
        assertTrue(o1.equals(o2) && o1.equals(o3) && o1.equals("a"));
        
        p2.println("INSERT 0 1 B");
        o1 = null; o2 = null; o3 = null;
        while (o1 == null)
        	o1 = br1.readLine();
        while (o2 == null)
        	o2 = br2.readLine();
        while (o3 == null)
        	o3 = br3.readLine();
        assertTrue(o1.equals(o2) && o1.equals(o3) && o1.equals("aB"));   
    }
}