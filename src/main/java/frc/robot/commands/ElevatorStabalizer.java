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
        // pitch is forward/back, roll is left/right
        if (Robot.getNavX().getRoll() > RobotMap.TIPPING_LIMIT || Robot.getNavX().getPitch() > RobotMap.TIPPING_LIMIT) {
            Robot.getElevator().move(10000);
        }
    }

    @Override
	protected boolean isFinished() {
		return false;
	}

}