package Transmitter;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;
import java.applet.*;

class Sender {
   Transmitter trn = null;
   byte [] classbytes = null;
   BufferedReader in;
   ObjectOutputStream out;

   public Sender(Transmitter trn) { 
      this.trn = trn; 
      trn.status.setText("Ready to send");
   }

   public void sendit (String classname) {
      InputStream is;
      try {
         if (trn.where.getSelectedIndex() != 1) {
            is = new FileInputStream ("C:/Users/Laura/workspace/Reflection/bin/Transmitter/"+classname+".class");
         } else {
            String host = "http://gauss.ececs.uc.edu/";
            String location = "Courses/c4003/java/Reflection/E3/Transmitter/";
            URL url = new URL(host+location+classname+".class");
            is = (InputStream)url.getContent();
         }
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         int ch;
         while ((ch = is.read()) != -1) buffer.write(ch);
         classbytes = buffer.toByteArray();
      } catch (Exception e) {
         trn.status.setText("No such class: "+classname+".class");
         return;
      }

      try {
         Socket socket = new Socket("localhost", 8670);
         InputStream ins = socket.getInputStream();
         in  = new BufferedReader(new InputStreamReader(ins));
         out = new ObjectOutputStream(socket.getOutputStream());
         out.writeObject(classbytes);
         in.close();
         out.close();
         socket.close();
         trn.status.setText("Class information sent successfully");
      } catch (SocketException e) {
         trn.status.setText("Cannot connect to server...is it running?");
      } catch (IOException e) {
         trn.status.setText("Cannot create stream to server");
      }
   }
}

public class Transmitter extends JFrame implements ActionListener {
   Sender sender = null;
   JTextField status = null;
   JComboBox <String> classname = null, where = null;
   JButton doit = null;
   JLabel label;

   public Transmitter () {
      setLayout(new BorderLayout());
      add("North", label = new JLabel("Transmitter",JLabel.LEFT));
      label.setForeground(Color.blue);
      label.setFont(new Font("TimesRoman",Font.BOLD,18));
      add("Center",status = new JTextField());

      JPanel p = new JPanel();
      p.setLayout(new FlowLayout());
      p.add(classname = new JComboBox <String> ());
      classname.addItem("Weather");
      classname.addItem("Sudoku");
      p.add(new JLabel("  "));
      p.add(doit = new JButton("Send it"));
      p.add(new JLabel("  "));
      p.add(where = new JComboBox <String> ());
      where.addItem("Local");
      where.addItem("Remote");
      add("South",p);
      status.setEditable(false);
      doit.addActionListener(this);
      sender = new Sender(this);
   }

   public void actionPerformed (ActionEvent evt) {
      sender.sendit((String)classname.getSelectedItem());
   }
}
