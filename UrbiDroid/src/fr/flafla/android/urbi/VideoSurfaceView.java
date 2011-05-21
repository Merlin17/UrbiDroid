package fr.flafla.android.urbi;

import static fr.flafla.android.urbi.log.LoggerFactory.logger;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class VideoSurfaceView extends SurfaceView {

	private BitmapDrawable bitmap;

	public VideoSurfaceView(Context context) {
		super(context);
	}

	public VideoSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public VideoSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private void redraw() {
		Canvas canvas = null;
		SurfaceHolder holder = getHolder();
		try {
			canvas = holder.lockCanvas();
			onDraw(canvas);
		} finally {
			if (canvas != null)
				holder.unlockCanvasAndPost(canvas);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (canvas != null) {
			logger().i("Surface", "draw");

			if (bitmap != null && bitmap.getBitmap() != null) {
				logger().i("Surface", "draw with bitmap");
				canvas.save();

				final Bitmap cameraImg = bitmap.getBitmap();
				final int width = getWidth();
				final int height = getHeight();
				final int imageHeight = cameraImg.getHeight();
				final int imageWidth = cameraImg.getWidth();
				canvas.drawBitmap(cameraImg, (width - imageWidth) / 2, (height - imageHeight) / 2, null);

				canvas.restore();
			}
		}
	}

	public void setBitmap(BitmapDrawable bitmap) {
		this.bitmap = bitmap;
		redraw();
	}
}
