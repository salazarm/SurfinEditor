package client;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;

/**
 * JTextAreaListen provides a KeyListener and CaretListener for the TextEditor.
 * This class will send updates to the server whenever a meaningful key is
 * pressed. The KeyListener looks for characters which are typed by the user.
 * The CaretListener observes and reports the position of the caret and mark at
 * any given time.
 * 
 * @author e3m3r
 * 
 */
public class JTextAreaListen extends JFrame implements KeyListener,
		CaretListener {

	private static final long serialVersionUID = 6950001634065526391L;
	protected final PrintWriter out;
	protected final int id;
	protected int caretPos;
	protected int cMark;
	protected int curr_KeyCode;
	protected boolean text_selected;
	protected boolean ctrl_down;
	protected int cMark_ctrl_down;
	protected int caretPos_ctrl_down;
	protected boolean text_selected_ctrl_down;
	
	
	protected boolean text_selected_KP;
	protected int cMark_KP;
	protected int caretPos_KP;

	/**
	 * Constructor for the JTextAreaListen. Implements the JTextAreaListen for
	 * the document. Requires the PrintWriter to the server socket, and the
	 * document ID for the document that we are editing.
	 * 
	 * @param out
	 * @param id
	 */
	public JTextAreaListen(PrintWriter out, int id) {
		super("JTextAreaListen");
		this.id = id;
		this.out = out;
	}

	/**
	 * keyEventHandler is used to filter all of the events of our KeyListener.
	 * 
	 * @param ev
	 */
	public void keyEventHandler(KeyEvent ev) {
		int evID = ev.getID();
		// There are three KeyEvents that we are interested in filtering:
		// KEY_PRESSED, KEY_RELEASED, and KEY_TYPED
		if (evID == KeyEvent.KEY_PRESSED) {
			curr_KeyCode = ev.getKeyCode();
			caretPos_KP = caretPos;
            cMark_KP = cMark;
            text_selected_KP = !(caretPos_KP == cMark_KP);
			// the keyCode 8 refers to "delete".
			if (curr_KeyCode == 8) {
				// We wish to handle deletes during the KEY_PRESSED event,
				// because the selected text is preserved in this event.
				if (text_selected) {
				    int tempCar = caretPos;
                    int tempCMark = cMark;

                    int startingPos = Math.min(tempCar, tempCMark);
                    for (int i = 0; i < getSelectedText().length(); i++)
                        delete(startingPos+1);
				} else {
					delete(caretPos);
				}
			}
			// The keyCode 10 refers to "ENTER", indicating a line break.
			else if (curr_KeyCode == 10) {
				// For selected text, we want to send deletes and replace it
				// with a line break.
				if (text_selected) {
				    int tempCar = caretPos;
                    int tempCMark = cMark;
                    int startingPos = Math.min(tempCar, tempCMark);
                    for (int i = 0; i < getSelectedText().length(); i++){
                        delete(startingPos+1);
                    }
					insert("\\n", startingPos);
				} else {
					insert("\\n", caretPos);
				}
			}
			//The keyCode 17 refers to the Ctrl button
			else if (curr_KeyCode == 17) {
				ctrl_down = true;
				caretPos_ctrl_down = caretPos;
				cMark_ctrl_down = cMark;
				text_selected_ctrl_down = text_selected;
			}
			//The keyCode 9 refers to "Tab" which is typically not detected by KeyListeners in Java.
			else if (curr_KeyCode == 9){
			    if(text_selected){
			        int tempCar = caretPos;
			        int tempCMark = cMark;
			        
			        int startingPos = Math.min(tempCar, tempCMark);
			        for (int i = 0; i < getSelectedText().length(); i++){
			            delete(startingPos + 1);
			        }
			        insert(" ", startingPos);
			    }
			    else{
			        insert(" ", caretPos);
			    }
			}
			
			
			
			
			//we are interested in knowing when the ctrl button is released (keyCode = 17)
		} else if (evID == KeyEvent.KEY_RELEASED) {
			if (ev.getKeyCode() == 17) {
				ctrl_down = false;
			}

			//keyTyped events typically refer to characters generated in teh textEditor
		} else if (evID == KeyEvent.KEY_TYPED) {
			char kc = ev.getKeyChar();

			if (curr_KeyCode == 8 || curr_KeyCode == 10 || ev.isActionKey()) {
				// pass over deletes and enters, since they've already been
				// taken care of.

			} else {
				String charString = String.valueOf(kc);
				if (!ev.isControlDown()) {
					if (ev.isShiftDown()) {
						charString.toUpperCase();
					}
					if (text_selected) {
	                    int tempCar = caretPos_KP;
	                    int tempCMark = cMark_KP;

	                    int startingPos = Math.min(tempCar, tempCMark);
	                    for (int i = 0; i < getSelectedText_KP().length(); i++)
	                        delete(startingPos+1);
	                    insert(charString, startingPos);
	                    
					} else {
						insert(charString, caretPos);
					}
				} else {

					/*
					 * We care about the Cut and Paste commands, because they
					 * affect the contents of the document in a way that the
					 * other user won't see unless a message is sent to the
					 * server.
					 */
					if (!((int) charString.charAt(0) == 24 || (int) charString
							.charAt(0) == 22)) {
						/*
						 * We do nothing. As long as control is down, the only
						 * other relevant commands are Ctrl+A and Ctrl+C, but we
						 * don't need to send any message for those. For any
						 * other Ctrl+(char), we expect no action.
						 */

					} else if ((int) charString.charAt(0) == 24) {
						/*
						 * if text is selected during a cut command, we need to
						 * send messages to delete each character in the
						 * selection.
						 */
						if (text_selected_KP) {
							
							int tempCar = caretPos_KP;
					        int tempCMark = cMark_KP;

					        int startingPos = Math.min(tempCar, tempCMark);
					        for (int i = 0; i < Math.abs(tempCar-tempCMark); i++)
					            delete(startingPos+1);
						} else {
							// if no text is selected during a cut command,
							// nothing happens.
						}
					} else if ((int) charString.charAt(0) == 22) {
						// Somehow send the contents of the clipboard one at a
						// time.
						String clipBoardString = getClipboardContents();
						if (clipBoardString.equals("")) {
							if (text_selected_KP) {
							    int tempCar = caretPos_KP;
						        int tempCMark = cMark_KP;

						        int startingPos = Math.min(tempCar, tempCMark);
						        for (int i = 0; i < getSelectedText_KP().length(); i++)
						            delete(startingPos+1);
							}
						} else {
						     String clipBoardString1 = getClipboardContents();
						        if (text_selected_KP) {

						            int tempCaretPos = caretPos_KP;
						            int tempCMark = cMark_KP;
						            int startingPos = Math.min(tempCaretPos, tempCMark);
						            
						            for (int i = 0; i < tempCaretPos-tempCMark; i++)
						                delete(startingPos+1);

						            int j = startingPos;
						            for (int i = 0; i < (clipBoardString1.length()); i++) {
						                insert(String.valueOf(clipBoardString1.charAt(i)), j);
						                j++;
						            }

						        } else {
						            int j = caretPos_KP;
						            for (int i = 0; i < clipBoardString1.length(); i++) {
						                insert(String.valueOf(clipBoardString1.charAt(i)), j);
						                j++;
						            }
						        }
							
						}
					}

				}

			}
		}

	}

	/**
	 * Get the String residing on the clipboard.
	 * 
	 * @return any text found on the Clipboard; if none found, return an empty
	 *         String.
	 */
	public String getClipboardContents() {
		String result = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		// odd: the Object param of getContents is not currently used
		Transferable contents = clipboard.getContents(null);
		boolean hasTransferableText = (contents != null)
				&& contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		if (hasTransferableText) {
			try {
				result = (String) contents
						.getTransferData(DataFlavor.stringFlavor);
			} catch (UnsupportedFlavorException ex) {
				// highly unlikely since we are using a standard DataFlavor
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * The keyPressed method is called whenever a key is pressed. Since
	 * KeyPressed events have the attribute keyCode, while KeyTyped events do
	 * not, we have to get the keyCode from keyPressed. We use keyPressed
	 * instead of keyTyped when we are more concerned with the key that has been
	 * pressed than the character that has been typed.
	 * 
	 * @param ev
	 * 
	 */
	@Override
	public void keyPressed(KeyEvent ev) {
		keyEventHandler(ev);
	}

	/**
	 * The keyReleased method is called whenever a key is released.
	 * 
	 * @param ev
	 */
	@Override
	public void keyReleased(KeyEvent ev) {
		keyEventHandler(ev);
	}

	/**
	 * The keyTyped method is called whenever a key is pressed and released. We
	 * use keyTyped when we are interested in the character that has been typed.
	 * 
	 * @param ev
	 */
	@Override
	public void keyTyped(KeyEvent ev) {
		keyEventHandler(ev);
	}

	/**
	 * The insert method sends an insert message to the server's command queue. It corresponds
	 * to the grammar given in the server code.	
	 * @param insertString
	 * @param index
	 */
    public void insert(String insertString, int index) {
        ClientLoader.sdl.sendMessage("INSERT " + id + " " + (index) + " "
                + insertString);

    }
    
    /**
     * The delete method sends a delete message to the server's command queue. It corresponds to the
     * grammar given in the server code.
     * @param index
     */
	public void delete(int index){
	    ClientLoader.sdl.sendMessage("DELETE " + String.valueOf(id) + " "
                + index);

	}
	

	/**
	 * The caretUpdate method is a method of CaretListener, and it will occur at
	 * any CaretEvent. This method allows us to update the index values of caret
	 * and mark. This will tell us their location in real-time. We are also
	 * interested in whether or not text is highlighted, and we can discern this
	 * using our CaretListener.
	 * 
	 * @param cev
	 */
	@Override
	public void caretUpdate(CaretEvent cev) {
		int dot = cev.getDot();
		int mark = cev.getMark();
		caretPos = dot;
		cMark = mark;
		if (dot == mark) {
			text_selected = false;
		} else if ((dot < mark) | (dot > mark)) {
			text_selected = true;
		}
	}
	
	/**
	 * The cutButton method is activated when someone presses the Cut button on the text editor in order to cut text.
	 * It is an alternative to ctrl+x, and is a nice feature.
	 */
	public void cutButton(){
	    //send delete commands to the server for the selected text
        if (text_selected) {
            int tempCar = caretPos;
            int tempCMark = cMark;
            int startingPos = Math.min(tempCar, tempCMark);
            for (int i = 0; i < Math.abs(tempCar-tempCMark); i++)
                delete(startingPos+1);
        } else {
            // if no text is selected during a cut command,
            // nothing happens.
        }
	}
	
	
	/**
	 * The pasteButton method is activated when someone presses the Paste button on the text editor in order to paste text.
	 * It is an alternative to ctrl+v, and is a nice feature.
	 */
	public void pasteButton(){
	 // send the contents of the clipboard one at a
        // time.
        String clipBoardString = getClipboardContents();
        if (clipBoardString.equals("")) {
            if (text_selected) {
                int tempCar = caretPos;
                int tempCMark = cMark;

                int startingPos = Math.min(tempCar, tempCMark);
                for (int i = 0; i < getSelectedText_KP().length(); i++)
                    delete(startingPos+1);
            }
        } 
        else {
             String clipBoardString1 = getClipboardContents();
                if (text_selected) {

                    int tempCaretPos = caretPos;
                    int tempCMark = cMark;
                    int startingPos = Math.min(tempCaretPos, tempCMark);
                    
                    for (int i = 0; i < tempCaretPos-tempCMark; i++)
                        delete(startingPos+1);

                    int j = startingPos;
                    for (int i = 0; i < (clipBoardString1.length()); i++) {
                        insert(String.valueOf(clipBoardString1.charAt(i)), j);
                        j++;
                    }

                } else {
                    int j = caretPos;
                    for (int i = 0; i < clipBoardString1.length(); i++) {
                        insert(String.valueOf(clipBoardString1.charAt(i)), j);
                        j++;
                    }
                }
            
        }
	}

	/**
	 * The getSelectedText method is an internal method that we use to get the
	 * text that is currently highlighted (text between the dot and mark)
	 * @return String
	 */
	public String getSelectedText() {
		if (caretPos != cMark) {
			try {
				String toReturn = ClientLoader.textEditorMap.get("" + id).document
						.getText(
								Math.min(caretPos, cMark),
								Math.max(caretPos, cMark)
										- Math.min(caretPos, cMark));
				return toReturn;
			} catch (BadLocationException e) {
				return "";
			}
		}
		return "";
	}
	
	/**
	 * The getSelectedText_KP method performs the same function as the getSelectedText method, but at a different time.
	 * It uses the caretPosition and Mark Position from the time when the key was pressed, rather than the
	 * last time the key was typed. This is important for a few special action characters.
	 * @return String
	 */
    public String getSelectedText_KP() {
        if (caretPos_KP != cMark_KP) {
            try {
                String toReturn = ClientLoader.textEditorMap.get("" + id).document
                        .getText(
                                Math.min(caretPos_KP, cMark_KP),
                                Math.max(caretPos_KP, cMark_KP)
                                        - Math.min(caretPos_KP, cMark_KP));
                return toReturn;
            } catch (BadLocationException e) {
                return "";
            }
        }
        return "";
    }
}