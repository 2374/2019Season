package frc.robot.commands.automate;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.RobotMap;

public class AlignToCenter extends Command {

    private boolean finished;

    public AlignToCenter() {
        requires (Robot.getDrivetrain());
    }

    @Override
    protected void initialize() {
        System.out.println("[Debug] Robot currently aligning to center.");
    }

    @Override
    protected void execute() {
        while (Robot.getPixy().getOffset(1) > 0.1) {
            Robot.getDrivetrain().arcadeDrive(0.0, Robot.getPixy().getOffset(0) / 60.0);
        }
        if (Robot.getLidar().getDistanceCm() > RobotMap.STOP_DISTANCE) {
            Robot.getDrivetrain().tankDrive(0.2, 0.2);
        } else {
            Robot.getDrivetrain().tankDrive(0.0, 0.0);
        }
        finished = true;
    }

    @Override
    protected boolean isFinished() {
        return finished;
    }

    @Override
    protected void interrupted() {

    }

    @Override
    protected void end() {
        Robot.getDrivetrain().tankDrive(0.0, 0.0);
    }

}