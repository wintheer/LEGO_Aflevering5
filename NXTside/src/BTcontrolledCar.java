import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;

/**
 * Receive data from a PC, a phone, 
 * or another Bluetooth device.
 * 
 * Waits for a Bluetooth connection, receives two integers that are
 * interpreted as power and duration for a forward command to a
 * differential driven car. The resulting tacho counter
 * value is send to the initiator of the connection.
 * 
 * Based on Lawrie Griffiths BTSend
 * 
 * @author Ole Caprani
 * @version 26-2-13
 */
public class BTcontrolledCar implements ButtonListener
{
    private String connected = "Connected";
    private String waiting = "Waiting...";
    private String closing = "Closing...";

    private BTConnection btc;
    private DataInputStream dis;
    private DataOutputStream dos;
	
    public void perform() 
    {		
        BTcontrolledCar listener = new BTcontrolledCar();
        Button.ESCAPE.addButtonListener(listener);
		
        LCD.drawString(waiting,0,0);

        btc = Bluetooth.waitForConnection();
        
        LCD.clear();
        LCD.drawString(connected,0,0);	

        dis = btc.openDataInputStream();
        dos = btc.openDataOutputStream();
        
        while (true)
        {
            try 
            {
                int power = dis.readInt();
                LCD.drawInt(power,7,0,1);
                LCD.refresh();
                int dur = dis.readInt();
                LCD.drawInt(dur,7,0,2);
                LCD.refresh();
                Car.forward(power, power);
                Thread.sleep(dur);
                Car.stop();
                dos.writeInt(Car.counter());
                dos.flush();
            }
            catch (Exception e)
            {}
        }
    }
	
    public void buttonPressed(Button b) {}
		   
    public void buttonReleased(Button b) 
    {
        LCD.clear();
        LCD.drawString(closing,0,0);
    	try 
    	{
    	    dis.close();
            dos.close();
            Thread.sleep(100); // wait for data to drain
            btc.close();    	
    	}
        catch (Exception e)
        {}
        try {Thread.sleep(1000);}catch (Exception e){}
        System.exit(0);
    }
    
    public static void main(String [] args)  
    {
        new BTcontrolledCar().perform();
    }

}
