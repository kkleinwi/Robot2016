
package org.usfirst.frc.team2506.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
    final String defaultAuto = "Default";
    final String customAuto = "My Auto";
    String autoSelected;
    SendableChooser chooser;
    
    DriveTrain driveTrain = new DriveTrain(3, 1, 2, 0);
    CANTalon bigRoller = new CANTalon(4);
    CANTalon littleRoller = new CANTalon(5);
    Arms littleArms = new Arms(2, 3);
    Arms bigArms = new Arms(0, 1);
    Ultrasonic ultrasonic = new Ultrasonic(1, 0);
    Joystick playerOne = new Joystick(0);
    Joystick playerTwo = new Joystick(1);
    int ultraLoop = 0;
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        chooser = new SendableChooser();
        chooser.addDefault("Default Auto", defaultAuto);
        chooser.addObject("My Auto", customAuto);
        SmartDashboard.putData("Auto choices", chooser);
        camera();
        
    }
    
	public void camera (){
	    SendableChooser cameraOn = new SendableChooser();
        cameraOn.addDefault("Default Auto", defaultAuto);
        cameraOn.addObject("My Auto", customAuto);
		final String defaultAuto = "Default";
	    final String customAuto = "My Auto";
	    
        cameraOn.addDefault("Default Auto", defaultAuto);
        cameraOn.addObject("My Auto", customAuto);
        SmartDashboard.putData("Auto choices", cameraOn);
        CameraServer camera = CameraServer.getInstance();
    	camera.setQuality(125);
    	camera.startAutomaticCapture("cam0");
	}
    
    
	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString line to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the switch structure below with additional strings.
	 * If using the SendableChooser make sure to add them to the chooser code above as well.
	 */
    public void autonomousInit() {
    	autoSelected = (String) chooser.getSelected();
//		autoSelected = SmartDashboard.getString("Auto Selector", defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
	/*	littleArms.getSolenoid().set(DoubleSolenoid.Value.kForward);
		bigArms.getSolenoid().set(DoubleSolenoid.Value.kReverse);
		
		try {
			Thread.sleep(2000);
			driveTrain.drive(0.5, 0.65);
			Thread.sleep(500);
			driveTrain.drive(0.5, 0.65);
			Thread.sleep(500);
			driveTrain.drive(0.5, 0.65);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		driveTrain.drive(0, 0);
		*/
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic(){
    }

    public void teleopInit() {
    	ultrasonic.setEnabled(true);
    	ultrasonic.setAutomaticMode(true);
    }
    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic(){
    	double axisOne = playerOne.getRawAxis(Xbox.LEFT_Y_AXIS);
    	double axisFive = playerOne.getRawAxis(Xbox.RIGHT_Y_AXIS);
    	if (playerOne.getRawAxis(Xbox.LEFT_TRIGGER) >= 0.1)
    		driveTrain.drive(axisOne * 0.3, axisFive * 0.3);
    	else if (axisOne > 0.09 || axisOne < -0.09 || axisFive > 0.09 || axisFive < -0.09)
    		driveTrain.drive(playerOne, Xbox.LEFT_Y_AXIS, Xbox.RIGHT_Y_AXIS);
    	else
    		driveTrain.drive(0, 0);
       // bigRoller.main(playerTwo, 5, 6, ultrasonic);
       //ittleRoller.main(playerTwo, 3, 2, ultrasonic);
        littleArms.main(playerTwo, 4);
        bigArms.main(playerTwo, 3);
        
      	if (playerTwo.getRawAxis(Xbox.RIGHT_TRIGGER) >= 0.1 && ultrasonic.getRangeMM() > 150)
      		littleRoller.set(0.45);
      	else if (playerTwo.getRawAxis(Xbox.LEFT_TRIGGER) >= 0.1)
      		littleRoller.set(-1);
      	else
      		littleRoller.set(0);
      	if (playerTwo.getRawButton(Xbox.LB_BUTTON))
      		bigRoller.set(0.75);
      	else if (playerTwo.getRawButton(Xbox.RB_BUTTON))
      		bigRoller.set(-0.75);
      	else
      		bigRoller.set(0);
      	ultraLoop++;
      	if (ultraLoop % 30 == 0)
			System.out.println("Left: " + playerOne.getRawAxis(1) + " Right: " + playerOne.getRawAxis(5));
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    
    }
    
}