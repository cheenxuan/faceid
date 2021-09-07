package com.baidu.idl.face.platform.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.media.ExifInterface;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon.BDFaceImageType;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Author: xuan
 * Created on 2021/9/7 09:09.
 * <p>
 * Describe:
 */
public final class BitmapUtils {
    private static final String TAG = "ImageUtils";
    private static final int QUALITY = 100;
    public static final int ROTATE0 = 0;
    public static final int ROTATE90 = 90;
    public static final int ROTATE180 = 180;
    public static final int ROTATE270 = 270;
    public static final int ROTATE360 = 360;
    public static final int PIC_COMPRESS_SIZE = 4;
    public static final int IMAGEBOUND = 128;
    public static final int MAXLENTH = 1024;

    private BitmapUtils() {
    }

    public static Bitmap createBitmap(Context context, byte[] data, float orientation) {
        Bitmap bitmap = null;
        Bitmap transformed = null;
        Options opts = new Options();

        try {
            int width = DensityUtils.getDisplayWidth(context);
            int hight = DensityUtils.getDisplayHeight(context);
            int min = Math.min(width, hight);
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, opts);
            opts.inSampleSize = computeSampleSize(opts, min, 1048576);
            opts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
            transformed = rotateBitmap(orientation, bitmap);
        } catch (OutOfMemoryError var9) {
            var9.printStackTrace();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }

            if (transformed != null && !transformed.isRecycled()) {
                transformed.recycle();
                transformed = null;
            }

            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, opts);
            opts.inSampleSize = computeSampleSize(opts, -1, opts.outWidth * opts.outHeight / 4);
            opts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
            transformed = rotateBitmap(orientation, bitmap);
        }

        if (transformed != bitmap && bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }

        return transformed;
    }

    public static Bitmap rotateBitmap(float orientation, Bitmap bitmap) {
        Matrix m = new Matrix();
        Bitmap transformed;
        if (orientation == 0.0F) {
            transformed = bitmap;
        } else {
            m.setRotate(orientation);
            transformed = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        }

        return transformed;
    }

    public static int computeSampleSize(Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            for(roundedSize = 1; roundedSize < initialSize; roundedSize <<= 1) {
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    public static int computeInitialSampleSize(Options options, int minSideLength, int maxNumOfPixels) {
        double w = (double)options.outWidth;
        double h = (double)options.outHeight;
        int lowerBound = maxNumOfPixels == -1 ? 1 : (int)Math.ceil(Math.sqrt(w * h / (double)maxNumOfPixels));
        int upperBound = minSideLength == -1 ? IMAGEBOUND : (int)Math.min(Math.floor(w / (double)minSideLength), Math.floor(h / (double)minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;
        } else if (maxNumOfPixels == -1 && minSideLength == -1) {
            return 1;
        } else {
            return minSideLength == -1 ? lowerBound : upperBound;
        }
    }

    public static int decodeImageDegree(String path) {
        boolean var1 = false;

        short degree;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt("Orientation", 1);
            switch(orientation) {
                case 3:
                    degree = 180;
                    break;
                case 6:
                    degree = 90;
                    break;
                case 8:
                    degree = 270;
                    break;
                default:
                    degree = 0;
            }
        } catch (Exception var4) {
            var4.printStackTrace();
            degree = 0;
        }

        return degree;
    }

    public static Bitmap scale(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (bitmap != null) {
            bitmap.recycle();
        }

        return newBitmap;
    }

    public static Bitmap scale(Bitmap bitmap, int w, int h) {
        if (bitmap == null) {
            return null;
        } else {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();
            float scaleWidth = (float)w / (float)width;
            float scaleHeight = (float)h / (float)height;
            matrix.postScale(scaleWidth, scaleHeight);
            return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        }
    }

    public static Bitmap calculateInSampleSize(Bitmap resBitmap, int desWidth, int desHeight) {
        int resWidth = resBitmap.getWidth();
        int resHeight = resBitmap.getHeight();
        if (resHeight <= desHeight && resWidth <= desWidth) {
            return resBitmap;
        } else {
            float heightRatio = (float)desHeight / (float)resHeight;
            float widthRatio = (float)desWidth / (float)resWidth;
            float scale = heightRatio < widthRatio ? heightRatio : widthRatio;
            return scale(resBitmap, scale);
        }
    }

    public static Bitmap createBitmap(Context context, String filename, int orientatoin) {
        Bitmap bitmap = null;
        Bitmap transformed = null;
        Options opts = new Options();

        try {
            int width = DensityUtils.getDisplayWidth(context);
            int hight = DensityUtils.getDisplayHeight(context);
            int min = Math.min(width, hight);
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filename, opts);
            opts.inSampleSize = computeSampleSize(opts, min, 1048576);
            opts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(filename, opts);
            transformed = rotateBitmap((float)orientatoin, bitmap);
        } catch (OutOfMemoryError var9) {
            var9.printStackTrace();
            if (bitmap != null) {
                bitmap.recycle();
            }

            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filename, opts);
            opts.inSampleSize = computeSampleSize(opts, -1, opts.outWidth * opts.outHeight / 4);
            opts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(filename, opts);
            transformed = rotateBitmap((float)orientatoin, bitmap);
        }

        if (transformed != bitmap && bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }

        return transformed;
    }

    public static Bitmap createBitmap(Context context, byte[] imageByte, int orientatoin) {
        Bitmap bitmap = null;
        Bitmap transformed = null;
        Options opts = new Options();

        try {
            int width = DensityUtils.getDisplayWidth(context);
            int hight = DensityUtils.getDisplayHeight(context);
            Math.min(width, hight);
            opts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
            transformed = rotateBitmap((float)orientatoin, bitmap);
        } catch (OutOfMemoryError var9) {
            var9.printStackTrace();
            if (bitmap != null) {
                bitmap.recycle();
            }

            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length, opts);
            opts.inSampleSize = computeSampleSize(opts, -1, opts.outWidth * opts.outHeight / 4);
            opts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length, opts);
            transformed = rotateBitmap((float)orientatoin, bitmap);
        }

        if (transformed != bitmap && bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }

        return transformed;
    }

    public static Bitmap createBitmap(Context context, int pw, int ph, int[] argbByte) {
        Bitmap bitmap = null;
        Bitmap transformed = null;
        Options opts = new Options();

        try {
            int width = DensityUtils.getDisplayWidth(context);
            int hight = DensityUtils.getDisplayHeight(context);
            Math.min(width, hight);
            opts.inJustDecodeBounds = false;
            bitmap = Bitmap.createBitmap(argbByte, pw, ph, Config.RGB_565);
        } catch (OutOfMemoryError var10) {
            var10.printStackTrace();
            if (bitmap != null) {
                bitmap.recycle();
            }
        }

        if (transformed != bitmap && bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }

        return (Bitmap)transformed;
    }

    public static byte[] bitmapCompress(Bitmap bitmap, int quality) {
        ByteArrayOutputStream out = null;

        Object var4;
        try {
            out = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.JPEG, quality, out);
            byte[] data = out.toByteArray();
            byte[] var16 = data;
            return var16;
        } catch (Exception var14) {
            var4 = null;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException var13) {
                    var13.printStackTrace();
                }
            }

        }

        return (byte[])var4;
    }

    public static String bitmapToJpegBase64(Bitmap bitmap, int quality, float maxSize) {
        try {
            float scale = maxSize / (float)Math.max(bitmap.getWidth(), bitmap.getHeight());
            if (scale < 1.0F) {
                bitmap = scale(bitmap, scale);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.JPEG, quality, out);
            byte[] data = out.toByteArray();
            out.close();
            return Base64Utils.encodeToString(data, 2);
        } catch (Exception var6) {
            return null;
        }
    }

    public static boolean saveBitmap(File file, Bitmap bitmap) {
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(file);
            bitmap.compress(CompressFormat.JPEG, 100, out);
            boolean var3 = true;
            return var3;
        } catch (Exception var13) {
            var13.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception var12) {
                var12.printStackTrace();
            }

        }

        return false;
    }

    public static Bitmap yuv2Bitmap(byte[] data, int width, int height) {
        int frameSize = width * height;
        int[] rgba = new int[frameSize];

        for(int i = 0; i < height; ++i) {
            for(int j = 0; j < width; ++j) {
                int y = 255 & data[i * width + j];
                int u = 255 & data[frameSize + (i >> 1) * width + (j & -2) + 0];
                int v = 255 & data[frameSize + (i >> 1) * width + (j & -2) + 1];
                y = y < 16 ? 16 : y;
                int r = Math.round(1.164F * (float)(y - 16) + 1.596F * (float)(v - 128));
                int g = Math.round(1.164F * (float)(y - 16) - 0.813F * (float)(v - 128) - 0.391F * (float)(u - 128));
                int b = Math.round(1.164F * (float)(y - 16) + 2.018F * (float)(u - 128));
                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);
                rgba[i * width + j] = -16777216 + (b << 16) + (g << 8) + r;
            }
        }

        Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        bmp.setPixels(rgba, 0, width, 0, 0, width, height);
        return bmp;
    }

    public static Bitmap Depth2Bitmap(byte[] depthBytes, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        int[] argbData = new int[width * height];

        for(int i = 0; i < width * height; ++i) {
            argbData[i] = (depthBytes[i * 2] + depthBytes[i * 2 + 1] * 256) / 10 & 255 | ((depthBytes[i * 2] + depthBytes[i * 2 + 1] * 256) / 10 & 255) << 8 | ((depthBytes[i * 2] + depthBytes[i * 2 + 1] * 256) / 10 & 255) << 16 | -16777216;
        }

        bitmap.setPixels(argbData, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public static Bitmap BGR2Bitmap(byte[] bytes, int width, int height) {
        Bitmap stitchBmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        byte[] rgba = new byte[width * height * 4];

        for(int i = 0; i < width * height; ++i) {
            byte b1 = bytes[i * 3 + 0];
            byte b2 = bytes[i * 3 + 1];
            byte b3 = bytes[i * 3 + 2];
            rgba[i * 4 + 0] = b3;
            rgba[i * 4 + 1] = b2;
            rgba[i * 4 + 2] = b1;
            rgba[i * 4 + 3] = -1;
        }

        stitchBmp.copyPixelsFromBuffer(ByteBuffer.wrap(rgba));
        return stitchBmp;
    }

    public static Bitmap getInstaceBmp(BDFaceImageInstance newInstance) {
        Bitmap transBmp = null;
        if (newInstance.imageType == BDFaceImageType.BDFACE_IMAGE_TYPE_RGBA) {
            transBmp = Bitmap.createBitmap(newInstance.width, newInstance.height, Config.ARGB_8888);
            transBmp.copyPixelsFromBuffer(ByteBuffer.wrap(newInstance.data));
        } else if (newInstance.imageType == BDFaceImageType.BDFACE_IMAGE_TYPE_BGR) {
            transBmp = BGR2Bitmap(newInstance.data, newInstance.width, newInstance.height);
        } else if (newInstance.imageType == BDFaceImageType.BDFACE_IMAGE_TYPE_YUV_NV21) {
            transBmp = yuv2Bitmap(newInstance.data, newInstance.width, newInstance.height);
        } else if (newInstance.imageType == BDFaceImageType.BDFACE_IMAGE_TYPE_GRAY) {
            transBmp = Depth2Bitmap(newInstance.data, newInstance.width, newInstance.height);
        }

        return transBmp;
    }
}
