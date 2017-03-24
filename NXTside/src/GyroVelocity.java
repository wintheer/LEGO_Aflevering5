import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.addon.GyroSensor;

public class GyroVelocity 
{
	public static void main(String[] args) throws Exception
	{
		GyroSensor gyro = new GyroSensor(SensorPort.S2);
		gyro.setOffset(0);
		
		float lastTime;
		
		float value =0;
		float lastValue = 0;
		float angle = 0;
		
		LCD.drawString("GyroVelocity:", 0, 0);
		Button.waitForAnyPress();
		float time = System.currentTimeMillis();

		
		Sound.beep();
		
		while(!Button.ESCAPE.isDown()) 
		{
			lastTime = time;
			time = System.currentTimeMillis();
			lastValue = value;
			value = gyro.readValue() - 606;
			
			angle += ((value + lastValue)/2)*((time - lastTime)/1000);
			
			LCD.clear();
			LCD.drawInt((int)angle, 6, 5);
			LCD.refresh();
			Thread.sleep(5);
		}
		Sound.beep();
	}
}