package frc.robot.subsystems;

import frc.robot.RobotMap;
import frc.robot.commands.ElevatorTeleop;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

public class Elevator extends Subsystem {

	private TalonSRX elevatorFront;
	private TalonSRX elevatorBack;
	
	public Elevator() {
		this.elevatorFront = new TalonSRX(RobotMap.TALON_ELEVATOR_FRONT);
		this.elevatorBack = new TalonSRX(RobotMap.TALON_ELEVATOR_BACK);

		elevatorFront.setNeutralMode(NeutralMode.Brake);
		elevatorBack.setNeutralMode(NeutralMode.Brake);
	}
	
	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new ElevatorTeleop());
	}
	
	public void move(double speed, int direction) {
		elevatorFront.set(ControlMode.PercentOutput, speed * direction);
		elevatorBack.set(ControlMode.PercentOutput, speed * direction);
	}
	
}
