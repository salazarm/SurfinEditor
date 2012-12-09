package client;

import javax.swing.*;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;


/**
 * JTextAreaListen provides a KeyListener and CaretListener for the TextEditor. This class will send updates to the server whenever a meaningful key is pressed.
 * The KeyListener looks for characters which are typed by the user. The CaretListener observes and reports the position of the caret and mark at
 * any given time.
 * @author e3m3r
 *
 */
public class JTextAreaListen extends JFrame
        implements DocumentListener, KeyListener, ActionListener, CaretListener {
     

    private static final long serialVersionUID = 6950001634065526391L;

    private JTextArea textArea;
     
    protected final PrintWriter out;
    protected final int id;

    protected final BufferedReader in;
    protected static int caretPos;
    protected static int cMark;
    private static int curr_KeyCode;
    protected static boolean text_selected;
     
    
    /**
     * Constructor for the JTextAreaListen. Implements the JTextAreaListen for the document.
     * @param out
     * @param in
     * @param id
     */
    public JTextAreaListen(PrintWriter out, BufferedReader in, int id) {
        super("JTextAreaListen");
        this.id = id;
        this.out = out;
        this.in = in;
         
        //TextEditor.document.addCaretListener(this);
        //TextEditor.document.addKeyListener(this);


    }

    /**
     *  A required DocumentListener Method.
     */
    public void changedUpdate(DocumentEvent ev) {
    }
     
    public void removeUpdate(DocumentEvent ev) { 
        
    }
     
    public void insertUpdate(DocumentEvent ev) {
    }
     

    @Override
    public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    /**
     * The KeyPressed method is called whenever a key is pressed. Since KeyPressed events have the attribute keyCode, while KeyTyped events
     * do not, we have to get the keyCode from keyPressed.
     */
    @Override
    public void keyPressed(KeyEvent ev) {
        curr_KeyCode = ev.getKeyCode();
        
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    /**
     * 
     */
    @Override
    public void keyTyped(KeyEvent ev) {
        System.out.println("Sth happening!");
        char kc = ev.getKeyChar();
        System.out.println("KeyChar: " + kc);
        boolean valid_Unicode = (ev.getKeyChar()!=(KeyEvent.CHAR_UNDEFINED));
        
        System.out.println("valid_Unicode: " +String.valueOf(valid_Unicode));
        System.out.println("currkeycode: " + curr_KeyCode);
        int evID = ev.getID();
        String keyString;
        int keyCode = curr_KeyCode;
        
        if(curr_KeyCode == 8){
            delete(ev);
        }
        else {
            boolean capital = ev.isShiftDown();
            String charString = String.valueOf(kc);
            if(capital){
                charString.toUpperCase();
            }
            if (text_selected){
                int tempCar2 = caretPos;
                int tempCMark2 = cMark;
                if (caretPos > cMark){
                    for (int i = tempCar2; i>=tempCMark2; i--){
                        sendMessage("DELETE" + " " + String.valueOf(id) + " " + String.valueOf(tempCMark2+1));
                    }
                    sendMessage("INSERT" + " " + String.valueOf(id) + " " + String.valueOf(tempCMark2) + " " + charString);
                }
                else if(caretPos < cMark){
                    for (int i = tempCar2; i >=tempCMark2; i++){
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
    
    public void delete(KeyEvent ev){
        if (text_selected){
            int tempCar = caretPos;
            int tempCMark = cMark;
            System.out.println("tempCar: " + tempCar);
            System.out.println("tempCMark: " + tempCMark);
            if (caretPos > cMark){
                for (int i = tempCar; i>=tempCMark; i--){
                    sendMessage("DELETE" + " " + String.valueOf(id) + " " + String.valueOf(tempCMark+1));
                }
            }
            else if(caretPos < cMark){
                for (int i = tempCar; i>=tempCMark; i++){
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