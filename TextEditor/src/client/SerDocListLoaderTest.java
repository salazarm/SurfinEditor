package client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.DefaultListModel;

import org.junit.Test;

import server.Server;

/**
 * The testing of the ServerDocumentListLoader consists of two part. The first part of it is a 
 * junit test which is presented in this java file. the main purpose of this part of the test 
 * is to ensure that the ServerDocumentListLoader handles the creation of file according to the
 * spec. Each user is updated whenever a document is created. The second part of test. however,
 * is testing the GUI JComponents' function which will be done by hand. 
 * 
 * More Details about the second part of the test:
 * The three things that are tested are the JTextField, the JButton, and the JList.
 * The first test is the when the JButton "New Document" is clicked, a document named should appear at the
 * Jlist "Existing Document" according to the text typed in the JTextField next to the button. The second 
 * second test test weather double clicking on a document in the JList opens up the document and invisible 
 * the ServerDocumentListLoader.
 * @author mpan1218
 */

public class SerDocListLoaderTest {
	
	/**
	 * The BasicFileTest tests the behavior of the ServerDocumentListLoader when a since client
	 * is connected to the server and creating documents. The server should be updating the list
	 * of document to the client every time a document is created.
	 */
	@Test
	public void BasicFileTest() throws UnknownHostException, IOException, InterruptedException{
		
		try {
        	File f = new File("serverDocs.cfg");
        	f.delete();
        }catch(Exception e){}
    	
		final Server server = new Server(new ServerSocket(1336));
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
        
		Socket s1 = new Socket("localhost", 1336);
		PrintWriter p1 = new PrintWriter(s1.getOutputStream(), true);
		ServerDocumentListLoader serDoc = new ServerDocumentListLoader(s1);
		
		p1.println("NEW file1");
		Thread.sleep(500);
		
		String[] docs = docArray(serDoc.docsList);
		assertEquals("file1", docs[0]);
		
		p1.println("NEW file2");
		Thread.sleep(500);
		
		docs = docArray(serDoc.docsList);
		assertTrue(docs[0].equals("file1") && docs[1].equals("file2"));
	}
	
	/**
	 * The MultiClientFileTest test the behavior of the server when two clients are connected
	 * to the server and creating documents. The server should be updating the list of 
	 * document to all of the clients every time a document is created. This is called
	 * MultiClientFileTest because any situation with at least two clients are expected to 
	 * behave the same.
	 */
	@Test
	public void MultiClientFileTest() throws IOException, InterruptedException {
		
		try {
        	File f = new File("serverDocs.cfg");
        	f.delete();
        }catch(Exception e){}
    	
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
        
		Socket s1 = new Socket("localhost", 1338);
		PrintWriter p1 = new PrintWriter(s1.getOutputStream(), true);
		ServerDocumentListLoader serDoc1 = new ServerDocumentListLoader(s1);
		
		Socket s2 = new Socket("localhost", 1338);
		PrintWriter p2 = new PrintWriter(s2.getOutputStream(), true);
		ServerDocumentListLoader serDoc2 = new ServerDocumentListLoader(s2);
		
		Thread.sleep(100);
		
		p1.println("NEW file1");
		Thread.sleep(500);
		
		String[] docs1 = docArray(serDoc1.docsList);
		String[] docs2 = docArray(serDoc2.docsList);
		String file1 = "file1";
		assertTrue(file1.equals(docs1[0]) && file1.equals(docs2[0]));
		
		p2.println("NEW file2");
		Thread.sleep(500);
		
		docs1 = docArray(serDoc1.docsList);
		docs2 = docArray(serDoc2.docsList);
		String file2 = "file2";
		assertTrue(file1.equals(docs1[0]) && file1.equals(docs2[0])
				&& file2.equals(docs1[1]) && file2.equals(docs2[1]));
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
