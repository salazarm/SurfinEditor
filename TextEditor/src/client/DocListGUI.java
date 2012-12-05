package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class DocListGUI extends JFrame{
	
	private JLabel label;
	private JTable docTable;
	private JButton newDocument;
	private JTextField newDocumentName;
	private JButton help;
	
	private String docList;
	
	public DocListGUI() {
		
		label = new JLabel();
		label.setName("Label");
		label.setText("List of Existing Document");
		
		docTable = new JTable();
		docTable.setName("Document List");
		
		newDocument = new JButton("New Document");
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
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		GroupLayout space = new GroupLayout(this.getContentPane());
		space.setAutoCreateGaps(true);
        space.setAutoCreateContainerGaps(true);
        this.getContentPane().setLayout(space);
        
        space.setHorizontalGroup(
        		space.createParallelGroup(GroupLayout.Alignment.LEADING)
        			.addGroup(space.createSequentialGroup()
        					.addComponent(newDocument)
	    					.addComponent(newDocumentName, 100, GroupLayout.PREFERRED_SIZE, 100000))
        			.addGroup(space.createSequentialGroup()
        					.addComponent(label, 400, 400, 400))
        			.addComponent(docTable));
        
        space.setVerticalGroup(
        		space.createSequentialGroup()
	    			.addGroup(space.createParallelGroup(GroupLayout.Alignment.CENTER)
	    					.addComponent(newDocument)
	    					.addComponent(newDocumentName, 30, 30, 30))
        			.addGroup(space.createParallelGroup(GroupLayout.Alignment.CENTER)
        					.addComponent(label, 30, 30, 30))
        			.addComponent(docTable));
	}
	
	public static void main(String[] arg){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DocListGUI main = new DocListGUI();
				main.setSize(500,500);
				main.setVisible(true);
			}
		});
	}
}
