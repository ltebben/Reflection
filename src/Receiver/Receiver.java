package Receiver;

import java.applet.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

class GetClass extends ClassLoader implements Runnable {
	String classname;
	Thread runner = null;
	Receiver rec = null;
	Method [] methlst;
	Method meth = null;
	Method ss = null;
	Method sz = null;
	Object obj = null;
	Class <?> cls = null;
	Hashtable <String,Class> classes = null;
	Hashtable <String,Method> methods = null;
	Hashtable <String,Object> objects = null;

	public GetClass (Receiver rec) { 
		this.rec = rec; 
		classes = new Hashtable <String,Class> ();
		methods = new Hashtable <String,Method> ();
		objects = new Hashtable <String,Object> ();
	}

	public void start () {
		if (runner == null) runner = new Thread(this);
		runner.start();
		rec.status.setText("Waiting for a class to arrive");
	}

	public void run () {
		byte [] classbytes = null;
		try {
			ServerSocket server = new ServerSocket(8670);
			while (true) {
				try {
					Socket socket = server.accept();
					InputStream ins = socket.getInputStream();
					ObjectInputStream in = new ObjectInputStream(ins);
					PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

					classbytes = (byte [])in.readObject();
					try {
						cls = defineClass(null, classbytes, 0, classbytes.length);
						classes.put(cls.getName(),cls);
						rec.status.setText("New class "+cls.getName()+" has arrived");
					} catch (LinkageError e) {
						// We have received this class already
						String str = e.toString();
						String clsname = 
								str.substring(str.lastIndexOf(':')+3,str.length()-1);
						clsname = clsname.replace('/','.');
						cls = classes.get(clsname);
						rec.status.setText("Using old class "+cls.getName());
					}

					
					
					//methlst = cls.getDeclaredMethods();
					/*for (int i=0 ; i < methlst.length ; i++) {
						if (methlst[i].getName().equals("init"))
							meth = methlst[i];
					}
					if (classes.get(cls.getName()) == null) {
						methods.put(classname, meth);
						objects.put(classname, obj);
					}*/
					
					//Get class and methods.
					Constructor <?> ct = cls.getConstructor();
					obj = ct.newInstance();
					Class <?> frame = cls.getSuperclass();
					ss = frame.getMethod("setSize", new Class <?>[] {Integer.TYPE, Integer.TYPE});
					sz = frame.getMethod("setVisible", new Class <?> []{Boolean.TYPE});
					
				} catch (IllegalAccessException e) {
					rec.status.setText("Class "+classname+" not accessible");
				} catch (InstantiationException e) {
					rec.status.setText("Class "+classname+" instantiation failed");
				} catch (NoSuchMethodException e) {
					rec.status.setText("No default constructor in class "+classname);
				} catch (ClassFormatError e) {
					rec.status.setText("Class "+classname+" is not a valid class");
				} catch (Exception e) { 
					rec.status.setText(e.toString()); 
				}
			}
		} catch (Exception f) {
			rec.status.setText(f.toString());
		}
	}
}

public class Receiver extends JFrame implements ActionListener {
	JButton b;
	JTextField status = null;
	JTextField width = null, height = null;
	GetClass gc = null;
	JLabel label;

	public Receiver () {
		setLayout(new BorderLayout());
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		p.add(label = new JLabel("Receiver"));
		p.add(new JLabel("         "));
		p.add(b = new JButton("Start the Applet"));
		add("North",p);
		add("South",status = new JTextField());
		
		//Prompt for width and height. When text fields clicked, clear prompt.
		width = new JTextField("Enter Width");
		height = new JTextField("Enter Height");
		
		width.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                width.setText("");
            }});
		height.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                height.setText("");
            }});
		
		p.add(width);
		p.add(height);
		
		status.setEditable(false);
		b.addActionListener(this);
		label.setForeground(Color.blue);
		label.setFont(new Font("TimesRoman",Font.BOLD,18));
		(gc = new GetClass(this)).start();
	}

	public void actionPerformed (ActionEvent evt) {
		try {
			if (evt.getSource() == b) {
				status.setText("Invoking Constructor");
				//gc.meth.invoke(gc.obj);
				
				//Set width and height. If no size specified, default to 500x600
				int w, h;
				String inputW = width.getText();
				String inputH = height.getText();
				if (inputW.equals("Enter Width")||inputW.equals(""))
					w = 600;
				else
					w = Integer.parseInt(width.getText());
				if (inputH.equals("Enter Height")||inputH.equals(""))
					h = 500;
				else
					h = Integer.parseInt(height.getText());
				
				gc.ss.invoke(gc.obj,new Object [] {w, h});  
				gc.sz.invoke(gc.obj, true);
			}
		} catch (Exception e) {
			status.setText(e.toString());
		}
	}
}
