import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import lejos.pc.comm.*;

import java.io.*;

public class PCsejway extends JFrame implements ActionListener
{
	   private TextField nameField = new TextField(12);
	   private TextField addressField = new TextField(20);
	   
	   private TextField kp = new TextField(10);
	   private TextField ki = new TextField(10);
	   private TextField kd = new TextField(10);
	   

	   private String name = "TrumpBot"; 
	   private String address = "0016530A1C88";
	   
	   private NXTComm nxtComm;
	   private NXTInfo nxtInfo;
	   private InputStream is;
	   private OutputStream os;
	   private DataInputStream dis;
	   private DataOutputStream dos;
	   
	   private JButton connectButton = new JButton("Connect");   
	   private JButton goButton = new JButton("Go");


	   /**
	    * Constructor builds GUI
	    */
	   public PCsejway() 
	   {		
	      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	      setTitle("Control NXT");
	      setSize(500,300);
	      
	      // holds labels and text fields
	      JPanel p1 = new JPanel();  
	      p1.add(new JLabel("Name:"));
	      p1.add(nameField);
	      nameField.setText(name);
	      p1.add(new JLabel("Address:"));
	      p1.add(addressField);
	      addressField.setText(address);
	     
	      try
	      {
	         nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
	      }
	      catch (NXTCommException nce) {
	      }
	      nxtInfo = new NXTInfo();
	     
	      // holds connect button
	      JPanel p2 = new JPanel();
	      p2.add(connectButton);
	      connectButton.addActionListener(this);
	      
	      JPanel p3 = new JPanel();  
	      p3.add(new JLabel("kp:"));
	      p3.add(kp);
	      kp.setText("100");
	      p3.add(new JLabel("ki:"));
	      p3.add(ki);
	      ki.setText("4");
	      p3.add(new JLabel("kd:"));
	      p3.add(kd);
	      kd.setText("33");
	      
	      // holds go button
	      JPanel p4 = new JPanel();
	      p4.add(goButton);
	      goButton.addActionListener(this);
	      
	    
	      
	      // North area of the frame
	      JPanel panel = new JPanel();  
	      panel.setLayout(new GridLayout(5,1));
	      panel.add(p1);
	      panel.add(p2);
	      panel.add(p3);
	      panel.add(p4);
	      
	      add(panel,BorderLayout.NORTH);

	   }
	   /**
	    * Required by action listener; 
	    * only action is generated by the get Length button
	    */	
	   public void actionPerformed(ActionEvent e)
	   {
	      if(e.getSource()== connectButton)
	      {
	         String name = nameField.getText();
	         String address = addressField.getText();
	         nxtInfo.name = name;
	         nxtInfo.deviceAddress = address;
	         try
	         {
	            nxtComm.open(nxtInfo);
	            is = nxtComm.getInputStream();
	            os = nxtComm.getOutputStream();
	      	    dis = new DataInputStream(is);
	      	    dos = new DataOutputStream(os);
	         }
	         catch (Exception ex) {
	         }
	      }	  
		   
	      if(e.getSource()== goButton)
	      {
	    	 try
	         {
	    		
	            String kpString = kp.getText();
	    	    int kp = new Integer(kpString).intValue();        
	            dos.writeInt(kp);
	            dos.flush();
	    	    String kiString = ki.getText();
	    	    int ki = new Integer(kiString).intValue();        
	            dos.writeInt(ki);
	            dos.flush();
	            String kdString = kd.getText();
	    	    int kd = new Integer(kdString).intValue();        
	            dos.writeInt(kd);
	            dos.flush();
	            
	            String freqString = "1234";
	    	    int freq = new Integer(freqString).intValue();        
	            dos.writeInt(freq);
	            dos.flush();	          
    		 
	            
	    	    
	         }
	         catch (Exception ex) {
	         }           
	      }
	   }
	   
	   /**
	    * Initialize the display Frame
	    */		
	   public static void main(String[] args)
	   {
	      PCsejway frame = new PCsejway();
	      frame.setVisible(true);
	   }
	}