package client;

public class GUIListenerTest {
	/**
	 * The purpose of this file is to document the test strategy regarding the multiple GUI window on the Client 
	 * side except for the ServerDocumentListLoader which has its own separate test. 
	 * 
	 * The first GUI the users will encounter is the ClientLoader; therefore, the testing process will begin with
	 * the ClientLoader. When type in the invalid host address, check whether a dialog pop up with message "Unknown 
	 * Host." When the unable to connect to a valid address, check whether a dialog pop up with message "Connection
	 * Refused". When port input include non-digits, check whether a dialog pop up with message "Please input a 
	 * number for port". When the server is correctly set up, and the host and port are correctly input, the client
	 * should be successfully connect to the server and the ServerDocumentListLoader should start running.
	 * 
	 * ServerDocumentListLoader, as mentioned before, is tested in the java file named SerDocListLoaderTest.
	 * 
	 * The last the GUI the users will encounter is the TextEditor; this is where the concurrent editing take 
	 * place. First, test the basic functions of the textEditor with only one client: insert, delete, copy, cut, 
	 * paste, control-c, control-x, control v, open, and quit. Then test the concurrent editing with two clients 
	 * (basically, open up two clients and the same document. Test whether the document is update to another user 
	 * simply by doing the basic function listed before. Then, type (or do other basic functions) at the same time!
	 * Then, test whether editing another file affect the original file. Finally, type insanely fast on both of 
	 * them!!!
	 */
}
