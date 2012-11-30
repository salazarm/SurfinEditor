package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class DocListGUI extends JFrame{
	
	private JLabel label;
	private JTable docList;
	private JButton newDocument;
	private JTextField newDocumentName;
	
	public DocListGUI() {
		label = new JLabel();
		label.setName("Label");
		label.setText("List of Existing Document");
		
		docList = new JTable();
		docList.setName("Document List");
		
		newDocument = new JButton();
		newDocument.setName("New Document");
		newDocument.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		newDocumentName = new JTextField();
		newDocumentName.setName("New Document Name");
		newDocumentName.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
	}
}
