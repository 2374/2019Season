package frc.robot.subsystems;

import frc.robot.RobotMap;
import frc.robot.commands.DrivetrainTeleop;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

public class Drivetrain extends Subsystem {

	private TalonSRX frontLeft;
	private TalonSRX frontRight;
	private TalonSRX backLeft;
	private TalonSRX backRight;
	
	public Drivetrain() {
		frontLeft = new TalonSRX(RobotMap.TALON_DRIVE_FRONT_LEFT);
		frontRight = new TalonSRX(RobotMap.TALON_DRIVE_FRONT_RIGHT);
		backLeft = new TalonSRX(RobotMap.TALON_DRIVE_BACK_LEFT);
		backRight = new TalonSRX(RobotMap.TALON_DRIVE_BACK_RIGHT);

		backLeft.follow(frontLeft);
		backRight.follow(frontRight);
	}
	
	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new DrivetrainTeleop());
	}
	
	public void tankDrive(double leftValue, double rightValue) {
		frontLeft.set(ControlMode.PercentOutput, -leftValue);
		frontRight.set(ControlMode.PercentOutput, rightValue);
	}

	public void arcadeDrive(double throttleValue, double turnValue) {
		double leftMotor = -(throttleValue / 1.2) + (turnValue / 2.0);
		double rightMotor = -(throttleValue / 1.2) - (turnValue / 2.0);

		frontLeft.set(ControlMode.PercentOutput, leftMotor);
		frontRight.set(ControlMode.PercentOutput, -rightMotor);
	}

}