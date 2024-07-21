package gadget;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class CommentAddableCalculator extends JFrame {
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unused")
	private static final CommentAddableCalculator self = new CommentAddableCalculator();
	
	{
		
		add( new Hints() );
		add( new Blocks() );
		addMouseListener( new Touchable() );
		Draggable.on( this );
		
		setDefaultCloseOperation( EXIT_ON_CLOSE );
		setLayout( null );
		setSize( 260 , 399 );
		setUndecorated( true );
		setBackground( new Color( 0 , 0 , 0 , 0 ) );
		setVisible( true );
		
	}
	
	private static class Blocks extends JComponent {
		private static final long serialVersionUID = 1L;

		private static final int CORNER_SIZE = 10;
		
		{
			
			setSize( 260 , 399 );
			
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
			
			setSize( 260 , 399 );
			
		}
		
		@Override
		public void paint( Graphics g ) {
			super.paint( g );
			
			drawBezierCurve( g , Strokes.EXIT );
			drawSmoothBezierCurve( g , SmoothStrokes.ZERO );
			
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
					g.drawImage( new BezierCurveImage( starts[ i ] , mids[ i ] , ends[ i ] , color , 5 ) , 0 , 0 , null );
				
			} catch ( NoSuchFieldException | SecurityException e ) { e.printStackTrace(); }
			
		}
		
		private void drawSmoothBezierCurve( Graphics g , SmoothStrokes ss ) {
			
			try {
				
				Color color = new Color( ss.getClass().getField( ss.name() ).getAnnotation( RGB.class ).HEXcode() );
				Curves c = ss.getClass().getField( ss.name() ).getAnnotation( Curves.class );
				Point[] starts = new Point[ c.startsX().length ];
				for ( int i = 0 ; i < starts.length ; i++ )
					starts[ i ] = new Point( c.startsX()[ i ] , c.startsY()[ i ] );
				Point[] mids = new Point[ c.midsX().length ];
				for ( int i = 0 ; i < mids.length ; i++ )
					mids[ i ] = new Point( c.midsX()[ i ] , c.midsY()[ i ] );
				Point[] ends = new Point[ c.endsX().length ];
				for ( int i = 0 ; i < ends.length; i++ )
					ends[ i ] = new Point( c.endsX()[ i ] , c.endsY()[ i ] );
				RatioLine[] lines = new RatioLine[ starts.length ];
				for ( int i = 0 ; i < lines.length ; i++ )
					lines[ i ] = new RatioLine( starts[ i ] , new Distance( starts[ i ] , ends[ i ] ) , new Distance( mids[ i ] , ends[ i ] ).length()/new Distance( starts[ i ] , mids[ i ] ).length() );
				g.drawImage( new SmoothBezierCurveImage( color , 10 , lines ) , 0 , 0 , null );
				
			} catch ( NoSuchFieldException | SecurityException e ) { e.printStackTrace(); }
			
		}
		
		private static class Distance extends Dimension {
			private static final long serialVersionUID = 1L;

			private Distance( Point p1 , Point p2 ) {
				
				super( p2.x - p1.x , p2.y - p1.y );
				
			}
			
			private double length() {
				
				return Math.sqrt( (double)( width*width + height*height ) );
				
			}
			
		}
		
	}
	
	private static class Touchable extends MouseAdapter {

		private static final ExitButtonArea exit = new ExitButtonArea();
		
		@Override
		public void mousePressed( MouseEvent event ) {
			super.mousePressed( event );
			
			if ( exit.includes( event ) )
				if ( event.getClickCount() > 0 )
					System.exit( 0 );
			
		}
		
	}
	private static class ExitButtonArea extends RectangleArea {
		private static final long serialVersionUID = 1L;

		private ExitButtonArea() {
			
			super( 215 , 15 , 30 , 30 );
			
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
		
		@SuppressWarnings("unused")
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
		@Bounds( x = 0 , y = 0 , width = 260 , height = 399 )
		@Counts( columns = 1 , rows = 1 )
		BACKGROUND,
		
		@RGB( HEXcode = 0x00FF00 )
		@Bounds( x = 4 , y = 5 , width = 252 , height = 130 )
		@Counts( columns = 1 , rows = 1 )
		SCREEN,
		
		@RGB( HEXcode = 0xFFA500 )
		@Bounds( x = 4 , y = 139 , width = 60 , height = 60 )
		@Counts( columns = 4 , rows = 4 )
		BUTTON;
		
	}
	
	private enum Strokes {
		
		@RGB( HEXcode = 0xFF0000 )
		@Curves( startsX = { 215 , 245 } , startsY = { 15 , 15 } , midsX = { 230 , 230 } , midsY = { 30 , 30 } , endsX = { 245 , 215 } , endsY = { 45 , 45 } )
		EXIT,
		
		@RGB( HEXcode = 0x000000 )
		ONE,
		
		@RGB( HEXcode = 0x000000 )
		TWO
		
	}
	
	private enum SmoothStrokes {
		
		@RGB( HEXcode = 0x000000 )
		@Curves( startsX = { 100 , 80 , 80 , 100 , 120 , 120 } , startsY = { 70 , 80 , 120 , 130 , 120 , 80 } , midsX = { 90 , 80 , 90 , 110 , 120 , 110 } , midsY = { 65 , 100 , 135 , 135 , 100 , 65 } , endsX = { 80 , 80 , 100 , 120 , 120 , 100 } , endsY = { 80 , 120 , 130 , 120 , 80 , 70 } )
		ZERO
		
	}
	
	private static class Draggable extends Point implements MouseMotionListener,Cloneable {
		private static final long serialVersionUID = 1L;
		
		private static final Draggable self = new Draggable();
		
		public static void on( Component target ) {
			
			target.addMouseMotionListener( self );
			
		}
		
		public static class ComponentWrapper {
			
			private final Component c;
			
			public ComponentWrapper( Component c ) {
				
				this.c = c;
				
			}
			
			public Point shift( int x , int y ) {
				
				Point before = c.getLocation();
				Point after = new Point( before.x + x , before.y + y );
				c.setLocation( after );
				return c.getLocationOnScreen();
				
			}
			
		}
		
		@Override
		public void mouseMoved( MouseEvent event ) {
			
			setLocation( event.getLocationOnScreen() );
			
		}
		
		@Override
		public void mouseDragged( MouseEvent event ) {
			
			Component target = (Component)event.getSource();
			ComponentWrapper helper = new ComponentWrapper( target );
			Point mouse = event.getLocationOnScreen();
			helper.shift( x == 0 ? 0 : mouse.x - x , y == 0 ? 0 : mouse.y - y );
			setLocation( event.getLocationOnScreen() );
			
		}
		
	}
	
	private static class Comparings {
		
		private static int max( Integer... values ) {
			
			Integer result = Integer.MIN_VALUE;
			for ( Integer value : values )
				if ( value > result )
					result = value;
			return result;
			
		}
		
	}
	
	private static class SmoothBezierCurveImage extends BufferedImage {
		
		private SmoothBezierCurveImage( Color color , int breadth , RatioLine... lines ) {
			super( new SmoothBezierCurveSize( breadth/2 , lines ).width , new SmoothBezierCurveSize( breadth/2 , lines ).height , BufferedImage.TYPE_INT_ARGB );
			
			SmoothBezierCurve curve = new SmoothBezierCurve( breadth , lines );
			for ( int x = 0 ; x < getWidth() ; x++ )
				for ( int y = 0 ; y < getHeight() ; y++ )
					if ( curve.includes( x , y ) )
						setRGB( x , y , color.getRGB() );
			
		}
		
	}
	private static class SmoothBezierCurve {
		
		private final BezierCurve[] curves;
		
		private SmoothBezierCurve( int breadth , RatioLine... lines ) {
			
			if ( lines.length > 1 ) {
				
				RatioLine first = lines[ 0 ];
				RatioLine last = lines[ lines.length - 1 ];
				if ( Points.equals( first.getLocation() , last.getEnd() ) )
					curves = new BezierCurve[ lines.length ];
				else
					curves = new BezierCurve[ lines.length - 1 ];
				
				for ( int i = 0 ; i < curves.length - 1 ; i++ ) {
					
					Point start = lines[ i ].getMid();
					Point mid = lines[ i ].getEnd();
					Point end = lines[ i + 1 ].getMid();
					curves[ i ] = new BezierCurve( start , mid , end , breadth );
					
				}
				if ( curves.length == lines.length ) {
					
					Point start = lines[ lines.length - 1 ].getMid();
					Point mid = lines[ lines.length - 1 ].getEnd();
					Point end = lines[ 0 ].getMid();
					curves[ curves.length - 1 ] = new BezierCurve( start , mid , end , breadth );
					
				}
				
			} else
				curves = new BezierCurve[ 0 ];
			
		}
		
		private static class Points {
			
			private static boolean equals( Point p1 , Point p2 ) {
				
				return p1.toString().equals( p2.toString() );
				
			}
			
		}
		
		private boolean includes( int x , int y ) {
			
			for ( BezierCurve curve : curves )
				if ( curve.includes( x , y ) )
					return true;
			return false;
			
		}
		
	}
	private static class SmoothBezierCurveSize extends Dimension {
		private static final long serialVersionUID = 1L;

		private SmoothBezierCurveSize( int radius , RatioLine... lines ) {
			
			Integer[] startsX = new Integer[ lines.length ];
			for ( int i = 0 ; i < startsX.length ; i++ )
				startsX[ i ] = lines[ i ].getLocation().x;
			
			Integer[] midsX = new Integer[ lines.length ];
			for ( int i = 0 ; i < midsX.length ; i++ )
				midsX[ i ] = lines[ i ].getMid().x;
			
			Integer[] endsX = new Integer[ lines.length ];
			for ( int i = 0 ; i < endsX.length ; i++ )
				endsX[ i ] = lines[ i ].getEnd().x;
			
			Integer[] Xs = new Integer[ lines.length*3 ];
			Xs = Arrays.copyOfRange( startsX , Xs , 0 , lines.length , 0 );
			Xs = Arrays.copyOfRange( midsX , Xs , 0 , lines.length , lines.length );
			Xs = Arrays.copyOfRange( endsX , Xs , 0 , lines.length , lines.length*2 );
			width = Comparings.max( Xs ) + radius;
			
			Integer[] startsY = new Integer[ lines.length ];
			for ( int i = 0 ; i < startsY.length ; i++ )
				startsY[ i ] = lines[ i ].getLocation().y;
			
			Integer[] midsY = new Integer[ lines.length ];
			for ( int i = 0 ; i < midsY.length ; i++ )
				midsY[ i ] = lines[ i ].getMid().y;
			
			Integer[] endsY = new Integer[ lines.length ];
			for ( int i = 0 ; i < endsY.length ; i++ )
				endsY[ i ] = lines[ i ].getEnd().y;
			
			Integer[] Ys = new Integer[ lines.length*3 ];
			Ys = Arrays.copyOfRange( startsY , Ys , 0 , lines.length , 0 );
			Ys = Arrays.copyOfRange( midsY , Ys , 0 , lines.length , lines.length );
			Ys = Arrays.copyOfRange( endsY , Ys , 0 , lines.length , lines.length*2 );
			height = Comparings.max( Ys ) + radius;
			
		}
		
		private static class Arrays {
			
			@SuppressWarnings("unchecked")
			private static <T> T[] copyOfRange( T[] filling , T[] original , int fillingFrom , int fillingTo , int originalFrom ) {
				
				if ( originalFrom < original.length ) {
					
					ArrayGadget<T> ag = new ArrayGadget<T>( original );
					ag.split( originalFrom );
					T[] fillingInNeed = extractFilling( filling , fillingFrom , fillingTo );
					ag.setTarget( ag.getPartViaArray( ag.join( ag.getFirst() , fillingInNeed , ag.getLast() ) , 0 , ag.getTotalLength() + fillingInNeed.length ) );
					ag.removeTargetElements( originalFrom + fillingInNeed.length , originalFrom + fillingInNeed.length > original.length ? original.length + fillingInNeed.length : originalFrom + fillingInNeed.length*2 );
					return ag.getTarget();
					
				} else {
					
					T[] fillingInNeed = extractFilling( filling , fillingFrom , fillingTo );
					T[] extendedFilling = append( fillingInNeed , originalFrom/fillingInNeed.length );
					ArrayGadget<T> ag = new ArrayGadget<T>( extendedFilling );
					ag.split( extendedFilling.length - fillingInNeed.length );
					T[] mid = ag.getPartViaArray( ag.getFirst() , 0 , originalFrom - original.length );
					for ( int i = 0 ; i < mid.length ; i++ )
						mid[ i ] = null;
					return ag.join( original , mid , ag.getLast() );
					
				}
				
			}
			private static <T> T[] extractFilling( T[] filling , int fillingFrom , int fillingTo ) {
				
				ArrayGadget<T> ag = new ArrayGadget<T>( filling );
				ag.split( fillingFrom );
				ag.setTarget( ag.getLast() );
				ag.split( fillingTo );
				return ag.getFirst();
				
			}
			private static <T> T[] append( T[] target , int repeats ) {
				
				List<T> l = java.util.Arrays.asList( target );
				ArrayList<T> al = new ArrayList<T> ( l );
				for ( int i = 1 ; i < repeats ; i++ )
					al.addAll( l );
				return al.toArray( target );
				
			}
			
		}
		
		private static class ArrayGadget<T> {
			
			private T[] target;
			private T[] first;
			private T[] last;
			
			private ArrayGadget( T[] target ) {
				
				this.target = target;
				
			}
			
			private void split( int index ) {
				
				first = getTargetList().subList( 0 , index ).toArray( java.util.Arrays.copyOfRange( target , 0 , 0 ) );
				last = getTargetList().subList( index , target.length ).toArray( java.util.Arrays.copyOfRange( target , 0 , 0 ) );
				
			}
			@SuppressWarnings("unchecked")
			private T[] join( T[]... arrays ) {
				
				if ( arrays.length > 0 ) {
					
					ArrayList<T> l = new ArrayList<T> ( getEmptyList() );
					for ( T[] array : arrays )
						l.addAll( getList( array ) );
					return l.toArray( java.util.Arrays.copyOfRange( target , 0 , 0 ) );
					
				} else
					return getEmptyArray();
				
			}
			private T[] getEmptyArray() {
				
				return getEmptyList().toArray( target );
				
			}
			private List<T> getEmptyList() {
				
				return getTargetList().subList( 0 , 0 );
				
			}
			private List<T> getTargetList() {
				
				return getList( target );
				
			}
			private T[] getPartViaArray( T[] array , int from , int to ) {
				
				return getPartViaList( array , from , to ).toArray( java.util.Arrays.copyOfRange( array , 0, 0 ) );
				
			}
			private List<T> getPartViaList( T[] array , int from , int to ) {
				
				return getList( array ).subList( from , to );
				
			}
			private List<T> getList( T[] array ) {
				
				return java.util.Arrays.asList( array );
				
			}
			
			private T[] getFirst() {
				
				return first;
				
			}
			private T[] getLast() {
				
				return last;
				
			}
			private T[] getTarget() {
				
				return target;
				
			}
			private void setTarget( T[] target ) {
				
				this.target = target;
				
			}
			
			private int getTotalLength() {
				
				return first.length + last.length;
				
			}
			
			@SuppressWarnings("unchecked")
			private void removeTargetElements( int from , int to ) {
				
				T[] target =  this.target;
				split( from );
				T[] leftNeed = getFirst();
				setTarget( target );
				split( to );
				T[] rightNeed = getLast();
				T[] result = join( leftNeed , rightNeed );
				setTarget( result );
				
			}
			
		}
		
	}
	private static class RatioLine extends Rectangle {
		private static final long serialVersionUID = 1L;
		
		private final double partBRatio;
		
		private RatioLine( Point start , Dimension length , double betweenZeroAndOne ) {
			
			super( start , length );
			partBRatio = betweenZeroAndOne;
			
		}
		
		private Point getMid() {
			
			Dimension partA = calculatePartALength();
			return new Point( x + partA.width , y + partA.height );
			
		}
		private Dimension calculatePartALength() {
			
			double ratio = calculatePartARatio();
			return new Dimension( (int)( width*ratio ) , (int)( height*ratio ) );
			
		}
		private double calculatePartARatio() {
			
			return 1/( 1 + partBRatio );
			
		}
		
		private Point getEnd() {
			
			return new Point( x + width , y + height );
			
		}
		
	}
	private static class BezierCurveImage extends BufferedImage {
		
		private BezierCurveImage( Point start , Point mid , Point finish , Color color , int breadth ) {
			
			super( new BezierCurveSize( start , mid , finish , breadth/2 ).width , new BezierCurveSize( start , mid , finish , breadth/2 ).height , BufferedImage.TYPE_INT_ARGB );
			BezierCurve curve = new BezierCurve( start , mid , finish , breadth/2 );
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
		private final int breadth;
		
		private BezierCurve( Point start , Point mid , Point finish , int breadth ) {
			
			this.breadth = breadth;
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
				
				if ( Point.distance( (double)x , (double)y , curvePoint.x , curvePoint.y ) < breadth/2 )
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

		private BezierCurveSize( Point start , Point mid , Point finish , int radius ) {
			
			int maxX = Math.max( Math.max( start.x , mid.x ) , finish.x );
			width = maxX + radius;
			int maxY = Math.max( Math.max( start.y , mid.y ) , finish.y );
			height = maxY + radius;
			
		}
		
	}

	public static void main(String[] args) {}

}
