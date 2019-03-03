package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Command;

public class ElevatorTeleop extends Command {

	public ElevatorTeleop() {
		requires (Robot.getElevator());
	}
	
	@Override
	protected void execute() {
		//System.out.println("Ticks: " + Robot.getElevator().getTicks());
		if (Robot.getInput().getLeftTrigger() > 0.0) {
			if (Robot.getElevator().getTicks() > RobotMap.ELEVATOR_ZERO_LIMIT) {
				Robot.getElevator().move(RobotMap.SPEED_ELEVATOR / 4.0, -1);
			} else {
				Robot.getElevator().move(0.0, 1);
			}
		} else if (Robot.getInput().getRightTrigger() > 0.0) {
			Robot.getElevator().move(RobotMap.SPEED_ELEVATOR, 1);
		} else {
			Robot.getElevator().move(RobotMap.ELEVATOR_BRAKE, 1);
			// Robot.getElevator().move(0.0, 0);
		}

	}
	
	@Override
	protected boolean isFinished() {
		return false;
	}
	
}
