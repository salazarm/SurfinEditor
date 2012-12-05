package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

public class Server {
	private final CopyOnWriteArrayList<Document> docs = new CopyOnWriteArrayList<Document>();
	private final String regex = "(NEW [\\s\\S]+)|(DELETE \\d+ \\d+)|(INSERT \\d+ \\d+ [\\s\\S]+)|(GET \\d+)|(CONNECT)";
	private final ServerSocket serverSocket;
	private Map<Socket, BufferedReader> ins = new ConcurrentHashMap<Socket, BufferedReader>();
	public static Map<Socket, PrintWriter> outs = new ConcurrentHashMap<Socket, PrintWriter>();
	private List<Socket> sockets = new ArrayList<Socket>();
	private static Random randomGenerator = new Random();

	public Server(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	private String getDoc(int ID, Socket socket) {
		docs.get(ID).addActiveUser(socket);
		return docs.get(ID).toString();
	}

	public static void main(String[] args) throws IOException {
		final int port = 1337;
		ServerSocket serverSocket = new ServerSocket(port);
		Server server = new Server(serverSocket);
		server.build();
		server.serve();
	}

	/**
	 * Reads the location of current existing documents on the server. Files are
	 * saved line by line in a serverDocs.cfg. Each line has the location of the
	 * localFile. This rebuilds the server everytime the server starts, so the server
	 * being off does not result in loss of data.
	 * 
	 * The grammar for the File is as follows: 
	 * 
	 * CONFIGFILE ::= LINE* 
	 * LINE ::= TITLE LOCATION 
	 * TITLE::= [.]+ 
	 * LOCATION::= [.]+
	 * 
	 * @throws IOException
	 */
	public void build() throws IOException {
		File f;
		f = new File("serverDocs.cfg");
		if (!f.exists()) {
			f.createNewFile();
		}
		BufferedReader fileIn = new BufferedReader(new FileReader(f));

		for (String line = fileIn.readLine(); line != null; line = fileIn
				.readLine()) {
			String[] tokens = line.split(" ");
			documentize(tokens[0], tokens[1]);
		}
		fileIn.close();
	}

	private void documentize(String title, String location) throws IOException {
		File f;
		f = new File(location);
		if (f.exists()) {
			CopyOnWriteArrayList<Character> docModel = new CopyOnWriteArrayList<Character>();
			BufferedReader fileIn = new BufferedReader(new FileReader(f));
			for (String line = fileIn.readLine(); line != null; line = fileIn
					.readLine()) {
				for (int i = 0; i < line.length(); i++) {
					docModel.add(line.charAt(i));
				}
				docModel.add('\n');
			}
			fileIn.close();
			docs.add(new Document(title, docModel, location));
		}
	}

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
	 * individual methods handle thse themselves).
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
					for (String line = in.readLine(); line != null; line = in.readLine()) {
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
	 * @param command
	 *            the command to be parsed
	 */
	private String handleRequest(String command, Socket socket) {
		if (!command.matches(regex))
			return ""; // Design Decision: Ignore invalid commands
		String[] tokens = command.split(" ");
		if (tokens[0].equals("NEW")) {
			makeNewDoc(tokens[1]);
			return "MADE";
		} else if (tokens[0].equals("GET")) {
			return getDoc(Integer.parseInt(tokens[1]), socket);
		} else if (tokens[0].equals("CONNECT")) {
			return getDocList();
		} else if (tokens[0].equals("INSERT")) {
			docs.get(Integer.parseInt(tokens[1])).insertChar(
					Integer.parseInt(tokens[2]), tokens[3]);
			return "";
		} else if (tokens[0].equals("DELETE")) {
			docs.get(Integer.parseInt(tokens[1])).removeChar(
					Integer.parseInt(tokens[2]));
			return "";
		} else {
			/* should not reach here */
			throw new UnsupportedOperationException();
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
		do{
			/* Design Decision* Only 2000^2 documents can have the same title. */
			lc = randomGenerator.nextInt(2000)+ title + randomGenerator.nextInt(2000);
			f = new File(lc);
		}while (f.exists());
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		docs.add(new Document(title, new CopyOnWriteArrayList<Character>(), lc));
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
	 * Grammar: 
	 * MESSAGE ::= (ID TITLE)* 
	 * ID ::= \\d+ 
	 * TITLE ::= [.]+
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
