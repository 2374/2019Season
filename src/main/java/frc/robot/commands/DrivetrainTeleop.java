package frc.robot.commands;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;

public class DrivetrainTeleop extends Command {

	public DrivetrainTeleop() {
		requires (Robot.getDrivetrain());
	}
	
	@Override
	public void initialize() {
		
	}
	
	@Override
	protected void execute() {
//		if (Robot.getInput().getButtonA()) {
//			System.out.println("Pressing A");
//			double derivative = Robot.getDrivetrain().ellipseDerivative();
//			Robot.getDrivetrain().arcadeDrive(0.3, derivative);
//		} else {
//			Robot.getDrivetrain().arcadeDrive(Robot.getInput().getJoystickLeftY(), Robot.getInput().getJoystickLeftX());
//		}
		
//		Robot.getDrivetrain().arcadeDrive(Robot.getInput().getJoystickLeftY(), Robot.getInput().getJoystickLeftX());
		//Robot.getDrivetrain().tankDrive(Robot.getInput().getJoystickLeftY(), Robot.getInput().getJoystickRightY());
		Robot.getDrivetrain().arcadeDrive(Robot.getInput().getJoystickLeftY(), Robot.getInput().getJoystickLeftX());
	}
	
	@Override
	protected void interrupted() {
		end();
	}
	
	@Override
	protected boolean isFinished() {
		return false;	
	}
	
	@Override
	protected void end() {
		Robot.getElevator().move(0.0, -1);
	}
}
