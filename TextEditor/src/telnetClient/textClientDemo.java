package telnetClient;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class textClientDemo {
	private final static JTextArea clientLog = new JTextArea();
	private final static JTextField field = new JTextField();
	private final static JPanel serverPanel = new JPanel();
	private final static JScrollPane scroll = new JScrollPane(clientLog);
	private final static JFrame clientWindow = new JFrame();
	private static BufferedReader in;
	private static PrintWriter out;
	private static Socket socket;
	
	
	{
		socket = null;
		try {
			socket = new Socket(InetAddress.getByName("127.0.0.1"), 1337);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (socket != null) {
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void makeGUI() {
		clientLog.setText("Connecting to 127.0.0.1:1337 ...");
		clientLog.setLineWrap(true);
		clientLog.setWrapStyleWord(true);
		clientLog.setEditable(false);
		scroll.setPreferredSize(new Dimension(500, 300));
		scroll.setAutoscrolls(true);
		
		field.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						out.println(field.getText());
						field.setText("");
					}
				});
				t.start();
			}
		});

		GroupLayout mainlayout = new GroupLayout(serverPanel);
		serverPanel.setLayout(mainlayout);
		mainlayout.setAutoCreateGaps(true);
		mainlayout.setAutoCreateContainerGaps(true);

		mainlayout.setHorizontalGroup(mainlayout.createSequentialGroup()
				.addGroup(mainlayout.createParallelGroup(GroupLayout.Alignment.CENTER, true)
						.addComponent(field)
						.addComponent(scroll)));
		mainlayout.setVerticalGroup(mainlayout.createSequentialGroup()
				.addComponent(field)
				.addComponent(scroll));

		clientWindow.add(serverPanel);
		clientWindow.pack();
		clientWindow.setVisible(true);
		clientWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		clientWindow.setTitle("TextClient for Concurrent TextEditor");
	}
	public static void main(String[] args) throws UnknownHostException, IOException{
		textClientDemo n = new textClientDemo();
		n.makeGUI();
		n.connect();
	}
	private void connect() throws UnknownHostException, IOException {
		clientLog.setText("Connected to"+socket.toString());
		Thread t = new Thread(new Runnable(){

			@Override
			public void run(){
				try{
					while(true){
						System.out.println("waiting");
					for (String line = in.readLine(); line != null; line = in.readLine()){
						clientLog.setText(clientLog.getText() +"\n"+ line);
						}
					}
				}catch(Exception e){e.printStackTrace();}finally{}
			}
			
		}); t.start();
	}
}
