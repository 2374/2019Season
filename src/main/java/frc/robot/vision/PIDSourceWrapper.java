package frc.robot.vision;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

/**
 * @author sbaron
 * @co-author ereese
 *
 */
public abstract class PIDSourceWrapper implements PIDSource{

	protected Object m_sensor;
	protected PIDSourceType m_pid_source_type = PIDSourceType.kDisplacement;
	
	public PIDSourceWrapper(Object sensor)
	{
		m_sensor = sensor;
	}
	
	@Override
	public void setPIDSourceType(PIDSourceType pidSource) {
    	m_pid_source_type = pidSource;		
	}

	@Override
	public PIDSourceType getPIDSourceType() {
		return m_pid_source_type;
	}

	@Override
	public abstract double pidGet();

}
