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
 * JTextAreaListen provides a KeyListener and CaretListener for the TextEditor. This class will send updates to the server whenever a meaningful key is pressed.
 * The KeyListener looks for characters which are typed by the user. The CaretListener observes and reports the position of the caret and mark at
 * any given time.
 * @author e3m3r
 *
 */
public class JTextAreaListen extends JFrame
        implements DocumentListener, KeyListener, CaretListener {
     
    private static final long serialVersionUID = 6950001634065526391L;
    protected final PrintWriter out;
    protected final int id;
    protected int caretPos;
    protected int cMark;
    protected int curr_KeyCode;
    protected boolean text_selected;
     
    
    /**
     * Constructor for the JTextAreaListen. Implements the JTextAreaListen for the document.
     * @param out
     * @param in
     * @param id
     */
    public JTextAreaListen(PrintWriter out, int id) {
        super("JTextAreaListen");
        this.id = id;
        this.out = out;
    }

    /**
     *  A required DocumentListener Method.
     */
    @Override
    public void changedUpdate(DocumentEvent ev) {
        System.out.println(ev.getType());
    }
    
    @Override
    public void removeUpdate(DocumentEvent ev) { 
        System.out.println(ev.getType());
        
    }
    
    @Override
    public void insertUpdate(DocumentEvent ev) {
        System.out.println(ev.getType());
    }
     

    /**
     * keyEventHandler is used to filter all of the events of our KeyListener.
     * @param ev
     */
    public void keyEventHandler(KeyEvent ev){
        int evID = ev.getID();
        if (evID==KeyEvent.KEY_PRESSED){
            System.out.println("KPEV:" + text_selected);
            curr_KeyCode = ev.getKeyCode();
            if(curr_KeyCode == 8){
                System.out.println("CP, CM before DE: " + caretPos + " " + cMark);
                delete(ev);
                System.out.println("CP, CM after DE: " + caretPos + " " + cMark);
            }
            else if(curr_KeyCode == 10){
                if (text_selected){
                    int tempCar3 = caretPos;
                    int tempCMark3 = cMark;
                    System.out.println("tempCar: " + tempCar3);
                    System.out.println("tempCMark: " + tempCMark3);
                    if (caretPos > cMark){
                        for (int i = tempCar3; i>tempCMark3; i--){
                            sendMessage("DELETE" + " " + String.valueOf(id) + " " + String.valueOf(tempCMark3+1));
                        }
                        sendMessage("INSERT" + " " + String.valueOf(id) + " " + String.valueOf(tempCMark3) + " " + "\\n");
                        
                    }
                    else if(caretPos < cMark){
                        for (int i = tempCar3; i<tempCMark3; i++){
                            sendMessage("DELETE" + " " + String.valueOf(id) + " " + String.valueOf(tempCar3+1));
                        }
                        sendMessage("INSERT" + " " + String.valueOf(id) + " " + String.valueOf(tempCar3) + " " + "\\n");
                    }
                }
                else{
                    sendMessage("INSERT" + " " + String.valueOf(id) + " " + String.valueOf(caretPos) + " " + "\\n");
                } 
            }
            
        }
        else if (evID==KeyEvent.KEY_RELEASED){
            System.out.println("KREV: " + text_selected);
            
        }
        else if (evID == KeyEvent.KEY_TYPED){
            System.out.println("KTEV: " + text_selected);
            char kc = ev.getKeyChar();
            System.out.println("KeyChar: " + kc);
            boolean valid_Unicode = (ev.getKeyChar()!=(KeyEvent.CHAR_UNDEFINED));
            
            System.out.println("valid_Unicode: " +String.valueOf(valid_Unicode));
            System.out.println("currkeycode: " + curr_KeyCode);
            System.out.println("CaretPos: " + caretPos);
            System.out.println("cMark: " + cMark);
            System.out.println("text_selected: " + text_selected);
            
            if (curr_KeyCode == 8 || curr_KeyCode == 10){
                
            }
            else {
                boolean capital = ev.isShiftDown();
                String charString = String.valueOf(kc);
                if(capital){
                    charString.toUpperCase();
                }

                if (text_selected){
                    System.out.println("textselected fired");
                    int tempCar2 = caretPos;
                    int tempCMark2 = cMark;
                    if (caretPos > cMark){
                        for (int i = tempCar2; i>tempCMark2; i--){
                            sendMessage("DELETE" + " " + String.valueOf(id) + " " + String.valueOf(tempCMark2+1));
                        }
                        sendMessage("INSERT" + " " + String.valueOf(id) + " " + String.valueOf(tempCMark2) + " " + charString);
                    }
                    else if(caretPos < cMark){
                        for (int i = tempCar2; i <tempCMark2; i++){
                            sendMessage("DELETE" + " "+ String.valueOf(id) + " " + String.valueOf(tempCar2+1));
                        }
                        sendMessage("INSERT" + " " + String.valueOf(id) + " " + String.valueOf(tempCar2) + " " + charString);
                    }
                }
                else{
                    sendMessage("INSERT" + " " + String.valueOf(id) + " " + String.valueOf(caretPos) + " " + charString);
                }  
            } 
        }
        

    }

    /**
     * The keyPressed method is called whenever a key is pressed. Since KeyPressed events have the attribute keyCode, while KeyTyped events
     * do not, we have to get the keyCode from keyPressed. We use keyPressed instead of keyTyped when we are more concerned
     * with the key that has been pressed than the character that has been typed.
     * @param ev
     * 
     */
    @Override
    public void keyPressed(KeyEvent ev) {
        keyEventHandler(ev);
    }
    

    /**
     * The keyReleased method is called whenever a key is released.
     * @param ev
     */
    @Override
    public void keyReleased(KeyEvent ev) {
        keyEventHandler(ev);
    }

    /**
     * The keyTyped method is called whenever a key is pressed and released. We use keyTyped when we are
     * interested in the character that has been typed.
     */
    @Override
    public void keyTyped(KeyEvent ev) {
        keyEventHandler(ev);
    }
    
    public void delete(KeyEvent ev){
        if (text_selected){
            int tempCar = caretPos;
            int tempCMark = cMark;
            System.out.println("tempCar: " + tempCar);
            System.out.println("tempCMark: " + tempCMark);
            if (caretPos > cMark){
                for (int i = tempCar; i>tempCMark; i--){
                    sendMessage("DELETE" + " " + String.valueOf(id) + " " + String.valueOf(tempCMark+1));
                }
            }
            else if(caretPos < cMark){
                for (int i = tempCar; i<tempCMark; i++){
                    sendMessage("DELETE" + " " + String.valueOf(id) + " " + String.valueOf(tempCar+1));
                }
            }
        }
        else{
            sendMessage("DELETE" + " " + String.valueOf(id) + " " + String.valueOf(caretPos));
        } 
    }
    

    
    public void sendMessage(String s){
        out.println(s);
        System.out.println(s);
    }

    @Override
    public void caretUpdate(CaretEvent cev) {
        int dot  = cev.getDot();
        int mark = cev.getMark();
        caretPos = dot;
        cMark = mark;
        if (dot == mark){
            text_selected = false;
        }
        else if((dot < mark) | (dot > mark)){
            text_selected = true;
        }
    }
    
   
     
     
}
