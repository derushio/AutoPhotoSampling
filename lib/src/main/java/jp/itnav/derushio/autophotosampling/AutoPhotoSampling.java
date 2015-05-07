package jp.itnav.derushio.autophotosampling;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by derushio on 15/05/05.
 */
public class AutoPhotoSampling {

	private static Bitmap autoPhotoSampling(final ImageView imageView, final Bitmap bitmap, final Uri uri, final Resources resources, final Integer resourceId) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		// サムネイルなのに大サイズの画像をいちいち読み込んでいたらメモリがいくらあっても足りない
		// なので、サンプリング（ピクセルを抜いて読む）して、画像サイズを大幅に小さくする
		options.inJustDecodeBounds = true;
		// trueにすることで、実際の画像は読まれず、情報だけ取ってこれる
		Bitmap readBitmap = null;
		// 読み込む対象Bitmap

		int width = imageView.getWidth();
		int height = imageView.getHeight();

		if (bitmap != null) {
			float sampleRate = getSampleRate(bitmap.getWidth(), bitmap.getHeight(), width, height);
			Log.d("size", (bitmap.getWidth() * sampleRate) + "," + (bitmap.getHeight() * sampleRate));
			readBitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * sampleRate), (int) (bitmap.getHeight() * sampleRate), false);
		} else if (uri != null) {
			// uriが存在したら
			BitmapFactory.decodeFile(uri.getPath(), options);
			// ファイルをデコード（情報だけ取ってこられていて、実際は読まれていない（optionsによって））

			int sampleSize = getSampleSize(options.outWidth, options.outHeight, width, height);

			options.inSampleSize = sampleSize;
			// サンプルサイズを確定

			options.inJustDecodeBounds = false;
			// falseにすることにより、実際に画像を読む
			readBitmap = BitmapFactory.decodeFile(uri.getPath(), options);
			// 画像をサンプリングして読む
		} else if (resources != null) {
			// resourceが存在したら
			BitmapFactory.decodeResource(resources, resourceId, options);
			// ファイルをデコード（情報だけ取ってこられていて、実際は読まれていない（optionsによって））

			int sampleSize = getSampleSize(options.outWidth, options.outHeight, width, height);

			options.inSampleSize = sampleSize;
			// サンプルサイズを確定

			options.inJustDecodeBounds = false;
			// falseにすることにより、実際に画像を読む
			readBitmap = BitmapFactory.decodeResource(resources, resourceId, options);
			// 画像をサンプリングして読む
		}

		try {
			imageView.setImageBitmap(readBitmap);
			// 読み込んだ画像をセットする
		} catch (NullPointerException e) {

		}

		imageView.invalidate();
		return readBitmap;
	}

	public static Bitmap autoPhotoSampling(ImageView imageView, Bitmap bitmap) {
		return autoPhotoSampling(imageView, bitmap, null, null, null);
	}

	public static Bitmap autoPhotoSampling(ImageView imageView, Uri uri) {
		return autoPhotoSampling(imageView, null, uri, null, null);
	}

	public static Bitmap autoPhotoSampling(ImageView imageView, Resources resources, int resourceId) {
		return autoPhotoSampling(imageView, null, null, resources, resourceId);
	}

	public static float getSampleRate(int width, int height, int reqWidth, int reqHeight) {
		float inSampleRate = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width < height) {
				inSampleRate = (float) reqHeight / (float) height;
			} else {
				inSampleRate = (float) reqWidth / (float) width;
			}
		}

		return inSampleRate;
	}

	public static int getSampleSize(int width, int height, int reqWidth, int reqHeight) {
		// オプションと求める幅と高さからサンプルサイズを確定
		int inSampleSize = 1;
		// デフォルトはフルサイズ

		if (height > reqHeight || width > reqWidth) {
			// フルサイズよりも小さくする必要があるなら
			if (width < height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}
}
