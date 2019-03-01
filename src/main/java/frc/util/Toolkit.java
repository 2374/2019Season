package frc.util;

public class Toolkit {

	/**
	 * Limits an input between -1 and 1
	 * @param num Input number
	 * @return Limited number
	 */
	public static double limit(double num) {
		return Math.max(Math.min(num, 1.0), -1.0);	
	}
	
}
