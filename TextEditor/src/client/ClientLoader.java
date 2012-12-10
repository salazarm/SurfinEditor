package client;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class ClientLoader {
	private JFrame mainFrame = new JFrame();
	private final ImageIcon logo = new ImageIcon("surfinEditor.png");
	private final String Default_PORT = "1337";
	private final String Default_HOST = "127.0.0.1";
	
	/** used to keep track of open JFrames **/
	protected static final Map<String, TextEditor> textEditorMap = new HashMap<String, TextEditor>();
	protected static int count = 0;
	protected static ServerDocumentListLoader sdl;

	private void makeGUI() {
		JPanel mainPanel = new JPanel();
		mainFrame.add(mainPanel);
		JPanel headerPanel = new JPanel();
		headerPanel.setMinimumSize(new Dimension(192, 110));
		headerPanel.setMaximumSize(new Dimension(192, 110));
		JPanel hostPanel = new JPanel();
		JPanel portPanel = new JPanel();
		JPanel loginPanel = new JPanel();
		JLabel copyright = new JLabel("Marco S, Eric E, Mengshaun P (c) 2012");

		JLabel banner = new JLabel(logo);
		headerPanel.add(banner);
		final JTextField hostField = new JTextField();
		hostField.setText(Default_HOST);
		JLabel hostLabel = new JLabel();
		hostLabel.setText("HOST: ");
		final JTextField portField = new JTextField();
		portField.setText(Default_PORT);
		JLabel portLabel = new JLabel();
		portLabel.setText("PORT: ");
		JButton loginButton = new JButton();
		loginButton.addActionListener(new ActionListener() {

			@Override
			/**
			 * Uses the input from the host and port field to open the file choosing window.
			 */
			public void actionPerformed(ActionEvent e) {
				if (hostField.getText() != "" && portField.getText() != "") {
					String host = hostField.getText();
					String port = portField.getText();
					hostField.setText("");
					portField.setText("");
					try {
						Socket socket = new Socket(InetAddress.getByName(host),
								Integer.parseInt(port));
						sdl = new ServerDocumentListLoader(socket);
						mainFrame.dispose();
					} catch (NumberFormatException e1) {
						JOptionPane.showMessageDialog(null,
								"Please input a number for port",
								"Invalid Port number",
								JOptionPane.ERROR_MESSAGE);
						portField.setText(Default_PORT);
						hostField.setText(Default_HOST);
					} catch (UnknownHostException e1) {
						JOptionPane.showMessageDialog(null, "Unknown Host",
								"Error", JOptionPane.ERROR_MESSAGE);
						portField.setText(Default_PORT);
						hostField.setText(Default_HOST);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null,
								"Connection Refused", "Error",
								JOptionPane.ERROR_MESSAGE);
						portField.setText(Default_PORT);
						hostField.setText(Default_HOST);
					}
				}
			}
		});
		

		loginButton.setText("Start surfin' the con-curren(t)-seas!");
		loginPanel.add(loginButton);

		GroupLayout hostLayout = new GroupLayout(hostPanel);
		hostPanel.setLayout(hostLayout);
		hostLayout.setAutoCreateContainerGaps(true);
		hostLayout.setAutoCreateGaps(true);

		hostLayout.setHorizontalGroup(hostLayout.createSequentialGroup()
				.addComponent(hostLabel).addComponent(hostField));

		hostLayout.setVerticalGroup(hostLayout.createSequentialGroup()
				.addGroup(
						hostLayout
								.createParallelGroup(
										GroupLayout.Alignment.CENTER, false)
								.addComponent(hostLabel)
								.addComponent(hostField)));

		GroupLayout portLayout = new GroupLayout(portPanel);
		portPanel.setLayout(portLayout);
		portLayout.setAutoCreateContainerGaps(true);
		portLayout.setAutoCreateGaps(true);

		portLayout.setHorizontalGroup(portLayout.createSequentialGroup()
				.addComponent(portLabel).addComponent(portField));

		portLayout.setVerticalGroup(portLayout.createSequentialGroup()
				.addGroup(
						portLayout
								.createParallelGroup(
										GroupLayout.Alignment.CENTER, false)
								.addComponent(portLabel)
								.addComponent(portField)));

		GroupLayout mainLayout = new GroupLayout(mainPanel);
		mainPanel.setLayout(mainLayout);
		mainLayout.setAutoCreateContainerGaps(true);
		mainLayout.setAutoCreateGaps(true);

		mainLayout.setHorizontalGroup(mainLayout
				.createParallelGroup(GroupLayout.Alignment.CENTER, false)
				.addComponent(headerPanel, 250, 250, 250)
				.addComponent(hostPanel, 250, 250, 250)
				.addComponent(portPanel, 250, 250, 250)
				.addComponent(loginPanel).addComponent(copyright));
		mainLayout.setVerticalGroup(mainLayout.createSequentialGroup()
				.addComponent(headerPanel).addComponent(hostPanel)
				.addComponent(portPanel).addComponent(loginPanel, 40, 40, 40)
				.addComponent(copyright));
		mainFrame.pack();
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setTitle("Surfin' Editor");
		mainFrame.setVisible(true);
		mainFrame.setResizable(false);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Starts the program and opens the main frame where user can submit host
	 * and port information.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
//		try {
//			UIManager
//					.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
//		} catch (Exception e) {
//		}
		(new ClientLoader()).makeGUI();
	}
}
