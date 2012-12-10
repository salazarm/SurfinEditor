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
import javax.swing.JOptionPane;
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
			// the keyCode 8 refers to "delete".
			if (curr_KeyCode == 8) {
				// We wish to handle deletes during the KEY_PRESSED event,
				// because the selected text is preserved in this event.
				if (text_selected) {
					deleteSelectedText();
				} else {
					singularDelete();
				}
			}
			// The keyCode 10 refers to "ENTER", indicating a line break.
			else if (curr_KeyCode == 10) {
				// For selected text, we want to send deletes and replace it
				// with a line break.
				if (text_selected) {
					replaceSelectedText("\\n");
				} else {
					singularInsert("\\n", caretPos);
				}
			}

		} else if (evID == KeyEvent.KEY_RELEASED) {

		} else if (evID == KeyEvent.KEY_TYPED) {
			char kc = ev.getKeyChar();

			if (curr_KeyCode == 8 || curr_KeyCode == 10 || ev.isActionKey()) {
				// pass over deletes and enters, since they've already been
				// taken care of.

			} else {
				String charString = String.valueOf(kc);
				System.out.println(charString+" int: "+(int)charString.charAt(0));
				//
				if (!ev.isControlDown()) {
					if (ev.isShiftDown()) {
						charString.toUpperCase();
					}
					if (text_selected) {
						replaceSelectedText(charString);

					} else {
						singularInsert(charString, caretPos);
					}
				} else {
					//JOptionPane.showMessageDialog(null, "Control is disabled.",
							//"Error", JOptionPane.ERROR_MESSAGE);
					/*
					 * We care about the Cut and Paste commands, because they
					 * affect the contents of the document in a way that the
					 * other user won't see unless a message is sent to the
					 * server.
					 */
					if (!((int)charString.charAt(0) == 24 || (int)charString.charAt(0) == 22)) {
						/*
						 * We do nothing. As long as control is down, the only
						 * other relevant commands are Ctrl+A and Ctrl+C, but we
						 * don't need to send any message for those. For any
						 * other Ctrl+(char), we expect no action.
						 */

					} else if ((int)charString.charAt(0) == 24) {
						/*
						 * if text is selected during a cut command, we need to
						 * send messages to delete each character in the
						 * selection.
						 */
						if (text_selected) {
							deleteSelectedText();
						} else {
							// if no text is selected during a cut command,
							// nothing happens.
						}
					} else if ((int)charString.charAt(0) == 22) {
						// Somehow send the contents of the clipboard one at a
						// time.
						String clipBoardString = getClipboardContents();
						if (clipBoardString.equals("")) {
							if (text_selected) {
								deleteSelectedText();
							}
						} else {
							pasteOverwrite();
						}
					}

				}

			}
		}

	}

	/**
	 * Handles paste messages.
	 */
	public void pasteOverwrite() {
		String clipBoardString = getClipboardContents();
		int tempCaretPos = caretPos;
		int tempCMark = cMark;
		if (text_selected) {
			int startingPos = Math.min(tempCaretPos, tempCMark);
			deleteSelectedText();
			int j = startingPos;
			for (int i = 0; i < (clipBoardString.length()); i++) {
				singularInsert(String.valueOf(clipBoardString.charAt(i)), j);
				j++;
			}

		} else {
			int j = caretPos;
			for (int i = 0; i < clipBoardString.length(); i++) {
				singularInsert(String.valueOf(clipBoardString.charAt(i)), j);
				j++;
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
				System.out.println(ex);
				ex.printStackTrace();
			} catch (IOException ex) {
				System.out.println(ex);
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
	 * This is a helper method for deletes of text blocks. It sends a message to
	 * delete each character in the highlighted text by sending a delete message
	 * on the same index as many times as the length of the selected text block.
	 * 
	 */
	public void deleteSelectedText() {
		if (text_selected) {
			int tempCar = caretPos;
			int tempCMark = cMark;
			if (caretPos > cMark) {
				for (int i = tempCar; i > tempCMark; i--) {
					ClientLoader.sdl.sendMessage("DELETE" + " "
							+ String.valueOf(id) + " "
							+ String.valueOf(tempCMark + 1));
					System.out.println("DELETE" + " " + String.valueOf(id)
							+ " " + String.valueOf(tempCMark + 1));
				}
			} else if (caretPos < cMark) {
				for (int i = tempCar; i < tempCMark; i++) {
					ClientLoader.sdl.sendMessage("DELETE" + " "
							+ String.valueOf(id) + " "
							+ String.valueOf(tempCar + 1));
					System.out.println("DELETE" + " " + String.valueOf(id)
							+ " " + String.valueOf(tempCar + 1));
				}
			}
		} else {
			ClientLoader.sdl.sendMessage("DELETE" + " " + String.valueOf(id)
					+ " " + String.valueOf(caretPos));
			System.out.println("DELETE" + " " + String.valueOf(id) + " "
					+ String.valueOf(caretPos));
		}
	}

	/**
	 * This helper method replaces selected text.
	 * 
	 * @param r
	 */
	public void replaceSelectedText(String r) {
		int tempCar3 = caretPos;
		int tempCMark3 = cMark;
		if (caretPos > cMark) {
			for (int i = tempCar3; i > tempCMark3; i--) {
				ClientLoader.sdl.sendMessage("DELETE" + " "
						+ String.valueOf(id) + " "
						+ String.valueOf(tempCMark3 + 1));
				System.out.println("DELETE" + " " + String.valueOf(id) + " "
						+ String.valueOf(tempCMark3 + 1));
			}
			ClientLoader.sdl.sendMessage("INSERT" + " " + String.valueOf(id)
					+ " " + String.valueOf(tempCMark3) + " " + r);
			System.out.println("INSERT" + " " + String.valueOf(id) + " "
					+ String.valueOf(tempCMark3) + " " + r);

		} else if (caretPos < cMark) {
			for (int i = tempCar3; i < tempCMark3; i++) {
				ClientLoader.sdl.sendMessage("DELETE" + " "
						+ String.valueOf(id) + " "
						+ String.valueOf(tempCar3 + 1));
				System.out.println("DELETE" + " " + String.valueOf(id) + " "
						+ String.valueOf(tempCar3 + 1));
			}
			ClientLoader.sdl.sendMessage("INSERT" + " " + String.valueOf(id)
					+ " " + String.valueOf(tempCar3) + " " + r);
			System.out.println("INSERT" + " " + String.valueOf(id) + " "
					+ String.valueOf(tempCar3) + " " + r);
		}
	}

	public void singularInsert(String insertString, int index) {
		ClientLoader.sdl.sendMessage("INSERT" + " " + String.valueOf(id) + " "
				+ index + " " + insertString);
		System.out.println("INSERT" + " " + id + " " + index + " "
				+ insertString);
	}

	public void singularDelete() {
		ClientLoader.sdl.sendMessage("DELETE" + " " + String.valueOf(id) + " "
				+ String.valueOf(caretPos));
		System.out.println("DELETE" + " " + String.valueOf(id) + " "
				+ String.valueOf(caretPos));
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

	public String getSelectedText() {
		System.out.println("inside getSelectedTest()");
		if (caretPos != cMark) {
			try {
				String toReturn = ClientLoader.textEditorMap.get("" + id).document
						.getText(Math.min(caretPos, cMark),
								Math.max(caretPos, cMark)-Math.min(caretPos, cMark));
				System.out.println("NEED TO RETURN"+toReturn);
				return toReturn;
			} catch (BadLocationException e) {
				return "";
			}
		}
		return "";
	}

}