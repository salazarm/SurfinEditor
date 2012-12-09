package client;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.text.DefaultEditorKit;

public class TextEditor extends JFrame {

	private static final long serialVersionUID = 5991470239888613993L;
	protected final JTextArea document = new JTextArea(20, 120);
	private String currentFile = "Untitled";
	private final BufferedReader in;
	private final PrintWriter out;
	private final Socket socket;
	private final TextEditor me;
	private final JFrame menu;
	protected final JTextAreaListen textAreaListener;
	
	public TextEditor(final PrintWriter out, final BufferedReader in, int id,
			Socket socket, JFrame menu) {
		
		this.textAreaListener = new JTextAreaListen(out, in, id);
		this.menu = menu;
		this.me = this;
		out.println("GET " + id);
		this.socket = socket;
		(new ChangeListenerWorker(out, in, document, id)).execute();
		this.out = out;
		this.in = in;
		document.setFont(new Font("Monospaced", Font.PLAIN, 12));

		JScrollPane scroll = new JScrollPane(document,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.add(scroll, BorderLayout.CENTER);

		JMenuBar JMB = new JMenuBar();
		this.setJMenuBar(JMB);
		JMenu file = new JMenu("File");
		JMenu edit = new JMenu("Edit");

		JMB.add(file);
		JMB.add(edit);

		file.add(Open);
		file.add(Quit);
		file.addSeparator();
		for (int i = 0; i < 2; i++)
			file.getItem(i).setIcon(null);

		edit.add("Cut");
		edit.add("Copy");
		edit.add("Paste");
		edit.getItem(0).setText("Cut");
		edit.getItem(1).setText("Copy");
		edit.getItem(2).setText("Paste");

		JToolBar tool = new JToolBar();
		this.add(tool, BorderLayout.NORTH);
		tool.add(Open);
		tool.addSeparator();

		JButton cut = tool.add(Cut), copy = tool.add(Copy), paste = tool
				.add(Paste);
		cut.setText(null);
		cut.setIcon(new ImageIcon("cut.png"));
		copy.setText(null);
		copy.setIcon(new ImageIcon("copy.png"));
		paste.setText(null);
		paste.setIcon(new ImageIcon("paste.png"));

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.pack();

		// add JTextAreaListen to the document. Do this only once, not twice.
		document.addKeyListener(textAreaListener);
		document.addCaretListener(textAreaListener);

		setTitle(currentFile);
		setVisible(true);
	}

	Action Open = new AbstractAction("Open", new ImageIcon("open.png")) {
		private static final long serialVersionUID = -474289105133169886L;

		public void actionPerformed(ActionEvent e) {
			menu.setVisible(true);
			out.println("CONNECT");
		}
	};
	Action Quit = new AbstractAction("Quit") {
		private static final long serialVersionUID = -5339245808869817726L;

		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	};
	ActionMap m = document.getActionMap();
	Action Cut = m.get(DefaultEditorKit.cutAction);
	Action Copy = m.get(DefaultEditorKit.copyAction);
	Action Paste = m.get(DefaultEditorKit.pasteAction);

}
