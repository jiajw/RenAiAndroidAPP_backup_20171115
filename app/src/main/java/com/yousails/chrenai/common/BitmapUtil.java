package com.yousails.chrenai.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wang on 2017/5/26.
 */

public class BitmapUtil {
    public static final String TAG = "BitmapUtils";

    public static Bitmap getBitmapFromBitmap(InputStream is) {
        if (is == null) {
            return null;
        }
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        try {
            Bitmap bm = BitmapFactory.decodeStream(is, null, opt);
            is.close();
            return bm;
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 获取图片的宽高
     *
     * @param path
     * @return
     */
    public static int[] getImageWidthAndHeight(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return new int[]{options.outWidth, options.outHeight};
    }


    public static boolean scaleBitmapAndStore(Bitmap bmp, String target) {
        boolean result = false;
        if (bmp == null || TextUtils.isEmpty(target)) {
            return result;
        }

        try {
            result = writeToFile(bmp, new File(target), 80, true);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return result;
    }

    public static boolean scaleBitmapAndStore(Bitmap bmp, int quality, String target) {
        boolean result = false;
        if (bmp == null || TextUtils.isEmpty(target)) {
            return result;
        }

        try {
            result = writeToFile(bmp, new File(target), quality, true);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 保存旋转后的图片文件
     *
     * @param srcFilePath  源文件
     * @param destFilePath 目标存放文件
     * @return
     */
    public static boolean writeRotateFileByPath(String srcFilePath, String destFilePath) {
        Bitmap bitmap = getRotateBitmapByPath(srcFilePath);
        if (bitmap == null || bitmap.isRecycled()) {
            return false;
        }
        return writeAndRecycle(bitmap, new File(destFilePath));
    }

    /**
     * 获取旋转后的图片
     *
     * @param path
     * @return
     */
    public static Bitmap getRotateBitmapByPath(String path) {
        return rotateBitmapByDegree(BitmapFactory.decodeFile(path), getBitmapDegree(path));
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            if (bm != null) {
                // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
                returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            }
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }


    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    //生成圆角图片
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float round) {
        try {
            Bitmap output = Bitmap.createBitmap(/*bitmap.getWidth()*/200,
                    /*bitmap.getHeight()*/200, Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, /*bitmap.getWidth()*/200,
                    /*bitmap.getHeight()*/200);
            final RectF rectF = new RectF(new Rect(0, 0, 200/*bitmap.getWidth()*/,
                    /*bitmap.getHeight()*/200));

            final float roundPx = round;
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            final Rect src = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());

            canvas.drawBitmap(bitmap, src, rect, paint);
            return output;
        } catch (Exception e) {
            return bitmap;
        }
    }

    //生成正圆图片
    public static Bitmap createRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int left = 0, top = 0, right = width, bottom = height;
        float roundPx = height / 2;
        if (width > height) {
            left = (width - height) / 2;
            top = 0;
            right = left + height;
            bottom = height;
        } else if (height > width) {
            left = 0;
            top = (height - width) / 2;
            right = width;
            bottom = top + width;
            roundPx = width / 2;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(left, top, right, bottom);
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }


    public static Bitmap getblurBitmapSmile(Bitmap bkg, View view) {
        float scaleFactor = 8;
        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor), (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);
        return overlay;
    }

    //static Bitmap srcBitmap = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.video_item_top);

    /**
     * 根据原图和变长绘制圆形图片
     *
     * @param dstBitmap
     * @param patch
     * @param width
     * @param height
     * @return
     */
//    public static Bitmap compositeImages(Bitmap dstBitmap, NinePatch patch, int width, int height) {
//        Bitmap bmp = null;
//        //下面这个Bitmap中创建的函数就可以创建一个空的Bitmap
//        int dstheight = dstBitmap.getHeight();
//        bmp = Bitmap.createBitmap(width, height, dstBitmap.getConfig());
//        Paint paint = new Paint();
//        Canvas canvas = new Canvas(bmp);
//        //首先绘制第一张图片，很简单，就是和方法中getDstImage一样
//        Rect src = new Rect(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight());
//        Rect dst = new Rect(0, 0, width, height);
//        canvas.drawBitmap(srcBitmap, src, dst, paint);
//
//        patch.draw(canvas, dst);
//        //在绘制第二张图片的时候，我们需要指定一个Xfermode
//        //这里采用Multiply模式，这个模式是将两张图片的对应的点的像素相乘
//        //，再除以255，然后以新的像素来重新绘制显示合成后的图像
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        Rect dsrc = new Rect(0, dstheight / 4, width, height + dstheight / 4);
//        canvas.drawBitmap(dstBitmap, dsrc, dst, paint);
//        return bmp;
//    }


    private static Bitmap veriV = null;
    private static Bitmap veriJ = null;
    private static Bitmap badgeB = null;
    private static Bitmap veriStudent = null;


    static public class BitmapWH {
        public int m_width;

        public int m_height;
    }


    public static Bitmap DecodeResource(int n_id, Context context) {
        if (context == null) {
            return null;
        }
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(n_id);
        } catch (Throwable e) {
            is = null;
        }
        Bitmap bitmap_ret = null;
        bitmap_ret = DecodeInputStream(is);

        try {
            if (is != null) {
                is.close();
            }
            is = null;
        } catch (Exception e) {
            // TODO: handle exception
            is = null;
        }

        return bitmap_ret;
    }

    public static Bitmap DecodeInputStream(InputStream is) {
        BitmapFactory.Options opt_decord = new BitmapFactory.Options();
        opt_decord.inPurgeable = true;
        opt_decord.inInputShareable = true;
        Bitmap bitmap_ret = null;
        try {
            bitmap_ret = BitmapFactory.decodeStream(is, null, opt_decord);
        } catch (Throwable e) {
            // TODO: handle exception
            bitmap_ret = null;
        }
        return bitmap_ret;
    }

    public static Bitmap DecodeInputStream(InputStream is, int opt_scale_time) {
        BitmapFactory.Options opt_decord = new BitmapFactory.Options();
        opt_decord.inPurgeable = true;
        opt_decord.inInputShareable = true;
        opt_decord.inSampleSize = opt_scale_time;
        Bitmap bitmap_ret = null;
        try {
            bitmap_ret = BitmapFactory.decodeStream(is, null, opt_decord);
        } catch (Throwable e) {
            // TODO: handle exception
            bitmap_ret = null;
        }
        return bitmap_ret;
    }

    public static Bitmap DecodeInputStream(InputStream is, int opt_scale_time, Bitmap.Config config) {
        BitmapFactory.Options opt_decord = new BitmapFactory.Options();
        opt_decord.inPurgeable = true;
        opt_decord.inInputShareable = true;
        opt_decord.inSampleSize = opt_scale_time;
        opt_decord.inPreferredConfig = config;
        Bitmap bitmap_ret = null;
        try {
            bitmap_ret = BitmapFactory.decodeStream(is, null, opt_decord);
        } catch (Throwable e) {
            // TODO: handle exception
            bitmap_ret = null;
        }
        return bitmap_ret;
    }

    public static boolean writeAndRecycle(Bitmap bitmap, File outBitmap) {
        boolean success = writeToFile(bitmap, outBitmap);
        bitmap.recycle();
        return success;
    }

    public static boolean writeAndRecycle(Bitmap bitmap, File outBitmap, int quality) {
        boolean success = writeToFile(bitmap, outBitmap, quality);
        bitmap.recycle();
        return success;
    }

    public static boolean writeToFile(Bitmap bitmap, File outBitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return false;
        }
        boolean success = false;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outBitmap);
            success = bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.close();
        } catch (IOException e) {
            // success is already false
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    public static boolean writeToFile(Bitmap bitmap, File outBitmap, int quality) {
        return writeToFile(bitmap, outBitmap, quality, true);
    }

    public static boolean writeToFile(Bitmap bitmap, File outBitmap, int quality, boolean recycle) {
        boolean success = false;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outBitmap);
            success = bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
            out.close();
        } catch (IOException e) {
            // success is already false
        } finally {
            try {
                if (out != null) {
                    out.close();
                    if (recycle) {
                        bitmap.recycle();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    public static BitmapWH GetBitmapWH(String str_path) {
        BitmapWH b_wh_ret = new BitmapWH();
        b_wh_ret.m_height = 0;
        b_wh_ret.m_width = 0;

        FileInputStream is = null;
        try {
            is = new FileInputStream(str_path);
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            is = null;
        }
        if (is != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bm_tmp = BitmapFactory.decodeStream(is, null, options);

            try {
                is.close();
                is = null;
            } catch (Exception e) {
                // TODO: handle exception
                is = null;
            }
            b_wh_ret.m_height = options.outHeight;
            b_wh_ret.m_width = options.outWidth;
        }
        return b_wh_ret;
    }


    /**
     * 显示首页gif图
     *
     * @param imageUrl
     * @param imageView
     * @param displayImageOptions
     */
//    public static void displayGifImageView(String imageUrl, final GifImageView imageView, com.nostra13.universalimageloader.core.DisplayImageOptions displayImageOptions) {
//        if (TextUtils.isEmpty(imageUrl)) {
//            com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(imageUrl, imageView, displayImageOptions);
//            return;
//        }
//        if (imageUrl.endsWith(".gif")) {
//            if (imageUrl.startsWith("http")) {
//                com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(imageUrl, imageView, BitmapUtils.getGifImageOptions(), new com.nostra13.universalimageloader.core.listener.ImageLoadingListener() {
//                    @Override
//                    public void onLoadingStarted(String s, View view) {
//                    }
//
//                    @Override
//                    public void onLoadingFailed(String s, View view, com.nostra13.universalimageloader.core.assist.FailReason failReason) {
//                    }
//
//                    @Override
//                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
//                        if (bitmap != null && !bitmap.isRecycled()) {
//                            bitmap.recycle();
//                        }
//                        File file = com.nostra13.universalimageloader.core.ImageLoader.getInstance().getDiskCache().get(s);
//                        if (file != null) {
//                            imageView.setImageURI(Uri.fromFile(file));
//                        }
//                    }
//
//                    @Override
//                    public void onLoadingCancelled(String s, View view) {
//                    }
//                });
//
//            } else {
//                try {
//                    imageView.setImageDrawable(new GifDrawable(imageUrl));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        } else {
//            if (imageUrl.startsWith("http")) {
//                com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(imageUrl, imageView, displayImageOptions);
//            } else {
//                com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage("file://" + imageUrl, imageView, displayImageOptions);
//            }
//        }
//    }

    /**
     * 合成分享图片，返回bitmap
     *
     * @param srcBitmap1
     * @param srcBitmap2
     * @return
     */
    public static Bitmap toConformBitmap(Bitmap srcBitmap1, Bitmap srcBitmap2, Bitmap bgBitmap) {
        if (srcBitmap1 == null || srcBitmap2 == null || bgBitmap == null) {
            return null;
        }

        int dstWidth = bgBitmap.getWidth();
//		int dstHeight = DisplayUtils.getHeight();

//		if(dstHeight < srcBitmap1.getHeight()+srcBitmap2.getHeight()) {
        int dstHeight = bgBitmap.getHeight();
//		}

        Bitmap newbmp = Bitmap
                .createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newbmp);
        // draw bg into
        cv.drawBitmap(bgBitmap, 0, 0, null);
        cv.drawBitmap(srcBitmap1, 0, 0, null);
        // draw fg into

        cv.drawBitmap(srcBitmap2, 0, bgBitmap.getHeight() - srcBitmap2.getHeight() - 10, null);// 在
        // 0，0坐标开始画入fg
        // ，可以从任意位置画入
        // save all clip
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        // store
        cv.restore();// 存储

        srcBitmap1.recycle();
        srcBitmap1 = null;
        srcBitmap2.recycle();
        bgBitmap.recycle();
        srcBitmap2 = null;

        return newbmp;
    }


    public static Bitmap toConformBitmapNew(Bitmap srcTtInfoBitmap, Bitmap srcImagBitmap, Bitmap srcTbInfoBitmap, Bitmap srcBottomBitmap, Bitmap bgBitmap) {
        if (srcTtInfoBitmap == null || srcTtInfoBitmap.isRecycled()
                || srcTtInfoBitmap == null || srcTtInfoBitmap.isRecycled()
                || srcImagBitmap == null || srcImagBitmap.isRecycled()
                || srcBottomBitmap == null || srcBottomBitmap.isRecycled()
                || bgBitmap == null || bgBitmap.isRecycled()) {
            return null;
        }
        //原图片高度之和
        int srcHeightSum = srcTtInfoBitmap.getHeight() + srcImagBitmap.getHeight() + srcTbInfoBitmap.getHeight() + srcBottomBitmap.getHeight();

        //输入文件宽高
        int dstWidth = bgBitmap.getWidth();
        int dstHeight = srcHeightSum > bgBitmap.getHeight() ? srcHeightSum : bgBitmap.getHeight();

        //背景图片缩放
        float scale = (float) dstHeight / (float) bgBitmap.getHeight();

        Bitmap outputBitmap = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);

        //背景
        //cv.drawBitmap(bgBitmap, 0, 0, null);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        canvas.drawBitmap(bgBitmap, matrix, null);

        canvas.drawBitmap(srcTtInfoBitmap, 0, 0, null);
        canvas.drawBitmap(srcImagBitmap, 0, srcTtInfoBitmap.getHeight(), null);
        canvas.drawBitmap(srcTbInfoBitmap, 0, srcTtInfoBitmap.getHeight() + srcImagBitmap.getHeight(), null);

        //cv.drawBitmap(srcBottomBitmap, 0, bgBitmap.getHeight()- srcBottomBitmap.getHeight() - 10, null);// 在
        canvas.drawBitmap(srcBottomBitmap, 0, dstHeight - srcBottomBitmap.getHeight(), null);

        canvas.save(Canvas.ALL_SAVE_FLAG);// 保存
        canvas.restore();// 存储
        srcTtInfoBitmap.recycle();
        srcTtInfoBitmap = null;

        srcImagBitmap.recycle();
        srcImagBitmap = null;

        srcTbInfoBitmap.recycle();
        srcTbInfoBitmap = null;

        srcBottomBitmap.recycle();
        srcBottomBitmap = null;

        bgBitmap.recycle();
        bgBitmap = null;

        return outputBitmap;
    }

    /**
     * 横向合并多张Bitmap
     *
     * @param bitmaps
     * @return
     */
    public static Bitmap addBitmapHorizontal(int periodMargin, int leftMargin, boolean isRecycle, Bitmap... bitmaps) {
        if (bitmaps == null || bitmaps.length == 0) {
            return null;
        }
        int width = 0;
        int height = 0;
        int leng = bitmaps.length;
        for (int i = 0; i < leng; i++) {
            if (bitmaps[i] != null) {
                width += bitmaps[i].getWidth();
                width += periodMargin;
                height = Math.max(height, bitmaps[i].getHeight());
            }
        }
        width -= periodMargin;
        width += leftMargin;
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        int left = 0;
        for (int i = 0; i < leng; i++) {
            if (i > 0) {
                if (bitmaps[i - 1] != null) {
                    left += bitmaps[i - 1].getWidth();
                    left += periodMargin;
                }
            }

            if (bitmaps[i] != null) {
                canvas.drawBitmap(bitmaps[i], left + leftMargin, (height - bitmaps[i].getHeight()) / 2, null);
            }

            if (isRecycle) {
                recycleBitmap(bitmaps[i]);
            }
        }
        return result;
    }

    /**
     * 竖向合并多张Bitmap
     *
     * @param margin
     * @param bitmaps
     * @return
     */
    public static Bitmap addBitmapVertical(int margin, boolean isRecycle, Bitmap... bitmaps) {
        if (bitmaps == null || bitmaps.length == 0) {
            return null;
        }
        int width = 0;
        int height = 0;
        int leng = bitmaps.length;
        for (int i = 0; i < leng; i++) {
            if (bitmaps[i] != null) {
                height += bitmaps[i].getHeight();
                height += margin;
                width = Math.max(width, bitmaps[i].getWidth());
            }
        }
        height -= margin;
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        int top = 0;
        for (int i = 0; i < leng; i++) {
            if (i > 0) {
                if (bitmaps[i - 1] != null) {
                    top += bitmaps[i - 1].getHeight();
                    top += margin;
                }
            }

            if (bitmaps[i] != null) {
                canvas.drawBitmap(bitmaps[i], (width - bitmaps[i].getWidth()) / 2, top, null);
            }

            if (isRecycle) {
                recycleBitmap(bitmaps[i]);
            }
        }
        return result;
    }

    /**
     * 横向拼接
     * <功能详细描述>
     *
     * @param first
     * @param second
     * @return
     */
    public static Bitmap addBitmapHorizontal(Bitmap first, Bitmap second, boolean isRecycle) {
        int width = first.getWidth() + second.getWidth();
        int height = Math.max(first.getHeight(), second.getHeight());
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(first, 0, 0, null);
        canvas.drawBitmap(second, first.getWidth(), 0, null);

        if (isRecycle) {
            recycleBitmap(first);
            recycleBitmap(second);
        }
        return result;
    }

    /**
     * 纵向拼接
     * <功能详细描述>
     *
     * @param first
     * @param second
     * @return
     */
    public static Bitmap addBitmapVertical(Bitmap first, Bitmap second, boolean isRecycle) {
        int width = Math.max(first.getWidth(), second.getWidth());
        int height = first.getHeight() + second.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(first, 0, 0, null);
        canvas.drawBitmap(second, first.getHeight(), 0, null);

        if (isRecycle) {
            recycleBitmap(first);
            recycleBitmap(second);
        }
        return result;
    }

    private static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    private static final int watermarkerTop = 100;
    private static final int watermarkerRight = 10;

    public static int LiveCaptureWidth;
    public static int LiveCaptureHeight;


    /**
     * 将view 转换成 bitmap
     *
     * @param view
     * @return
     */
    public static Bitmap convertViewToBitmap(View view) {
        view.setDrawingCacheEnabled(true);//buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }


    /**
     * 计算图片的大小
     *
     * @param bitmap
     * @return
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return 0;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {// API 12
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    /**
     * caculate the bitmap sampleSize
     *
     * @return
     */
    public final static int caculateInSampleSize(BitmapFactory.Options options, int rqsW) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (rqsW == 0) return 1;
        if (width > rqsW) {
            inSampleSize = Math.round((float) width / (float) rqsW);
        }
        return inSampleSize;
    }

    /**
     * 压缩指定路径的图片，并得到图片对象
     *
     * @param path bitmap source path
     * @return Bitmap {@link android.graphics.Bitmap}
     */
    public final static Bitmap compressBitmap(String path, int rqsW) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        final int height = options.outHeight;
        final int width = options.outWidth;

        if (rqsW == 0) {
            rqsW = 1;
        }

        options.inSampleSize = Math.round((float) width / (float) rqsW);
        options.inJustDecodeBounds = false;
        Bitmap tempBitmap = BitmapFactory.decodeFile(path, options);
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(tempBitmap, rqsW, rqsW * height / width,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        return bitmap;
    }

    /**
     * 缩放Bitmap
     *
     * @param width  缩放后的宽度
     * @param height 缩放后的高度
     */
    public static Bitmap getResizedBitmap(Bitmap bitmap, int width, int height) {
        Matrix matrix = new Matrix();
        if (matrix != null) {
            matrix.postScale((float) width / (float) bitmap.getWidth(), (float) height / (float) bitmap.getHeight()); //长和宽放大缩小的比例
        }

        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    /**
     * 缩放Bitmap
     *
     * @param scale 缩放比例
     */
    public static Bitmap getResizedBitmap(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();
        if (matrix != null) {
            matrix.postScale(scale, scale); //长和宽放大缩小的比例
        }

        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    /**
     * 将bitmap缓存到本地
     */
    public static void saveBitmapToDisk(String fileName, Bitmap bitmap, int sampleSize) {
        if (!isBitmapAvailable(bitmap)) {
            return;
        }
        if (TextUtils.isEmpty(fileName)) {
            return;
        }
        saveBitmapToDisk(new File(fileName), bitmap, sampleSize);
    }

    /**
     * 将bitmap缓存到本地
     */
    public static void saveBitmapToDisk(String fileName, Bitmap bitmap) {
        saveBitmapToDisk(fileName, bitmap, 100);
    }

    /**
     * 将bitmap缓存到本地
     */
    public static void saveBitmapToDisk(File file, Bitmap bitmap) {
        saveBitmapToDisk(file, bitmap, 100);
    }

    /**
     * 将bitmap缓存到本地
     */
    public static void saveBitmapToDisk(File file, Bitmap bitmap, int sampleSize) {
        if (!isBitmapAvailable(bitmap)) {
            return;
        }
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, sampleSize, out);
            out.flush();
            out.close();
        } catch (Throwable e) {
            e.printStackTrace();
            file.delete();//出错了就删除
        }
    }

    /**
     * 判断bitmap是否可用 不能为空 不能是已经被回收的 isRecycled返回false
     *
     * @param bitmap
     * @return
     */
    public static boolean isBitmapAvailable(Bitmap bitmap) {
        if (null == bitmap || "".equals(bitmap) || bitmap.isRecycled()) {// 如果为null或者是已经回收了的就证明是不可用的
            return false;
        }
        return true;
    }

    /**
     * 从本地获取bitmap
     */
    public static Bitmap getBitmapFromDisk(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        return getBitmapFromDisk(new File(fileName));
    }

    /**
     * 从本地获取bitmap
     */
    public static Bitmap getBitmapFromDisk(File file) {
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            if (file.exists()) {
                fis = new FileInputStream(file);
                bitmap = BitmapFactory.decodeStream(fis);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError error) {//这里是有可能内存溢出的
            error.printStackTrace();
            return null;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public static String getSquarePic(String picPath) {
        try {
            if (TextUtils.isEmpty(picPath)) return "";
            Bitmap resBitmap = getBitmapFromDisk(picPath);
            if (!isBitmapAvailable(resBitmap)) return "";
            int width = resBitmap.getWidth();
            int height = resBitmap.getHeight();
            if (width > height) return "";//高要求是要比宽大的
            if (width == height) return picPath;
            int cut = (height - width) / 2;
            int y = cut;
            int bit_width = width;
            int bit_height = width;
            Bitmap desBitmap = Bitmap.createBitmap(resBitmap, 0, y, bit_width, bit_height);
            File srcFile = new File(picPath);
            String srcFolder = srcFile.getParentFile().getAbsolutePath();
            String srcName = srcFile.getName();
            String destPath = srcFolder + File.separator + "square_" + srcName;
            saveBitmapToDisk(destPath, desBitmap);
            return destPath;
        } catch (Throwable e) {
            return "";
        }
    }

    public static Bitmap compressBmpFromFile(String srcPath, float hh, float ww) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;//只读边,不读内容
        BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置采样率

        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;//该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收

        return BitmapFactory.decodeFile(srcPath, newOpts);
    }

    /**
     * 图片按比例大小压缩方法
     *
     * @param image （根据Bitmap图片压缩）
     * @return
     */
    public static Bitmap compressScale(Bitmap image, float hight, float width) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        // 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
        if (baos.toByteArray().length / 1024 > 500) {
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        Log.i(TAG, w + "---------------" + h);
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        // float hh = 800f;// 这里设置高度为800f
        // float ww = 480f;// 这里设置宽度为480f
        float hh = 512f;
        float ww = 512f;
        if (hight != 0) {
            hh = hight;
            ww = width;
            LogUtil.i("htmlActivity", "upLoadImagesCompress url ");
        }
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) { // 如果高度高的话根据高度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be; // 设置缩放比例
        // newOpts.inPreferredConfig = Config.RGB_565;//降低图片从ARGB888到RGB565

        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);

        return compressImageScale(bitmap);// 压缩好比例大小后再进行质量压缩

    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImageScale(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;

        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
     */
    @TargetApi(19)
    public static String getImageAbsolutePath(Context context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            //计算缩放比例
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * bitmap压缩
     *
     * @param bitmap
     * @return
     */
    public static Bitmap compressImage(Bitmap bitmap, int speed, int size) {

        try {
            double targetwidth = Math.sqrt(size * 1024);
            if (bitmap != null && bitmap.getWidth() > targetwidth || bitmap.getHeight() > targetwidth) {
                // 创建操作图片用的matrix对象
                Matrix matrix = new Matrix();
                // 计算宽高缩放率
                double x = Math.max(targetwidth / bitmap.getWidth(), targetwidth
                        / bitmap.getHeight());
                // 缩放图片动作
                matrix.postScale((float) x, (float) x);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 缩放图片
    public static Bitmap zoomImg(String img, int newWidth, int newHeight) {
        try {
            // 图片源
            Bitmap bm = BitmapFactory.decodeFile(img);
            if (null != bm && !bm.isRecycled()) {
                return zoomImg(bm, newWidth, newHeight);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap zoomImg(Context context, String img, int newWidth, int newHeight) {
// 图片源
        try {
            Bitmap bm = BitmapFactory.decodeStream(context.getAssets()
                    .open(img));
            if (null != bm && !bm.isRecycled()) {
                return zoomImg(bm, newWidth, newHeight);
            }
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    // 缩放图片
    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        try {
            if (bm == null || bm.isRecycled()) {
                return null;
            }
            // 获得图片的宽高
            int width = bm.getWidth();
            int height = bm.getHeight();
            // 计算缩放比例
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // 取得想要缩放的matrix参数
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // 得到新的图片
            Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
            return newbm;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int calculateLocalInSampleSize(BitmapFactory.Options options,
                                                 int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            //计算缩放比例
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 从本地获取bitmap
     *
     * @param imgUri
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromLocal(String imgUri,
                                                      int reqWidth, int reqHeight) {
        if (reqWidth == 0 || reqHeight == 0 || TextUtils.isEmpty(imgUri)) {
            return null;
        }
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgUri, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateLocalInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imgUri, options);
    }

    public static Bitmap stringtoBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }


    public String bitmaptoString(Bitmap bitmap) {
//将Bitmap转换成字符串
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }
}
