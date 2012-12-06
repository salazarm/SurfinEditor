package client;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

public class DocListener {
    //where initialization occurs:
    //ed
    textArea = new JTextArea();
    textArea.getDocument().addDocumentListener(new MyDocumentListener());
    textArea.getDocument().putProperty("name", "Text Area");
}


/*
 * This is our DocumentListener for our particular model.
 */
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
        int index=0;
        String curr_char;
        String insert_string = "insert";
        String final_string;
        
        final_string = insert_string + " " + curr_char;
        Pair<String, Integer> currpair = new Pair<String, Integer>(final_string, index);
        updateUpdateLog(currpair);
    }
    
    /*
     * handles character removes by the user
     * @param e, DocumentEvent e
     * 
     */
    public void removeUpdate(DocumentEvent e) {
        int index=0;
        String curr_char;
        String remove_string = "remove";
        String final_string;
        
        final_string = remove_string + " " + curr_char;
        Pair<String, Integer> currpair = new Pair<String, Integer>(final_string, index);
        updateUpdateLog(currpair);
    }
    
    /*
     * handles other types of updates.Actually though, it isn't included in our design, but exists in the DocumentListener class.
     * @param e, DocumentEvent e
     */
    public void changedUpdate(DocumentEvent e) {
        //does not happen
    }

    /*
     * updates the updateLog by appending events to it
     */
    public void updateUpdateLog(Pair<String, Integer> action) {
        updateLog.add(action);
        /*
        Document doc = (Document)e.getDocument();
        int changeLength = e.getLength();
        
        displayArea.append(
            changeLength + " character" +
            ((changeLength == 1) ? " " : "s ") +
            action + doc.getProperty("name") + "." + newline +
            "  Text length = " + doc.getLength() + newline);
            */
    }
    
    /*
     * gets the updateLog
     * @return List<Pair<String, Integer>> updateLog
     */
    public List<Pair<String, Integer>> getUpdateLog(){
        return updateLog;
    }
    
    /*
     * sends parsed event to server.
     * @param updateLog, an array of updates.
     * @param docID, document ID 
     *
     */
    public void sendUpdatesToServer(List<Pair<String, Integer>> updateLog, Integer docID){
        
    }
    
    
    
    
}}