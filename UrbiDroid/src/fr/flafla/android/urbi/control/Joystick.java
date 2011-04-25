package fr.flafla.android.urbi.control;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import fr.flafla.android.urbi.robot.Robot;

/**
 * This class handles user input in the form of one or more virtual joystick(s).
 * 
 * @author merlin, packadal
 * 
 */
public class Joystick extends View implements OnTouchListener {

	enum Orientation {
		VERTICAL, HORIZONTAL
	}

	private static final int spaceBetweenJoysticks = 300;
	private static final int widthJoystick = 300;
	private static final int heightJoystick = 300;

	/**
	 * @author packadal Base class for a joystick that draws itself.
	 * 
	 */
	public static abstract class JoystickBase {
		protected float xPosition;
		protected float yPosition;
		protected float halfWidth;
		protected float halfHeight;

		public boolean isInUse = false;

		/** Reference to axes model */
		private final Axes axes;

		public JoystickBase(Axes axes, float xPosition, float yPosition, float width, float height) {
			this.axes = axes;
			this.xPosition = xPosition;
			this.yPosition = yPosition;
			halfHeight = height / 2;
			halfWidth = width / 2;
		}

		public void setxPosition(float xPosition) {
			this.xPosition = xPosition;
		}

		public void setyPosition(float yPosition) {
			this.yPosition = yPosition;
		}

		public void onDraw(Canvas canvas) {
			RectF oval = new RectF(xPosition - halfWidth, yPosition - halfHeight, xPosition + halfWidth, yPosition + halfHeight);
			Paint paint = new Paint();
			paint.setStrokeWidth(5);
			paint.setStyle(Paint.Style.STROKE);
			paint.setARGB(255, 0, 0, 255);
			canvas.drawOval(oval, paint);
		}

		public boolean isIn(float x, float y) {
			return (x <= xPosition + halfWidth && x >= xPosition - halfWidth) && (y <= yPosition + halfHeight && y >= yPosition - halfHeight);
		}

		public void move(float x, float y) {
			if (axes.x != null) {
				float relX = x - xPosition;
				if (relX > 0)
					axes.x.value = (int) (relX * axes.x.maxValue / halfWidth);
				else if (relX < 0)
					axes.x.value = (int) (relX * axes.x.minValue / -halfWidth);
				else
					axes.x.value = 0;
			}
			if (axes.y != null) {
				float relY = yPosition - y;
				if (relY > 0)
					axes.y.value = (int) (relY * axes.y.maxValue / halfHeight);
				else if (relY < 0)
					axes.y.value = (int) (relY * axes.y.minValue / -halfHeight);
				else
					axes.y.value = 0;
			}
		}

		public void released() {
			if (axes.x != null)
				axes.x.value = 0;
			if (axes.y != null)
				axes.y.value = 0;
		}
	}

	/**
	 * Implementation of JoystickBase for a 1-axis Joystick.
	 */
	public static final class AxisJoystick extends JoystickBase {
		public AxisJoystick(Axes axes, float xPosition, float yPosition, Orientation o) {
			super(axes, xPosition, yPosition, o == Orientation.HORIZONTAL ? heightJoystick / 2 : widthJoystick, o == Orientation.VERTICAL ? widthJoystick / 2 : heightJoystick);
		}
	}

	/**
	 * Implementation of JoystickBase for a 1-axis Joystick.
	 */
	public static final class Axis2Joystick extends JoystickBase {
		public Axis2Joystick(Axes axes, float xPosition, float yPosition) {
			super(axes, xPosition, yPosition, widthJoystick, heightJoystick);
		}
	}


	private final List<JoystickBase> joysticks = new ArrayList<JoystickBase>();

	public Joystick(Context context) {
		super(context);
		setOnTouchListener(this);
	}

	public void setAxes(Axes[] axes) {
		int n = 0;
		for (Axes a : axes) {
			createJoystick(a, n++);
		}
	}

	private void createJoystick(Axes a, int n) {
		int xPosition = widthJoystick * n + 100;
		int yPosition = heightJoystick / 2 + 100;

		// We will suppose they not have more than 2 joystick
		if (a.x != null && a.y != null) {
			// Use a 2-axis joystick
			joysticks.add(new Axis2Joystick(a, xPosition, yPosition));
			Log.d("joystick", "2 axis joystick created");
		} else {
			// Use a 1-axis joystick
			joysticks.add(new AxisJoystick(a, xPosition, yPosition, a.x == null ? Orientation.HORIZONTAL : Orientation.VERTICAL));
			Log.d("joystick", "1 axis joystick created");
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		int n = 0;
		for (JoystickBase joystick : joysticks) {
			int xPosition = (w - 200) * n + 100;
			int yPosition = heightJoystick / 2 + 100;

			joystick.setxPosition(xPosition);
			joystick.setyPosition(yPosition);

			n++;
		}
	}

	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_POINTER_2_UP:
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_MOVE:

			for (JoystickBase j : joysticks) {
				boolean oneAtLeast = false;
				for (int id = 0; id <= event.getPointerCount(); ++id) {
					// Check we are in the zone
					float xId = event.getX(id);
					float yId = event.getY(id);

					if (j.isIn(xId, yId)) {
						oneAtLeast = true;
						j.isInUse = true;

						j.move(xId, yId);
						break;
					}

				}
				if (!oneAtLeast && j.isInUse) {
					j.released();
				}
			}

			move();

			break;
		}

		return true;
	}

	private void move() {
		Robot.actuel.move();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (JoystickBase joystick : joysticks) {
			joystick.onDraw(canvas);
		}
	}

}