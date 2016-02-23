package org.usfirst.frc.team2506.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;

public class Arms {
	private DoubleSolenoid solenoid;
	private boolean armsClear = true;
	
	public Arms(int inputChannel, int outputChannel) {
		solenoid = new DoubleSolenoid(inputChannel, outputChannel);
	}
	
	public void main(Joystick joystick, int button) {
        if (joystick.getRawButton(button)) {
        	if (solenoid.get() == DoubleSolenoid.Value.kOff)
        		solenoid.set(DoubleSolenoid.Value.kForward);
        	else if (solenoid.get() == DoubleSolenoid.Value.kForward && armsClear) {
        		solenoid.set(DoubleSolenoid.Value.kReverse);
        		armsClear = false;
        	}
        	else if (solenoid.get() == DoubleSolenoid.Value.kReverse && armsClear) {
        		solenoid.set(DoubleSolenoid.Value.kForward);
        		armsClear = false;
        	}
        }
        else
        	armsClear = true;
	}
	
	public DoubleSolenoid getSolenoid() {
		return solenoid;
	}
	
	public DoubleSolenoid.Value getStatus() {
		return solenoid.get();
	}
}
