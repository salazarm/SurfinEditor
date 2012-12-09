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
import javax.swing.JTextField;

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
	protected JList docList = new JList(docsList);
	private JScrollPane scroll = new JScrollPane(docList);
	protected static JFrame mainFrame = new JFrame();
	private JPanel mainPanel = new JPanel();
	protected final BufferedReader in;
	protected final PrintWriter out;
	private final MessageHandler messageHandler;

	ServerDocumentListLoader(Socket socket) throws IOException {
		this.in = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		this.out = new PrintWriter(socket.getOutputStream(), true);
		this.messageHandler = new MessageHandler();
		start();
	}

	protected void sendMessage(String docToSend) {
		docToSend = StringAsciiConversion.toAscii(docToSend);
		messageHandler.sendMessage(docToSend);
	}

	private void start() throws IOException {
		out.println("CONNECT");
		makeGUI();
		messageHandler.start();
	}

	private void makeGUI() {
		mainFrame.add(mainPanel);

		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int id = docList.getSelectedIndex();
					TextEditor editor = new TextEditor(out, id);
					ClientLoader.textEditorMap.put("" + id, editor);
					ClientLoader.count +=1;
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
		mainFrame.addWindowListener(new WindowListener(){

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				if(ClientLoader.count==0)
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
