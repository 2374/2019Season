package frc.robot.vision;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Timer;

/**
 * Class implements the interface to the Pixy Camera via I2C.
 *
 * Creates and manages the interface to the Pixy Camera. Able to create a separate thread to process frames 
 * periodically and return the frame data for any detected objects.
 */

/**
 * @author sbaron
 * @co-author ereese
 * Copied from https://bitbucket.org/team702/team-702-pixy-code/overview, all credit goes to its rightful owners
 *
 */
public class Pixy implements AdvancedPIDSource {
	
	/**
     * The frame data that is returned by the Pixy camera over the I2C interface
     */
    public static class PixyFrame
    {
        public int sync = 0;
        public int checksum = 0;
        public double timestamp = 0;
        public int signature;
        public int xCenter;
        public int yCenter;
        public int width;
        public int height;
        public int angle;
        public int area;
        
    }
    
    
    /**
     * Order to return the sorted frames
     */
    public enum FrameOrder {
    	XCenter,
    	YCenter,
    	HEIGHT,
    	WIDTH,
    	ANGLE,
    	AREA,
    	SIGNATURE
    }
    
	/**********************************************************
	 *  Pixy CMU Communication Parameters
	 **********************************************************/
	public static final int PIXY_ARRAYSIZE               = 64;
	public static final int PIXY_START_WORD              = 0xaa55;
	public static final int PIXY_START_WORD_CC           = 0xaa56;
	public static final int PIXY_START_WORDX             = 0x55aa;
	public static final int PIXY_SERVO_SYNC              = 0xff;
	public static final int PIXY_CAM_BRIGHTNESS_SYNC     = 0xfe;
	public static final int PIXY_LED_SYNC                = 0xfd;
	public static final int PIXY_OUTBUF_SIZE             = 64;
	public static final int PIXY_SYNC_BYTE               = 0x5a;
	public static final int PIXY_SYNC_BYTE_DATA          = 0x5b;
	
	/**********************************************************
	 * Define Constants for the image size
	 *********************************************************/
	public static final int PIXY_MIN_X = 0;
	public static final int PIXY_MAX_X = 319;
	public static final double PIXY_X_CENTER = (PIXY_MIN_X + PIXY_MAX_X)/2;
	public static final int PIXY_MIN_Y = 0;
	public static final int PIXY_MAX_Y = 199;
	public static final double PIXY_Y_CENTER = (PIXY_MIN_Y + PIXY_MAX_Y)/2;
	
	/**********************************************************
	 * Stock field of view is 75 degrees horizontal and 47 degrees vertical
	 *********************************************************/
	public static final double PIXY_X_FOV = 75.0;
	public static final double PIXY_Y_FOV = 47.0;
	public static final double PIXY_X_DEG_PER_PIXEL = PIXY_X_FOV/(PIXY_MAX_X+1);
	public static final double PIXY_Y_DEG_PER_PIXEL = PIXY_Y_FOV/(PIXY_MAX_Y+1);
	
	/**********************************************************
	 *  Pixy CMU I2C Interface
	 **********************************************************/
    private static final int DATA_SIZE = 92;
    
    /**********************************************************
	 *  Local Variables
	 **********************************************************/
    private List<PixyFrame> m_currentframes;
    private double m_lastupdate = 0.0;
    private byte m_zeroBuffer[];
    private double m_maxDataAge = 0.2; // second
	private int m_max_objects = 2;
	private int m_min_objects = 2;
    private FrameOrder m_sort_by = FrameOrder.XCenter;
    private boolean m_sort_ascending = true;
	
	/**********************************************************
	 *  Threading
	 **********************************************************/
    private java.util.Timer m_scheduler;    // The timer object to manage the thread
    private double m_period = 0.02; // 0.02 of a second seems to be a stable update rate
    
    /**********************************************************
	 *  PID Interface Variables
	 **********************************************************/
    private PIDSourceType m_pid_source_type = PIDSourceType.kDisplacement;
    private PIDType m_pid_type      = PIDType.ANGLE;
    private volatile double m_degrees_from_target = 0;
    /**********************************************************
	 *  I2C
	 **********************************************************/
    public static final int PIXY_I2C_DEFAULT_ADDR = 0x54; //0xa8;
    private I2C m_i2c_bus;
    private int m_i2c_address;
    private ReentrantLock m_i2c_mutex;
    
    /**********************************************************
	 *  Debug variables
	 **********************************************************/
    private static boolean flg_debug = false;
    
	/**
	 * PixyTask is the private scheduler within Pixy that 
	 * performs I2C reads to get the frame data
	 * from the connected Pixy device.
	 */
	private class PixyTask extends TimerTask {

        private Pixy m_pixy;

        public PixyTask(Pixy pixy) {
          if (pixy == null) {
            throw new NullPointerException("Pixy Instance was null");
          }
          this.m_pixy = pixy;
        }

        @Override
        public void run() {
        	m_pixy.getFrames();
        }
      }
    
    /**
     * The constructor for the class. Initialize any data and the bus, pass null to any parameters not being used. 
     * The class allows an optional ReentrantLock to be passed for if the Pixy is sharing the I2C bus with
     * another device. The WPILib class should handle this without needing a lock.
     * @param i2c_port - The I2C object for the Pixy - new I2C(I2C.Port.kOnboard, 0x54)
     * @param period - The period in seconds to perform I2C reads
     * @param mutex - The reentrant lock if the bus is being shared
     */
    public Pixy(I2C i2c_port, Double period, ReentrantLock mutex)
    {
    	// Set I2C address and period
    	this.setPeriod((period == null ? 0.1 : period.doubleValue()));
    	this.m_i2c_bus = i2c_port == null ? new I2C(I2C.Port.kOnboard, getI2CAddress()) : i2c_port;
    	this.m_i2c_mutex = mutex == null ? new ReentrantLock() : mutex;
    	
    	//this.setI2CAddress((i2c_address_in == null ? PIXY_I2C_DEFAULT_ADDR : i2c_address_in.intValue()));
    	//m_i2c_bus = new I2C(I2C.Port.kOnboard, getI2CAddress());
    	
    	m_currentframes = new LinkedList<PixyFrame>();
    	m_zeroBuffer = new byte [DATA_SIZE];
    	
    	for(int idx = 0; idx < DATA_SIZE; idx++)
    		m_zeroBuffer[idx] = (byte)0;
    	
    	// Call the start method to schedule reading the data periodically
    	if(period != null)
    	{
    		this.start(getPeriod());
    	}
    }
    
    /**
     * Start a thread to read data from the Pixy periodically
     * 
     * @param period - period in seconds to schedule update
     */
    public void start(double period)
    {
    	setPeriod(period);
   	
    	// Schedule the Pixy task to execute every <period> seconds
    	if(m_scheduler == null && period > 0)
    	{
    		System.out.println("Attempting to enable Pixy at a " + Double.toString(period) + " second rate.");
    	} else if(period > 0) {
    		System.out.println("Rescheduling Pixy Thread at a " + Double.toString(period) + " second rate.");
    		m_scheduler.cancel();
    	} else {
    		System.out.println("Period must be greater than zero! Given " + Double.toString(period));
    		return;
    	}
    	
    	// Start the timer
    	m_scheduler = new java.util.Timer();
		m_scheduler.schedule(new PixyTask(this), 0L, (long) (this.getPeriod() * 1000));
    }
    
    
    /**
     * Cancel a running timer and attempt to null the variable
     */
    public void stop()
    {
    	// If the timer object is not null, cancel the scheduler
    	if(m_scheduler != null)
    	{
    		System.out.println("Attempting to disable Pixy auto polling.");
    		m_scheduler.cancel();
    		
    		// Set the timer to NULL to allow it to be reallocated if necessary
    		m_scheduler = null;
    		
    	} else {
    		// nothing to do
    	}
    }
    
    /**
     * Performs an I2C read at the specified address and decodes any data received.
     * $
     * @return linked list of PixyFrames of detected objects
     */
    public synchronized void getFrames()
    {
    	// Initialize the local linked list for the results
    	List<PixyFrame> frames = new LinkedList<PixyFrame>();
    	
    	// readBuffer is the raw data output from the i2c bus
    	byte [] readBuffer = new byte [DATA_SIZE];
    	boolean isEqual = true;
    	
    	// Lock the bus mutex
    	m_i2c_mutex.lock();
    	try
    	{
	    	// Read data from the i2c bus 
	    	m_i2c_bus.readOnly(readBuffer, DATA_SIZE);
    	} finally {
    		m_i2c_mutex.unlock();	
    	}
    	
		for(int idx = 0; idx < readBuffer.length; idx++)
		{
			if(m_zeroBuffer[idx] != readBuffer[idx])
			{
				isEqual = false;
				break;
			}
		}
    	
    	if(isEqual)
    	{
    		if(flg_debug)
    			System.out.println("Pixy " + this.m_i2c_address + " - Read Failed! All elements returned were 0!");
    		return;
    	}
    	
    	if(flg_debug)
    	{
    		// Allocate debug string
    		String readBuffer_char = "";
        	
    		// Build the string for debug printouts        	
    		for(int idx = 0; idx < readBuffer.length; idx++)
    			readBuffer_char += Integer.toString(readBuffer[idx] & 0xFF) + " ";

    		// Print the raw buffer to the console
    		System.out.println(readBuffer_char);
    	}
    	
    	/* 
    	 * Move through the array and look for the pattern that indicates the start of a frame.
    	 * Frame data is encoded as follows:
    	 * 
         * Bytes    16-bit word    Description
	     * ----------------------------------------------------------------
	     * 0, 1     y              sync: 0xaa55=normal object, 0xaa56=color code object
	     * 2, 3     y              checksum (sum of all 16-bit words 2-6)
	     * 4, 5     y              signature number
	     * 6, 7     y              x center of object
	     * 8, 9     y              y center of object
	     * 10, 11   y              width of object
	     * 12, 13   y              height of object
    	 */
		for(int idx = 0; idx < readBuffer.length-18; idx++ ) 
    	{
    		/*
    		 * The Pixy sends the data in Little Endian format [https://en.wikipedia.org/wiki/Endianness]
	    	 *  with the data packed in 16 bit integers (4 hex digits each) [0xFF]. Little Endian format 
	    	 *  means that the bytes (one hex digit) are sent with the least-significant byte first, so they
	    	 *  need to be reversed before combining them into a 16 bit word.
    		 * */	
			
    		// Two Pixy start words 0xAA55 indicate that 
			int firstWord = (((readBuffer[idx+1] << 8) | readBuffer[idx] ) & 0xFFFF);
			int nextWord  = (((readBuffer[idx+3] << 8) | readBuffer[idx+2] ) & 0xFFFF);
			
    		if( firstWord != PIXY_START_WORD  )
    		{
    			// If this word wasn't found, cancel executing the rest of the loop iteration and move to the next byte
    			continue; 
    		}
    		
    		int startIdx = idx+2;
			
    		if(frames.size() == 0)
    		{
    			// The next word needs to be a start word to indicate a new block
    			if( nextWord != PIXY_START_WORD && nextWord != PIXY_START_WORD_CC)
        		{
        			continue;
        		}
    			
    			startIdx += 2;
    			
    		} else {
    			
    			if( nextWord  == PIXY_START_WORD )
    			{
            		continue;
    			}
    			
    		}
    		    		
    		// Move the index up
    		idx+=2;
    		
    		/* If we make it this far, we found two instances of 0xaa55 back-to-back which means that the next 14 bytes
    		 * make up the data for the frame. Create a new instance of the Frame class (which acts as a structure) and start 
    		 * pulling out byte pairs and switching them before packing them into integers. Since some of the data 
    		 * on the i2c bus is encoded as unsigned 16 bit integers and Java doesn't have unsigned types, we need to manually
    		 * re-encode the data into 32 bit integers which can correctly represent the value.
    		 * [https://en.wikipedia.org/wiki/Integer_(computer_science)]
    		 * 
    		 * To do this we treat the 16 bit unsigned value as a 32 bit signed value (how Java stores integers)
    		 * so the real-world value that we can work with represents the correct data.
    		 */
        	PixyFrame tempFrame = new PixyFrame();
    		tempFrame.checksum = convertBytesToInt(readBuffer[startIdx+1], readBuffer[startIdx]); startIdx+=2;
    		tempFrame.signature = convertBytesToInt(readBuffer[startIdx+1], readBuffer[startIdx]); startIdx+=2;
    		tempFrame.xCenter = convertBytesToInt(readBuffer[startIdx+1], readBuffer[startIdx]); startIdx+=2;
    		tempFrame.yCenter = convertBytesToInt(readBuffer[startIdx+1], readBuffer[startIdx]); startIdx+=2;
    		tempFrame.width = convertBytesToInt(readBuffer[startIdx+1], readBuffer[startIdx]); startIdx+=2;
    		tempFrame.height = convertBytesToInt(readBuffer[startIdx+1], readBuffer[startIdx]); startIdx+=1;
    		tempFrame.area = tempFrame.height * tempFrame.width;
    		tempFrame.timestamp = m_lastupdate;
    		idx = startIdx;
    		
    		// Verify the checksum -- If it fails skip parsing the frame
    		if(tempFrame.checksum != tempFrame.signature + tempFrame.xCenter + tempFrame.yCenter + tempFrame.width + tempFrame.height)
    		{
    			System.out.println("Checksum Error!");
    			continue;
    		}
    		
    		// Concatenate the data in Frame into a string and print to the console
    		if(flg_debug)
    		{
	    		System.out.println("Checksum: "+ Integer.toString(tempFrame.checksum) + 
	    				" Signature: "+ Integer.toString(tempFrame.signature) +
	    				" xCenter: "+ Integer.toString(tempFrame.xCenter) + 
	    				" yCenter: "+ Integer.toString(tempFrame.yCenter) +
	    				" width: "+ Integer.toString(tempFrame.width) +
	    				" height: "+ Integer.toString(tempFrame.height));
    		}
    		
    		if(frames.size() >= m_max_objects)
    		{
    			break;
    		}
    		
    		// Append the constructed frame to the Linked List which will be returned to the caller
    		frames.add(tempFrame);
    	}
    	
		// If more than one frame was returned, sort objects by their sort by property
		if(frames.size() > 1)
		{
			boolean asc = getSortAscending();
			switch(getSortBy())
			{
				case ANGLE:
						Collections.sort(frames, (a, b) -> (asc ? a.angle : b.angle) - (asc ? b.angle : a.angle));
					break;
				case AREA:
					Collections.sort(frames, (a, b) -> (asc ? a.area : b.area) - (asc ? b.area : a.area));
					break;
				case HEIGHT:
					Collections.sort(frames, (a, b) -> (asc ? a.height : b.height) - (asc ? b.height : a.height));
					break;
				case SIGNATURE:
					Collections.sort(frames, (a, b) -> (asc ? a.signature : b.signature) - (asc ? b.signature : a.signature));
					break;
				case WIDTH:
					Collections.sort(frames, (a, b) -> (asc ? a.width : b.width) - (asc ? b.width : a.width));
					break;
				case XCenter:
					Collections.sort(frames, (a, b) -> (asc ? a.xCenter : b.xCenter) - (asc ? b.xCenter : a.xCenter));
					break;
				case YCenter:
					Collections.sort(frames, (a, b) -> (asc ? a.yCenter : b.yCenter) - (asc ? b.yCenter : a.yCenter));
					break;
			}
		}
		
		/**
		 *  Occasionally the pixy will return a frame with no objects despite
		 *  having objects in the FoV, if this happens we don't want to update
		 *  the frame with them unless the current data has expired 
		 */
		if(frames.size() > 0 || getDataAge() > getMaxDataAge())
		{
			m_lastupdate = Timer.getFPGATimestamp();
    		m_currentframes.clear();
    		m_currentframes = frames;
		}
    }
    
    
    /**************************************************************
     * 
     * HELPER METHODS--HELPER METHODS--HELPER METHODS
     * 
     **************************************************************/

    /**
     * Sets the brightness value of the Pixy camera
     * $
     * @param brightness - 0-255
     * @return false if success, true if abort
     */
    public synchronized boolean setBrightness(byte brightness)
    {
    	byte[] outBuf = new byte[3];
    	
    	outBuf[0] = 0x00;
    	outBuf[1] = (byte)PIXY_CAM_BRIGHTNESS_SYNC;
    	outBuf[2] = brightness;
    	
    	// Perform the I2C Transaction
    	return m_i2c_bus.transaction(outBuf, 3, null, 0);
    }
    
    /**
     * Sets the RGB LED value of the Pixy camera
     * $
     * @param R
     * @param G
     * @param B
     * @return false if success, true if abort
     */
    public synchronized boolean setLED(byte R, byte G, byte B)
    {
    	byte[] outBuf = new byte[5];
    	
    	outBuf[0] = 0x00;
    	outBuf[1] = (byte)PIXY_LED_SYNC;
    	outBuf[2] = R;
    	outBuf[3] = G;
    	outBuf[4] = B;
    	
    	// Perform the I2C Transaction
    	return m_i2c_bus.transaction(outBuf, 5, null, 0);
    }
    
    /**
     * @return the number of targets currently detected
     */
    public synchronized int getNumObjectsDetected()
    {
    	return getCurrentframes().size();
    }
    
    /**
     * Remove this and replace with getTotalDegrees(int nObjects)
     * @return the total degrees from center of 1 or 2 objects
     */
    @Deprecated
    public synchronized double getTotalDegrees() {
    	double degrees = 0.0;
    	
    	if(getNumObjectsDetected() == 1)
		{
    		degrees = Pixy.degreesXFromCenter(getCurrentframes().get(0));
		} else if(getNumObjectsDetected() >= 2) {
			degrees = (Pixy.degreesXFromCenter(getCurrentframes().get(0)) +
							 Pixy.degreesXFromCenter(getCurrentframes().get(1))) / 2;
			
		} else {
			degrees = Double.NaN;
		}
    	return degrees;
    }
    
    /**
     * Get the average degrees from center of N detected objects
     * @param nObjects - Number of detected objects
     * @return average area or Double.NaN if objects < nObjects
     */
    public synchronized double getTotalDegrees(int nObjects) {
    	double degrees = 0.0;
//    	System.out.println("n objects: " + nObjects);
//    	System.out.println("Objects: " + getNumObjectsDetected());
    	
    	if(getNumObjectsDetected() >= nObjects)
		{
    		for(int idx = 0; idx < nObjects; idx++)
    		{
    			degrees+=Pixy.degreesXFromCenter(getCurrentframes().get(idx));
    		}
    		degrees /= nObjects;
			
		} else {
			degrees = Double.NaN;
		}
    	return degrees;
    }
    
    /**
     * Calculates the total area of all detected objects
     * @param nObjects
     * @return total area
     */
    public synchronized double getTotalArea(int nObjects) {
    	double area = 0.0;
    	
    	if(getNumObjectsDetected() >= nObjects)
		{
    		for(int idx = 0; idx < nObjects; idx++)
    		{
    			area+=getCurrentframes().get(idx).area;
    		}
		} else {
			area = Double.NaN;
		}
    	return area;
    }
    
    
    
    public synchronized double getOffset(int targetNumber) {
    	
    	if (getDataAge() < 1) {
    		
        	if (getNumObjectsDetected() > targetNumber) {
        		
        		return Pixy.degreesXFromCenter(getCurrentframes().get(targetNumber));
        		
        	} else {
        		
        		return -0.0;
        		
        	}
    		
    	}
    	
    	return 0.0;
    	
    }
    
    /**
     * Return the difference in area between objects 0 and 1
     * @param nObjects - Minimum number of detected objects 
     * @return area difference
     */
    public synchronized double getTotalOffset(int nTargets) {
    	
    	if(getNumObjectsDetected() >= nTargets)
		{
    		return getCurrentframes().get(0).area - getCurrentframes().get(1).area;
		} else {
			return 0;
		}
    }
    
    /**
     * Return the normalized area difference between two targets
     * @param nObjects - Minimum number of detected objects
     * @return normalized offset between -1 and 1
     */
    public synchronized double getNormalizedOffset(int nObjects) {
    	
    	if(getNumObjectsDetected() >= nObjects)
		{
    		double totArea = 0;
    		
    		for(PixyFrame frame : getCurrentframes())
    		{
    			totArea += frame.area;
    		}
    		
    		return (getCurrentframes().get(0).area - getCurrentframes().get(1).area) / totArea;

		} else {
			return 0;
		}
    }
    
    
    /**************************************************************
     * 
     * STATICS--STATICS--STATICS--STATICS--STATICS--STATICS
     * 
     **************************************************************/
    
    /**
     * Converts two unsigned bytes to a signed integer
     * $
     * @param msb - Most significant byte
     * @param lsb - Least significant byte
     * @return - Integer value
     */
    public static int convertBytesToInt(int msb, int lsb)
    {
        if (msb < 0)
            msb += 256;
        int value = msb * 256;

        if (lsb < 0)
        {
            // lsb should be unsigned
            value += 256;
        }
        value += lsb;
        return value;
    }
    
    /**
     * Calculates the estimated distance the object(s) are away from the pixy
     * @param area of detected objects
     * @param constant determined from calibration
     * @return distance 
     */
    public static double getCalcDistance(double area, double constant)
    {
    	return Math.pow(constant / area,1/1.9);
    }
    
    /**
     * Returns the offset from the X center, negative indicates center is to the left, positive indicates it is to the right.
     * @param frame PixyFrame representing the detected object
     * @return number of pixels from center
     */
    public static double xCenterDelta(PixyFrame frame)
    {
    	return frame.xCenter - Pixy.PIXY_X_CENTER;
    }
    
    /**
     * Returns the number of degrees along the horizontal axis the detected object is away from the center
     * $
     * @param frame PixyFrame representing the detected object
     * @return degrees along x axis from center of field of view
     */
    public static double degreesXFromCenter(PixyFrame frame)
    {
    	return xCenterDelta(frame)*PIXY_X_DEG_PER_PIXEL;
    }
    
    /**************************************************************
     * 
     * GETTERS/SETTERS--GETTERS/SETTERS--GETTERS/SETTERS
     * 
     **************************************************************/
    
    /**
     * @return currentFrames - Returns the thread synchronized list of current frames
     */
    public synchronized List<PixyFrame> getCurrentframes() {
		return m_currentframes;
	}
    
    /**
     * @return m_lastupdate - The FPGA timestamp of the last time the frame was processed.
     */
    public synchronized double getLastupdate()
    {
    	return m_lastupdate;
    }
    
    /**
     * The number of seconds since the last Pixy Frame was processed.
     * $
     * @return dataAge - The number of seconds between the current timestamp and the last time the Pixy Frame was processed
     */
    public synchronized double getDataAge()
    {
    	return Timer.getFPGATimestamp() - this.getLastupdate();
    }
    
    /**
     * Sets the frequency the read process will occur at.
     * $
     * @param period in seconds
     */
    public synchronized void setPeriod(double period)
    {
    	if(period < 0)
    	{
    		throw new IllegalArgumentException("Period must be a positive value");
    	}
    	
    	m_period = period;
    }
    
    /**
     * Gets the frequency the read process will occur at.
     * $
     * @return period in seconds
     */
    public synchronized double getPeriod()
    {
    	return this.m_period;
    }
    
    /**
     * Sets the i2c address of the Pixy to connect to
     * $
     * @param i2c_address
     */
    public synchronized void setI2CAddress(int i2c_address)
    {
    	if(i2c_address <= 0 || i2c_address > 255)
    	{
    		throw new IllegalArgumentException("Invalid I2C address for Pixy Camera");
    	}
    	
    	this.m_i2c_address = i2c_address;
    }
    
    /**
     * Gets the i2c address of the Pixy we are trying to communicate with
     * $
     * @return i2c address
     */
    public synchronized int getI2CAddress()
    {
    	return this.m_i2c_address;
    }   
    
    /**
	 * Gets the maximum data age for a frame to consider it still valid
	 * @return seconds a frame should be considered valid after it's been read
	 */
    public synchronized double getMaxDataAge() {
		return m_maxDataAge;
	}

    /**
	 * Sets the maximum data age for a frame to consider it still valid
	 * @double maxDataAge - seconds a frame should be considered valid after it's been read
	 */
	public synchronized void setMaxDataAge(double maxDataAge) {
		this.m_maxDataAge = maxDataAge;
	}
	
	/**
	 * @return
	 */
	public synchronized int getMaxObjects() {
		return m_max_objects;
	}

	/**
	 * @param m_max_objects
	 */
	public synchronized void setMaxObjects(int m_max_objects) {
		this.m_max_objects = m_max_objects;
	}
	
	/**************************************************************
     * PID SOURCE INTERFACE
     **************************************************************/
    
    public PIDSourceType getPIDSourceType() {
    	return m_pid_source_type;
    }
    
    public void setPIDSourceType(PIDSourceType type) {
    	m_pid_source_type = type;
    }

    public synchronized double getTargetAngle()
    {
    	double tmp = 0;
    	if(m_min_objects <= 0)
		{
			tmp = getTotalDegrees();
			if(!Double.isNaN(tmp))
			{
				m_degrees_from_target = tmp;
			} else {
			}
		} else {
			tmp = getTotalDegrees(m_min_objects);
			if(!Double.isNaN(tmp))
			{
				m_degrees_from_target = tmp;
			} else {
			}
		}
	
		return m_degrees_from_target;
    }
    
	/**
	 * @return Sorted FrameOrder
	 */
	public FrameOrder getSortBy() {
		return m_sort_by;
	}

	/**
	 * @return sorted frame order is ascending (smallest to largest)
	 */
	public boolean getSortAscending() {
		return m_sort_ascending;
	}

	/**
	 * Set the sorted frame order, true for ascending, false for descending
	 * @param order
	 */
	public void setSortAscending(boolean m_sort_ascending) {
		this.m_sort_ascending = m_sort_ascending;
	}

	/**
	 * Sets the property to sort returned frames by
	 * @param m_sort_by FrameOrder
	 */
	public void setSortBy(FrameOrder m_sort_by) {
		this.m_sort_by = m_sort_by;
	}
	
	/**
	 * @return the m_min_objects
	 */
	public synchronized int getMinObjects() {
		return m_min_objects;
	}

	/**
	 * @param minimum number of objects to base calculations on
	 */
	public synchronized void setMinObjects(int m_min_objects) {
		this.m_min_objects = m_min_objects;
	}

	/* (non-Javadoc)
	 * @see edu.wpi.first.wpilibj.PIDSource#pidGet()
	 * Implement the pidGet method
	 */
	@Override
	public synchronized double pidGet() {
		double tmp = 0;
		
		switch(getPIDType())
		{
		case ANGLE:
			if(m_min_objects <= 0)
			{
				tmp = getTotalDegrees();
				if(!Double.isNaN(tmp))
				{
					m_degrees_from_target = tmp;
				} else {
				}
			} else {
				tmp = getTotalDegrees(m_min_objects);
				if(!Double.isNaN(tmp))
				{
					m_degrees_from_target = tmp;
				} else {
				}
			}
			
			return m_degrees_from_target;
		
		case OFFSET:
			return getTotalOffset(2);
			
		default:
			return 0;
		}
			
	}

	@Override
	public void setPIDType(PIDType pidSource) {
		m_pid_type = pidSource;
	}

	@Override
	public PIDType getPIDType() {
		return m_pid_type;
	}   
}