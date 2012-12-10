package server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class Document {
	private final CopyOnWriteArrayList<Socket> activeClients = new CopyOnWriteArrayList<Socket>();
	private final CopyOnWriteArrayList<String> docModel;
	private final ConcurrentLinkedQueue<String[]> commandsQueue = new ConcurrentLinkedQueue<String[]>();
	private final String name;
	private final String location;
	private final int id;

	/**
	 * Constructor that makes document with Document Model docModel and Title
	 * title.
	 * 
	 * @param title
	 *            the Title of the document
	 * @param docModel
	 *            the model for the document.
	 */
	public Document(String title, CopyOnWriteArrayList<String> docModel,
			String location, int id) {
		this.docModel = docModel;
		this.name = title;
		this.location = location;
		this.id = id;

		/* Starts up command listener for document */
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					if (!commandsQueue.isEmpty()) {
						String[] currCommand = commandsQueue.remove();
						if (currCommand[0].equals("insert")) {
							insert(Integer.parseInt(currCommand[1]),
									currCommand[2]);
						} else if (currCommand[0].equals("remove")) {
							remove(Integer.parseInt(currCommand[1]));
						}
						updateActiveUsers();
					}
				}

			}

		});
		t.start();
	}

	/**
	 * Synchronized for good measure
	 * 
	 * @param index
	 */
	private synchronized void remove(int index) {
		if (index - 1 >= 0 && index - 1 <= docModel.size())
			try{
				docModel.remove(index - 1);
			}catch (ArrayIndexOutOfBoundsException e){
				while(commandsQueue.size()!=0){}
				updateActiveUsers();
			}
	}

	private synchronized void insert(int index, String charToAdd) {
		if (index >= 0 && index <= docModel.size()) {
			docModel.add(index, charToAdd);
		}
	}

	/**
	 * Registers a user to be available to updates by adding the user to the
	 * ActiveUsers list.
	 * 
	 * @param socket
	 *            the socket of the user to be added.
	 */
	public void addActiveUser(Socket socket) {
		if (!activeClients.contains(socket))
			activeClients.add(socket);
	}

	/**
	 * Updates all users of changes to the document model.
	 */
	public void updateActiveUsers() {
		if ((commandsQueue.size() ==0)) {
			String currentDoc = this.toString();
			for (Socket socket : activeClients) {
				if (!socket.isClosed())
					Server.outs.get(socket).println(id + "A" + currentDoc);
			}
		}
	}

	/**
	 * Adds an insert command to this document's queue.
	 * 
	 * @param index
	 *            the position where the character should be inserted
	 * @param charToInsert
	 *            the character to insert
	 */
	public void insertChar(int index, String charToInsert) {
		commandsQueue.add(new String[] { "insert", "" + index, charToInsert });
	}

	/**
	 * Add a remove command to this document's queue
	 * 
	 * @param index
	 *            the index of the character that should be removed.
	 */

	public void removeChar(int index) {
		commandsQueue.add(new String[] { "remove", "" + index });
	}

	/**
	 * 
	 * @return the name of this document
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Also updates the file.
	 * 
	 * @return String representationg of document (concats all the characters in
	 *         the document model).
	 */

	@Override
	public String toString() {
		StringBuilder docAsStringtoSend = new StringBuilder();
		for (String c : docModel) {
			if(!c.equals("a")){
				docAsStringtoSend.append(c + "a");
			}
		}
		String newFile = docAsStringtoSend.toString();
		updateFile(newFile);
		return newFile;
	}

	private void updateFile(String doc) {
		File f = new File(location);
		try {
			if(f.exists())
				f.delete();
			f.createNewFile();
			PrintWriter fileOut = new PrintWriter(new FileWriter(f));
			fileOut.println(doc);
			fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
