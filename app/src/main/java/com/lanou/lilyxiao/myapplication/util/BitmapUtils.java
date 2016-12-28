/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lanou.lilyxiao.myapplication.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore.Images.ImageColumns;

/**
 * Utils for bitmap operations.
 */
public class BitmapUtils {

	private static final String TAG = "BitmapUtils";
	private static final int INDEX_ORIENTATION = 0;
	private static final int DEFAULT_SIZE = 1000;
	private static final int DEFAULT_SIZE_SMALL = 650;

	private static final String[] IMAGE_PROJECTION = new String[] { ImageColumns.ORIENTATION };
	private static final String[] PATH_PROJECTION = new String[] { ImageColumns.DATA };
	
	public static final int DEFAULT_COMPRESS_QUALITY = 90;
	
	private final Context context;

	public BitmapUtils(Context context) {
		this.context = context;
	}

	/**
	 * Creates a mutable bitmap from subset of source bitmap, transformed by the
	 * optional matrix.
	 */
	public static Bitmap createBitmap(Bitmap source, int x, int y, int width,
			int height, Matrix m) {
		// Re-implement Bitmap createBitmap() to always return a mutable bitmap.
		Canvas canvas = new Canvas();

		Bitmap bitmap;
		Paint paint;
		if ((m == null) || m.isIdentity()) {
			bitmap = Bitmap.createBitmap(width, height, source.getConfig());
			paint = null;
		} else {
			RectF rect = new RectF(0, 0, width, height);
			m.mapRect(rect);
			try {
				bitmap = Bitmap.createBitmap(Math.round(rect.width()),
						Math.round(rect.height()), source.getConfig());
			} catch(OutOfMemoryError e) {
				e.printStackTrace();
				return null;
			}


			canvas.translate(-rect.left, -rect.top);
			canvas.concat(m);

			paint = new Paint(Paint.FILTER_BITMAP_FLAG);
			if (!m.rectStaysRect()) {
				paint.setAntiAlias(true);
			}
		}
		bitmap.setDensity(source.getDensity());
		canvas.setBitmap(bitmap);

		Rect srcBounds = new Rect(x, y, x + width, y + height);
		RectF dstBounds = new RectF(0, 0, width, height);
		canvas.drawBitmap(source, srcBounds, dstBounds, paint);
		return bitmap;
	}
	
	
	public static Bitmap createBitmapSmaller(Bitmap source, int x, int y, int width,
			int height, float scale, Matrix m) {
		// Re-implement Bitmap createBitmap() to always return a mutable bitmap.
		Canvas canvas = new Canvas();

		Bitmap bitmap;
		Paint paint;
		
		if ((m == null) || m.isIdentity()) {
			bitmap = Bitmap.createBitmap(Math.round(width * scale), Math.round(height * scale), source.getConfig());
			paint = null;
		} else {
			RectF rect = new RectF(0, 0, Math.round(height * scale), Math.round(width * scale));
			m.mapRect(rect);
			bitmap = Bitmap.createBitmap(Math.round(rect.height()), Math.round(rect.width()),
					source.getConfig());

			canvas.scale(scale, scale);
			canvas.translate(-rect.left, -rect.top);
			canvas.concat(m);

			paint = new Paint(Paint.FILTER_BITMAP_FLAG  | Paint.DITHER_FLAG);
		}

		canvas.setBitmap(bitmap);

		Rect srcBounds = new Rect(x, y, x + width, y + height);
		RectF dstBounds = new RectF(0, 0, Math.round(height * scale), Math.round(width * scale));
		canvas.drawBitmap(source, srcBounds, dstBounds, paint);
		return bitmap;
	}
	
	public static Bitmap createBitmapSmaller(Bitmap source, float scale, Matrix m) {

		int width = Math.round(source.getWidth() * scale);
		int height = Math.round(source.getHeight() * scale);
		if (width == source.getWidth() && height == source.getHeight())
			return source;
		Bitmap target = Bitmap.createBitmap(width, height, getConfig(source));
		Canvas canvas = new Canvas(target);
		canvas.scale(scale, scale);
		
		Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
		canvas.drawBitmap(source, 0, 0, paint);

		if(m != null) {
			Bitmap transformed = createBitmap(target, 0, 0, target.getWidth(),
					target.getHeight(), m);
			target.recycle();
			return transformed;
		}
		return target;
	}

	private void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private Rect getBitmapBounds(Uri uri) {
		Rect bounds = new Rect();
		InputStream is = null;

		try {			
		    String pefix = "file://";
            if(uri.getScheme().toLowerCase().startsWith("file")){
                String file = uri.toString();
                int index = pefix.length();
                file = file.substring(index);
                is = new FileInputStream(file);
            }else{
                is = context.getContentResolver().openInputStream(uri);
            }
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(is, null, options);

			bounds.right = options.outWidth;
			bounds.bottom = options.outHeight;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			closeStream(is);
		}

		return bounds;
	}

	public int getOrientation(Uri uri) {
		int orientation = 0;
		if(uri.getScheme().toLowerCase().contains("file")){
		    String filePath = uri.getPath();
		    if(filePath!=null&&(filePath.toLowerCase().endsWith("jpg")||filePath.toLowerCase().endsWith("jpeg"))){
		    	File file = new File(filePath);
		    	InputStream stream = null;
		    	ByteBuffer buffer = null;
				try {
					stream = new FileInputStream(file);
					buffer = ByteBuffer.allocate(stream.available());
					stream.read(buffer.array());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if(buffer != null && buffer.array() != null)
					orientation = Exif.getOrientation(buffer.array());
		    }
		}else{
    		Cursor cursor = null;
    		try {
    			cursor = context.getContentResolver().query(uri, IMAGE_PROJECTION,
    					null, null, null);
    			if ((cursor != null) && cursor.moveToNext()) {
    				orientation = cursor.getInt(INDEX_ORIENTATION);
    			}
    		} catch (Exception e) {
    			// Ignore error for no orientation column; just use the default
    			// orientation value 0.
    		} finally {
    			if (cursor != null) {
    				cursor.close();
    			}
    		}
		}
		return orientation;
	}

	/**
	 * Decodes bitmap (maybe immutable) that keeps aspect-ratio and spans most
	 * within the bounds.
	 */
	public Bitmap decodeBitmap(Uri uri, int width, int height) {
		InputStream is = null;
		Bitmap bitmap = null;
		
		boolean bOk = true;
		int sampleSize = -1;

		do {
			try {
				// TODO: Take max pixels allowed into account for calculation to
				// avoid possible OOM.
				Rect bounds = getBitmapBounds(uri);
				int w = bounds.width();
				int h = bounds.height();

				BitmapFactory.Options options = new BitmapFactory.Options();
				if(sampleSize == -1) {
					options.inSampleSize = computeSampleSizeLarger(w,
							h, width > height ? width : height);
					sampleSize = options.inSampleSize;
				} else {
					sampleSize *= 2;
					options.inSampleSize = sampleSize;
				}
				
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;

				String pefix = "file://";
	            if(uri.getScheme().toLowerCase().startsWith("file")){
	                String file = uri.toString();
	                int index = pefix.length();
	                file = file.substring(index);
	                is = new FileInputStream(file);
	            }else{
	                is = context.getContentResolver().openInputStream(uri);
	            }
				bitmap = BitmapFactory.decodeStream(is, null, options);
				bOk = false;
			} catch (FileNotFoundException e) {
			    //LogUtil.e(TAG, "FileNotFoundException: " + uri);
			    bOk = false;
			} catch (OutOfMemoryError e) {
			    //LogUtil.e(TAG, "OutOfMemoryError: " + uri);
			} finally {
				closeStream(is);
			}
		} while(bOk);


		// Ensure bitmap in 8888 format, good for editing as well as GL
		// compatible.
		if ((bitmap != null) && (bitmap.getConfig() != Bitmap.Config.ARGB_8888)) {
			Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
			bitmap.recycle();
			bitmap = copy;
		}

		if (bitmap != null) {
			// Scale down the sampled bitmap if it's still larger than the
			// desired dimension.
			float scale = Math.min((float) width / bitmap.getWidth(),
					(float) height / bitmap.getHeight());
			scale = Math.max(
					scale,
					Math.min((float) height / bitmap.getWidth(), (float) width
							/ bitmap.getHeight()));
			if (scale < 1) {
				Matrix m = new Matrix();
				m.setScale(scale, scale);
				Bitmap transformed = createBitmap(bitmap, 0, 0,
						bitmap.getWidth(), bitmap.getHeight(), m);
				bitmap.recycle();
				return transformed;
			}
		}
		return bitmap;
	}
	
	   private Bitmap decodeBitmapWithoutResize(Uri uri) {
	        InputStream is = null;
	        Bitmap bitmap = null;
	        
			boolean bOk = true;
			int sampleSize = -1;

			do{
	        try {
	            // TODO: Take max pixels allowed into account for calculation to
	            // avoid possible OOM.
	            Rect bounds = getBitmapBounds(uri);
	            int w = bounds.width();
	            int h = bounds.height();

	            BitmapFactory.Options options = new BitmapFactory.Options();
				if(sampleSize == -1) {
					options.inSampleSize = computeSampleSizeSmaller(w, h, false);
				} else {
					sampleSize *= 2;
					options.inSampleSize = sampleSize;
				}
	            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

	            String pefix = "file://";
	            if(uri.getScheme().toLowerCase().startsWith("file")){
	                String file = uri.toString();
	                int index = pefix.length();
	                file = file.substring(index);
	                is = new FileInputStream(file);
	            }else{
	                is = context.getContentResolver().openInputStream(uri);
	            }
	            bitmap = BitmapFactory.decodeStream(is, null, options);
	            
	            bOk = false;
	        } catch (FileNotFoundException e) {
	        	bOk = false;
	            //LogUtil.e(TAG, "FileNotFoundException: " + uri);
	        } catch (OutOfMemoryError e) {
	            //LogUtil.e(TAG, "OutOfMemoryError: " + uri);
	        } catch (Exception e) {
	        	
	        }finally {
	            closeStream(is);
	        }
			}
	        
	        while(bOk);

	        // Ensure bitmap in 8888 format, good for editing as well as GL
	        // compatible.
	        if ((bitmap != null) && (bitmap.getConfig() != Bitmap.Config.ARGB_8888)) {
	            Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
	            bitmap.recycle();
	            bitmap = copy;
	        }

	        return bitmap;
	    }

	/**
	 * Gets decoded bitmap that keeps orientation as well.
	 */
	public Bitmap getBitmap(Uri uri, int width, int height) {
		Bitmap bitmap = decodeBitmap(uri, width, height);

		// Rotate the decoded bitmap according to its orientation if it's
		// necessary.
		if (bitmap != null) {
			int orientation = getOrientation(uri);
			if (orientation != 0) {
				Matrix m = new Matrix();
				m.setRotate(orientation);
				Bitmap transformed = createBitmap(bitmap, 0, 0,
						bitmap.getWidth(), bitmap.getHeight(), m);
				bitmap.recycle();
				return transformed;
			}
		}
		return bitmap;
	}
	
	   public Bitmap getBitmapWithoutResize(Uri uri) {
	        Bitmap bitmap = decodeBitmapWithoutResize(uri);

	        // Rotate the decoded bitmap according to its orientation if it's
	        // necessary.
	        if (bitmap != null) {
	            int orientation = getOrientation(uri);
	            if (orientation != 0) {
	                Matrix m = new Matrix();
	                m.setRotate(orientation);
	                Bitmap transformed = createBitmap(bitmap, 0, 0,
	                        bitmap.getWidth(), bitmap.getHeight(), m);
	                bitmap.recycle();
	                return transformed;
	            }
	        }
	        return bitmap;
	    }

	/*
	 * Compute the sample size as a function of minSideLength and
	 * maxNumOfPixels. minSideLength is used to specify that minimal width or
	 * height of a bitmap. maxNumOfPixels is used to specify the maximal size in
	 * pixels that is tolerable in terms of memory usage.
	 * 
	 * The function returns a sample size based on the constraints. Both size
	 * and minSideLength can be passed in as -1 which indicates no care of the
	 * corresponding constraint. The functions prefers returning a sample size
	 * that generates a smaller bitmap, unless minSideLength = -1.
	 * 
	 * Also, the function rounds up the sample size to a power of 2 or multiple
	 * of 8 because BitmapFactory only honors sample size this way. For example,
	 * BitmapFactory downsamples an image by 2 even though the request is 3. So
	 * we round up the sample size to avoid OOM.
	 */
	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels < 0) ? 1 : (int) Math.ceil(Math.sqrt(w
				* h / maxNumOfPixels));
		int upperBound = (minSideLength < 0) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if (maxNumOfPixels < 0 && minSideLength < 0) {
			return 1;
		} else if (minSideLength < 0) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	// Returns the previous power of two.
	// Returns the input if it is already power of 2.
	// Throws IllegalArgumentException if the input is <= 0
	public static int prevPowerOf2(int n) {
		if (n <= 0)
			throw new IllegalArgumentException();
		return Integer.highestOneBit(n);
	}

	// This computes a sample size which makes the longer side at least
	// minSideLength long. If that's not possible, return 1.
	public static int computeSampleSizeLarger(int w, int h, int minSideLength) {
		int initialSize = Math.max(w / minSideLength, h / minSideLength);
		if (initialSize <= 1)
			return 1;

		return initialSize <= 8 ? prevPowerOf2(initialSize)
				: initialSize / 8 * 8;
	}

    public static int computeSampleSizeSmaller(int w, int h, boolean bSmall) {
    	int minSize = DEFAULT_SIZE;
    	if (bSmall) {
    		minSize = DEFAULT_SIZE_SMALL;
    	}
        int initialSize = Math.max(w / minSize, h / minSize);
        int roundedSize = 0;

        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        
        while(roundedSize > 0 && Math.max(w / roundedSize, h / roundedSize) < minSize) {
            roundedSize /= 2;
        }
        
        return roundedSize;
    }
	
	public static Bitmap makeLimitedBitmap(byte[] jpegData,
			int maxNumOfPixels, int orientation) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory
					.decodeByteArray(jpegData, 0, jpegData.length, options);
			if (options.mCancel || options.outWidth == -1
					|| options.outHeight == -1) {
				return null;
			}
			options.inSampleSize = computeSampleSize(options,
					-1, maxNumOfPixels);
			options.inJustDecodeBounds = false;

			options.inDither = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			bitmap = BitmapFactory.decodeByteArray(jpegData, 0,
					jpegData.length, options);
		} catch (OutOfMemoryError ex) {
		    //LogUtil.e(TAG, "Got oom exception ", ex);
			return null;
		}

		if (orientation != 0) {
			Matrix m = new Matrix();
			m.setRotate(orientation);
			Bitmap transformed = createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), m);
			bitmap.recycle();
			return transformed;
		}
		return bitmap;
	}

	public static Bitmap makeFitBitmap(byte[] jpegData,
			int orientation, boolean bSmall) {
		Bitmap bitmap = null;
		
		if(jpegData == null) {
			return null;
		}
		
		boolean bOk = true;
		int sampleSize = -1;
		
		do {
			
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory
						.decodeByteArray(jpegData, 0, jpegData.length, options);
				if (options.mCancel || options.outWidth == -1
						|| options.outHeight == -1) {
					return null;
				}
				
				if(sampleSize == -1) {
					options.inSampleSize = computeSampleSizeSmaller(options.outWidth,
							options.outHeight, bSmall);
					sampleSize = options.inSampleSize;
				} else {
					sampleSize *= 2;
					options.inSampleSize = sampleSize;
				}
				


				options.inJustDecodeBounds = false;

				options.inDither = false;
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				bitmap = BitmapFactory.decodeByteArray(jpegData, 0,
						jpegData.length, options);
				
				bOk = false;
			} catch (OutOfMemoryError ex) {
			    //LogUtil.e(TAG, "Got oom exception ", ex);
			}
		} while (bOk);

		if (orientation != 0) {
			Matrix m = new Matrix();
			m.setRotate(orientation);
			Bitmap transformed = createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), m);
			bitmap.recycle();
			return transformed;
		}
		return bitmap;
	}
	
	public static Bitmap makeBitmap(byte[] jpegData, int minSideLength, int orientation, int maxNumOfPixels) {
		Bitmap bitmap = null;
		if(jpegData == null) {
			return null;
		}
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory
					.decodeByteArray(jpegData, 0, jpegData.length, options);
			if (options.mCancel || options.outWidth == -1
					|| options.outHeight == -1) {
				return null;
			}

			options.inSampleSize = computeSampleSize(options, minSideLength, maxNumOfPixels);
			options.inJustDecodeBounds = false;

			options.inDither = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_4444;
			bitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length, options);
		} catch (OutOfMemoryError ex) {
		    //LogUtil.e(TAG, "Got oom exception ", ex);
			return null;
		}

		if (orientation != 0) {
			Matrix m = new Matrix();
			m.setRotate(orientation);
			Bitmap transformed = createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), m);
			bitmap.recycle();
			return transformed;
		}
		return bitmap;
	}

	/**
	 * Saves the bitmap by given directory, filename, and format; if the
	 * directory is given null, then saves it under the cache directory.
	 */
//	public File saveBitmap(Bitmap bitmap, String directory, String filename,
//			CompressFormat format,String filterName) {
//
//		if (directory == null) {
//			directory = context.getCacheDir().getAbsolutePath();
//		} else {
//			// Check if the given directory exists or try to create it.
//			File file = new File(directory);
//			if (!file.isDirectory() && !file.mkdirs()) {
//				return null;
//			}
//		}
//
//		File file = null;
//		OutputStream os = null;
//
//		try {
//			filename = (format == CompressFormat.PNG) ? filename + ".png"
//					: filename + ".jpg";
//			file = new File(directory, filename);
//			os = new FileOutputStream(file);
//			if(format==CompressFormat.JPEG && filterName!=null&& filterName.length()>0)	{
//			    ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			    bitmap.compress(CompressFormat.JPEG, DEFAULT_COMPRESS_QUALITY, bos);
//                byte[] dataByte = Exif.writeFilter(bos.toByteArray(), filterName);
//                os.write(dataByte);
//                bos.close();
//			}
//			else {
//			    bitmap.compress(format, DEFAULT_COMPRESS_QUALITY, os);
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			closeStream(os);
//		}
//		return file;
//	}
	
    public static Bitmap resizeDownBySideLength(
            Bitmap bitmap, int maxLength, boolean recycle) {
        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();
        float scale = Math.min(
                (float) maxLength / srcWidth, (float) maxLength / srcHeight);
        if (scale >= 1.0f) return bitmap;
        return resizeBitmapByScale(bitmap, scale, recycle);
    }
    
    public static Bitmap resizeBitmapByScale(
            Bitmap bitmap, float scale, boolean recycle) {
        int width = Math.round(bitmap.getWidth() * scale);
        int height = Math.round(bitmap.getHeight() * scale);
        if (width == bitmap.getWidth()
                && height == bitmap.getHeight()) return bitmap;
        Bitmap target = Bitmap.createBitmap(width, height, getConfig(bitmap));
        Canvas canvas = new Canvas(target);
        canvas.scale(scale, scale);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        if (recycle) bitmap.recycle();
        return target;
    }
    
    private static Bitmap.Config getConfig(Bitmap bitmap) {
        Bitmap.Config config = bitmap.getConfig();
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }
        return config;
    }
    
	public static  void deletePicture(ContentResolver resolver,Uri uri) {
		String filePath = null;
		Cursor cursor = null;
		try {
			cursor = resolver.query(uri, PATH_PROJECTION,
					null, null, null);
			if ((cursor != null) && cursor.moveToNext()) {
				filePath = cursor.getString(INDEX_ORIENTATION);
			}
			
            if(filePath != null){
                File file = new File(filePath);
                if(file.isFile() && file.exists()){
                    file.delete();
                }
            }
            resolver.delete(uri, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
	
	public File saveJpegData(byte[] jpeg, String directory, String filename,
			CompressFormat format,String filterName) {
		if (directory == null) {
			directory = context.getCacheDir().getAbsolutePath();
		} else {
			// Check if the given directory exists or try to create it.
			File file = new File(directory);
			if (!file.isDirectory() && !file.mkdirs()) {
				return null;
			}
		}

		File file = null;
		OutputStream os = null;

		try {
			filename = (format == CompressFormat.PNG) ? filename + ".png"
					: filename + ".jpg";
			file = new File(directory, filename);
			os = new FileOutputStream(file);
			if(format==CompressFormat.JPEG && filterName!=null&& filterName.length()>0)	{
//			    ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			    bitmap.compress(CompressFormat.JPEG, DEFAULT_COMPRESS_QUALITY, bos);
//                byte[] dataByte = Exif.writeFilter(bos.toByteArray(), filterName);
//                os.write(dataByte);
//                bos.close();
			}
			else {
			    os.write(jpeg);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeStream(os);
		}
		return file;
	}
	
	public File saveBitmap(Bitmap bitmap, String directory, String filename,
			CompressFormat format,String filterName) {

		if (directory == null) {
			directory = context.getCacheDir().getAbsolutePath();
		} else {
			// Check if the given directory exists or try to create it.
			File file = new File(directory);
			if (!file.isDirectory() && !file.mkdirs()) {
				return null;
			}
		}

		File file = null;
		OutputStream os = null;
		boolean sucess = false;

		try {
			filename = (format == CompressFormat.PNG) ? filename + ".png"
					: filename + ".jpg";
			file = new File(directory, filename);
			os = new FileOutputStream(file);
			if(format==CompressFormat.JPEG && filterName!=null&& filterName.length()>0)	{
//			    ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			    bitmap.compress(CompressFormat.JPEG, DEFAULT_COMPRESS_QUALITY, bos);
//                byte[] dataByte = Exif.writeFilter(bos.toByteArray(), filterName);
//                os.write(dataByte);
//                bos.close();
			}
			else {
				sucess = bitmap.compress(format, DEFAULT_COMPRESS_QUALITY, os);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
				
		} finally {
			closeStream(os);
		}
		
		if(sucess)
			return file;
		else
			return null;
	}
	
    public static Bitmap resizeDownAndCropCenter(Bitmap bitmap, int size,
            boolean recycle) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int minSide = Math.min(w, h);
        if (w == h && minSide <= size) return bitmap;
        size = Math.min(size, minSide);

        float scale = Math.max((float) size / bitmap.getWidth(),
                (float) size / bitmap.getHeight());
        Bitmap target = Bitmap.createBitmap(size, size, getConfig(bitmap));
        int width = Math.round(scale * bitmap.getWidth());
        int height = Math.round(scale * bitmap.getHeight());
        Canvas canvas = new Canvas(target);
        canvas.translate((size - width) / 2f, (size - height) / 2f);
        canvas.scale(scale, scale);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        if (recycle) bitmap.recycle();
        return target;
    }
	
}
