package frc.robot;

import edu.wpi.first.wpilibj.XboxController;

public class OI {
	
	private XboxController driver;
	private XboxController operator;
	
	public OI() {
		driver = new XboxController(RobotMap.CONTROLLER_DRIVER);
		operator = new XboxController(RobotMap.CONTROLLER_OPERATOR);
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
