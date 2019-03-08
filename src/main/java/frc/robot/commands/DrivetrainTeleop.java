package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.RobotMap;
import edu.wpi.first.wpilibj.command.Command;

public class DrivetrainTeleop extends Command {

	private boolean moved = false;
	private boolean moving = false;
	private boolean aligned = false;
	private boolean aligning = false;
	private boolean squaring = false;
	private boolean squared = false;
	private double offset1 = 0.0;
	private double offset2 = 0.0;
	private double initialAngle = 400.0;
	private double heading = 0.0;
	private double distance = 0.0;
	private int squarecount = 0;

	public DrivetrainTeleop() {
		requires (Robot.getDrivetrain());
	}
	
	@Override
	protected void execute() {

		if (Robot.getInput().getButtonB()) {
				aligned = false;
				moved = false;
				squared = false;	
		}

		if (Robot.getInput().getButtonA()) {
			if (!aligning && !aligned) {
				aligning = true;
			
				offset1 = Robot.getPixy().getOffset(0);
				offset2 = Robot.getPixy().getOffset(1);
				if ( offset1  == -0.0) {
					System.out.println("PIXY FAIL");
				} else {
					// System.out.println("my other offset: " +  Robot.getPixy().getOffset(0));
					if (initialAngle == 400.0) { initialAngle = -offset1; }

					Robot.getDrivetrain().arcadeDrive(0.0, offset1 / 60.0);
					// System.out.println("distance: " + Robot.getLidar().getDistanceCm());
				
					if (!Robot.getNavX().isMoving() && offset1 < 4.0 && offset1 > -4.0) {
						aligned = true;
						System.out.println("Init Angle:"+initialAngle);
						System.out.println("Offset:"+offset1);
						heading = Robot.getNavX().getFusedHeading();
						System.out.println("Heading:"+ heading);
						distance = Robot.getLidar().getDistanceCm();
						System.out.println("Aligned, distance: " + distance);
					}
				}
				aligning = false;

			}

			if (aligned && !moved) {
				double dist = Robot.getLidar().getDistanceCm();
				if (!moving) {
					System.out.println("Distance: " + dist);
					moving = true;
					if ( dist > RobotMap.STOP_DISTANCE) {
						System.out.println("Heading:"+Robot.getNavX().getFusedHeading());
					
						System.out.println("adjust="+ (heading - Robot.getNavX().getFusedHeading()));
						Robot.getDrivetrain().arcadeDrive(-0.4, (heading - Robot.getNavX().getFusedHeading())/360);
					}
					moving = false;
				}
				if (dist < RobotMap.STOP_DISTANCE) {
					moved = true;
					squarecount = 10;
					System.out.println("Distance: " + dist);
					Robot.getDrivetrain().arcadeDrive(0.0, 0.0);
				}
			}

			// if (aligned && moved && !squaring && !squared) {
			// 	squaring = true;
			// 	squarecount--;
			// 	if ( squarecount <= 0 )
			// 	{
			// 		squared = true;
			// 	}
			// 	Robot.getDrivetrain().arcadeDrive(0.0, initialAngle/360);

			// 	System.out.println("Squaring");
			// 	squaring = false;
			// 	// squared = true;
			// }

			// if (squared)
			// {
			// 	Robot.getDrivetrain().arcadeDrive(0.0, 0.0);
			// 	// aligned = false;
			// 	// moved = false;
			// 	// squared = false;
			// }

		} else {
			Robot.getDrivetrain().tankDrive(Robot.getInput().getJoystickLeftY(), Robot.getInput().getJoystickRightY());
			//Robot.getDrivetrain().arcadeDrive(Robot.getInput().getJoystickLeftY(), Robot.getInput().getJoystickLeftX());
		}

		if (Robot.getInput().getButtonB()) System.out.println("Record ticks: " + Robot.getElevator().getTicks());
		if (Robot.getInput().getButtonX()) Robot.getElevator().move(RobotMap.ELEVATOR_HATCH_1_PICKUP);
		if (Robot.getInput().getButtonY()) Robot.getElevator().move(RobotMap.ELEVATOR_HATCH_2);
		if (Robot.getInput().getButtonStart()) Robot.getElevator().move(RobotMap.ELEVATOR_HATCH_1);
	}
	
	@Override
	protected boolean isFinished() {
		return false;
	}

}
