package org.usfirst.frc.team2506.robot;

import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoCamera.WhiteBalance;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.VisionThread;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	private double centerX;
	private VisionThread visionThread;
	private final Object imgLock = new Object();
	private Gyro gyro;
	private Rect found;
	
	private static final int IMG_WIDTH = 320;
	private static final int IMG_HEIGHT = 240;
	
	private static double CALIB_DISTANCE = 6.0; // calibrate with target 6' from camera
	private static double CALIB_PIXHEIGHT = 57.0; // target height in pixels at calibration distance
	private static double TARGET_HEIGHT = 1.0; // target actual height in feet
	
	private static double SPEED_SPIN = 0.2;
	
	private double hLow = 60;
	private double hHigh = 100;
	private double sLow = 150;
	private double sHigh = 255;
	private double vLow = 200;
	private double vHigh= 255;
	private double areaMin = 300;
	private double aspectLow = 0;
	private double aspectHigh = 1000;
	private GripPipeline pipeline;
	private double angle = 0;
	private int brightness = 10;
	private int exposure = 10;
	
	private UsbCamera camera;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		SmartDashboard.putNumber("hlow", hLow);
		SmartDashboard.putNumber("hhigh", hHigh);
		SmartDashboard.putNumber("slow", sLow);
		SmartDashboard.putNumber("shigh", sHigh);
		SmartDashboard.putNumber("vlow", vLow);
		SmartDashboard.putNumber("vhigh", vHigh);
		SmartDashboard.putNumber("areaMin", areaMin);
		SmartDashboard.putNumber("aspectLow", aspectLow);
		SmartDashboard.putNumber("aspectHigh", aspectHigh);
		SmartDashboard.putNumber("brightness", brightness);
		SmartDashboard.putNumber("exposure", exposure);

		gyro = new ADXRS450_Gyro();
		gyro.calibrate();
		
		camera = CameraServer.getInstance().startAutomaticCapture();
		System.out.println("resolution");
		camera.setResolution(IMG_WIDTH, IMG_HEIGHT);
		System.out.println("white balance");
		camera.setWhiteBalanceManual(WhiteBalance.kFixedIndoor);
		System.out.println("brightness");
		camera.setBrightness(brightness);
		camera.setExposureManual(exposure);
		
		CvSource vsource = CameraServer.getInstance().putVideo("test", 320, 200);

		pipeline = new GripPipeline(vsource);
		pipeline.sethLow(hLow);
		pipeline.sethHigh(hHigh);
		pipeline.setsLow(sLow);
		pipeline.setsHigh(sHigh);
		pipeline.setvLow(vLow);
		pipeline.setvHigh(vHigh);
		pipeline.setAreaMin(areaMin);
		pipeline.setAspectLow(aspectLow);
		pipeline.setAspectHigh(aspectHigh);
		visionThread = new VisionThread(camera, pipeline, pipeline -> {
			if (!pipeline.filterContoursOutput().isEmpty()) {
				Rect r = Imgproc.boundingRect(pipeline.filterContoursOutput().get(0));
				synchronized (imgLock) {
					found = r;
				}
			}
			else
			{
				found = null;
			}
		});
		visionThread.start();
	}

	
	DoubleTankDrive drive = new DoubleTankDrive (3, 1, 2, 0);
	Joystick joystick = new Joystick (0);
	double noTargetCount = 0;
	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		hLow = SmartDashboard.getNumber("hlow", 0);
		hHigh = SmartDashboard.getNumber("hhigh", 180);
		sLow = SmartDashboard.getNumber("slow", 0);
		sHigh = SmartDashboard.getNumber("shigh", 255);
		vLow = SmartDashboard.getNumber("vlow", 0);
		vHigh = SmartDashboard.getNumber("vhigh", 255);
		areaMin = SmartDashboard.getNumber("areaMin", 0);
		aspectLow = SmartDashboard.getNumber("aspectLow", 0);
		aspectHigh = SmartDashboard.getNumber("aspectHigh", 1000);
		pipeline.sethLow(hLow);
		pipeline.sethHigh(hHigh);
		pipeline.setsLow(sLow);
		pipeline.setsHigh(sHigh);
		pipeline.setvLow(vLow);
		pipeline.setvHigh(vHigh);
		pipeline.setAreaMin(areaMin);
		pipeline.setAspectLow(aspectLow);
		pipeline.setAspectHigh(aspectHigh);
		
		int newBrightness = (int) SmartDashboard.getNumber("brightness", 10);
		int newExposure = (int) SmartDashboard.getNumber("exposure", 10);
		
		// check if brightness or exposure changed
		if (newBrightness != brightness || newExposure != exposure)
		{
			// update camera calibration
			camera.setBrightness(newBrightness);
			camera.setExposureManual(newExposure);
			camera.setWhiteBalanceManual(WhiteBalance.kFixedIndoor);
			
			// remember new values
			brightness = newBrightness;
			exposure = newExposure;
		}
		
		boolean targetAcq = false;
		
		synchronized(imgLock)
		{
			if (found != null)
			{
				double center = found.x + found.width / 2;
				double centerOffset = center - IMG_WIDTH / 2;
				
				double range = CALIB_PIXHEIGHT / found.height * CALIB_DISTANCE; // calibration
				double offset = (centerOffset * TARGET_HEIGHT * range) / (CALIB_PIXHEIGHT * CALIB_DISTANCE);
				angle = Math.atan2(offset, range) * 180 / Math.PI;
				targetAcq = true;
				
				SmartDashboard.putNumber("target x", found.x);
				SmartDashboard.putNumber("target y", found.y);
				SmartDashboard.putNumber("target width", found.width);
				SmartDashboard.putNumber("target height", found.height);
				SmartDashboard.putNumber("range", range);
				SmartDashboard.putNumber("offset", offset);
				SmartDashboard.putNumber("angle", angle);
				SmartDashboard.putString("state", "target acquired");
				found = null;
				noTargetCount = 0;
			}
			else
			{
				if (noTargetCount++ > 25)
				{
					SmartDashboard.putString("state", "no target");
				}
			}
		}
		
		if (joystick.getRawButton(1))
		{
			if (noTargetCount <= 20)
			{
				if (angle > 0.5)
				{
					System.out.println("drive CW");
					drive.drive(-SPEED_SPIN, SPEED_SPIN);
				}
				else if (angle < -0.5)
				{
					System.out.println("drive CCW");
					drive.drive(SPEED_SPIN, -SPEED_SPIN);
				}
				else
				{
					drive.drive(0,  0);
				}
			}
		}
		else
		{
			drive.drive(joystick.getRawAxis(1), joystick.getRawAxis(5));
		}
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}

