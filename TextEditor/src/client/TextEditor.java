package client;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
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
	private JTextArea document = new JTextArea(20, 120);
	private JFileChooser dialog = new JFileChooser(
			System.getProperty("user.dir"));
	private String currentFile = "Untitled";
	private boolean changed = false;
	private int id;
	private final BufferedReader in;
	private final PrintWriter out;

	public TextEditor(final PrintWriter out, final BufferedReader in, int id) {
		this.out = out;
		this.in = in;
		this.id = id;
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
		file.add(Save);
		file.add(Quit);
		file.add(SaveAs);
		file.addSeparator();
		for (int i = 0; i < 4; i++)
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
		tool.add(Save);
		tool.addSeparator();

		JButton cut = tool.add(Cut), copy = tool.add(Copy), paste = tool
				.add(Paste);
		cut.setText(null);
		cut.setIcon(new ImageIcon("cut.png"));
		copy.setText(null);
		copy.setIcon(new ImageIcon("copy.png"));
		paste.setText(null);
		paste.setIcon(new ImageIcon("paste.png"));

		Save.setEnabled(false);
		SaveAs.setEnabled(false);

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.pack();
		document.addKeyListener(keyPressed);
		setTitle(currentFile);
		setVisible(true);
		final int id2 = id;
		Thread t = new Thread(new Runnable() {
			public void run() {
				out.println("GET " + id2);
				while (true) {
					out.println("GET " + id2);
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
					document.setText(line);
					
				}
			}
		});
		t.start();
	}

	private KeyListener keyPressed = new KeyAdapter() {

		public void keyPressed(KeyEvent e) {
			changed = true;
			Save.setEnabled(true);
			SaveAs.setEnabled(true);
		}
	};
	Action Open = new AbstractAction("Open", new ImageIcon("open.png")) {
		private static final long serialVersionUID = -474289105133169886L;

		public void actionPerformed(ActionEvent e) {
			saveOld();
			if (dialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				readInFile(dialog.getSelectedFile().getAbsolutePath());
			}
			SaveAs.setEnabled(true);
		}
	};
	Action Save = new AbstractAction("Save", new ImageIcon("save.png")) {
		private static final long serialVersionUID = 2064233284536910855L;

		public void actionPerformed(ActionEvent e) {
			if (!currentFile.equals("Untitled"))
				saveFile(currentFile);
			else
				saveFileAs();
		}
	};
	Action SaveAs = new AbstractAction("Save as...") {
		private static final long serialVersionUID = -5473532525926088880L;

		public void actionPerformed(ActionEvent e) {
			saveFileAs();
		}
	};
	Action Quit = new AbstractAction("Quit") {
		private static final long serialVersionUID = -5339245808869817726L;

		public void actionPerformed(ActionEvent e) {
			saveOld();
			System.exit(0);
		}
	};
	ActionMap m = document.getActionMap();
	Action Cut = m.get(DefaultEditorKit.cutAction);
	Action Copy = m.get(DefaultEditorKit.copyAction);
	Action Paste = m.get(DefaultEditorKit.pasteAction);

	private void saveFileAs() {
		if (dialog.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
			saveFile(dialog.getSelectedFile().getAbsolutePath());
	}

	private void saveOld() {
		if (changed) {
			if (JOptionPane.showConfirmDialog(this, "Save " + currentFile
					+ " ?", "Save", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				saveFile(currentFile);
		}
	}

	private void readInFile(String fileName) {
		try {
			FileReader r = new FileReader(fileName);
			document.read(r, null);
			r.close();
			currentFile = fileName;
			setTitle(currentFile);
			changed = false;
		} catch (IOException e) {
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(this, "Could not find " + fileName);
		}
	}

	private void saveFile(String fileName) {
		try {
			FileWriter w = new FileWriter(fileName);
			document.write(w);
			w.close();
			currentFile = fileName;
			setTitle(currentFile);
			changed = false;
			Save.setEnabled(false);
		} catch (IOException e) {
			JOptionPane
					.showMessageDialog(this,
							"An error has occurred. Your document may not have been saved");
		}
	}
}
