package client;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.DefaultListModel;

import org.junit.Test;

import server.Server;

public class SerDocListLoaderTest {
	
	@Test
	public void BasicFileCreationTest() throws UnknownHostException, IOException, InterruptedException{
		
		try {
        	File f = new File("serverDocs.cfg");
        	f.delete();
        }catch(Exception e){}
    	
		// Server created.
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
        
        // Client Connected.
		Socket s1 = new Socket("localhost", 1337);
		PrintWriter p1 = new PrintWriter(s1.getOutputStream(), true);
		ServerDocumentListLoader serDoc = new ServerDocumentListLoader(s1);
		
		// Creating the first file.
		p1.println("NEW file1");
		Thread.sleep(250);
		String[] docs = docArray(serDoc.docsList);
		// Testing update of first file.
		assertEquals("file1", docs[0]);
		
		// Creating the second file.
		p1.println("NEW file2");
		Thread.sleep(250);
		docs = docArray(serDoc.docsList);
		// Testing updates of first and second file.
		assertEquals("file1", docs[0]);
		assertEquals("file2", docs[1]);
	}
	
	@Test
	public void TwoPersonCreatingFileTest() throws UnknownHostException, IOException, InterruptedException{
		
		try {
        	File f = new File("serverDocs.cfg");
        	f.delete();
        }catch(Exception e){}
    	
		// Server created.
		final Server server = new Server(new ServerSocket(1338));
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
        
        // Client 1 Connected.
		Socket s1 = new Socket("localhost", 1338);
		PrintWriter p1 = new PrintWriter(s1.getOutputStream(), true);
		ServerDocumentListLoader serDoc1 = new ServerDocumentListLoader(s1);
		
		// Client 2 Connected.
		Socket s2 = new Socket("localhost", 1338);
		PrintWriter p2 = new PrintWriter(s2.getOutputStream(), true);
		ServerDocumentListLoader serDoc2 = new ServerDocumentListLoader(s2);
		
		// Client 1 creates the first file.
		p1.println("NEW f1");
		Thread.sleep(250);
		
		// check the update of first file.
		String[] docs1 = docArray(serDoc1.docsList);
		String[] docs2 = docArray(serDoc2.docsList);
		
		assertEquals("f1", docs1[0]);
		assertEquals("f1", docs2[0]);
		
		// Client 2 creates the second file.
		p2.println("NEW f2");
		Thread.sleep(250);
		
		docs1 = docArray(serDoc1.docsList);
		docs2 = docArray(serDoc2.docsList);
		
		// check the updates of first and second files.
		assertEquals("f1", docs1[0]);
		assertEquals("f2", docs1[1]);
		assertEquals("f1", docs2[0]);
		assertEquals("f2", docs2[1]);
	}
	
	public static String[] docArray(DefaultListModel listModel){
		int numOfDoc = listModel.getSize();
		String[] docs= new String [numOfDoc];
		for(int i = 0; i<numOfDoc; i++){
			docs[i] = (String) listModel.get(i);
		}
		return docs;
	}

}
