

import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.applet.*;
import java.security.*;

public class Weather extends JFrame implements ActionListener {
   JTextArea text;
   JComboBox <String> locale = null;
   JButton start;
   String url = "http://tgftp.nws.noaa.gov/data/observations/metar/decoded";

   public Weather () {
      setLayout(new BorderLayout());
      setBackground(new Color(255,255,224));

      add("Center", new JScrollPane(text = new JTextArea()));

      JPanel p = new JPanel();
      p.setLayout(new FlowLayout());
      p.setBackground(new Color(255,255,224));
      p.add(start = new JButton("Fetch"));
      p.add(new JLabel("  "));
      p.add(locale = new JComboBox <String> ());
      add("South", p);

      start.addActionListener(this);
      start.setFont(new Font("TimesRoman", Font.PLAIN, 18));

      locale.addItem("KCVG (Cincinnati)");
      locale.addItem("KLUK (Cincinnati)");
      locale.addItem("KHAO (Hamilton)");
      locale.addItem("KDAY (Dayton)");
      locale.addItem("KBWI (Baltimore)");
      locale.addItem("KFME (Fort Meade)");
      locale.addItem("KPIT (Pittsburgh)");
      locale.addItem("KOAK (Oakland)");
      locale.addItem("KCLE (Cleveland)");
      locale.addItem("KZZV (Zanesville)");
      locale.addItem("KCMH (Columbus)");
      locale.addItem("KORD (Chicago, O'Hare)");
      locale.addItem("KDPA (Chicago, Du Page)");
      locale.addItem("KPWA (Wiley Post, OK)");
      locale.addItem("KNYC (New York)");
      locale.addItem("KSFO (San Francisco)");
      locale.addItem("KLAX (Los Angeles)");
      locale.addItem("CYYZ (Toronto)");
      locale.addItem("CWBA (Banff)");
      locale.addItem("SBRJ (Rio De Janeiro)");
      locale.addItem("EGLL (London)");
      locale.addItem("EGPH (Edinburgh)");
      locale.addItem("LFPB (Paris)");
      locale.addItem("LIRF (Rome)");
      locale.addItem("EDDM (Munchen)");
      locale.addItem("KQTZ (Baghdad)");
      locale.addItem("UUDD (Moscow)");
      locale.addItem("UTTT (Tashkent)");
      locale.addItem("ZGGG (Guangzhou)");
      locale.addItem("VIDP (Delhi)");
      locale.addItem("YSSY (Sydney)");
      locale.addItem("NZAA (Auckland)");
      locale.addItem("NZSP (Antartica)");
      locale.addItem("NVVV (Vanuatu)");
      locale.setFont(new Font("TimesRoman", Font.PLAIN, 18));


   }

   public void actionPerformed (ActionEvent evt) {
      text.setText("");
      String line;
      String[] place=((String)locale.getSelectedItem()).split(" ");
      try {
	 URL weather = new URL(url+"/"+place[0]+".TXT");
	 InputStream is = (InputStream)weather.getContent();
	 InputStreamReader isr = new InputStreamReader(is);
	 BufferedReader bis = new BufferedReader(isr);
	 while ((line = bis.readLine()) != null) {
	    text.append(line+"\n");
	    int l = text.getDocument().getLength();
	    text.setCaretPosition(l);
	 }
      } catch (Exception e) { 
	 text.append(e.toString());
      }
   }
}
