package client;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.PrintWriter;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.text.DefaultEditorKit;

public class TextEditor extends JFrame {

	private static final long serialVersionUID = 5991470239888613993L;
	protected final JTextArea document = new JTextArea(20, 120);
	protected final JTextAreaListen textAreaListener;
	
	public TextEditor(final PrintWriter out, final int id, String title) {
		this.textAreaListener = new JTextAreaListen(out, id);
		out.println("GET " + id);
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

		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();

		// add JTextAreaListen to the document. Do this only once, not twice.
		document.setFocusTraversalKeysEnabled(false);
		document.addKeyListener(textAreaListener);
		document.addCaretListener(textAreaListener);
		

		setTitle(title);
		setVisible(true);
		this.addWindowListener(new WindowListener(){

			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				System.out.println(ClientLoader.textEditorMap.size());
				ClientLoader.textEditorMap.remove(id);
				ClientLoader.count -= 1;
				if(ClientLoader.count ==0)
					ServerDocumentListLoader.mainFrame.setVisible(true);
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {}
			@Override
			public void windowDeiconified(WindowEvent arg0) {	}
			@Override
			public void windowIconified(WindowEvent arg0) {}
			@Override
			public void windowOpened(WindowEvent arg0) {}
		
		});
	}

	Action Open = new AbstractAction("Open", new ImageIcon("open.png")) {
		private static final long serialVersionUID = -474289105133169886L;

		public void actionPerformed(ActionEvent e) {
			ServerDocumentListLoader.mainFrame.setVisible(true);
		}
	};
	Action Quit = new AbstractAction("Quit") {
		private static final long serialVersionUID = -5339245808869817726L;

		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	};
	ActionMap m = document.getActionMap();

	Action Copy = m.get(DefaultEditorKit.copyAction);
	
	Action Cut = new AbstractAction("Cut", new ImageIcon("cut.png")){
        private static final long serialVersionUID = -3218760224238810832L;

        public void actionPerformed(ActionEvent e){
        	System.out.println(textAreaListener.getSelectedText());
        	StringSelection selection = new StringSelection(textAreaListener.getSelectedText());
        	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        	clipboard.setContents(selection, selection);
	        textAreaListener.cutButton();
	    }
	};
    Action Paste = new AbstractAction("Paste", new ImageIcon("paste.png")){
        private static final long serialVersionUID = -8950087920797506481L;

        public void actionPerformed(ActionEvent e){
            m.get(DefaultEditorKit.pasteAction);
            textAreaListener.pasteButton();
        }
    };
    
    
}
