import java.io.*;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.*;

public class NotePadClass extends JFrame implements ActionListener, WindowListener {

    JTextArea jta = new JTextArea();
    JTextField status = new JTextField();
    File fnameContainer;

    public NotePadClass(){
        Font fnt = new Font("Arial", Font.PLAIN,15);
        Container con = getContentPane();
        JMenuBar jmb = new JMenuBar();
        JMenu jmfile = new JMenu("File");
        JMenu jmedit = new JMenu("Edit");
        JMenu jmhelp = new JMenu("Help");

        con.setLayout(new BorderLayout());
        JScrollPane sbrText = new JScrollPane(jta);
        sbrText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sbrText.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        sbrText.setVisible(true);

        jta.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                JTextArea editArea = (JTextArea)e.getSource();

                // Lets start with some default values for the line and column.
                int linenum = 1;
                int columnnum = 1;

                // We create a try catch to catch any exceptions. We will simply ignore such an error for our demonstration.
                try {
                    // First we find the position of the caret. This is the number of where the caret is in relation to the start of the JTextArea
                    // in the upper left corner. We use this position to find offset values (eg what line we are on for the given position as well as
                    // what position that line starts on.
                    int caretpos = editArea.getCaretPosition();
                    linenum = editArea.getLineOfOffset(caretpos);

                    // We subtract the offset of where our line starts from the overall caret position.
                    // So lets say that we are on line 5 and that line starts at caret position 100, if our caret position is currently 106
                    // we know that we must be on column 6 of line 5.
                    columnnum = caretpos - editArea.getLineStartOffset(linenum);

                    // We have to add one here because line numbers start at 0 for getLineOfOffset and we want it to start at 1 for display.
                    linenum += 1;
                }
                catch(Exception ex) { }

                // Once we know the position of the line and the column, pass it to a helper function for updating the status bar.
                updateStatus(linenum, columnnum);
            }
        });
        jta.setFont(fnt);
        jta.setLineWrap(false);
        jta.setWrapStyleWord(false);

        add(status, BorderLayout.SOUTH);

        con.add(sbrText);

        createMenuItem(jmfile, "New");
        createMenuItem(jmfile, "Open");
        createMenuItem(jmfile, "Save");
        jmfile.addSeparator();
        createMenuItem(jmedit, "Exit");
        createMenuItem(jmfile, "Cut");
        createMenuItem(jmfile, "Copy");
        createMenuItem(jmfile, "Paste");

        createMenuItem(jmhelp, "About Notepad");

        jmb.add(jmfile);
        jmb.add(jmedit);
        jmb.add(jmhelp);

        setJMenuBar(jmb);

        setIconImage(Toolkit.getDefaultToolkit().createImage("notepad.gif"));
        addWindowListener(this);
        setSize(500,500);
        setTitle("Untitled.txt - Notepad");
        setVisible(true);

        updateStatus(1,1);
    }

    private void updateStatus(int linenumber, int columnnumber) {
        status.setText("Line: " + linenumber + " Column: " + columnnumber);
    }

    public void createMenuItem(JMenu jm, String txt){
        JMenuItem jmi = new JMenuItem(txt);
        jmi.addActionListener(this);
        jm.add(jmi);
    }

    public void actionPerformed(ActionEvent e){
        JFileChooser jfc = new JFileChooser();
        if(e.getActionCommand().equals("New")){
            this.setTitle("Untitled.txt - Notepad");
            jta.setText("");
            fnameContainer = null;
        }else if(e.getActionCommand().equals("Open")){
            int ret = jfc.showDialog(null, "Open");
            if(ret == JFileChooser.APPROVE_OPTION){
                try{
                    File fyl = jfc.getSelectedFile();
                    openFile(fyl.getAbsolutePath());
                    this.setTitle(fyl.getName() + " - Notepad");
                    fnameContainer = fyl;
                }catch(IOException ex){

                }
            }
        }else if(e.getActionCommand().equals("Save")){
            if(fnameContainer!=null){
                jfc.setCurrentDirectory((fnameContainer));
                jfc.setSelectedFile(fnameContainer);
            }else{
                jfc.setSelectedFile(new File("Untitled.txt"));
            }
            int ret = jfc.showSaveDialog(null);
            if(ret == JFileChooser.APPROVE_OPTION){
                try{
                    File fyl = jfc.getSelectedFile();
                    saveFile(fyl.getAbsolutePath());
                    this.setTitle(fyl.getName() + " - Notepad");
                    fnameContainer = fyl;
                }catch(Exception ex){}
            }
        }else if(e.getActionCommand().equals("Exit")){
            Exiting();
        }else if(e.getActionCommand().equals("Copy")){
            jta.copy();
        }else if(e.getActionCommand().equals("Paste")){
            jta.paste();
        }else if(e.getActionCommand().equals("About Notepad")){
            JOptionPane.showMessageDialog(this, "Simple Notepad", "Notepad", JOptionPane.INFORMATION_MESSAGE);
        }else if (e.getActionCommand().equals("Cut")){
            jta.cut();
        }
    }

    public void DocumentListener(){

    }

    public void openFile(String fname) throws IOException{
        BufferedReader d = new BufferedReader(new InputStreamReader(new FileInputStream(fname)));
        String l;
        jta.setText("");
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        while((l=d.readLine()) != null){
            jta.setText(jta.getText() + 1 +"\r\n");
        }
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        d.close();
    }

    public void saveFile(String fname) throws IOException{
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        DataOutputStream o = new DataOutputStream(new FileOutputStream(fname));
        o.writeBytes(jta.getText());
        o.close();
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    public void windowDeactivated(Window e){}

    public void windowActivated(Window e){}

    public void windowDeIconified(Window e) {}

    public void windowIconified(Window e){}

    public void windowClosed(Window e) {}

    public void windowClosing(Window e) {
        Exiting();
    }

    public void windowOpened(Window e) {}

    public void Exiting(){
        System.exit(0);
    }


    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }



}

