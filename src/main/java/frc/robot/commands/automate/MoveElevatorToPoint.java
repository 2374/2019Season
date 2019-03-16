package frc.robot.commands.automate;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.subsystems.Elevator;

public class MoveElevatorToPoint extends Command {

    private Elevator elevator;
    private int destination;

    public MoveElevatorToPoint(int destination) {
        requires (Robot.getElevator());

        this.destination = destination;
        this.elevator = Robot.getElevator();
    }

    @Override
    protected void initialize() {
        System.out.println("[Debug] Moving Elevator to: " + destination + " ticks.");
    }

    @Override
    protected void execute() {
        if (elevator.getTicks() < destination) {
			while (elevator.getTicks() < destination) {
				elevator.move(RobotMap.SPEED_ELEVATOR, 1);
			}
			elevator.move(RobotMap.ELEVATOR_BRAKE, 1);
		} else {
			if (elevator.getTicks() > RobotMap.ELEVATOR_ZERO_LIMIT) {
				while (elevator.getTicks() > destination && elevator.getTicks() > RobotMap.ELEVATOR_ZERO_LIMIT) {
					elevator.move(RobotMap.SPEED_ELEVATOR / 4.0, -1);
				}
				elevator.move(RobotMap.ELEVATOR_BRAKE, 1);
			}
		}
    }

    @Override
    protected boolean isFinished() {
        return true;
    }

    @Override
    protected void end() {
        elevator.move(0.0, 1);
    }

}