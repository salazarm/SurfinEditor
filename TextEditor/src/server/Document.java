package server;

import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class Document {
	private CopyOnWriteArrayList<Socket> activeClients = new CopyOnWriteArrayList<Socket>();
	private final CopyOnWriteArrayList<Character> docModel;
	private final ConcurrentLinkedQueue<String[]> commandsQueue = new ConcurrentLinkedQueue<String[]>();
	private final String name;
	
	/**
	 * Constructor that makes document with Document Model docModel and Title title.
	 * @param title the Title of the document
	 * @param docModel the model for the document.
	 */
	public Document(String title, CopyOnWriteArrayList<Character> docModel, String location) {
		this.docModel = docModel;
		this.name = title;
		
		/* Starts up command listener for document*/
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				while(true){
					if(!commandsQueue.isEmpty()){
						String[] currCommand = commandsQueue.remove();
						System.out.print("Handling: "+ currCommand);
						if (currCommand[0].equals("insert")){
							insert(Integer.parseInt(currCommand[1]),currCommand[2]);
						}else if(currCommand[0].equals("remove")){
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
	 * @param index
	 */
	protected synchronized void remove(int index) {
		docModel.remove(index);
		
	}

	protected synchronized void insert(int index, String charToAdd) {
		docModel.add(index,charToAdd.charAt(0));
	}

	/**
	 * Registers a user to be available to updates by adding the user to the ActiveUsers list.
	 * @param socket the socket of the user to be added.
	 */
	public void addActiveUser(Socket socket) {
		activeClients.add(socket);
	}
	
	/**
	 *  Updates all users of changes to the document model.
	 */
	public void updateActiveUsers(){
		String currentDoc = this.toString();
		for (Socket socket: activeClients){
			Server.outs.get(socket).println(currentDoc);
		}
	}

	/**
	 * Adds an insert command to this document's queue.
	 * @param index the position where the character should be inserted
	 * @param charToInsert the character to insert
	 */
	public void insertChar(int index, String charToInsert) {
		assert(charToInsert.length() == 1);
		commandsQueue.add(new String[]{"insert", ""+index, charToInsert});
	}
	
	/**
	 * Add a remove command to this document's queue
	 * @param index the index of the character that should be removed.
	 */

	public void removeChar(int index) {
		commandsQueue.add(new String[]{"remove", ""+index});
	}
	
	/**
	 *
	 * @return the name of this document
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return String representationg of document (concats all the characters in the document model).
	 */
	@Override
	public String toString(){
		StringBuilder docAsString = new StringBuilder();
		for(char c: docModel){
			docAsString.append(c);
		}
		return docAsString.toString();
	}

}
