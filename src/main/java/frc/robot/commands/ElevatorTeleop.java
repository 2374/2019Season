package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Command;

public class ElevatorTeleop extends Command {

	public ElevatorTeleop() {
		requires (Robot.getElevator());
	}
	
	@Override
	public void initialize() {
		
	}
	
	@Override
	protected void execute() {
		if (Robot.getInput().getLeftTrigger() > 0.0) {
			Robot.getElevator().move(RobotMap.SPEED_ELEVATOR, 1);
		} else if (Robot.getInput().getRightTrigger() > 0.0) {
			Robot.getElevator().move(RobotMap.SPEED_ELEVATOR, -1);
		} else {
			Robot.getElevator().move(0.0, 0);
		}
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
		Robot.getElevator().move(0.0, 0);
	}
	
}
