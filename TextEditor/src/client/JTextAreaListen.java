package client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.PrintWriter;
import javax.swing.JFrame;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
public class JTextAreaListen extends JFrame implements
        KeyListener, CaretListener {

    private static final long serialVersionUID = 6950001634065526391L;
    protected final PrintWriter out;
    protected final int id;
    protected int caretPos;
    protected int cMark;
    protected int curr_KeyCode;
    protected boolean text_selected;

    /**
     * Constructor for the JTextAreaListen. Implements the JTextAreaListen for
     * the document. Requires the PrintWriter to the server socket, and the document ID for
     * the document that we are editing.
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
        //There are three KeyEvents that we are interested in filtering: KEY_PRESSED, KEY_RELEASED, and KEY_TYPED
        if (evID == KeyEvent.KEY_PRESSED) {
            System.out.println("KPEV:" + text_selected);
            curr_KeyCode = ev.getKeyCode();
            System.out.println("KPEV KEYCODE: " + curr_KeyCode);
            //the keyCode 8 refers to "delete".
            if (curr_KeyCode == 8) {
                System.out.println("CP, CM before DE: " + caretPos + " "
                        + cMark);
                delete(ev);
                System.out
                        .println("CP, CM after DE: " + caretPos + " " + cMark);
            } else if (curr_KeyCode == 10) {
                if (text_selected) {
                    int tempCar3 = caretPos;
                    int tempCMark3 = cMark;
                    System.out.println("tempCar: " + tempCar3);
                    System.out.println("tempCMark: " + tempCMark3);
                    if (caretPos > cMark) {
                        for (int i = tempCar3; i > tempCMark3; i--) {
                            ClientLoader.sdl.sendMessage("DELETE" + " " + String.valueOf(id)
                                    + " " + String.valueOf(tempCMark3 + 1));
                        }
                        ClientLoader.sdl.sendMessage("INSERT" + " " + String.valueOf(id) + " "
                                + String.valueOf(tempCMark3) + " " + "\\n");

                    } else if (caretPos < cMark) {
                        for (int i = tempCar3; i < tempCMark3; i++) {
                            ClientLoader.sdl.sendMessage("DELETE" + " " + String.valueOf(id)
                                    + " " + String.valueOf(tempCar3 + 1));
                        }
                        ClientLoader.sdl.sendMessage("INSERT" + " " + String.valueOf(id) + " "
                                + String.valueOf(tempCar3) + " " + "\\n");
                    }
                } else {
                    ClientLoader.sdl.sendMessage("INSERT" + " " + String.valueOf(id) + " "
                            + String.valueOf(caretPos) + " " + "\\n");
                }
            }

        } else if (evID == KeyEvent.KEY_RELEASED) {
            System.out.println("KREV: " + text_selected);

        } else if (evID == KeyEvent.KEY_TYPED) {
            System.out.println("KTEV: " + text_selected);
            char kc = ev.getKeyChar();
            System.out.println("KeyChar: " + kc);
            boolean valid_Unicode = (ev.getKeyChar() != (KeyEvent.CHAR_UNDEFINED));

            System.out.println("valid_Unicode: "
                    + String.valueOf(valid_Unicode));
            System.out.println("currkeycode: " + curr_KeyCode);
            System.out.println("CaretPos: " + caretPos);
            System.out.println("cMark: " + cMark);
            System.out.println("text_selected: " + text_selected);

            if (curr_KeyCode == 8 || curr_KeyCode == 10) {

            } else {
                String charString = String.valueOf(kc);
                if (!ev.isControlDown()) {
                    if (ev.isShiftDown()) {
                        charString.toUpperCase();
                    }
                    if (text_selected) {
                        System.out.println("textselected fired");
                        int tempCar2 = caretPos;
                        int tempCMark2 = cMark;
                        if (caretPos > cMark) {
                            for (int i = tempCar2; i > tempCMark2; i--) {
                                ClientLoader.sdl.sendMessage("DELETE" + " " + String.valueOf(id)
                                        + " " + String.valueOf(tempCMark2 + 1));
                            }
                            ClientLoader.sdl.sendMessage("INSERT" + " " + String.valueOf(id)
                                    + " " + String.valueOf(tempCMark2) + " "
                                    + charString);
                        } else if (caretPos < cMark) {
                            for (int i = tempCar2; i < tempCMark2; i++) {
                                ClientLoader.sdl.sendMessage("DELETE" + " " + String.valueOf(id)
                                        + " " + String.valueOf(tempCar2 + 1));
                            }
                            ClientLoader.sdl.sendMessage("INSERT" + " " + String.valueOf(id)
                                    + " " + String.valueOf(tempCar2) + " "
                                    + charString);
                        }
                    } else {
                        ClientLoader.sdl.sendMessage("INSERT" + " " + String.valueOf(id) + " "
                                + String.valueOf(caretPos) + " " + charString);
                    }
                } else {
                    /*
                     * We care about the Cut and Paste commands, because they
                     * affect the contents of the document in a wayt the other
                     * user won't see unless a message is sent to the server.
                     */
                    if (!(charString.equals("x") | charString.equals("v"))) {
                        /*
                         * We do nothing. As long as control is down, the only other relevant commands are Ctrl+A and Ctrl+C, but we
                         * don't need to send any message for those. For any other Ctrl+(char), we expect no action.
                         */
                        
                    } else if (charString.equals("x")) {
                        /*if text is selected during a cut command, we need to send messages to 
                         * delete each character in the selection.
                         */
                        if (text_selected) {
                            int tempCar = caretPos;
                            int tempCMark = cMark;
                            System.out.println("tempCar: " + tempCar);
                            System.out.println("tempCMark: " + tempCMark);
                            if (caretPos > cMark) {
                                for (int i = tempCar; i > tempCMark; i--) {
                                    ClientLoader.sdl.sendMessage("DELETE" + " "
                                            + String.valueOf(id) + " "
                                            + String.valueOf(tempCMark + 1));
                                }
                            } else if (caretPos < cMark) {
                                for (int i = tempCar; i < tempCMark; i++) {
                                    ClientLoader.sdl.sendMessage("DELETE" + " "
                                            + String.valueOf(id) + " "
                                            + String.valueOf(tempCar + 1));
                                }
                            }
                        }
                        else{
                            //if no text is selected during a cut command, nothing happens.
                        }
                    } else if (charString.equals("v")) {
                        // Somehow send the contents of the clipboard one at a
                        // time.
                    }

                }

            }
        }

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
     */
    @Override
    public void keyTyped(KeyEvent ev) {
        keyEventHandler(ev);
    }

    public void delete(KeyEvent ev) {
        if (text_selected) {
            int tempCar = caretPos;
            int tempCMark = cMark;
            System.out.println("tempCar: " + tempCar);
            System.out.println("tempCMark: " + tempCMark);
            if (caretPos > cMark) {
                for (int i = tempCar; i > tempCMark; i--) {
                    ClientLoader.sdl.sendMessage("DELETE" + " " + String.valueOf(id) + " "
                            + String.valueOf(tempCMark + 1));
                }
            } else if (caretPos < cMark) {
                for (int i = tempCar; i < tempCMark; i++) {
                    ClientLoader.sdl.sendMessage("DELETE" + " " + String.valueOf(id) + " "
                            + String.valueOf(tempCar + 1));
                }
            }
        } else {
            ClientLoader.sdl.sendMessage("DELETE" + " " + String.valueOf(id) + " "
                    + String.valueOf(caretPos));
        }
    }


    /**
     * The caretUpdate method is a method of CaretListener, and it will occur at any CaretEvent. This method allows us to update the index values of caret and mark.
     * This will tell us their location in real-time. We are also interested in whether or not text is highlighted, and we can
     * discern this using our CaretListener.
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

}
