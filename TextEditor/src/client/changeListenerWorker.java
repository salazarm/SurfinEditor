package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public class changeListenerWorker extends SwingWorker<Void, String > {

	private final PrintWriter out;
	private final BufferedReader in;
	private final int id;
	private final JTextArea document;

	public changeListenerWorker(PrintWriter out, BufferedReader in, int id, JTextArea document){
		this.out = out;
		this.in = in;
		this.id = id;
		this.document = document;
	}

	@Override
	protected Void doInBackground() throws Exception {
		while (true) {
			this.out.println("GET " + id);
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
