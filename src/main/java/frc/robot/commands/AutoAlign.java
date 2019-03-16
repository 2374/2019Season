package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.RobotMap;

public class AutoAlign extends Command {

    public AutoAlign() {
        requires (Robot.getElevator());
    }

	@Override
	protected void execute() {

        System.out.println("Running....");

        if (Robot.getPixy().getOffset(1) > 0.1) {
            Robot.getDrivetrain().arcadeDrive(0.0, Robot.getPixy().getOffset(1));
        } else {
            if (Robot.getLidar().getDistanceCm() > RobotMap.STOP_DISTANCE) {
                Robot.getDrivetrain().tankDrive(0.2, 0.2);
            } else {
                Robot.getDrivetrain().tankDrive(0.0, 0.0);
            }
        }
        
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

}