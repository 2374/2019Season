package frc.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Intake;
import frc.robot.vision.AdvancedPIDSource.PIDType;
import frc.robot.vision.LidarLite;
import frc.robot.vision.LidarLite.HWVERSION;
import frc.robot.vision.Pixy;
import frc.robot.vision.Pixy.FrameOrder;

public class Robot extends TimedRobot {
	
	private static OI oi;
	private static Drivetrain drivetrain;
	private static Intake intake;
	private static Elevator elevator;
	private static Pixy pixy;
	private static LidarLite lidar;

	@Override
	public void robotInit() {
		oi = new OI();
		drivetrain = new Drivetrain();
		intake = new Intake();
		elevator = new Elevator();
		pixy = new Pixy(new I2C(I2C.Port.kOnboard, 0x54), 0.02, null);
		lidar = new LidarLite(new I2C(I2C.Port.kOnboard, 0x62), 0.02, null, null, HWVERSION.V2);
		
		pixy.setPIDType(PIDType.ANGLE);
		pixy.setSortBy(FrameOrder.AREA);
		pixy.setSortAscending(false);
		pixy.setMaxObjects(2);
		
		UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(160, 120);
		camera.setBrightness(40);
	}
	
	@Override
	public void autonomousInit() {
		Scheduler.getInstance().run();
	}

	@Override
	public void autonomousPeriodic() {
		
	}

	@Override
	public void teleopInit() {
		
	}

	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
		
		SmartDashboard.putNumber("Pixy Objects Detected", pixy.getNumObjectsDetected());
		SmartDashboard.putNumber("Pixy Degree Offset", pixy.getOffset(1));
	}
	
	@Override
	public void testPeriodic() {
		//new Keypad();
	}
	
	@Override
	public void disabledInit() {
		
	}
	
	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}
	
	public static OI getInput() { return oi; }
	
	public static Drivetrain getDrivetrain() { return drivetrain; }
	
	public static Intake getIntake() { return intake; }
	
	public static Elevator getElevator() { return elevator; }
	
	public static Pixy getPixy() { return pixy; }
	
	public static LidarLite getLidar() { return lidar; }
	
}
