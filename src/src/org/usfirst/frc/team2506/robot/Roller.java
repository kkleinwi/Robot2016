package org.usfirst.frc.team2506.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Ultrasonic;

public class Roller {
	private CANTalon talon;
	private boolean useTriggers;
	
	public Roller(int talonPort, boolean useTriggers) {
		talon = new CANTalon(talonPort);
		this.useTriggers = useTriggers;
	}
	
	public void main(Joystick joystick, int input, int output, Ultrasonic ultrasonic) {
		if (useTriggers) {
	      	if (joystick.getRawAxis(input) >= 0.1 && ultrasonic.getRangeMM() > 150)
	      		talon.set(0.3);
	      	else if (joystick.getRawAxis(output) >= 0.1)
	      		talon.set(-1);
	      	else
	      		talon.set(0);
		} else {
	      	if (joystick.getRawButton(input))
	      		talon.set(0.75);
	      	else if (joystick.getRawButton(output))
	      		talon.set(-0.75);
	      	else
	      		talon.set(0);
		}
	}
}
