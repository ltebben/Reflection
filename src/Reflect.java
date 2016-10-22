import Transmitter.*;
import Receiver.*;
import java.applet.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/* <applet code=Reflect.class width=90 height=22></applet> */

public class Reflect extends Applet implements ActionListener {
   JButton go;

   public void init () {
      setLayout(new BorderLayout());
      add("Center", go = new JButton("Applet"));
      go.addActionListener(this);
   }

   public void actionPerformed (ActionEvent evt) {
      Transmitter tf = new Transmitter();
      tf.setSize(400,120);
      tf.setVisible(true);
      Receiver rf = new Receiver();
      rf.setSize(400,100);
      rf.setVisible(true);
   }
}

