package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class InitialClientLoader {
	private JFrame mainFrame = new JFrame();

	private void makeGUI() {
		JPanel mainPanel = new JPanel();
		mainFrame.add(mainPanel);

		JPanel headerPanel = new JPanel();
		JPanel hostPanel = new JPanel();
		JPanel portPanel = new JPanel();
		JPanel loginPanel = new JPanel();

		JLabel banner = new JLabel();
		banner.setText("Surfin' Editor");
		final JTextField hostField = new JTextField();
		hostField.setText("127.0.0.1");
		JLabel hostLabel = new JLabel();
		hostLabel.setText("HOST: ");
		final JTextField portField = new JTextField();
		portField.setText("1337");
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
						@SuppressWarnings("unused")
						ServerDocumentListLoader s = new ServerDocumentListLoader(
								socket);
						mainFrame.setVisible(false);
					} catch (NumberFormatException e1) {
						JOptionPane.showMessageDialog(null,
								"Please input a number for port",
								"Invalid Port number",
								JOptionPane.ERROR_MESSAGE);
						portField.setText("1337");
						hostField.setText("127.0.0.1");
					} catch (UnknownHostException e1) {
						JOptionPane.showMessageDialog(null, "Unknown Host",
								"Error", JOptionPane.ERROR_MESSAGE);
						portField.setText("1337");
						hostField.setText("127.0.0.1");
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null,
								"Connection Refused", "Error",
								JOptionPane.ERROR_MESSAGE);
						portField.setText("1337");
						hostField.setText("127.0.0.1");
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

		mainLayout.setHorizontalGroup(mainLayout.createSequentialGroup()
				.addGroup(
						mainLayout
								.createParallelGroup(
										GroupLayout.Alignment.CENTER, false)
								.addComponent(headerPanel)
								.addComponent(hostPanel)
								.addComponent(portPanel)
								.addComponent(loginPanel)));
		mainLayout.setVerticalGroup(mainLayout.createSequentialGroup()
				.addComponent(headerPanel).addComponent(hostPanel)
				.addComponent(portPanel).addComponent(loginPanel));
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
		try {
		    UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
		} catch (Exception e) {
		    e.printStackTrace();
		}

		final InitialClientLoader a = new InitialClientLoader();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				a.makeGUI();
			}
		});
	}
}
