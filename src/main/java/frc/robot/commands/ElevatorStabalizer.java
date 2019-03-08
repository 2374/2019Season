package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.RobotMap;

public class ElevatorStabalizer extends Command {

    public ElevatorStabalizer() {
        requires (Robot.getDrivetrain());
    }

    @Override
	protected void execute() {
        if (Robot.getNavX().getRoll() > RobotMap.TIPPING_LIMIT || Robot.getNavX().getPitch() > RobotMap.TIPPING_LIMIT) {
			Robot.getDrivetrain().tankDrive(0.0, 0.0);
			emergencyDrop();
        }
    }

	public void emergencyDrop() {
		while (Robot.getElevator().getTicks() > 0) {
			Robot.getElevator().move(0.80, -1);
		}
	}

    @Override
	protected boolean isFinished() {
		return false;
	}

}