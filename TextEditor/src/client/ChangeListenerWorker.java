package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import client.JTextAreaListen;

public class ChangeListenerWorker extends SwingWorker<Void, String > {

	private final BufferedReader in;
	private final JTextArea document;

	public ChangeListenerWorker(PrintWriter out, BufferedReader in, JTextArea document){
		this.in = in;
		this.document = document;
	}

	@Override
	protected Void doInBackground() throws Exception {
		while (true) {
			String line = null;
			do{
				try {
					line = in.readLine();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null,
							"Connection Lost", "Error",
							JOptionPane.ERROR_MESSAGE);
					System.exit(-1);
				}
			}while (line == null);
			publish(line);
		}	
	}
	
	@Override
	protected void process(List<String> strings){
		for(String doc: strings){
			int temp = JTextAreaListen.caretPos;
			document.setText(doc);
			document.setCaretPosition(temp);
		}
	}

}
