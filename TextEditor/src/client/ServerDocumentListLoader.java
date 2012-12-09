package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.*;

@SuppressWarnings("unused")
/**
 * Opens the file picking window.
 * @author Marco Salazar
 *
 */
public class ServerDocumentListLoader {
	private JLabel existingDocsLabel = new JLabel();
	private JButton newDocumentButton = new JButton();
	private JTextField newDocumentField = new JTextField();
	private DefaultListModel docsList = new DefaultListModel();
	private JList docList = new JList(docsList);
	private JScrollPane scroll = new JScrollPane(docList);
	private JFrame mainFrame = new JFrame();
	private JPanel mainPanel = new JPanel();
	private final BufferedReader in;
	private final PrintWriter out;

	ServerDocumentListLoader(final BufferedReader in,final PrintWriter out) throws IOException {
		this.in = in;
		this.out = out;
		out.println("CONNECT");
		System.out.print("Printed Connect");
		Thread t = new Thread(new Runnable() {
			public void run() {
				while (true) {
					String line = null;
					while (line == null) {
						try {
							line = in.readLine();
						} catch (IOException e) {
							JOptionPane.showMessageDialog(null,
									"Connection Lost", "Error",
									JOptionPane.ERROR_MESSAGE);
							System.exit(-1);
						}
					}
					final String[] tokens = line.split("%");
					assert (tokens.length % 2 == 0);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							synchronized (docList) {
								synchronized (docsList) {
									docsList.clear();
									for (int i = 0; i + 1 < tokens.length; i += 2) {
										docsList.addElement(tokens[i + 1]);
									}
								}
							}
						}
					});
				}
			}
		});
		t.start();
		makeGUI();
	}

	private void makeGUI() {
		mainFrame.add(mainPanel);

		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int id = docList.getSelectedIndex();
					new TextEditor(out, in, id);
					mainFrame.dispose();
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
					out.println("NEW " + fileName);
					out.println("CONNECT");
				}
			}
		});

		newDocumentField.setText("exampleFile.txt");
		newDocumentField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (newDocumentField.getText() != "") {
					String fileName = newDocumentField.getText();
					out.println("NEW " + fileName);
					out.println("CONNECT");
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
		mainFrame.pack();
		mainFrame.setSize(500, 500);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setTitle("File Selection Window");
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
