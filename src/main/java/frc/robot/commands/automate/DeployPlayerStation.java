package frc.robot.commands.automate;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.commands.automate.MoveElevatorToPoint;

public class DeployPlayerStation extends CommandGroup {

    public DeployPlayerStation(int destination) {
        requires (Robot.getDrivetrain());
        requires (Robot.getElevator());

        addSequential(new AlignToCenter(), 5);
        addSequential(new MoveToIdealPoint(RobotMap.STOP_DISTANCE, 1));
        addSequential(new MoveElevatorToPoint(destination));
        addSequential(new MoveToIdealPoint(RobotMap.STOP_PLAYERSTATION_DISTANCE, 1));
        addSequential(new MoveElevatorToPoint(destination + RobotMap.DEPLOY_OFFSET));
        addSequential(new MoveToIdealPoint(RobotMap.STOP_IDEAL_STOP_DISTANCE, -1));
        addSequential(new MoveElevatorToPoint(RobotMap.ELEVATOR_HATCH_1_DEPLOY));

    }

}