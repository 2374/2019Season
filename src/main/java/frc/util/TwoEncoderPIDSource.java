package frc.util;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class TwoEncoderPIDSource implements PIDSource {
 
	private Encoder leftEncoder, rightEncoder;
	
	public static final double WHEEL_DIAMETER_INCHES = 6.0;
	// these values need to be recalibrated
	public static final double EC_PER_REV_LEFT = 4096;
	public static final double EC_PER_REV_RIGHT = 4096;
	
	// use encoder constructor of form (sourceA, sourceB, reverseDirection, CounterBase.k4x)
	public TwoEncoderPIDSource(Encoder left, Encoder right) {
		leftEncoder = left;
		rightEncoder = right;
	}

	@Override
	public PIDSourceType getPIDSourceType() {
		return PIDSourceType.kDisplacement;
	}

	@Override
	public double pidGet() {
		double leftInches = getLeftDistanceInches();
		double rightInches = getRightDistanceInches();
		return (leftInches + rightInches) / 2;
	}

	@Override
	public void setPIDSourceType(PIDSourceType pidSource) {
	}

	public double getLeftDistanceInches() {
		return encoderCntsToInches(leftEncoder.getDistance(), EC_PER_REV_LEFT);
	}

	public double getRightDistanceInches() {
		return encoderCntsToInches(rightEncoder.getDistance(), EC_PER_REV_RIGHT);
	}

	public static double encoderCntsToInches(double counts, double countsPerRev) {
		return (counts / countsPerRev) * (WHEEL_DIAMETER_INCHES * Math.PI);
	}

	public static double inchesToEncoderCnts(double inches, double countsPerRev) {
		return inches * countsPerRev / (WHEEL_DIAMETER_INCHES * Math.PI);
	}

}
