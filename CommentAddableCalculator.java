package gadget;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class CommentAddableCalculator extends JFrame {
	private static final long serialVersionUID = 1L;
	
	{
		
		setTitle( "calculator" );
		setDefaultCloseOperation( EXIT_ON_CLOSE );
		setLayout( null );
		setSize( 276 , 408 );
		setVisible( true );
		getContentPane().setBackground( Color.YELLOW );
		add( new Blocks() );
		
	}
	
	public static class Blocks extends JComponent {
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
		
		@RGB( HEXcode = 0x000000 )
		ONE,
		
		@RGB( HEXcode = 0x000000 )
		TWO
		
	}
	
	private static class BezierCurveImage extends BufferedImage {
		
		private BezierCurveImage( Point start , Point mid , Point finish ) {
			
			super( new BezierCurveSize( start , mid , finish , BezierCurve.radius ).width , new BezierCurveSize( start , mid , finish , BezierCurve.radius ).height , BufferedImage.TYPE_INT_ARGB );
			BezierCurve curve = new BezierCurve( start , mid , finish );
			for ( int x = 0 ; x < getWidth() ; x++ )
				for ( int y = 0 ; y < getHeight() ; y++ )
					if ( curve.includes( x , y ) )
						setRGB( x , y , Color.ORANGE.getRGB() );
			
		}
		
	}
	
	private static class BezierCurve {
		
		private final Point start;
		private final Point mid;
		private final Point finish;
		private final int width2X;
		private final int height2X;
		private static final double radius = 5;
		
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
				
				if ( Point.distance( (double)x , (double)y , curvePoint.x , curvePoint.y ) < radius )
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
		
		private BezierCurveSize( Point start , Point mid , Point finish , double radius ) {
			
			int maxX = Math.max( Math.max( start.x , mid.x ) , finish.x );
			width = (int)( maxX + radius );
			int maxY = Math.max( Math.max( start.y , mid.y ) , finish.y );
			height = (int)( maxY + radius );
			
		}
		
	}
	
	public static class Hints extends JComponent {
		private static final long serialVersionUID = 1L;
		
		{
			
			setSize( 260 , 369 );
			
		}
		
		@Override
		public void paint( Graphics g ) {
			super.paint( g );
			Graphics2D g2d = (Graphics2D)g;
			
			drawCurves( g2d , Strokes.ONE );
			
		}
		
		private void drawCurves( Graphics2D g2d , Strokes s ) {
			
			try {
				
				g2d.setColor( new Color( s.getClass().getField( s.name() ).getAnnotation( RGB.class ).HEXcode() ) );
				g2d.draw( getBounds() );
				
			} catch (NoSuchFieldException | SecurityException e) { e.printStackTrace(); }
			
		}
		
		public static class BezierCurve implements Shape {

			private final Point start;
			private final Point mid;
			private final Point end;
			private final int breadth;
			
			public BezierCurve( Point start , Point mid , Point end , int breadth ) {
				
				this.start = start;
				this.mid = mid;
				this.end = end;
				this.breadth = breadth;
				
			}
			
			@Override
			public Rectangle getBounds() {
				
				int x = Math.min( Math.min( start.x , mid.x ) , end.x );
				int y = Math.min( Math.min( start.y , mid.y ) , end.y );
				int maxX = Math.max( Math.max( start.x , mid.x ) , end.x );
				int maxY = Math.max( Math.max( start.y , mid.y ) , end.y );
				int width = maxX - x;
				int height = maxY - y;				
				return new Rectangle( x , y , width, height );

			}

			@Override
			public Rectangle2D getBounds2D() {
				
				Rectangle bounds = getBounds();
				return new Rectangle2D() {
					
					@Override
					public boolean isEmpty() {
						
						if ( bounds.x == 0 && bounds.y == 0 && bounds.width == 0 && bounds.height == 0 )
							return true;
						return false;
						
					}
					
					@Override
					public double getY() {

						return bounds.y;
						
					}
					
					@Override
					public double getX() {

						return bounds.x;
						
					}
					
					@Override
					public double getWidth() {

						return bounds.width;
						
					}
					
					@Override
					public double getHeight() {

						return bounds.height;
						
					}
					
					@Override
					public void setRect( double x , double y , double w , double h ) {

						bounds.x += x;
						bounds.y += y;
						bounds.width += w;
						bounds.height += h;
						
					}
					
					@Override
					public int outcode( double x , double y ) {

						return 0;
						
					}
					
					@Override
					public Rectangle2D createUnion( Rectangle2D r ) {

						
						return null;
						
					}
					
					@Override
					public Rectangle2D createIntersection(Rectangle2D r) {
						// TODO Auto-generated method stub
						return null;
					}
				};
				
			}

			@Override
			public boolean contains(double x, double y) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean contains(Point2D p) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean intersects(double x, double y, double w, double h) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean intersects(Rectangle2D r) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean contains(double x, double y, double w, double h) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean contains(Rectangle2D r) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public PathIterator getPathIterator(AffineTransform at) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public PathIterator getPathIterator(AffineTransform at, double flatness) {
				// TODO Auto-generated method stub
				return null;
			}
			
		}
		
	}

	public static void main(String[] args) {

		new CommentAddableCalculator();
		
	}

}
