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



public class JTextAreaListen extends JFrame
        implements DocumentListener, KeyListener, ActionListener, CaretListener {
     

    private static final long serialVersionUID = 6950001634065526391L;

    private JTextArea textArea;
     
    protected final PrintWriter out;
    protected final int id;

    protected final BufferedReader in;
    protected static int caretPos;
    private static int cMark;
    protected static boolean text_selected;
    
     
/*
 * Connecting to server.
(1337 is the port we are going to use).

socket = new Socket(InetAddress.getByName("127.0.0.1"), 1337);

(Open a new outStream, you can save this instead of opening on every time you want to send a message. If the connection is lost your should to out.close();

out = new PrintWriter(socket.getOutputStream(), true);

out.print("message"); to send something to the server.
 */
     
     
    public JTextAreaListen(PrintWriter out, BufferedReader in, int id) {
        super("JTextAreaListen");
        this.id = id;
        this.out = out;
        this.in = in;
         
        TextEditor.document.addCaretListener(this);
        TextEditor.document.addKeyListener(this);


    }
     
    // Listener methods
     
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

    @Override
    public void keyPressed(KeyEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void keyTyped(KeyEvent ev) {
        System.out.println("Sth happening!");
        System.out.println(ev.getKeyCode());
        int evID = ev.getID();
        String keyString;
        int keyCode;
        if (evID == KeyEvent.KEY_TYPED) {
            if (ev.getKeyChar()==KeyEvent.CHAR_UNDEFINED){
                keyCode = ev.getKeyCode();
                if (keyCode==8){
                    if (text_selected){
                        if (caretPos > cMark){
                            for (int i = caretPos; i>=cMark; i--){
                                sendMessage("DELETE" + " " + String.valueOf(id) + " " + String.valueOf(cMark+1));
                            }
                        }
                        else if(caretPos < cMark){
                            for (int i = caretPos; i>=cMark; i++){
                                sendMessage("DELETE" + " " + String.valueOf(id) + " " + String.valueOf(cMark+1));
                            }
                        }
                    }
                    else{
                        sendMessage("DELETE" + " " + String.valueOf(id) + " " + String.valueOf(caretPos+1));
                    }
                }
            }
            else{
                char c = ev.getKeyChar();
                boolean capital = ev.isShiftDown();
                String charString = String.valueOf(c);
                if(capital){
                    charString.toUpperCase();
                }
                if (text_selected){
                    if (caretPos > cMark){
                        for (int i = caretPos; i>=cMark; i--){
                            sendMessage("DELETE" + " " + String.valueOf(id) + " " + String.valueOf(cMark+1));
                        }
                        sendMessage("INSERT" + " " + String.valueOf(id) + " " + String.valueOf(cMark) + " " + charString);
                    }
                    else if(caretPos < cMark){
                        for (int i = caretPos; i >=cMark; i++){
                            sendMessage("DELETE" + " "+ String.valueOf(id) + " " + String.valueOf(caretPos+1));
                        }
                        sendMessage("INSERT" + " " + String.valueOf(id) + " " + String.valueOf(caretPos) + " " + charString);
                    }
                }
                else{
                    sendMessage("INSERT" + " " + String.valueOf(id) + " " + String.valueOf(caretPos) + " " + charString);
                }
            }
            
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