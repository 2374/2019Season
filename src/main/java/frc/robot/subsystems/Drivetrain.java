package frc.robot.subsystems;

import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.commands.DrivetrainTeleop;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

public class Drivetrain extends Subsystem {
	
	private double initial;
	private double current;

	private TalonSRX frontLeft;
	private TalonSRX frontRight;
	private TalonSRX backLeft;
	private TalonSRX backRight;
	
	public Drivetrain() {
		initial = 0.0;
		current = 0.0;

		frontLeft = new TalonSRX(RobotMap.TALON_DRIVE_FRONT_LEFT);
		frontRight = new TalonSRX(RobotMap.TALON_DRIVE_FRONT_RIGHT);
		backLeft = new TalonSRX(RobotMap.TALON_DRIVE_BACK_LEFT);
		backRight = new TalonSRX(RobotMap.TALON_DRIVE_BACK_RIGHT);

		backLeft.follow(frontLeft);
		backRight.follow(frontRight);

		frontLeft.setInverted(false);
		frontRight.setInverted(false);
	}
	
	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new DrivetrainTeleop());
	}
	
	public void tankDrive(double leftValue, double rightValue) {
		frontLeft.set(ControlMode.PercentOutput, leftValue);
		frontRight.set(ControlMode.PercentOutput, rightValue);
	}

	public void arcadeDrive(double throttleValue, double turnValue) {
		double leftMotor = -throttleValue + turnValue;
		double rightMotor = -throttleValue - turnValue;

		tankDrive((leftMotor / 2.0), -(rightMotor / 2.0));
	}
	
	public double ellipseDerivative() {
		double angle = Robot.getPixy().getOffset(1);
		double distance = Robot.getLidar().getDistanceCm();
		
		double vertical = Math.sin(90 - angle) * distance;
		double horizontal = Math.cos(90 - angle) * distance;
		
		if (initial == 0.0) { initial = horizontal; }
		
		double progress = horizontal - Math.cos(90 - Robot.getPixy().getOffset(1)) * Robot.getLidar().getDistanceCm();
		
		current = Math.atan(progress * vertical / (Math.pow(horizontal, 2)* Math.sqrt(1 - Math.pow(progress, 2) / Math.pow(horizontal, 2)))) - current;

		return current;
	}
	
}