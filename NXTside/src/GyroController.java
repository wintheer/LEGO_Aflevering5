import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.addon.GyroSensor;


public class GyroController {

	public static void main(String[] args) throws Exception
	{
		GyroSensor gyro = new GyroSensor(SensorPort.S2);
		int sampleInterval = 5; // ms
		
		LCD.drawString("GyroController:", 0, 0);
		Button.waitForAnyPress();
		
		gyro.recalibrateOffset();
		
		Sound.beep();
		
		float time = System.currentTimeMillis();
		float lastTime;
		float velocity = gyro.getAngularVelocity();
		float lastVelocity;
		float angle = 0f;
				
		while(!Button.ESCAPE.isDown()) 
		{
			lastTime = time;
			time = System.currentTimeMillis();
			lastVelocity = velocity;
			velocity = gyro.getAngularVelocity();
			
			angle += ((velocity+lastVelocity)/2)*((time-lastTime)/1000);
			
			LCD.clear();
			
			LCD.drawInt(Math.round(angle), 4, 4);
			LCD.drawString("LUL", 6, 5);

			Thread.sleep(sampleInterval);
				
		}
		Sound.beep();
	}
}