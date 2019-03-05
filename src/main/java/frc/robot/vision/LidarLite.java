package frc.robot.vision;

import java.util.Arrays;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.filters.LinearDigitalFilter;

/**
 * @author sbaron
 * @co-author ereese
 *
 */
public class LidarLite  implements PIDSource {
	
	public static final int DEFAULT_I2C_ADDRESS = 0x62;
	
	public static enum HWVERSION {
			V1,
			V2
	}
	
	@SuppressWarnings("unused")
	private LinearDigitalFilter m_lpf;
	
	public boolean flg_debug = false;
    
    /**********************************************************
	 *  Threading
	 **********************************************************/
    java.util.Timer m_scheduler;
    private double m_period;
    
    /**********************************************************
	 *  I2C
	 **********************************************************/
    // The I2C bus object
    private I2C m_i2c_bus;
    private int m_i2c_address;
    private ReentrantLock m_i2c_mutex;
    private HWVERSION m_lidar_version = HWVERSION.V2;
    
    /**********************************************************
	 *  Digital Out Pin for Enable
	 **********************************************************/
    @SuppressWarnings("unused")
	private DigitalOutput m_lidarenable;
    
    /**********************************************************
	 *  PID Interface Variables
	 **********************************************************/
    PIDSourceType m_pid_source_type = PIDSourceType.kDisplacement;
    
    /**
     * Mode Control Register - Commands the LIDAR Lite to change operating modes
     *  using a bit-packed control word;
     *  
     * Bit 0 - Preamp Off - Shutdown Preamp between measurements
     * Bit 1 - Clk Shut - External Clock Shutdown (Not Used)
     * Bit 2 - FPGA Sleep - Full FPGA sleep after measurement
     * Bit 3 - DET OFF - Turns off detector bias after measurement
     * Bit 4 - N/A
     * Bit 5 - Velocity Scale Factor
     * Bit 6 - Inhibit Reference
     * Bit 7 - Velocity
     * 
     **/
//    private static int CR_MODE_CONTROL_04 = 0x04; commented out because not being used
    
    /**
     * Mode Control Register - Commands the LIDAR Lite to change operating modes
     *  using a bit-packed control word;
     *  
     * Bit 0 - OSC Disable - Disable oscillator reference (Not Used)
     * Bit 1 - RCVR PWR Disable - Turns on receiver regulator
     * Bit 2 - SLEEP - Processor Sleep, reduces power to 20mA
     * Bit 3 - Det Bias Disable - 
     * Bit 4 - N/A
     * Bit 5 - N/A
     * Bit 6 - N/A
     * Bit 7 - N/A
     * 
     */
//    private static int CR_POWER_CONTROL_101 = 0x65; commented out because not being used
    
    /**
     * External interface for the scheduled polling data
     */
    private double m_distance = 0.0;
    private boolean m_data_valid = false;
    private double m_data_timestamp = 0;
    
	/**
	 * @author sbaron
	 * LidarLite_Task is the private scheduler within LidarLite that 
	 * automatically performs I2C reads to get the frame data
	 * from the connected Lidar device.
	 */
	private class LidarLite_Task extends TimerTask {

        private LidarLite m_lidarlite;

        public LidarLite_Task(LidarLite lidar) {
          if (lidar == null) {
            throw new NullPointerException("Given LidarLite Instance was null");
          }
          m_lidarlite = lidar;
        }

        @Override
        public void run() {
        	m_lidarlite.readDistance();
        }
      }
        
    /**
     * Constructor for the Lidar Object
     *
     * @param i2c_address_in, int, or null for default
     * @param period, double or null for default
     * @param mutex, ReentrantLock if sharing the i2c bus
     * @param i2c_bus, I2C bus or null to create a new reference
     */
    public LidarLite(I2C i2c_bus, Double period, ReentrantLock mutex, DigitalOutput lidar_enable_port, HWVERSION lidar_version)
    {
    	m_i2c_bus = i2c_bus == null ? new I2C(I2C.Port.kMXP, m_i2c_address) : i2c_bus;
    	m_period = period == null ? 0.25 : period.doubleValue();
    	m_i2c_mutex = mutex == null ? new ReentrantLock() : mutex;
    	m_lidarenable = lidar_enable_port;
    	m_lidar_version = lidar_version == null ? HWVERSION.V2 : lidar_version;
    	
    	m_scheduler = new java.util.Timer();
    	
    	m_lpf = LinearDigitalFilter.singlePoleIIR(new PIDSourceWrapper(this) {
				@Override
				public double pidGet() {
					return ((LidarLite) m_sensor).getDistance();
				}
			}, 1, m_period);
    	
    	if(period != null)
    	{
        	// Schedule the LidarLite task to execute every <period> seconds
    		this.start(m_period);
    	}
    }
    
    
    /**
     * Start a thread to read data from the Lidar periodically
     * 
     * @param period - period in seconds to schedule update
     */
    public void start(double period)
    {
    	m_period = period;
//    	double filtered_distance = m_lpf.pidGet();
    	
    	// Schedule the LidarLite task to execute every <period> seconds
    	if(m_scheduler == null)
    	{
    		System.out.println("Attempting to enable Lidar at a " + Double.toString(period) + " second rate.");
    		
    	} else if(period > 0) {
    		System.out.println("Rescheduling Lidar Thread at a " + Double.toString(period) + " second rate.");
    		m_scheduler.cancel();

    	} else {
    		System.out.println("Period must be greater than zero! Given " + Double.toString(period));
    		return;
    	}
    	
    	m_scheduler = new java.util.Timer();
		m_scheduler.schedule(new LidarLite_Task(this), 0L, (long) (m_period * 1000));
    }
    
    
    /**
     * Cancel a running timer and attempt to null the variable
     */
    public void stop()
    {
    	// If the timer object is not null, cancel the scheduler
    	if(m_scheduler != null)
    	{
    		System.out.println("Attempting to disable LidarLite auto polling.");
    		m_scheduler.cancel();
    		m_scheduler.purge();
    		m_scheduler = null; // this may break things or cause memory leaks, verify
    	} else {
    		// nothing to do
    	}
    }
    
    /**
     * Polls the I2C bus to see if a device with the address specified is present
     * @return - true if present, false if not detected
     */
    public synchronized boolean isI2CDevicePresent()
    {
    	m_i2c_mutex.lock();
    	
    	try
    	{
    		return !m_i2c_bus.addressOnly();
    	} finally {
    		m_i2c_mutex.unlock();
    	}
    }
    
    /**
     * Polls the I2C bus to read the serial number from the connected LIDAR
     * @return - true if present, false if not detected
     */
    public synchronized int readSerialNumber()
    {
    	final byte readBuffer[] = new byte [2];
    	int serialNumber = 0;
    	
    	m_i2c_mutex.lock();
    	
    	try
    	{
    		// Read data from the i2c bus        	
        	switch(m_lidar_version)
        	{
        		case V1:
        			m_i2c_bus.transaction(new byte[]{(byte)0x16},1,readBuffer,2);
        			break;
        		case V2:
        			m_i2c_bus.writeBulk(new byte[]{(byte)0x16});
                	m_i2c_bus.readOnly(readBuffer, 2);
                	break;
        	}
        	
        	// The contents of byte 5 and 6 represents the distance in CM
        	serialNumber = ((readBuffer[0] << 8) | readBuffer[1]) & 0xFFFF;
        	
    	} finally {
    		m_i2c_mutex.unlock();
    	}
    	
    	return serialNumber;
    }
    
    /**
     * Commands the lidar to return data based on distance not signal strength
     */
    public synchronized void configReturn(boolean returnShortest, boolean useDistance)
    {
    	byte writeCommand = 0x00;
    	
    	if(!returnShortest)
    		writeCommand |= 0x04;
    	
    	if(useDistance)
    		writeCommand |= 0x02;
    	
    	m_i2c_mutex.lock();
    	
    	try
    	{
    		// Read data from the i2c bus        	
        	switch(m_lidar_version)
        	{
        		case V1:
        			m_i2c_bus.transaction(new byte[]{(byte)0x4b,writeCommand},2,null,0);
        			break;
        		case V2:
        			m_i2c_bus.writeBulk(new byte[]{(byte)0x4b,writeCommand});
                	break;
        	}
        	
        	
    	} finally {
    		m_i2c_mutex.unlock();
    	}
    }
    
    /**
     * Attempts to read data from the LidarLite. If the I2C read fails or if 
     * all data that is returned is 0 or negative (invalid) a -1 will be
     * returned. If the read is a success, the distance detected will be returned
     * and the corresponding values in the class will be set.
     * 
     * @return - distance detected in cm
     */
    public synchronized double readDistance()
    {
    	final byte readBuffer[] = new byte [2];
    	final boolean stabilizePreamp = true;
    	double processedDistance = Double.NaN;
    	
    	// Lock the bus mutex
    	m_i2c_mutex.lock();
    	try
    	{
    		// Command the Lidar to update the distance reading
    		if(stabilizePreamp)
    		{
    			m_i2c_bus.writeBulk(new byte[]{0x00,0x04});
    		} else {
    			m_i2c_bus.writeBulk(new byte[]{0x00,0x03});
    		}
    		
        	// Wait for the distance read to complete and be loaded into the Lidar controller
        	Timer.delay(0.005);
        	
        	// Read data from the i2c bus        	
        	switch(m_lidar_version)
        	{
        		case V1:
        			m_i2c_bus.transaction(new byte[]{(byte)0x8f},1,readBuffer,2);
        			break;
        		case V2:
        			m_i2c_bus.writeBulk(new byte[]{(byte)0x8f});
                	m_i2c_bus.readOnly(readBuffer, 2);
                	break;
        	}
    	} finally {
    		m_i2c_mutex.unlock();
    	}
    	
    	// If every element in the readBuffer is zero, no data was returned on the i2c bus
    	if(Arrays.equals(new byte[]{0,0}, readBuffer))
    	{
    		//System.out.println("Lidar Lite - Read Failed! All elements returned were 0!");
        	setDataValid(false);
    	} else {
    		// The contents of byte 0 and 1 represents the distance in CM
        	processedDistance = (readBuffer[0] & 0xFF) << 8 | (readBuffer[1] & 0xFF);
    		
        	// If the MSB of the high byte is not 1 (positive)
        	if(processedDistance <= 50*12*2.54 && ((readBuffer[0] & 0x80) >> 7) != 1)
        	{
            	setDistance(processedDistance);
            	
            	setDataTimestamp(Timer.getFPGATimestamp());
            	setDataValid(true);
            	
        	} else { // Set to -1 (invalid)
        		processedDistance = Double.NaN;
        		setDataValid(false);
        	}
    	}
    	
    	
    	if(flg_debug)
    	{   		
    		System.out.println( "Raw Data: "
					+ Integer.toString(readBuffer[0] & 0xFF) 
					+ Integer.toString(readBuffer[1] & 0xFF) +
					" Data Received: " + Double.toString(processedDistance));
    		
    		System.out.println("Lidar Lite - Distance: " + Double.toString(processedDistance));
    	}

    	return processedDistance;
    }
    
    
    /************************************************************
     * Getters and Setters
     ************************************************************/
    
    
    /**
     * Returns the most recent distance measurement in feet
     * @return distance measured in feet
     */
    public synchronized double getDistance() {
		return (m_distance / 30.48);
	}
    
    /**
     * Returns the most recent distance measurement in cm
     * @return distance measured in cm
     */
    public synchronized double getDistanceCm() {
    	return m_distance;
    }

	/**
	 * Sets the distance property from the thread performing the i2c reads
	 * @param distance measured in feet
	 */
	private synchronized void setDistance(double processedDistance) {
		this.m_distance = processedDistance;
	}

	/**
	 * Indicates if the distance reading is valid and fresh
	 * @return
	 */
	public synchronized boolean isDataValid() {
		return m_data_valid;
	}

	/**
	 * Sets the data validity field
	 * @param data_valid
	 */
	private synchronized void setDataValid(boolean m_data_valid) {
		this.m_data_valid = m_data_valid;
	}

	/**
	 * Returns the timestamp of the last valid distance reading
	 * @return timestamp
	 */
	public synchronized double getDataTimestamp() {
		return m_data_timestamp;
	}
	
	/**
	 * Returns the number of seconds since the last successful data update
	 * @return
	 */
	public synchronized double getDataAge() {
		return Timer.getFPGATimestamp() - getDataTimestamp();
	}

	/**
	 * Sets the timestamp for the last successful data read
	 * @param m_data_timestamp
	 */
	private synchronized void setDataTimestamp(double m_data_timestamp) {
		this.m_data_timestamp = m_data_timestamp;
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
	public double pidGet() {
		return getDistance();
	}

}