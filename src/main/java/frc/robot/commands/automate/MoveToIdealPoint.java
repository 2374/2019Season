package frc.robot.commands.automate;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.RobotMap;

public class MoveToIdealPoint extends Command {

    private int stopDistance;
    private int direction;

    public MoveToIdealPoint(int stopDistance, int direction) {
        requires (Robot.getDrivetrain());

        this.stopDistance = stopDistance;
        this.direction = direction;
    }

    @Override
    protected void initialize() {
        System.out.println("[Debug] Moving robot to ideal position.");
    }

    @Override
    protected void execute() {
        if (direction > 0) {
            while (Robot.getLidar().getDistanceCm() < stopDistance) {
                Robot.getDrivetrain().tankDrive(RobotMap.SPEED_DRIVE, RobotMap.SPEED_DRIVE);
            }
            Robot.getDrivetrain().tankDrive(0.0, 0.0);
        } else {
            while (Robot.getLidar().getDistanceCm() > stopDistance) {
                Robot.getDrivetrain().tankDrive(-RobotMap.SPEED_DRIVE, -RobotMap.SPEED_DRIVE);
            }
            Robot.getDrivetrain().tankDrive(0.0, 0.0);
        }
    }

    @Override
    protected boolean isFinished() {
        return true;
    }

    @Override
    protected void interrupted() {

    }

    @Override
    protected void end() {
        Robot.getDrivetrain().tankDrive(0.0, 0.0);
    }

}