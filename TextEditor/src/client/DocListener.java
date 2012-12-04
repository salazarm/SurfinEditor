package client;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

public class DocListener{
    //where initialization occurs:
    textField = new JTextField(20);
    textField.addActionListener(new MyTextActionListener());
    textField.getDocument().addDocumentListener(new MyDocumentListener());
    textField.getDocument().putProperty("name", "Text Field");

    textArea = new JTextArea();
    textArea.getDocument().addDocumentListener(new MyDocumentListener());
    textArea.getDocument().putProperty("name", "Text Area");
}



class MyDocumentListener implements DocumentListener {
    
    public class Pair<L,R> {
        private L l;
        private R r;
        public Pair(L l, R r){
            this.l = l;
            this.r = r;
        }
        public L getL(){ return l; }
        public R getR(){ return r; }
        public void setL(L l){ this.l = l; }
        public void setR(R r){ this.r = r; }
    }
    List<Pair<String, Integer>> updateLog = new ArrayList<Pair<String, Integer>>();
    String newline = "\n";
 
    /*
     * handles character inserts by user.
     * @param e, DocumentEvent e
     * 
     */
    public void insertUpdate(DocumentEvent e) {
        updateUpdateLog(e, "inserted into");
    }
    
    /*
     * handles character removes by the user
     * @param e, DocumentEvent e
     * 
     */
    public void removeUpdate(DocumentEvent e) {
        updateUpdateLog(e, "removed from");
    }
    
    /*
     * handles other types of updates
     * @param e, DocumentEvent e
     */
    public void changedUpdate(DocumentEvent e) {
        //Plain text components do not fire these events
    }

    /*
     * updates the updateLog by appending events to it
     */
    public void updateUpdateLog(DocumentEvent e, String action) {
        Document doc = (Document)e.getDocument();
        int changeLength = e.getLength();
        /*
        displayArea.append(
            changeLength + " character" +
            ((changeLength == 1) ? " " : "s ") +
            action + doc.getProperty("name") + "." + newline +
            "  Text length = " + doc.getLength() + newline);
            */
    }
    
    /*
     * gets the updateLog
     */
    public List<Pair<String, Integer>> getUpdateLog(){
        return updateLog;
    }
    
    /*
     * sends parsed event to server.
     * @param updateLog, an array of updates. 
     *
     */
    public void sendUpdatesToServer(List<Pair<String, Integer>> updateLog){
        
    }
    
    
    
    
}}