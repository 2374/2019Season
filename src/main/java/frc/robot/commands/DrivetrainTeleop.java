package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.RobotMap;
import edu.wpi.first.wpilibj.command.Command;

public class DrivetrainTeleop extends Command {

	public DrivetrainTeleop() {
		requires (Robot.getDrivetrain());
	}
	
	@Override
	protected void execute() {
		if (Robot.getInput().getButtonA()) {
			if (Robot.getLidar().getDistanceCm() > RobotMap.STOP_DISTANCE) {
				// double derivative = Robot.getDrivetrain().ellipseDerivative();
				double average = (Robot.getPixy().getOffset(0) + Robot.getPixy().getOffset(1)) / 2.0;

				System.out.println("Turn: " + average);
				Robot.getDrivetrain().arcadeDrive(0.3, average);

				//Robot.getDrivetrain().arcadeDrive(-1.0, derivative);
				// System.out.println("Derivative: " + derivative);
			} else {
				System.out.println("Too close: " + Robot.getLidar().getDistanceCm());
				Robot.getDrivetrain().tankDrive(0.0, 0.0);
			}
		} else {
			Robot.getDrivetrain().arcadeDrive(Robot.getInput().getJoystickLeftY(), Robot.getInput().getJoystickLeftX());
		}
 
		if (Robot.getInput().getButtonB()) System.out.println("Record ticks: " + Robot.getElevator().getTicks());
		if (Robot.getInput().getButtonX()) Robot.getElevator().move(RobotMap.ELEVATOR_HATCH_1_PICKUP);
		if (Robot.getInput().getButtonY()) Robot.getElevator().move(RobotMap.ELEVATOR_HATCH_2);
		if (Robot.getInput().getButtonStart()) Robot.getElevator().move(RobotMap.ELEVATOR_HATCH_1);
	}
	
	@Override
	protected boolean isFinished() {
		return false;	
	}

}
