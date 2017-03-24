import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.GyroSensor;

/**
 * Test of Gyro Sensor
 * Records the minimum, maximum and current values
 * 
 * @author Lawrie Griffiths
 */
public class GyroTest 
{
	public static void main(String[] args) throws Exception
	{
		DataLogger logger = new DataLogger("GyroTest_Log.txt");
		GyroSensor gyro = new GyroSensor(SensorPort.S2);
		float minValue = 1023, maxValue = 0;
		int sampleInterval = 5; // ms
		
		LCD.drawString("Gyro Test:", 0, 0);
		Button.waitForAnyPress();
		
		LCD.drawString("Min:", 0, 2);
		LCD.drawString("Max:", 0, 3);
		LCD.drawString("Current:", 0, 4);
		
		while(!Button.ESCAPE.isDown()) 
		{
			float value = gyro.readValue();
			minValue = Math.min(minValue, value);
			maxValue = Math.max(maxValue, value);
			int logValue = Math.round(value);
			logger.writeSample(logValue);
			
			LCD.drawInt((int) minValue, 6, 5, 2);
			LCD.drawInt((int) maxValue, 6, 5, 3);
			LCD.drawInt((int)(value), 6, 9, 4);
			
			Thread.sleep(sampleInterval);
				
		}
		logger.close();
	}
}






