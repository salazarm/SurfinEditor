package client;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Test;

import server.Server;

public class SerDocListLoaderTest {
	
	@Test
	public void StringToAsciiTest() throws UnknownHostException, IOException{
		
		try {
        	File f = new File("serverDocs.cfg");
        	f.delete();
        }catch(Exception e){}
    	
		final Server server = new Server(new ServerSocket(1337));
		server.build();
    	
        Thread serv = new Thread(new Runnable() {
            public void run() {
				try {
					server.serve();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                }
        });
        serv.start();
        
		Socket s1 = new Socket("localhost", 1337);
		ServerDocumentListLoader serDoc = new ServerDocumentListLoader(s1);
	}	
}