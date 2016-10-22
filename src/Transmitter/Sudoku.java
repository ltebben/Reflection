package Transmitter;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Sudoku extends JFrame implements ActionListener, KeyListener, Runnable {
   JTextField numbs[][] = new JTextField[9][9], bcktrk;
   JComboBox <String> speed;
   JButton start, clearall, clear, file;
   int hasfocus = 0;
   boolean new_file = false;
   File inpfile = null;
   int board[][], backs;
   boolean fixed[];
   Thread runner = null;

   // Initialize the fixed array: elements of the array correspond to matrix
   // positions increasing in row major order.  fixed[i] = false means that 
   // number correponding to position [i/9][i%9] has not been fixed in the 
   // input, fixed[i] = true otherwise.
  public Sudoku() {
      setLayout(new BorderLayout());

      JPanel p = new JPanel();
      p.setLayout(new GridLayout(11,11));
      for (int i=0 ; i < 9 ; i++) {
         if (i==3 || i==6) for (int j=0 ; j < 11 ; j++) p.add(new JLabel(""));
         for (int j=0 ; j < 9 ; j++) {
            if (j == 3 || j == 6) p.add(new JLabel(""));
            numbs[i][j] = new JTextField(" ",1);
            numbs[i][j].setFont(new Font("TimesRoman", Font.PLAIN, 22));
            numbs[i][j].setEditable(true);
            numbs[i][j].setHorizontalAlignment(JTextField.CENTER);
            numbs[i][j].addActionListener(this);
            numbs[i][j].addKeyListener(this);
            p.add(numbs[i][j]);
         }
      }

      add("Center",p);

      p = new JPanel();
      p.setLayout(new FlowLayout(FlowLayout.CENTER,10,10));
      p.add(start = new JButton("Solve"));
      p.add(speed = new JComboBox <String> ());
      p.add(new JLabel("choices:",JLabel.RIGHT));
      p.add(bcktrk = new JTextField(5));
      p.add(new JLabel("  "));
      p.add(file = new JButton("File"));
      p.add(clearall = new JButton("Clear All"));
      p.add(clear = new JButton("Reset"));
      add("South",p);

      speed.addItem("Fast");
      speed.addItem("Med");
      speed.addItem("Slow");
      speed.addItem("Crawl");

      bcktrk.setText("0");
      bcktrk.setHorizontalAlignment(JTextField.RIGHT);
      bcktrk.setFont(new Font("TimesRoman", Font.PLAIN, 18));
      bcktrk.setEditable(false);

      start.addActionListener(this);

      file.addActionListener(this);

      clear.addActionListener(this);
      clearall.addActionListener(this);

      clearAll();

      setUp();
      new_file = true;


   }

   public void setUp () {
      int[] inp ={ 
         0,6,0, 1,0,4, 0,5,0, 0,0,8, 3,0,5, 6,0,0, 2,0,0, 0,0,0, 0,0,1,
         8,0,0, 4,0,7, 0,0,6, 0,0,6, 0,0,0, 3,0,0, 7,0,0, 9,0,1, 0,0,4,
         5,0,0, 0,0,0, 0,0,2, 0,0,7, 2,0,6, 9,0,0, 0,4,0, 5,0,8, 0,7,0
      };
      
      int k = 0;
      int number = 0;
      for (int i=0 ; i < 9 ; i++) {
         for (int j=0 ; j < 9 ; j++) {
            board[i][j] = inp[k++];
            if (board[i][j] != 0) {
               fixed[i*9+j] = true;
               numbs[i][j].setEditable(false);
               numbs[i][j].setBackground(new Color(150,150,150));
               numbs[i][j].setForeground(new Color(255,255,255));
            } else {
               fixed[i*9+j] = false;
               numbs[i][j].setBackground(new Color(232,232,232));
               numbs[i][j].setForeground(new Color(0,0,0));
            }
         }
      }
      printBoard();
   }

   public void printBoard () {
      for (int i=0 ; i < 9 ; i++) {
         for (int j=0 ; j < 9 ; j++) {
            if (board[i][j] == 0) {
               numbs[i][j].setText("");
            } else {
               String str = String.valueOf((char)(board[i][j]+'0'));
               numbs[i][j].setText(str);
            }
         }
      }
   }

   public void readFile (File file) {
      byte buffer[] = null;
      if (!file.exists()) {
         System.out.println("File "+file.getName()+" does not exist");
         return;
      }

      try {
         DataInputStream in = new DataInputStream(new FileInputStream(file));
         buffer = new byte[(int)file.length()];
         in.readFully(buffer);
         in.close();
      } catch (IOException e) {
         System.out.println("Exception: "+e.toString());
         System.exit(0);
      }

      int k = 0;
      int number = 0;
      for (int i=0 ; i < 9 ; i++) {
         for (int j=0 ; j < 9 ; j++) {
            while (true) {
               number = (int)buffer[k]-48;
               k++;
               if (number <= 9 && number >= 0) break;
            }
            board[i][j] = number;
            if (board[i][j] != 0) {
               fixed[i*9+j] = true;
               numbs[i][j].setBackground(new Color(150,150,150));
               numbs[i][j].setForeground(new Color(255,255,255));
            } else {
               fixed[i*9+j] = false;
               numbs[i][j].setBackground(new Color(232,232,232));
               numbs[i][j].setForeground(new Color(0,0,0));
            }
         }
      }
      printBoard();
   }

   public void clearAll () {
      board = new int[20][20];
      fixed = new boolean[81];
      for (int i=0 ; i < 81 ; i++) fixed[i] = false; 
      backs = 0;

      bcktrk.setText("0");
      for (int i=0 ; i < 9 ; i++) {
         for (int j=0 ; j < 9 ; j++) {
            numbs[i][j].setBackground(new Color(232,232,232));
            numbs[i][j].setForeground(new Color(0,0,0));
            numbs[i][j].setText(" ");
            fixed[i*9+j] = false;
            board[i][j] = 0;
         }
      }
      numbs[0][0].requestFocusInWindow();
   }

   public void clearIt () {
      bcktrk.setText("0");
      for (int i=0 ; i < 9 ; i++) {
         for (int j=0 ; j < 9 ; j++) {
            if (fixed[i*9+j]==false) {
               numbs[i][j].setBackground(new Color(232,232,232));
               numbs[i][j].setForeground(new Color(0,0,0));
               numbs[i][j].setText(" ");
               board[i][j] = 0;
            }
         }
      }
   }

   public void run () { 
      solve(0); 
      printBoard(); 
      bcktrk.setText(String.valueOf(backs));
      runner = null;
   }

   // Visit positions in row major order.  Skip over fixed positions 
   // (determined by input).  Repeatedly set the number in that position
   // from 1 to 9.  Each time check to see whether constraints are violated.
   // If not, go to the next position and continue similarly.  If so for all
   // numbers, backtrack to the previous position (setting the position's 
   // number to 0 again - that is what it was upon entering this code segment).
   boolean solve (int p) {
      if (!((String)speed.getSelectedItem()).equals("Fast")) printBoard(); 
      if (p == 81) return true;

      if (fixed[p]) {
         if (solve(p+1)) return true;
      } else {
         int i = p / 9;
         int j = p % 9;
         for (int k=1 ; k <= 9 ; k++) {
            board[i][j] = k;
            int slp = 0;

            if (((String)speed.getSelectedItem()).equals("Med"))
               slp = 2;
            else if (((String)speed.getSelectedItem()).equals("Slow"))
               slp = 10;
            else if (((String)speed.getSelectedItem()).equals("Crawl"))
               slp = 100;

            if (slp > 0) try { Thread.sleep(slp); } catch (Exception e) { }

            if (check(i,j)) {
               if (solve(p+1)) return true;
            } else {
               if (!((String)speed.getSelectedItem()).equals("Fast"))
                  bcktrk.setText(String.valueOf(backs));
               backs++;
            }
         }
         board[i][j] = 0;
      }
      return false;
   }

   // A number has just been placed in row i, column j.  If all columns
   // rows and squares do not violate contraints then return true, otherwise
   // return false.
   boolean check (int i, int j) {
      int rec[] = new int[9];

      // Check small square
      for (int s=0; s < 9; s++) rec[s] = 0;
      int imin = (i/3)*3;
      int jmin = (j/3)*3;
      for (int r=imin ; r < imin+3 ; r++) {
         for (int c=jmin ; c < jmin+3 ; c++) {
            if (board[r][c] == 0) continue;
            if (rec[board[r][c]-1] == 0) rec[board[r][c]-1] += 1;
            else return false;
         }
      }
      
      // Check row
      for (int c=0 ; c < 9 ; c++) rec[c] = 0;
      for (int c=0 ; c < 9 ; c++) {
         if (board[i][c] == 0) continue;
         if (rec[board[i][c]-1] == 0) rec[board[i][c]-1] += 1;
         else return false;
      }

      // Check column
      for (int r=0 ; r < 9 ; r++) rec[r] = 0;
      for (int r=0 ; r < 9 ; r++) {
         if (board[r][j] == 0) continue;
         if (rec[board[r][j]-1] == 0) rec[board[r][j]-1] += 1;
         else return false;
      }
      return true;
   }

   public void keyPressed(KeyEvent evt) { 
      for (int i=0 ; i < 9 ; i++) {
         for (int j=0 ; j < 9 ; j++) {
            if (numbs[i][j].hasFocus()) {
               hasfocus = i*9+j;
               break;
            }
         }
      }
      int row = hasfocus/9;
      int col = hasfocus % 9;
      if (evt.getKeyCode() == 39) {
         if (++col > 8) col = 0;
         numbs[row][col].requestFocusInWindow();
         hasfocus = row*9 + col;
      } else if (evt.getKeyCode() == 37) {
         if (--col < 0) col = 8;
         numbs[row][col].requestFocusInWindow();
         hasfocus = row*9 + col;
      } else if (evt.getKeyCode() == 38) {
         if (--row < 0) row = 8;
         numbs[row][col].requestFocusInWindow();
         hasfocus = row*9 + col;
      } else if (evt.getKeyCode() == 40) {
         if (++row > 8) row = 0;
         numbs[row][col].requestFocusInWindow();
         hasfocus = row*9 + col;
      } else if (numbs[row][col].hasFocus()) {
         if(fixed[row*9+col] == false){
         numbs[row][col].setBackground(new Color(232,232,232));
         numbs[row][col].setForeground(new Color(0,0,0));}
      }
   }

   public void keyReleased(KeyEvent evt) { }
   public void keyTyped(KeyEvent evt) { }

   public void actionPerformed (ActionEvent evt) {
      if (evt.getSource() == start) { 
         if (!new_file) clearIt(); 
	 else {
	    if (runner == null) runner = new Thread(this);
	    runner.start(); 
	 }
         new_file = false;
      }
      else if (evt.getSource() == clearall) clearAll();
      else if (evt.getSource() == clear) clearIt();
      else if (evt.getSource() == file) {
         JFileChooser fd = new JFileChooser(".");
         int r;
         if ((r = fd.showOpenDialog(this)) == JFileChooser.APPROVE_OPTION)
            inpfile = fd.getSelectedFile();
	 
	 board = new int[20][20];
	 fixed = new boolean[81];
	 for (int i=0 ; i < 81 ; i++) fixed[i] = false; 
	 backs = 0;
	 bcktrk.setText("0");

         readFile(inpfile);
         new_file = true;
      } else {
         for (int i=0 ; i < 9 ; i++) {
            for (int j=0 ; j < 9 ; j++) {
               if (evt.getSource() == numbs[i][j] && numbs[i][j].hasFocus()) {
                  int number;
                  try {
                     number = Integer.parseInt(numbs[i][j].getText().trim());
                  } catch (Exception e) { 
                     number = 0;
                  }

                  if (number <= 0 || number > 9) {
                     fixed[i*9+j] = false;
                     board[i][j] = 0;
                     numbs[i][j].setBackground(new Color(232,232,232));
                     numbs[i][j].setForeground(new Color(0,0,0));
                     numbs[i][j].setText(" ");
                  } else {
                     fixed[i*9+j] = true;
                     board[i][j] = number;
                     numbs[i][j].setBackground(new Color(150,150,150));
                     numbs[i][j].setForeground(new Color(255,255,255));
                     numbs[i][j].setText(String.valueOf(number));
                  }
                  return;
               }
            }
         }
      }
   }
}

