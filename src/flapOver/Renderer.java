package flapOver;

import java.awt.Graphics;
import javax.swing.JPanel;

public class Renderer extends JPanel {

	/**
	 * Add Default Serial Version, surpass the warning
	 */
	private static final long serialVersionUID = 1L;

    @Override
    protected void paintComponent(Graphics g) {
    	// TODO Auto-generated method stub
    	//Call Paint Component First
    	super.paintComponent(g);
    	
    	//Then Call our Code, so we can pass the graphics for our Game.
    	FlapOver.flapOver.repaint(g);
    }
    
}
