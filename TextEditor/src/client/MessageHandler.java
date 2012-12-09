package client;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

/**
 * Handles sending messages and receiving messages.
 * @author Marco Salazar
 *
 */
public class MessageHandler {
	
	protected void sendMessage(String docToSend) {
		docToSend = StringAsciiConversion.toAscii(docToSend);
		ClientLoader.sdl.out.println(docToSend);
	}
	
	/**
	 * This is the main thread that reads ALL incoming messages from the
	 * server and takes appropriate action. This includes handling messages
	 * to multiple open documents as well as updating the list of documents
	 * on the server.
	 */

	protected void start(){
		(new SwingWorker<Void, String>() {
			private Pattern regex = Pattern.compile("[\\d]+A");

			@Override
			protected Void doInBackground() throws Exception {
				while (true) {
					String line = null;
					while (line == null) {
						try {
							line = ClientLoader.sdl.in.readLine();
							publish(line);
						} catch (IOException e) {
							JOptionPane.showMessageDialog(null,
									"Connection Lost", "Error",
									JOptionPane.ERROR_MESSAGE);
							System.exit(-1);
						}
					}
				}
			}

			@Override
			protected void process(List<String> lines) {
				for (String line : lines) {
					if (line.length() >= 2 && line.charAt(1) == '%') {
						/* This is an update regarding documents on the server */
						final String[] tokens = line.split("%");
						System.out.println(line);
						assert (tokens.length % 2 == 0);
						synchronized (ClientLoader.sdl.docList) {
							synchronized (ClientLoader.sdl.docsList) {
								ClientLoader.sdl.docsList.clear();
								for (int i = 0; i + 1 < tokens.length; i += 2) {
									ClientLoader.sdl.docsList
											.addElement(tokens[i + 1]);
								}
							}
						}
					} else {
						/* This is a document update */
						Matcher matcher = regex.matcher(line);
						if (matcher.find() && matcher.start() == 0) {
							String id = line.substring(0, matcher.end() - 1);
							System.out.println("PARSED ID: " + id);
							if (ClientLoader.textEditorMap.containsKey(id)) {
								TextEditor editor = ClientLoader.textEditorMap
										.get(id);
								JTextArea document = editor.document;

								String docAsAsciiCode = line.substring(id
										.length() + 1);
								String docInAsciiText = StringAsciiConversion
										.asciiToString(docAsAsciiCode);

								int temp = editor.textAreaListener.caretPos;

								document.setText(docInAsciiText);
								document.setCaretPosition(temp);
							}
						} else {
							System.out
									.println("REGEX DIDN'T MATCH ON: " + line);
						}
					}
				}
			}
		}).execute();
	}
}
