package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

/**
 * Opens the file picking window. Only 1 of these is ever made but the main
 * frame's visibility chages. This lets the documentList update in the
 * background. Also has the main thread that handles incoming messages from the
 * server.
 * 
 * @author Marco Salazar
 * 
 */
public class ServerDocumentListLoader {
	private JLabel existingDocsLabel = new JLabel();
	private JButton newDocumentButton = new JButton();
	private JTextField newDocumentField = new JTextField();
	protected DefaultListModel docsList = new DefaultListModel();
	private JList docList = new JList(docsList);
	private JScrollPane scroll = new JScrollPane(docList);
	protected static JFrame mainFrame = new JFrame();
	private JPanel mainPanel = new JPanel();
	protected final BufferedReader in;
	protected final PrintWriter out;

	// private final diff_match_patch dmp = new diff_match_patch();

	ServerDocumentListLoader(Socket socket) throws IOException {
		this.in = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		this.out = new PrintWriter(socket.getOutputStream(), true);
		start();
	}

	protected void sendMessage(String docToSend) {
		docToSend = StringAsciiConversion.toAscii(docToSend);
		out.println(docToSend);
	}

	private void start() throws IOException {
		out.println("CONNECT");
		makeGUI();

		/**
		 * This is the main thread that reads ALL incoming messages from the
		 * server and takes appropriate action. This includes handling messages
		 * to multiple open documents as well as updating the list of documents
		 * on the server.
		 */
		(new SwingWorker<Void, String>() {
			private Pattern regex = Pattern.compile("[\\d]+A");

			@Override
			protected Void doInBackground() throws Exception {
				while (true) {
					String line = null;
					while (line == null) {
						try {
							line = in.readLine();
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
						assert (tokens.length % 2 == 0);
						synchronized (docList) {
							synchronized (docsList) {
								docsList.clear();
								for (int i = 0; i + 1 < tokens.length; i += 2) {
									docsList.addElement(tokens[i + 1]);
								}
							}
						}
					} else {
						/* This is a document update */
						Matcher matcher = regex.matcher(line);
						if (matcher.find() && matcher.start() == 0) {
							String id = line.substring(0, matcher.end() - 1);
							if (ClientLoader.textEditorMap.containsKey(id)) {
								TextEditor editor = ClientLoader.textEditorMap
										.get(id);
								// editor.activeCommands -= 1;
								// if (editor.activeCommands == -1) {
								JTextArea document = editor.document;

								String docAsAsciiCode = line.substring(id
										.length() + 1);
								String docInAsciiText = StringAsciiConversion
										.asciiToString(docAsAsciiCode);
								int carPos = editor.document.getCaret()
										.getDot();
								int temp;
								if (docInAsciiText.substring(0, carPos).equals(
										editor.document.getText().substring(0,
												carPos)))
									temp = carPos;
								else if (docInAsciiText.length() > editor.document
										.getText().length())
									temp = carPos + 1;
								else
									temp = carPos - 1;
								if (!editor.textAreaListener.text_selected) {
									document.setText(docInAsciiText);
									document.setCaretPosition(temp);
								}
								/**
								 * We know there is an error thrown here if we
								 * attempt to put the caret position too high
								 * but the default behavior is to set it to the
								 * max which is a
								 */
								else if (editor.textAreaListener.text_selected) {
									int dotbefore = document.getCaret()
											.getDot();
									document.setText(docInAsciiText);
									document.getCaret().moveDot(dotbefore);
								}

							}
						} else {
							System.out
									.println("REGEX DIDN'T MATCH ON: " + line);
						}
					}
					// }
				}
			}
		}).execute();
	}

	private void makeGUI() {
		mainFrame.add(mainPanel);

		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int id = docList.getSelectedIndex();
					String title = (String) docsList.getElementAt(id);
					TextEditor editor = new TextEditor(out, id, title);
					ClientLoader.textEditorMap.put("" + id, editor);
					ClientLoader.count += 1;
					mainFrame.setVisible(false);
				}
			}
		};

		docList.addMouseListener(mouseListener);
		existingDocsLabel.setText("Existing Documents");
		newDocumentButton.setText("New Document");
		newDocumentButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (newDocumentField.getText() != "") {
					String fileName = newDocumentField.getText();
					if (!Pattern.matches("[\\s\\S]*%[\\s\\S]*", fileName)) {
						out.println("NEW " + fileName);
						out.println("CONNECT");
					} else {
						JOptionPane.showMessageDialog(null,
								"Please do not use '%' in your file name",
								"Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		newDocumentField.setText("exampleFile.txt");
		newDocumentField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (newDocumentField.getText() != "") {
					String fileName = newDocumentField.getText();
					if (!Pattern.matches("[\\s\\S]*%[\\s\\S]*", fileName)) {
						out.println("NEW " + fileName);
						out.println("CONNECT");
					} else {
						JOptionPane.showMessageDialog(null,
								"Please do not use '%' in your file name",
								"Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		JPanel newDocumentPanel = new JPanel();
		GroupLayout newDocumentLayout = new GroupLayout(newDocumentPanel);
		newDocumentPanel.setLayout(newDocumentLayout);
		newDocumentLayout.setAutoCreateGaps(true);
		newDocumentLayout.setAutoCreateContainerGaps(true);

		newDocumentLayout.setHorizontalGroup(newDocumentLayout
				.createSequentialGroup().addComponent(newDocumentField)
				.addComponent(newDocumentButton));

		newDocumentLayout.setVerticalGroup(newDocumentLayout
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(newDocumentField, 25, 25, 25)
				.addComponent(newDocumentButton, 25, 25, 25));

		GroupLayout mainLayout = new GroupLayout(mainPanel);
		mainLayout.setAutoCreateGaps(true);
		mainLayout.setAutoCreateContainerGaps(true);
		mainPanel.setLayout(mainLayout);

		mainLayout.setHorizontalGroup(mainLayout
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(newDocumentPanel).addComponent(existingDocsLabel)
				.addComponent(scroll));

		mainLayout.setVerticalGroup(mainLayout.createSequentialGroup()
				.addComponent(newDocumentPanel).addComponent(existingDocsLabel)
				.addComponent(scroll));
		mainFrame.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				if (ClientLoader.count == 0)
					System.exit(0);
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}

		});
		mainFrame.pack();
		mainFrame.setSize(500, 500);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setTitle("File Selection Window");
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}
}
