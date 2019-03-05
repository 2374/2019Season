package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class ElevatorStabalizer extends Command {

    public ElevatorStabalizer() {
        requires (Robot.getDrivetrain());
    }

    @Override
	protected void execute() {
        System.out.println("execute");
        // pitch is forward/back, roll is left/right COPY CODE FROM ELEVATOR.JAVA
    }

    @Override
	protected boolean isFinished() {
		return false;
	}

}