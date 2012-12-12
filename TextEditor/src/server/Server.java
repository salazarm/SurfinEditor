package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JOptionPane;

/**
 * In our design, the Server serve as the central server of the entire process, all of the clients connect
 * to the server. All the requests are sent to the server, and the server handles the requests that create
 * a new document and send the entire list of document to all the clients. For the document editing
 * requests, the server will send the editing commands to the particular document being edited according
 * to the ID inputed. Also, all the document are stored in the server.
 */

public class Server {
	private final CopyOnWriteArrayList<Document> docs = new CopyOnWriteArrayList<Document>();
	private final String regex = "(NEW [\\s\\S]+)|(DELETE \\d+ \\d+)|(INSERT \\d+ \\d+ [ ]?\\d+)|(GET \\d+)|(CONNECT)";
	private final ServerSocket serverSocket;
	private Map<Socket, BufferedReader> ins = new ConcurrentHashMap<Socket, BufferedReader>();
	protected static Map<Socket, PrintWriter> outs = new ConcurrentHashMap<Socket, PrintWriter>();
	private List<Socket> sockets = new ArrayList<Socket>();
	private static Random randomGenerator = new Random();

	public Server(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	private void getDoc(int ID, Socket socket) {
		docs.get(ID).addActiveUser(socket);
		outs.get(socket).println(ID + "A" + docs.get(ID).toString());
		sockets.remove(socket);
	}

	/**
	 * Starts a server for our concurrent text editor
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// 1337 is the port we choose to use.
		final int port = 1337;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			Server server = new Server(serverSocket);
			server.build();
			server.serve();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Port " + port
					+ " already in use", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
	}

	/**
	 * Reads the location of current existing documents on the server. Files are
	 * saved line by line in a serverDocs.cfg. Each line has the location of the
	 * localFile. This rebuilds the server every time the server starts, so the
	 * server being off does not result in loss of data.
	 * 
	 * The grammar for the File is as follows:
	 * 
	 * CONFIGFILE ::= LINE* LINE ::= TITLE LOCATION TITLE::= [.]+ LOCATION::=
	 * [.]+
	 * 
	 * @throws IOException
	 */
	public void build() throws IOException {
		File f;
		f = new File("serverDocs.cfg");
		if (!f.exists()) {
			f.createNewFile();
			PrintWriter fwriter = new PrintWriter(new FileWriter(f));
			fwriter.println("##STARTSERVER.CFG");
			fwriter.close();
		}
		BufferedReader fileIn = new BufferedReader(new FileReader(f));
		for (String line = fileIn.readLine(); line != null; line = fileIn
				.readLine()) {
			String[] tokens = line.split(" ");
			if (tokens.length == 2)
				documentize(tokens[0], tokens[1]);
		}
		f.setWritable(true);
		fileIn.close();
	}

	private void documentize(String title, String location) throws IOException {
		File f;
		f = new File(location);
		if (f.exists()) {
			CopyOnWriteArrayList<String> docModel = new CopyOnWriteArrayList<String>();
			BufferedReader fileIn = new BufferedReader(new FileReader(f));
			for (String line = fileIn.readLine(); line != null; line = fileIn
					.readLine()) {
				String[] tokens = line.split("a");
				for (int i = 0; i < tokens.length; i++)
					docModel.add(tokens[i]);
			}
			fileIn.close();
			docs.add(new Document(title, docModel, location, docs.size()));
		}
	}

	/**
	 * Starts up the server and listens for new connections
	 * 
	 * @throws IOException
	 */
	public void serve() throws IOException {
		while (true) {
			final Socket socket = serverSocket.accept();
			sockets.add(socket);
			Thread thread = null;
			try {
				thread = new Thread(new Runnable() {
					public void run() {
						try {
							handleConnection(socket);
						} catch (IOException e) {
						}
					}
				});
				thread.start();
			} catch (Exception e) {
				try {
					if (!socket.isClosed()) {
						socket.close();
					}
				} catch (IOException e1) {
				}
			} finally {
				if (thread != null && !thread.isAlive()) {
					if (socket.isClosed()) {
						socket.close();
						sockets.remove(socket);
					}
				}
			}
		}
	}

	/**
	 * Manages the Sockets, BufferedReaders, and PrintWriters. (Rather than have
	 * individual methods handle these themselves).
	 * 
	 * @param socket
	 *            The socket the current connection is coming from
	 * 
	 * @throws IOException
	 */
	private void handleConnection(Socket socket) throws IOException {
		if (!socket.isClosed()) {
			if (!ins.containsKey(socket)) {
				ins.put(socket,
						new BufferedReader(new InputStreamReader(socket
								.getInputStream())));
				outs.put(socket,
						new PrintWriter(socket.getOutputStream(), true));
			}
			BufferedReader in = ins.get(socket);
			try {
				for (String line = in.readLine(); line != null; line = in
						.readLine()) {
					handleRequest(line, socket);
				}
			} finally {
				if (socket.isClosed()) {
					in.close();
					ins.remove(in);
				}
			}
		}

	}

	/**
	 * Grammar: COMMAND ::= NEW | INSERT | DELETE | GET NEW ::= "NEW" NAME NAME
	 * ::= [.]+ DELETE ::= "DELETE" ID INDEX INSERT ::= "INSERT" ID INDEX
	 * ASCIICODE GET ::= "GET" ID ID ::= \\d+ INDEX ::= \\d+ ASCIICODE ::= \\d+
	 * CONNECT ::= "CONNECT"
	 * 
	 * handle the request sent from the clients. 
	 * 
	 * @param command
	 *            the command to be parsed
	 */
	private void handleRequest(String command, Socket socket) {
		if (command.matches(regex)) {
			String[] tokens = command.split(" ");
			if (tokens[0].equals("NEW")) {
				makeNewDoc(tokens[1]);
			} else if (tokens[0].equals("GET")) {
				getDoc(Integer.parseInt(tokens[1]), socket);
			} else if (tokens[0].equals("CONNECT")) {
				if (!sockets.contains(socket))
					sockets.add(socket);
					outs.get(socket).println(getDocList());
			} else if (tokens[0].equals("INSERT")) {
				String ch = tokens[3];
				int id = Integer.parseInt(tokens[1]);
				if (tokens[3].equals(""))
					ch = tokens[4];
				if (id < docs.size())
					docs.get(Integer.parseInt(tokens[1])).insertChar(
							Integer.parseInt(tokens[2]), ch);
			} else if (tokens[0].equals("DELETE")) {
				int id = Integer.parseInt(tokens[1]);
				if (id < docs.size())
					docs.get(id).removeChar(Integer.parseInt(tokens[2]));
			} else {
				/* should not reach here */
				throw new UnsupportedOperationException();
			}
		}
	}

	/**
	 * Makes a new file on the server
	 * 
	 * @param title
	 *            the title of the document
	 */
	private void makeNewDoc(String title) {
		File f = new File("%"); // Should never exist
		String lc;
		do {
			/* Design Decision* Only 2000^2 documents can have the same title. */
			lc = randomGenerator.nextInt(2000) + title
					+ randomGenerator.nextInt(2000);
			File dir = new File("documents");
			if (!dir.exists()) {
				dir.mkdir();
			}
			f = new File(dir, lc);
		} while (f.exists());
		try {
			f.createNewFile();
			File f2 = new File("serverDocs.cfg");
			PrintWriter fileOut = new PrintWriter(new FileWriter(f2, true));
			fileOut.println(title + " documents\\" + lc);
			fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		docs.add(new Document(title, new CopyOnWriteArrayList<String>(), "documents\\" +lc,
				docs.size()));
		updateUsersDocList(); // Updates all users of the new file being added.
	}

	/**
	 * Updates the users as to what documents are on the server. This method is
	 * synchronized in case a user is added in the middle of iterating then he
	 * won't be given an incomplete list (can happen for a split second).
	 */
	private synchronized void updateUsersDocList() {
		String docsString = getDocList();
		for (Socket socket : sockets) {
			outs.get(socket).println(docsString);
		}

	}

	/**
	 * No need to synchronize because documents CANNOT be deleted (Another
	 * design decision). Also if files are being added then it is okay that we
	 * are in the middle of iterating over because we want that file to be sent
	 * as well (Doesn't hurt). This sends a message of all of the documents to
	 * the client in a string using the following
	 * 
	 * Grammar: MESSAGE ::= (ID TITLE)* ID ::= \\d+ TITLE ::= [.]+
	 * 
	 * @return all the documents as a string.
	 */
	private String getDocList() {
		StringBuilder documentsString = new StringBuilder();
		for (int i = 0; i < docs.size(); i++) {
			/* Design Decision* : Files cannot have '%' in their name */

			documentsString.append(i + "%" + docs.get(i).getName() + "%");
		}
		return documentsString.toString();
	}
}
