package frc.robot.commands.automate;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Robot;
import frc.robot.RobotMap;

public class DeployBall extends CommandGroup {

    public DeployBall(int level) {
        requires (Robot.getDrivetrain());
        requires (Robot.getElevator());

        addSequential(new AlignToCenter(), 5);
        addSequential(new MoveToIdealPoint(RobotMap.STOP_DISTANCE, 1));
        addSequential(new MoveElevatorToPoint(level));
        addSequential(new ThrowBall(), 1);
        addSequential(new MoveToIdealPoint(RobotMap.STOP_IDEAL_STOP_DISTANCE, -1));
        addSequential(new MoveElevatorToPoint(RobotMap.ELEVATOR_HATCH_1_DEPLOY));
    }

}