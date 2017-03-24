import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;

public class ColorSejway implements ButtonListener {
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
	int offset;
	int prev_error;
	float int_error;

	ColorSensor ls;

	boolean start = false;

	public ColorSejway() {
		ls = new ColorSensor(SensorPort.S2);
	}

	public void shutDown() {
		// Shut down light sensor, motors
		Motor.B.flt();
		Motor.C.flt();
		ls.setFloodlight(false);
	}
	
	public void connect(){
		ColorSejway listener = new ColorSejway();
		Button.ESCAPE.addButtonListener(listener);

		LCD.drawString(waiting, 0, 0);

		btc = Bluetooth.waitForConnection();

		LCD.clear();
		LCD.drawString(connected, 0, 0);

		dis = btc.openDataInputStream();
		dos = btc.openDataOutputStream();

	}

	public void getBalancePos() {
		// Wait for user to balance and press orange button
		ls.setFloodlight(true);
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
                		
				int startknap = dis.readInt();
				
				if (startknap == 1234) {
					start = true;
					startknap = 0;
				}
			}

			catch (Exception e) {
			}

			// NXTway must be balanced.
			offset = ls.getNormalizedLightValue();
			LCD.clear();
			LCD.drawString("Start!", 0, 0);
			LCD.drawInt(KP,7,0,1);
			LCD.drawInt(KI,7,0,2);
			LCD.drawInt(KD,7,0,3);
			LCD.refresh();
			
			
		}
		start = false;

	}

	public void pidControl() {
		
		while (!Button.ENTER.isDown()) {
			
			int normVal = ls.getNormalizedLightValue();

			// Proportional Error:
			int error = normVal - offset;
			// Adjust far and near light readings:
			if (error < 0)
				error = (int) (error * 1.8F);

			// Integral Error:
			int_error = ((int_error + error) * 2) / 3;

			// Derivative Error:
			int deriv_error = error - prev_error;
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
		ColorSejway sej = new ColorSejway();
		
		
		
		sej.connect();
		
		while(!Button.ESCAPE.isDown()){
		sej.getBalancePos();
		sej.pidControl();
		}
		
		sej.shutDown();

	}

}