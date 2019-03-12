package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.robot.commands.KeypadTeleop;

public class OI {

	private XboxController driver;
	private XboxController operator;

	private JoystickButton button1;
	private JoystickButton button2;
	private JoystickButton button3;
	private JoystickButton button4;
	private JoystickButton button5;
	private JoystickButton button6;
	private JoystickButton button7;
	private JoystickButton button8;
	private JoystickButton button9;
	private JoystickButton button10;
	private JoystickButton button11;
	
	public OI() {
		driver = new XboxController(RobotMap.CONTROLLER_DRIVER);
		operator = new XboxController(RobotMap.CONTROLLER_OPERATOR);

		this.button1 = new JoystickButton(operator, RobotMap.CONTROLLER_BUTTON_A);
		this.button2 = new JoystickButton(operator, RobotMap.CONTROLLER_BUTTON_B);
		this.button3 = new JoystickButton(operator, RobotMap.CONTROLLER_BUTTON_X);
		this.button4 = new JoystickButton(operator, RobotMap.CONTROLLER_BUTTON_Y);
		this.button5 = new JoystickButton(operator, RobotMap.CONTROLLER_LEFT_BUMPER);
		this.button6 = new JoystickButton(operator, RobotMap.CONTROLLER_RIGHT_BUMPER);
		this.button7 = new JoystickButton(operator, RobotMap.CONTROLLER_BUTTON_BACK);
		this.button8 = new JoystickButton(operator, RobotMap.CONTROLLER_BUTTON_START);
		this.button9 = new JoystickButton(operator, RobotMap.CONTROLLER_BUTTON_M1);
		this.button10 = new JoystickButton(operator, RobotMap.CONTROLLER_BUTTON_M2);
		this.button11 = new JoystickButton(operator, 11);

		button1.whenPressed(new KeypadTeleop(RobotMap.ELEVATOR_HATCH_1));
		button2.whenPressed(new KeypadTeleop(RobotMap.ELEVATOR_HATCH_2));
		button3.whenPressed(new KeypadTeleop(RobotMap.ELEVATOR_HATCH_3));
		button4.whenPressed(new KeypadTeleop(RobotMap.CONTROLLER_BUTTON_Y));
		button5.whenPressed(new KeypadTeleop(RobotMap.ELEVATOR_BALL_1));
		button6.whenPressed(new KeypadTeleop(RobotMap.ELEVATOR_BALL_2));
		button7.whenPressed(new KeypadTeleop(RobotMap.ELEVATOR_BALL_3));
		button8.whenPressed(new KeypadTeleop(RobotMap.CONTROLLER_BUTTON_START));
		button9.whenPressed(new KeypadTeleop(RobotMap.CONTROLLER_BUTTON_M1));
		button10.whenPressed(new KeypadTeleop(RobotMap.ELEVATOR_HATCH_1_PICKUP));
		button11.whenPressed(new KeypadTeleop(RobotMap.ELEVATOR_HATCH_1_DEPLOY));

	}
	
	public double getJoystickLeftX() { return deadZone(driver.getRawAxis(RobotMap.CONTROLLER_LEFT_AXIS_X), 0.3); }
	 
	public double getJoystickLeftY() { return deadZone(driver.getRawAxis(RobotMap.CONTROLLER_LEFT_AXIS_Y), 0.3); }

	public double getJoystickRightX() { return deadZone(driver.getRawAxis(RobotMap.CONTROLLER_RIGHT_AXIS_X), 0.5); }
	
	public double getJoystickRightY() { return deadZone(driver.getRawAxis(RobotMap.CONTROLLER_RIGHT_AXIS_Y), 0.3); }

	public double getLeftTrigger() { return deadZone(driver.getRawAxis(RobotMap.CONTROLLER_LEFT_TRIGGER), 0.5); }

	public double getRightTrigger() { return deadZone(driver.getRawAxis(RobotMap.CONTROLLER_RIGHT_TRIGGER), 0.5); }
	
	public boolean getLeftBumper() { return driver.getRawButton(RobotMap.CONTROLLER_LEFT_BUMPER); }

	public boolean getRightBumper() { return driver.getRawButton(RobotMap.CONTROLLER_RIGHT_BUMPER); }

	public boolean getButtonA() { return driver.getRawButton(RobotMap.CONTROLLER_BUTTON_A); }
	
	public boolean getButtonB() { return driver.getRawButton(RobotMap.CONTROLLER_BUTTON_B); }

	public boolean getButtonX() { return driver.getRawButton(RobotMap.CONTROLLER_BUTTON_X); }

	public boolean getButtonY() { return driver.getRawButton(RobotMap.CONTROLLER_BUTTON_Y); }

	public boolean getButtonBack() { return driver.getRawButton(RobotMap.CONTROLLER_BUTTON_BACK); }
	
	public boolean getButtonStart() { return driver.getRawButton(RobotMap.CONTROLLER_BUTTON_START); }

	public boolean getButtonM1() { return driver.getRawButton(RobotMap.CONTROLLER_BUTTON_M1); }

	public boolean getButtonM2() { return driver.getRawButton(RobotMap.CONTROLLER_BUTTON_M2); }
	
	public XboxController getDriverController() { return driver; }

	public double getOperatorJoystickLeftX() { return deadZone(operator.getRawAxis(RobotMap.CONTROLLER_LEFT_AXIS_X), 0.3); }
	 
	public double getOperatorJoystickLeftY() { return deadZone(operator.getRawAxis(RobotMap.CONTROLLER_LEFT_AXIS_Y), 0.3); }

	public double getOperatorJoystickRightX() { return deadZone(operator.getRawAxis(RobotMap.CONTROLLER_RIGHT_AXIS_X), 0.5); }
	
	public double getOperatorJoystickRightY() { return deadZone(operator.getRawAxis(RobotMap.CONTROLLER_RIGHT_AXIS_Y), 0.3); }

	public double getOperatorLeftTrigger() { return deadZone(operator.getRawAxis(RobotMap.CONTROLLER_LEFT_TRIGGER), 0.5); }

	public double getOperatorRightTrigger() { return deadZone(operator.getRawAxis(RobotMap.CONTROLLER_RIGHT_TRIGGER), 0.5); }
	
	public boolean getOperatorLeftBumper() { return operator.getRawButton(RobotMap.CONTROLLER_LEFT_BUMPER); }

	public boolean getOperatorRightBumper() { return operator.getRawButton(RobotMap.CONTROLLER_RIGHT_BUMPER); }

	public boolean getOperatorButtonA() { return operator.getRawButton(RobotMap.CONTROLLER_BUTTON_A); }
	
	public boolean getOperatorButtonB() { return operator.getRawButton(RobotMap.CONTROLLER_BUTTON_B); }

	public boolean getOperatorButtonX() { return operator.getRawButton(RobotMap.CONTROLLER_BUTTON_X); }

	public boolean getOperatorButtonY() { return operator.getRawButton(RobotMap.CONTROLLER_BUTTON_Y); }

	public boolean getOperatorButtonBack() { return operator.getRawButton(RobotMap.CONTROLLER_BUTTON_BACK); }
	
	public boolean getOperatorButtonStart() { return operator.getRawButton(RobotMap.CONTROLLER_BUTTON_START); }

	public boolean getOperatorButtonM1() { return operator.getRawButton(RobotMap.CONTROLLER_BUTTON_M1); }

	public boolean getOperatorButtonM2() { return operator.getRawButton(RobotMap.CONTROLLER_BUTTON_M2); }

	public XboxController getOperatorController() { return operator; }

	public static double deadZone(double axisValue, double deadValue) {
		if (Math.abs(axisValue) < deadValue) {
			return 0.0;
		} else if (1 - axisValue < deadValue) {
			return axisValue;
		} else if (-1 - axisValue > -deadValue) {
			return -axisValue;
		} else {
			return axisValue;
		}
	}
	
}
