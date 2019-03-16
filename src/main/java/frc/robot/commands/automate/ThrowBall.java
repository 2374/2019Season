package frc.robot.commands.automate;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class ThrowBall extends Command {

    public ThrowBall() {
        requires (Robot.getDrivetrain());
        requires (Robot.getIntake());
    }

    @Override
    protected void initialize() {
        System.out.println("[Debug] Throwing a ball.");
    }

    @Override
    protected void execute() {
        Robot.getIntake().move(1.0, 1);
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
        Robot.getIntake().move(0.0, 1);
    }

}