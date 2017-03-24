import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.addon.GyroSensor;


public class GyroSejway implements ButtonListener {
	
	private String connected = "Connected";
	private String waiting = "Waiting...";
	private String closing = "Closing...";

	private BTConnection btc;
	private DataInputStream dis;
	private DataOutputStream dos;


	
	// PID constants
	static int KP = 100;
	static int KI = 4;
	static int KD = 33;
	final int SCALE = 18;

	// Global vars:
	double offset = 606.22;
	double EMA = 0.0000002;
	double prev_error;
	double int_error;

	GyroSensor gyro;

	boolean start = false;

	public GyroSejway() {
		gyro = new GyroSensor(SensorPort.S2);
	}

	public void shutDown() {
		// Shut down light sensor, motors
		Motor.B.flt();
		Motor.C.flt();
	}
	
	public void connect(){
		GyroSejway listener = new GyroSejway();
		Button.ESCAPE.addButtonListener(listener);

		LCD.drawString(waiting, 0, 0);

		btc = Bluetooth.waitForConnection();

		LCD.clear();
		LCD.drawString(connected, 0, 0);

		dis = btc.openDataInputStream();
		dos = btc.openDataOutputStream();

	}

	public void getBalancePos() {
		
		while (start != true) {
			try {
				
			
				KP = dis.readInt();
                LCD.drawInt(KP,7,0,1);
                LCD.refresh();
                
                KI = dis.readInt();
                LCD.drawInt(KI,7,0,2);
                LCD.refresh();
                
                KD = dis.readInt();
                LCD.drawInt(KD,7,0,3);
                LCD.refresh();
                
            	EMA = dis.readDouble();
    			LCD.drawString(String.valueOf(EMA), 0, 4);
    			LCD.refresh();
    			
    			offset = dis.readDouble();
    			LCD.drawInt((int)offset, 7, 0, 5);
    			LCD.refresh();
                		
				int startknap = dis.readInt();
				
				if (startknap == 1234) {
					start = true;
					startknap = 0;
				}
			}

			catch (Exception e) {
			}

			// NXTway must be balanced.
			
			
			
			LCD.clear();
			LCD.drawString("Start!", 0, 0);
			LCD.drawInt(KP,7,0,1);
			LCD.drawInt(KI,7,0,2);
			LCD.drawInt(KD,7,0,3);
			LCD.drawString(String.valueOf(EMA), 0, 4);
			LCD.drawString(String.valueOf(offset), 0, 5);

			LCD.refresh();
			
			
			
		}
		start = false;

	}

	public void pidControl() {
		double time = System.currentTimeMillis();
		double lastTime;
		double value = 0;
		double lastValue= 0;
		double angle = 0;
		double goffset = offset;
		
		while (!Button.ENTER.isDown()) {
			lastTime = time;
			time = System.currentTimeMillis();
			lastValue = value;
			value = gyro.readValue() - offset;
			
			angle += (((value + lastValue))/2)*((time - lastTime)/1000);
			
			
			
			// Proportional Error:
			double error = - angle;
			// Adjust far and near light readings:
			if (error < 0)
				error = (int) (error * 1.8F);

			// Integral Error:
			int_error = ((int_error + error) * 2) / 3;

			// Derivative Error:
			double deriv_error = error - prev_error;
			prev_error = error;

			int pid_val = (int) (KP * error + KI * int_error + KD * deriv_error)
					/ SCALE;
			

			if (pid_val > 100)
				pid_val = 100;
			if (pid_val < -100)
				pid_val = -100;

			// Power derived from PID value:
			int power = Math.abs(pid_val);
			power = 55 + (power * 45) / 100; // NORMALIZE POWER

			
			
			if (pid_val > 0) {
				MotorPort.B.controlMotor(power, BasicMotorPort.FORWARD);
				MotorPort.C.controlMotor(power, BasicMotorPort.FORWARD);
			} else {
				MotorPort.B.controlMotor(power, BasicMotorPort.BACKWARD);
				MotorPort.C.controlMotor(power, BasicMotorPort.BACKWARD);
			}
		}
		
		MotorPort.B.controlMotor(0, BasicMotorPort.FORWARD);
		MotorPort.C.controlMotor(0, BasicMotorPort.FORWARD);

	}

	public void buttonPressed(Button b) {
	}

	public void buttonReleased(Button b) {
		LCD.clear();
		LCD.drawString(closing, 0, 0);
		try {
			dis.close();
			dos.close();
			Thread.sleep(100); // wait for data to drain
			btc.close();
		} catch (Exception e) {
		}
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}
		System.exit(0);
	}

	public static void main(String[] args) {

		GyroSejway sej = new GyroSejway();
		
		
		
		sej.connect();
		
		while(!Button.ESCAPE.isDown()){
		sej.getBalancePos();
		Sound.beep();	
		
		sej.pidControl();
		}
		
		sej.shutDown();

	}

}
