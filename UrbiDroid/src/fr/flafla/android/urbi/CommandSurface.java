/**
 * 
 */
package fr.flafla.android.urbi;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnTouchListener;
import fr.flafla.android.urbi.control.Point;
import fr.flafla.android.urbi.control.Pointer;
import fr.flafla.android.urbi.robot.Robot;
import fr.flafla.android.urbi.robot.Robot.ImageHandler;

/**
 * @author merlin
 * 
 */
public class CommandSurface extends SurfaceView implements Callback,
		OnTouchListener {
	private static final int nbJoystick = 2;

	/**
	 * La liste des pointers de joystick
	 */
	private List<Pointer> pointers = new ArrayList<Pointer>();
	/**
	 * La liste des joysticks
	 */
	private List<Point> joysticks = new ArrayList<Point>();

	// TODO choisir le robot
	// private Robot robot = new FakeRobot();
	// private Robot robot = new Spykee();

	private int width = -1;
	private int height = -1;
	private List<Drawable> joystickImgs = new ArrayList<Drawable>();
	private int iconSize = 100;
	private int iconMargin = 50;

	private static final float maxMvt = 10f;
	// private float mX = 0, mY = 0;
	// private float dX, dY;
	public boolean already = false;

	private Bitmap cameraImg;

	public CommandSurface(Context context) {
		super(context);

		SurfaceHolder holder = getHolder();
		holder.setKeepScreenOn(true);
		holder.addCallback(this);

		this.setOnTouchListener(this);
	}

	private Pointer getPointer(float x, float y) {
		return getPoint(pointers, x, y);
	}

	private <H extends Point> H getPoint(List<H> points, float x, float y) {
		float min = Float.MAX_VALUE;
		H p = null;
		for (H pointer : points) {
			float dst = pointer.distance(x, y);
			if (dst < min) {
				min = dst;
				p = pointer;
			}
		}
		Log.i("surface", p + "");
		return p;
	}

	public void redraw(Bitmap cameraImg) {
		Canvas canvas = null;
		this.cameraImg = cameraImg;
		SurfaceHolder holder = getHolder();
		try {
			canvas = holder.lockCanvas();
			onDraw(canvas);
		} finally {
			if (canvas != null)
				holder.unlockCanvasAndPost(canvas);
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// this.width = width;
		// this.height = height;

		if (joysticks.size() == 0) {
			for (int i = 0; i < nbJoystick; ++i) {
				joysticks.add(new Point());
			}
			joystickImgs.add(getContext().getResources().getDrawable(
					R.drawable.orange));
			joystickImgs.add(getContext().getResources().getDrawable(
					R.drawable.orange2));
		}
		int stepX = width - iconMargin * 2;
		for (int i = 0; i < nbJoystick; ++i) {
			joysticks.get(i).x = iconMargin + stepX * i;
			joysticks.get(i).y = height - iconMargin;
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		acquire();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (canvas != null) {
			if (width == -1)
				width = canvas.getWidth();
			if (height == -1)
				height = canvas.getHeight();

			if (cameraImg != null) {
				canvas.drawBitmap(cameraImg,
						(width - cameraImg.getWidth()) / 2,
						((height - cameraImg.getHeight()) / 2)
								- (cameraImg.getHeight() / 2), null);
			}

			for (int i = 0; i < joysticks.size(); ++i) {
				Point joystick = joysticks.get(i);
				int left = (int) (joystick.x - iconSize / 2);
				int top = (int) (joystick.y - iconSize / 2);
				for (Pointer p : pointers) {
					if (p.joystick == i) {
						left -= p.mX;
						top -= p.mY;
					}
				}

				// canvas.drawColor(android.R.color.black);
				// canvas.drawRect(iconMargin-maxMvt, height - maxMvt -
				// iconMargin - iconSize, iconMargin+maxMvt + iconSize, height +
				// maxMvt - iconMargin + iconSize, new Paint());

				Drawable img = joystickImgs.get(i);
				img.invalidateSelf();
				img.setBounds(left, top, left + iconSize, top + iconSize);
				img.draw(canvas);
			}

			already = true;

			// TODO tester
			postInvalidate();
		}
	}

	private void acquire() {
		Robot.actuel.acquire(new ImageHandler() {
			public void handle(Bitmap bitmap) {
				redraw(bitmap);
			}
		});
	}

	public void walk() {
		float trackL = 0f;
		float trackR = 0f;
		for (Pointer pointer : pointers) {
			if (pointer.joystick == 0)
				trackL = pointer.mY;
			if (pointer.joystick == 1)
				trackR = pointer.mY;
		}
		Robot.actuel.go((int) (trackL * 100 / maxMvt),
				(int) (trackR * 100 / maxMvt));
	}

	public boolean onTouch(View v, MotionEvent event) {
		Pointer pointer;
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			for (int i = 0; i < event.getPointerCount(); ++i) {
				pointer = getPointer(event.getX(i), event.getY(i));
				if (pointer != null) {
					// Passer l'ordre
					pointer.mX = event.getX(i) - pointer.x;
					pointer.mY = pointer.y - event.getY(i);
					if (pointer.mX > maxMvt)
						pointer.mX = maxMvt;
					if (pointer.mX < -maxMvt)
						pointer.mX = -maxMvt;
					if (pointer.mY > maxMvt)
						pointer.mY = maxMvt;
					if (pointer.mY < -maxMvt)
						pointer.mY = -maxMvt;

				}
			}
			walk();
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
		case MotionEvent.ACTION_POINTER_2_DOWN:
		case MotionEvent.ACTION_DOWN:
			for (int i = pointers.size(); i < event.getPointerCount(); ++i) {
				pointer = new Pointer(event);
				pointer.joystick = joysticks.indexOf(getPoint(joysticks, event
						.getX(i), event.getY(i)));
				pointers.add(pointer);
			}
			Log.i("Surface", "down : nb de doigts : " + pointers.size());
			break;

		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_UP:
			for (int i = 0; i < event.getPointerCount(); ++i) {
				pointer = getPointer(event.getX(i), event.getY(i));
				if (pointer != null) {
					pointers.remove(pointer);
				}
			}
			walk();
			Log.i("Surface", "up : nb de doigts : " + pointers.size());
			break;
		}

		return true;
	}
}
