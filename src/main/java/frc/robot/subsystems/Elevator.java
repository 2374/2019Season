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

		elevatorBack.follow(elevatorFront);

		elevatorFront.setNeutralMode(NeutralMode.Brake);
		elevatorBack.setNeutralMode(NeutralMode.Brake);

		elevatorFront.setSelectedSensorPosition(0);
	}
	
	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new ElevatorTeleop());
	}
	
	public void move(double speed, int direction) {
		elevatorFront.set(ControlMode.PercentOutput, speed * -direction);
	}

	public void move(int destination) {
		if (getTicks() < destination) {
			while (getTicks() < destination) {
				move(RobotMap.SPEED_ELEVATOR, 1);
			}
			move(RobotMap.ELEVATOR_BRAKE, 1);
		} else {
			if (getTicks() > RobotMap.ELEVATOR_ZERO_LIMIT) {
				while (getTicks() > destination && getTicks() > RobotMap.ELEVATOR_ZERO_LIMIT) {
					move(RobotMap.SPEED_ELEVATOR / 4.0, -1);
				}
				move(RobotMap.ELEVATOR_BRAKE, 1);
			}
		}
	}

	public int getTicks() { return elevatorFront.getSelectedSensorPosition(); }
	
}
