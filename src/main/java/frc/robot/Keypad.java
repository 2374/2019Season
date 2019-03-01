package frc.robot;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JTextField;

public class Keypad {
	
	JFrame frame = new JFrame();
	JTextField text = new JTextField();
	
	Container contentPane = frame.getContentPane();
	
	public Keypad() {
		  
		  text.addKeyListener(key);
		  contentPane.add(text, BorderLayout.NORTH);
		  frame.pack();
		  frame.setVisible(true);
	}
	
	KeyListener key = new KeyListener() {
		
		@Override
		 
		public void keyPressed(KeyEvent event) {
		 
			char key = event.getKeyChar();
			
			if (key == '1') {

				System.out.println("1: Rocket Level 1 Hatch");
				
			} else if (key == '2') {
				
				System.out.println("2: Rocket Level 2 Hatch");
				
			} else if (key == '3') {
				
				System.out.println("3: Rocket Level 3 Hatch");
				
			} else if (key == '4') {
				
				System.out.println("4: Rocket Level 1 Ball");
				
			} else if (key == '5') {
				
				System.out.println("5: Rocket Level 2 Ball");
				
			} else if (key == '6') {
				
				System.out.println("6: Rocket Level 3 Ball");
				
			} else if (key == '7') {
				
				System.out.println("7: Cargo Hatch");
				
			} else if (key == '8') {
				
				System.out.println("8: Cargo Ball");
				
			} else if (key == '9') {
				
				System.out.println("9: Nothing yet");
				
			} else if (key == '0') {
				
				System.out.println("0: Player Station Hatch");
				
			} else if (key == '.') {
				
				System.out.println("Del: Player Station Ball");
				
			}
			
		    System.out.println("# " + event.getKeyChar());
		 
		}
		 
		@Override
		public void keyReleased(KeyEvent event) {
		 
		}
		 
		@Override
		public void keyTyped(KeyEvent event) {
		 
		}
		
	};
	
}
