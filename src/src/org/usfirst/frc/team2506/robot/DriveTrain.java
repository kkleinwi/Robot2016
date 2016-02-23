package org.usfirst.frc.team2506.robot;

import edu.wpi.first.wpilibj.Joystick;

public class DriveTrain {
	private MasterCANTalon leftMaster;
	private MasterCANTalon rightMaster;
	private SlaveCANTalon leftSlave;
	private SlaveCANTalon rightSlave;
	
	public DriveTrain(int leftMasterPort, int rightMasterPort, int leftSlavePort, int rightSlavePort) {
		leftMaster = new MasterCANTalon(leftMasterPort);
		rightMaster = new MasterCANTalon(rightMasterPort);
		leftSlave = new SlaveCANTalon(leftSlavePort, leftMaster);
		rightSlave = new SlaveCANTalon(rightSlavePort, rightMaster);
	}
	
	public void drive(Joystick joystick, int leftAxis, int rightAxis) {
		leftMaster.drive(joystick.getRawAxis(leftAxis));
		rightMaster.drive(-joystick.getRawAxis(rightAxis));
	}
	public void drive(double leftPercent, double rightPercent) {
		leftMaster.drive(leftPercent);
		rightMaster.drive(-rightPercent);
	}
}