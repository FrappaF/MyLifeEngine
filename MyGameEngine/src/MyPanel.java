import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

import entities.Cell;
import entities.Dna;
import utils.Coordinate;
import utils.Utils;
import world.World;


//Main panel to draw the world
public class MyPanel extends JPanel implements ActionListener {

	private  int winWidth = Utils.getScreenWidth();
	private  int winHeight = Utils.getScreenHeight();
	private static final long serialVersionUID = 1L;
	
	private World world;
	
	private boolean pause;
	private Timer timer;
	
	//Input text to set new cells aspect and DNA
	JTextField damageArm;
	JTextField eatArm;
	JTextField reproduceArm;
	JTextField food;
	JTextField awarness;
	JTextField reproduce;
	JTextField speed;
	
	//Hint text
	JTextArea damageArmText;
	JTextArea eatArmText;
	JTextArea reproduceArmText;
	JTextArea foodText;
	JTextArea awarnessText;
	JTextArea reproduceText;
	JTextArea speedText;
	
	public MyPanel() {
		this.setBackground(new Color(173, 112, 112)); 
		this.setPreferredSize(new Dimension(winWidth, winHeight));
		setSize(winWidth, winHeight);
		
		//Initialize all the JText
		food = new JTextField(3);
		food.setFont(food.getFont().deriveFont(10f));
		
		reproduce = new JTextField(3);
		reproduce.setFont(reproduce.getFont().deriveFont(10f));
		
		awarness = new JTextField(3);
		awarness.setFont(awarness.getFont().deriveFont(10f));
		
		speed = new JTextField(2);
		speed.setFont(speed.getFont().deriveFont(10f));
		
		eatArm = new JTextField(2);
		eatArm.setFont(eatArm.getFont().deriveFont(10f));
		
		damageArm = new JTextField(2);
		damageArm.setFont(damageArm.getFont().deriveFont(10f));
		
		reproduceArm = new JTextField(2);
		reproduceArm.setFont(reproduceArm.getFont().deriveFont(10f));
		
		damageArmText = new JTextArea("Damage Arm:");
		damageArmText.setFont(damageArmText.getFont().deriveFont(10f));
		
		eatArmText = new JTextArea("Eat Arm:");
		eatArmText.setFont(eatArmText.getFont().deriveFont(10f));
		
		reproduceArmText = new JTextArea("Reproduce Arm:");
		reproduceArmText.setFont(reproduceArmText.getFont().deriveFont(10f));
		
		foodText = new JTextArea("Food range:");
		foodText.setFont(foodText.getFont().deriveFont(10f));
		
		awarnessText = new JTextArea("Awarness range:");
		awarnessText.setFont(awarnessText.getFont().deriveFont(10f));
		
		reproduceText = new JTextArea("Reproduce range:");
		reproduceText.setFont(reproduceText.getFont().deriveFont(10f));
		
		speedText = new JTextArea("Speed:");
		speedText.setFont(speedText.getFont().deriveFont(10f));
		
		
		//Adding to panel in order to get visible
		add(foodText); add(food); 
		add(reproduceText); add(reproduce); 
		add(awarnessText); add(awarness); 
		add(speedText); add(speed);  
		add(damageArmText); add(damageArm); 
		add(eatArmText); add(eatArm); 
		add(reproduceArmText); add(reproduceArm); 
		
		//Initialize all values
		world = new World();
		pause = false;
		timer = new Timer(40, this);
		timer.start();
			
	}
	
	//Call the world.Paint function
	public void paint(Graphics g)
	{
		super.paint(g);
		getWorld().paint(g);
	}		
	
	//Update the world status every frame
	@Override
	public void actionPerformed(ActionEvent e) {
		//System.out.println("*******************************************");
		if(!pause) {
			getWorld().update();
			repaint();
		}
		
	}
	
	//Create and return a cell with the values in the JTextField
	public Cell createCell(Coordinate position) {
		
		try {
			
			int damageArmInt = Integer.parseInt(damageArm.getText());
			int reproduceArmInt = Integer.parseInt(reproduceArm.getText());
			int eatArmInt =  Integer.parseInt(eatArm.getText());
			int foodInt = Integer.parseInt(food.getText());
			int reproduceInt = Integer.parseInt(reproduce.getText());
			int awarnessInt = Integer.parseInt(awarness.getText());
			int speedInt = Integer.parseInt(speed.getText());
			
			Dna dna = new Dna( damageArmInt, reproduceArmInt, eatArmInt, foodInt, reproduceInt, awarnessInt, speedInt );
			Cell newCell = new Cell(world, position, dna);
			return newCell;
			
		}catch(NumberFormatException e) {
			//If something went wrong return a random cell
			return new Cell(world, position);
		}
	}


	public void switchPause() { pause = !pause; }
	
	public World getWorld() { return world; }

}
