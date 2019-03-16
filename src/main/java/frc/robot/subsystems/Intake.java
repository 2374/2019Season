package frc.robot.subsystems;

import frc.robot.RobotMap;
import frc.robot.commands.IntakeTeleop;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.command.Subsystem;

public class Intake extends Subsystem {

	private VictorSPX intakeLeft;
	private VictorSPX intakeRight;
	
	public Intake() {
		intakeLeft = new VictorSPX(RobotMap.VICTOR_INTAKE_LEFT);
		intakeRight = new VictorSPX(RobotMap.VICTOR_INTAKE_RIGHT);

		intakeRight.follow(intakeLeft);
	}
	
	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new IntakeTeleop());
	}
	
	public void move(double speed, int direction) {
		intakeLeft.set(ControlMode.PercentOutput, speed * direction);
	}
	
}
