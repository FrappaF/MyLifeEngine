
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import entities.Cell;
import utils.Coordinate;



//Main class of the engine. It implements ComponentListener and MouseListener to handle resize events and mouse click events
public class GameEngine extends JFrame implements ComponentListener, MouseListener{
	
	private static final long serialVersionUID = 1L;
	private MyPanel panel;
	
	
	public GameEngine() {
		panel = new MyPanel();
		
		//In order to loose the focuse on the JTextField
		panel.setFocusable(true);
		panel.requestFocusInWindow();
		panel.addComponentListener(this);
		
		//KeyPress listener
		//Pressing G key it is possible to show/hide the GUI
		//The SPACE key to stop/resume the game
		//The R key to spawn random cells and random food in the world
		panel.addKeyListener(new KeyListener() {
		    public void keyPressed(KeyEvent e) { 
		    	switch(e.getKeyCode()) {
		    	case KeyEvent.VK_SPACE:
		    		panel.switchPause();
		    		break;
		    	case KeyEvent.VK_G:
		    		panel.getWorld().paintGui = !panel.getWorld().paintGui;
		    		break;
		    	case KeyEvent.VK_R:
		    		panel.getWorld().randomWorld();
		    		break;
		    	default:
		    		break;
		    	}
		    }

		    public void keyReleased(KeyEvent e) { /* ... */ }

		    public void keyTyped(KeyEvent e) { /* ... */ }
		});
		
		panel.addMouseListener(this);

		this.add(panel);
		this.pack();
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	//Initialize a new GameEngine object and start the simulation
	public static void main(String[] args) {
		new GameEngine();
	}


	@Override
	public void componentResized(ComponentEvent e) {
		//Update the windowDimension in the current world
		panel.getWorld().winHeight = getSize().height;
		panel.getWorld().winWidth = getSize().width;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Object source = e.getSource();
		if(source instanceof MyPanel){
			MyPanel myPanel = (MyPanel) source;
			myPanel.requestFocusInWindow();
			Coordinate mouseCoord = new Coordinate(e.getPoint().x, e.getPoint().y);
			Cell toAdd = myPanel.createCell(mouseCoord);
			myPanel.getWorld().addCell(toAdd);
		}
	}

	@Override
	public void componentMoved(ComponentEvent e) { }

	@Override
	public void componentShown(ComponentEvent e) { }

	@Override
	public void componentHidden(ComponentEvent e) { }

	@Override
	public void mousePressed(MouseEvent e) { }

	@Override
	public void mouseReleased(MouseEvent e) { }

	@Override
	public void mouseEntered(MouseEvent e) { }

	@Override
	public void mouseExited(MouseEvent e) { }
}
