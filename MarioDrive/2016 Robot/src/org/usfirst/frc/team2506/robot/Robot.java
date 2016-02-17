package org.usfirst.frc.team2506.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.CameraServer;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	/**************************************************************************
	 * Tuning options section
	 * You can tweak the values in this section to fine tune the
	 * control responses of the robot
	 */
	
	// set to true to square the raw joystick values
	// this makes the drive less sensitive off center
	static final boolean OPT_SQUARE_DRIVE_INPUT = true;

	// max drive rate in non-turbo mode
	static final double OPT_NONTURBO_FACTOR = 0.5;
	
	// pushing the left/right joystick past this point will switch to pivot mode
	// and cause the robot to pivot around its center point
	static final double OPT_DRIVE_PIVOT_THRESHOLD = 0.9;

	/*************************************************************************/
	
	// some constants for the controller inputs
	static final int INPUT_AXIS_FRONT_BACK = 1; // left stick front/back
	static final int INPUT_AXIS_LEFT_RIGHT = 4; // right stick left/right
	static final int INPUT_BUTTON_TURBO = 5; // button above right stick
	
	// some constants for the control channels
	static final int OUTPUT_CAN_DRIVE_FR = 0;
	static final int OUTPUT_CAN_DRIVE_FL = 3;
	static final int OUTPUT_CAN_DRIVE_BR = 1;
	static final int OUTPUT_CAN_DRIVE_BL = 2;
	
	// initialize control objects
	Joystick stick = new Joystick(0);
	CANTalon TalonFR = new CANTalon(OUTPUT_CAN_DRIVE_FR);
	CANTalon TalonFL = new CANTalon(OUTPUT_CAN_DRIVE_FL);
	CANTalon TalonBR = new CANTalon(OUTPUT_CAN_DRIVE_BR);
	CANTalon TalonBL = new CANTalon(OUTPUT_CAN_DRIVE_BL);
	DoubleSolenoid actuator1 = new DoubleSolenoid(0, 1);
	JoystickButton yButton = new JoystickButton(stick, 8);
	Ultrasonic ultrasonic = new Ultrasonic(2, 3);
	Compressor compressor = new Compressor(5);
	RobotDrive tankDrive = new RobotDrive(TalonFL, TalonBL, TalonFR, TalonBR);
	int x;
	boolean Latch = true;

	public void robotInit() {
		//Compressor.start();
		CameraServer camera = CameraServer.getInstance();
		camera.setQuality(100);
		camera.startAutomaticCapture("cam0");
		//TalonFR.enableBrakeMode(false); //2
		//TalonBL.enableBrakeMode(false); //0
		//TalonBR.enableBrakeMode(false); //3
		//TalonFL.enableBrakeMode(false); //1

	}

	public void autonomousInit()
	{
		TalonFR.enableBrakeMode(false); //2
		TalonBL.enableBrakeMode(false); //0
		TalonBR.enableBrakeMode(false); //3
		TalonFL.enableBrakeMode(false); //1
	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic()
	{
//		tankDrive.tankDrive(-.5, -.5);
	}

	public void teleopInit()
	{
		TalonFR.enableBrakeMode(true); //2
		TalonBL.enableBrakeMode(true); //0
		TalonBR.enableBrakeMode(true); //3
		TalonFL.enableBrakeMode(true); //1
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic()
	{
		//int counter;
		//Ultrasonic.setAutomaticMode(true);
		//double y = Ultrasonic.getRangeInches();
		//System.out.println(y);
		/*if (y <= 10){
			tankDrive.stopMotor();
			if (counter)

		}*/

		// sticks return values between -1 and +1
		double stickFrontBack = stick.getRawAxis(INPUT_AXIS_FRONT_BACK);
		double stickLeftRight = stick.getRawAxis(INPUT_AXIS_LEFT_RIGHT);

		// if square option enabled, square the raw inputs
		if (OPT_SQUARE_DRIVE_INPUT)
		{
			stickFrontBack *= Math.abs(stickFrontBack);
			stickLeftRight *= Math.abs(stickLeftRight);
		}

		// if turbo button not pressed, limit drive to % of max
		if (!stick.getRawButton(INPUT_BUTTON_TURBO))
		{
			stickFrontBack *= OPT_NONTURBO_FACTOR;
		}

		// default to driving both sides the same
		double driveLeft = stickFrontBack;
		double driveRight = stickFrontBack;

		// check for left pivot
		if (stickLeftRight < -OPT_DRIVE_PIVOT_THRESHOLD)
		{
			driveLeft = -driveRight;
		}
		// check for gradual left turn
		else if (stickLeftRight < 0)
		{
			// decrease left drive proportional to left turn amount
			driveLeft *= (1 + stickLeftRight);
		}

		// check for right pivot
		else if (stickLeftRight > OPT_DRIVE_PIVOT_THRESHOLD)
		{
			driveRight = -driveLeft;
		}
		// check for right turn
		else if (stickLeftRight > 0)
		{
			// decrease right drive proportional to right turn amount
			driveRight *= (1 - stickLeftRight);
		}

		// drive it!
		tankDrive.tankDrive(driveLeft, driveRight);

		/*if (x == 60){
			Compressor.stop();
		}
		if (yButton.get() && Latch == true) {
			actuator1.set(Value.kForward);
			Latch = false;
		} 
		else if (yButton.get() && Latch == false){
			actuator1.set(Value.kReverse);
			Latch = true;
		}*/
	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic()
	{
		double x = ultrasonic.getRangeInches();
		TalonFR.enableBrakeMode(true);
		TalonFL.enableBrakeMode(true);
		TalonBR.enableBrakeMode(true);
		TalonBL.enableBrakeMode(true);
		int counter = 0;
		if (x <= 20) {
			if (counter < 30)
				tankDrive.stopMotor();
			if (counter > 30 && counter < 60)
				tankDrive.tankDrive(.75, -.75);
		}
		else{
			tankDrive.tankDrive(-.75, -.75);
		}
	}

}


