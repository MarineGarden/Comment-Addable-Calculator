package gadget;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class CommentAddableCalculator extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private static final CommentAddableCalculator self = new CommentAddableCalculator();
	
	{
		
		setTitle( "calculator" );
		setDefaultCloseOperation( EXIT_ON_CLOSE );
		setLayout( null );
		setSize( 276 , 408 );
		setVisible( true );
		getContentPane().setBackground( Color.YELLOW );
		add( new Hints() );
		add( new Blocks() );
		addMouseListener( new Touchable() );
		
	}
	
	private static class Blocks extends JComponent {
		private static final long serialVersionUID = 1L;

		private static final int CORNER_SIZE = 10;
		
		{
			
			setSize( 260 , 369 );
			
		}
		
		@Override
		public void paint( Graphics g ) {
			super.paint( g );
			
			drawMcolumnsNrows( g , ColorBlocks.BACKGROUND );
			drawMcolumnsNrows( g , ColorBlocks.SCREEN );
			drawMcolumnsNrows( g , ColorBlocks.BUTTON );
			
		}
		
		private void drawMcolumnsNrows( Graphics g , ColorBlocks c ) {
			
			try {
				
				g.setColor( new Color( c.getClass().getField( c.name() ).getAnnotation( RGB.class ).HEXcode() ) );
				Bounds bs = c.getClass().getField( c.name() ).getAnnotation( Bounds.class );
				Counts cs = c.getClass().getField( c.name() ).getAnnotation( Counts.class );
				for ( int x = 0 ; x < cs.columns() ; x++ )
					for ( int y = 0 ; y < cs.rows() ; y++ )
						g.fillRoundRect( bs.x() + 64*x , bs.y() + 65*y , bs.width() , bs.height() , CORNER_SIZE , CORNER_SIZE );
				
			} catch (NoSuchFieldException | SecurityException e) { e.printStackTrace(); }
			
		}
		
	}
	
	private static class Hints extends JComponent {
		private static final long serialVersionUID = 1L;
		
		{
			
			setSize( 260 , 369 );
			
		}
		
		@Override
		public void paint( Graphics g ) {
			super.paint( g );
			
			drawBezierCurve( g , Strokes.EXIT );
			
		}
		
		private void drawBezierCurve( Graphics g , Strokes s ) {
			
			try {
				
				Color color = new Color( s.getClass().getField( s.name() ).getAnnotation( RGB.class ).HEXcode() );
				Curves c = s.getClass().getField( s.name() ).getAnnotation( Curves.class );
				Point[] starts = new Point[ c.startsX().length ];
				for ( int i = 0 ; i < starts.length ; i++ )
					starts[ i ] = new Point( c.startsX()[ i ] , c.startsY()[ i ] );
				Point[] mids = new Point[ c.midsX().length ];
				for ( int i = 0 ; i < mids.length ; i++ )
					mids[ i ] = new Point( c.midsX()[ i ] , c.midsY()[ i ] );
				Point[] ends = new Point[ c.endsX().length ];
				for ( int i = 0 ; i < ends.length; i++ )
					ends[ i ] = new Point( c.endsX()[ i ] , c.endsY()[ i ] );
				for ( int i = 0 ; i < starts.length ; i++ )
					g.drawImage( new BezierCurveImage( starts[ i ] , mids[ i ] , ends[ i ] , color ) , 0 , 0 , null );
				
			} catch ( NoSuchFieldException | SecurityException e ) { e.printStackTrace(); }
			
		}
		
	}
	
	private static class Touchable extends MouseAdapter {

		private static final ExitButtonArea exit = new ExitButtonArea();
		
		@Override
		public void mousePressed( MouseEvent event ) {
			super.mousePressed( event );
			
			if ( exit.includes( event ) )
				if ( event.getClickCount() > 1 )
					System.exit( 0 );
			
		}
		
	}
	private static class ExitButtonArea extends RectangleArea {
		
		private ExitButtonArea() {
			
			super( 50 , 50 , 100 , 100 );
			
		}
		
	}
	private static class RectangleArea extends EventArea {
		private static final long serialVersionUID = 1L;

		private RectangleArea( int x , int y , int width , int height ) {
			
			super( x , y , width , height );
			
		}
		
		@Override
		public boolean includes( MouseEvent event ) {
			
			Point p = event.getPoint();
			return p.x >= x && p.x <= width + x && p.y >= y && p.y <= height + y;
			
		}
		
	}
	private static class EventArea extends Rectangle {
		private static final long serialVersionUID = 1L;

		private EventArea( int x , int y , int width , int height ) {
			
			super( x , y , width , height );
			
		}
		
		public boolean includes( MouseEvent event ) {
			
			return false;
			
		}
		
	}
	
	@Retention(RUNTIME)
	@Target(ElementType.FIELD)
	private @interface RGB {
		
		int HEXcode();

	}
	
	@Retention(RUNTIME)
	@Target(ElementType.FIELD)
	private @interface Bounds {
		
		int x();
		int y();
		int width();
		int height();
		
	}
	
	@Retention(RUNTIME)
	@Target(ElementType.FIELD)
	private @interface Counts {
		
		int columns();
		int rows();
		
	}
	
	@Retention(RUNTIME)
	@Target(ElementType.FIELD)
	private @interface Curves {
		
		int[] startsX();
		int[] startsY();
		int[] midsX();
		int[] midsY();
		int[] endsX();
		int[] endsY();
		
	}
	
	private enum ColorBlocks {
		
		@RGB( HEXcode = 0xFFC0CB )
		@Bounds( x = 0 , y = 0 , width = 260 , height = 369 )
		@Counts( columns = 1 , rows = 1 )
		BACKGROUND,
		
		@RGB( HEXcode = 0x00FF00 )
		@Bounds( x = 4 , y = 5 , width = 252 , height = 100 )
		@Counts( columns = 1 , rows = 1 )
		SCREEN,
		
		@RGB( HEXcode = 0xFFA500 )
		@Bounds( x = 4 , y = 109 , width = 60 , height = 60 )
		@Counts( columns = 4 , rows = 4 )
		BUTTON;
		
	}
	
	private enum Strokes {
		
		@RGB( HEXcode = 0xFF0000 )
		@Curves( startsX = { 50 , 150 } , startsY = { 50 , 50 } , midsX = { 100 , 100 } , midsY = { 100 , 100 } , endsX = { 150 , 50 } , endsY = { 150 , 150 } )
		EXIT,
		
		@RGB( HEXcode = 0x000000 )
		ONE,
		
		@RGB( HEXcode = 0x000000 )
		TWO
		
	}
	
	private static class BezierCurveImage extends BufferedImage {
		
		private BezierCurveImage( Point start , Point mid , Point finish , Color color ) {
			
			super( new BezierCurveSize( start , mid , finish , BezierCurve.RADIUS ).width , new BezierCurveSize( start , mid , finish , BezierCurve.RADIUS ).height , BufferedImage.TYPE_INT_ARGB );
			BezierCurve curve = new BezierCurve( start , mid , finish );
			for ( int x = 0 ; x < getWidth() ; x++ )
				for ( int y = 0 ; y < getHeight() ; y++ )
					if ( curve.includes( x , y ) )
						setRGB( x , y , color.getRGB() );
			
		}
		
	}
	private static class BezierCurve {
		
		private final Point start;
		private final Point mid;
		private final Point finish;
		private final int width2X;
		private final int height2X;
		private static final double RADIUS = 5;
		
		private BezierCurve( Point start , Point mid , Point finish ) {
			
			this.start = start;
			this.mid = mid;
			this.finish = finish;
			int maxX = Math.max( Math.max( start.x , mid.x ) , finish.x );
			int minX = Math.min( Math.min( start.x , mid.x ) , finish.x );
			int width = maxX - minX;
			width2X = width*2;
			int maxY = Math.max( Math.max( start.y , mid.y ) , finish.y );
			int minY = Math.min( Math.min( start.y , mid.y ), finish.y );
			int height = maxY - minY;
			height2X = height*2;
			
		}
		
		private boolean includes( int x , int y ) {
			
			boolean isWider = width2X >= height2X;
			for ( int curvePointToken = 0 ; curvePointToken < ( isWider ? width2X : height2X ) ; curvePointToken += 1 ) {
				
				Point2D.Double curvePoint = getLocation( curvePointToken/( (double)( isWider ? width2X : height2X ) ) );
				
				if ( Point.distance( (double)x , (double)y , curvePoint.x , curvePoint.y ) < RADIUS )
					return true;
				
			}
			return false;
			
		}
		
		private Point2D.Double getLocation( double ratio ) {
			
			double x = calculateBezier( ratio , start.x , mid.x , finish.x );
			double y = calculateBezier( ratio , start.y , mid.y , finish.y );
			return new Point2D.Double( x , y );
			
		}
		
		private double calculateBezier( double ratio , int p1 , int p2 , int p3 ) {
			
			return ( 1 - ratio )*( 1 - ratio )*p1 + 2*( 1 - ratio )*ratio*p2 + ratio*ratio*p3;
			
		}
		
	}
	private static class BezierCurveSize extends Dimension {
		private static final long serialVersionUID = 1L;

		private BezierCurveSize( Point start , Point mid , Point finish , double radius ) {
			
			int maxX = Math.max( Math.max( start.x , mid.x ) , finish.x );
			width = (int)( maxX + radius );
			int maxY = Math.max( Math.max( start.y , mid.y ) , finish.y );
			height = (int)( maxY + radius );
			
		}
		
	}

	public static void main(String[] args) {}

}
