package gadget.development;

import java.awt.event.MouseAdapter;

import gadget.CommentAddableCalculator;

@SuppressWarnings("serial")
public class SmoothCurveScaffolding extends CommentAddableCalculator {

	{
		
		addMouseListener( new ScaffoldPainter () );
		
	}
	
	private static class ScaffoldPainter extends MouseAdapter {
		
		
		
	}
	
	public static void main(String[] args) {}

}
