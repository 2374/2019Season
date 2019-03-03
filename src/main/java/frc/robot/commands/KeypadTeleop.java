package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.RobotMap;

public class KeypadTeleop extends Command {

    public KeypadTeleop() {
		requires (Robot.getElevator());
	}
	
	@Override
	protected void execute() {
        
        System.out.println("Test");

        if (Robot.getInput().getOperatorButtonA()) {
			System.out.println("1: Rocket Level 1 Hatch");
			Robot.getElevator().move(RobotMap.ELEVATOR_HATCH_1);
		} else if (Robot.getInput().getOperatorButtonB()) {
			System.out.println("2: Rocket Level 2 Hatch");
			Robot.getElevator().move(RobotMap.ELEVATOR_HATCH_2);
		} else if (Robot.getInput().getOperatorButtonX()) {
			System.out.println("3: Rocket Level 3 Hatch");
			Robot.getElevator().move(RobotMap.ELEVATOR_HATCH_3);
		} else if (Robot.getInput().getOperatorButtonY()) {
			System.out.println("4: Rocket Level 1 Ball");
			Robot.getElevator().move(RobotMap.ELEVATOR_BALL_1);
		} else if (Robot.getInput().getOperatorLeftBumper()) {
			System.out.println("5: Rocket Level 2 Ball");
			Robot.getElevator().move(RobotMap.ELEVATOR_BALL_2);
		} else if (Robot.getInput().getOperatorRightBumper()) {
			System.out.println("6: Rocket Level 3 Ball");
			Robot.getElevator().move(RobotMap.ELEVATOR_BALL_3);
		} else if (Robot.getInput().getOperatorButtonBack()) {
			System.out.println("7: Cargo Hatch");
		} else if (Robot.getInput().getOperatorButtonStart()) {
			System.out.println("8: Cargo Ball");
		} else if (Robot.getInput().getOperatorButtonM1()) {
			System.out.println("9: Set to base level");
			Robot.getElevator().move(0);
		} else if (Robot.getInput().getOperatorButtonM2()) {
			System.out.println("0: Player Station Hatch");
		}
        
	}
	
	@Override
	protected boolean isFinished() {
		return false;	
	}

}