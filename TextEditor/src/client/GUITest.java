package client;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;

public class GUITest {
	/**
	 * Since this a GUI interface that is going to directly be used by the users, the testing strategy is testing each individual JComponent
	 * manually according to the specification. Therefore, the main thing to test is to make sure that the responses to actions are correct.
	 * 
	 * There are 5 JComponents the DocListGUI
	 * 
	 * JLabel label
	 * The lable just displays line that says ""List of Existing Document," so the only test is whether the label show up correctly in the 
	 * desired location on the window.
	 * 
	 * JTable docTable
	 * The docList shows a list of existing documents with a JBotton corresponding to each document.
	 * Test 1: the location of the JTable and the appearance of the JTable.
	 * Test 2: check whether the JTable includes all the existing list.
	 * Test 3: check when a new document is created whether the document is added to the existing list.
	 * Test 4: check whether clicking on the JButtons associated with documents opens up the correct document.
	 * 
	 * JButton newDocument
	 * Test 1: the location of the JButton and the appearance of the JButton.
	 * Test 2: check whether clicking on the JButton creates and opens a new document.
	 * Test 3: check whether the new Document is added to the docTable.
	 * Test 4: check whether the new Document is accessible to other clients by clicking on the document through a different thread.
	 * 
	 * JTextField newDocumentName
	 * Test 1: the location of the JButton and the appearance of the JButton.
	 * Test 2: check whether the documents are created according to the text in this field.
	 * Test 3: check whether the document names are the ID number when documents are created with this field left blank.
	 * Test 4: check whether pressing "Enter" creates a document.
	 * Test 5: check whether the text field is clear after creating a document.
	 * 
	 * JButton help
	 * Test 1: the location of the JButton and the appearance of the JButton.
	 * Test 2: check whether clicking on the JButton opens up a window with information. 
	 */
}
