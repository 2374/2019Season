package frc.robot;

import edu.wpi.first.wpilibj.XboxController;

public class OI {
	
	private XboxController controller;
	
	public OI() {
		controller = new XboxController(RobotMap.CONTROLLER);
	}
	
	public double getJoystickLeftX() { return deadZone(controller.getRawAxis(RobotMap.CONTROLLER_LEFT_AXIS_X), 0.3); }
	 
	public double getJoystickLeftY() { return deadZone(controller.getRawAxis(RobotMap.CONTROLLER_LEFT_AXIS_Y), 0.3); }

	public double getJoystickRightX() { return deadZone(controller.getRawAxis(RobotMap.CONTROLLER_RIGHT_AXIS_X), 0.5); }
	
	public double getJoystickRightY() { return deadZone(controller.getRawAxis(RobotMap.CONTROLLER_RIGHT_AXIS_Y), 0.3); }

	public double getLeftTrigger() { return deadZone(controller.getRawAxis(RobotMap.CONTROLLER_LEFT_TRIGGER), 0.5); }

	public double getRightTrigger() { return deadZone(controller.getRawAxis(RobotMap.CONTROLLER_RIGHT_TRIGGER), 0.5); }
	
	public boolean getLeftBumper() { return controller.getRawButton(RobotMap.CONTROLLER_LEFT_BUMPER); }

	public boolean getRightBumper() { return controller.getRawButton(RobotMap.CONTROLLER_RIGHT_BUMPER); }

	public boolean getButtonA() { return controller.getRawButton(RobotMap.CONTROLLER_BUTTON_A); }
	
	public boolean getButtonB() { return controller.getRawButton(RobotMap.CONTROLLER_BUTTON_B); }

	public boolean getButtonX() { return controller.getRawButton(RobotMap.CONTROLLER_BUTTON_X); }

	public boolean getButtonY() { return controller.getRawButton(RobotMap.CONTROLLER_BUTTON_Y); }
	
	public boolean getButtonBack() { return controller.getRawButton(RobotMap.CONTROLLER_BUTTON_BACK); }
	
	public boolean getButtonStart() { return controller.getRawButton(RobotMap.CONTROLLER_BUTTON_START); }

	public XboxController getController() { return controller; }
	
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
