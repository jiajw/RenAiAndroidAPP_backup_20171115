package com.yousails.chrenai.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    /**
     * 隐藏文件夹的名字
     */
    private static final String INV_FLODER_NAME = "inv_folder";

    /**
     * log文件夹的名字
     */
    private static final String LOG_FLODER_NAME = "log_folder";

    /**
     * 保存的图片文件夹名字
     */
    private static final String IMAGE_FLODER_NAME = "images";

    private static String BASE_ROOT_PATH = null;

    private static String PROJECT_FILE_NAME = "chrenai/";

    public static String getRootPath(Context context) {
        if (BASE_ROOT_PATH == null) {
            initRootPath(context);
        }
        return BASE_ROOT_PATH;
    }

    public static String getLogPath(Context context) {
        return getRootPath(context) + LOG_FLODER_NAME + File.separator;
    }

    public static String getImagePath(Context context) {
        String path = getRootPath(context) + IMAGE_FLODER_NAME + File.separator;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getInnerFloder(Context context) {
        String dir;
        if (isAvailable(context)) {
            try {
                dir = context.getExternalCacheDir() + LOG_FLODER_NAME + File.separator;
            } catch (SecurityException e) {
                dir = getRootPath(context) + LOG_FLODER_NAME + File.separator;
                e.printStackTrace();
            }
        } else {
            dir = getRootPath(context) + LOG_FLODER_NAME + File.separator;
        }

        File f = new File(dir);
        if (f != null && !f.exists()) {
            f.mkdirs();
        }

        return dir;
    }

    /**
     * 创建目录
     *
     * @param dir
     */
    public static void createDir(String dir) {
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    /**
     * 创建文件
     *
     * @param file
     */
    public static void createFile(String file) {
        File f = new File(file);
        if (f != null && !f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建nomedia文件，屏蔽媒体文件
     *
     * @param path
     */
    public static void createNoMediaFile(String path) {
        String file = path + File.separator + ".nomedia";
        createFile(file);
    }

    /**
     * 删除nomedia
     *
     * @param path
     */
    public static void deleteNoMediaFile(String path) {
        String filePath = path + File.separator + ".nomedia";
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 检查文件md5
     *
     * @param file
     * @param md5
     */
    public static boolean checkFileMd5(File file, String md5) {
        if (file != null && file.exists()) {
            String fileMd5 = SecurityUtils.encryptMD5(file);


            if (fileMd5 != null && fileMd5.equalsIgnoreCase(md5)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 缩略图路径（不需要展示在图库）
     *
     * @param context
     * @return
     */
    public static String getNomediaFloder(Context context) {
        String dir;
        if (isAvailable(context)) {
            dir = context.getExternalCacheDir() + INV_FLODER_NAME;
        } else {
            dir = getRootPath(context) + INV_FLODER_NAME;
        }

        File f = new File(dir);
        if (f != null && !f.exists()) {
            f.mkdirs();
        }
        createNoMediaFile(dir);

        return dir;
    }

    public static void makeDir(String dir) {
        File f = new File(dir);
        if (f != null && !f.exists()) {
            f.mkdirs();
        }
    }

    /**
     * 需要保存图片的路径
     *
     * @return
     */
    public static String getShareFilePath(Context context) {
        //图片名称
        String filename = "share_img.jpg";

        String dir = getRootPath(context) + INV_FLODER_NAME;

        File fileDir = new File(dir);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
            createNoMediaFile(dir);
        }

        return dir + File.separator + filename;
    }

    public static boolean copyFile(File src, File dest) {
        try {
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            if (!dest.exists()) {
                dest.createNewFile();
            }

            FileOutputStream outputStream = new FileOutputStream(dest);
            FileInputStream inputStream = new FileInputStream(src);
            DataInputStream dataInput = new DataInputStream(inputStream);
            DataOutputStream dataOutput = new DataOutputStream(outputStream);
            byte[] wxj = new byte[1024];
            int length = dataInput.read(wxj);
            while (length != -1) {
                dataOutput.write(wxj, 0, length);
                length = dataInput.read(wxj);
            }
            outputStream.close();
            inputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 保存文件
     *
     * @param path
     * @param data
     * @return
     */
    public static boolean saveFile(String path, byte[] data) {
        try {
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file); // Get file output stream
            fos.write(data); // Written to the file
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除缓存
     */
    public static void clearCache(Context context) {
        try {
            clearFolder(getNomediaFloder(context));
            CleanUtils.cleanInternalDefinedCache(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除图片
     */
    public static void clearFolder(String path) {
        try {
            File file = new File(path);
            if (file.isFile()) {
                file.delete();
            } else {
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    for (File f : files) {
                        if (f.isDirectory()) {
                            clearFolder(f.getAbsolutePath());
                        } else {
                            f.delete();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件或者目录
     *
     * @param filepath 要删除的文件路径
     */
    public static void deleteFile(String filepath) {
        File file = new File(filepath);
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    if (files.length > 0) {
                        File[] delFiles = file.listFiles();
                        if (delFiles != null && delFiles.length > 0) {//fix  delFiles 为空导致空指针
                            for (File delFile : delFiles) {
                                deleteFile(delFile.getAbsolutePath());
                            }
                        }
                    }
                }
            }
            file.delete();
        }
    }

    /**
     * 删除文件或者目录
     *
     * @param file 要删除的文件
     */
    public static void deleteFile(File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                File[] filelist = file.listFiles();
                if (filelist != null && filelist.length > 0) {
//                    File[] delFiles = file.listFiles();
                    for (File delFile : filelist) {
                        if (delFile.exists())
                            deleteFile(delFile);
                    }
                }
            }
            file.delete();
        }
    }

    public static void saveBitmap(Bitmap bitmap, String filePath) {
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(filePath));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }


    public static File getLatestFile(File[] files) {
        if (files == null || files.length == 0) {
            return null;
        } else {
            long lastMod = Long.MIN_VALUE;
            File choice = null;
            for (File file : files) {
                if (file.isDirectory()) {
                    continue;
                } else {
                    if (file.lastModified() > lastMod) {
                        choice = file;
                        lastMod = file.lastModified();
                    }
                }
            }
            return choice;
        }
    }

    public static void zipFile(File destZip, File... files) throws IOException {
        if (files == null || files.length == 0) {
            return;
        }
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(destZip));
        for (File file : files) {
            if (!file.isDirectory()) {
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipOutputStream.putNextEntry(zipEntry);
                FileInputStream inputStream = new FileInputStream(file);
                copyStream(inputStream, zipOutputStream);
                inputStream.close();
                zipOutputStream.closeEntry();
            }
        }
        zipOutputStream.close();
    }

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        if (in == null || out == null) {
            return;
        }
        byte[] buffer = new byte[4096];
        int len;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
    }

    public static boolean unzip(String zipFileString, String outPathString) {
        boolean b_ret = true;
        try {
            ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
            ZipEntry zipEntry;
            String szName = "";
            while ((zipEntry = inZip.getNextEntry()) != null) {
                szName = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    // get the folder name of the widget
                    szName = szName.substring(0, szName.length() - 1);
                    File folder = new File(outPathString + File.separator + szName);
                    folder.mkdirs();
                } else {
                    int n_last_index = szName.lastIndexOf("/");
                    if (n_last_index != -1) {
                        String str_folder_name = outPathString + File.separator + szName.substring(0, n_last_index);
                        File folder = new File(str_folder_name);
                        if (!folder.isDirectory()) {
                            folder.mkdirs();
                        }
                    }
                    File file = new File(outPathString + File.separator + szName);
                    file.createNewFile();
                    // get the output stream of the file
                    FileOutputStream out = new FileOutputStream(file);
                    int len;
                    byte[] buffer = new byte[1024];
                    // read (len) bytes into buffer
                    while ((len = inZip.read(buffer)) != -1) {
                        // write (len) byte from buffer at the position 0
                        out.write(buffer, 0, len);
                        out.flush();
                    }
                    out.close();
                }
            }
            inZip.close();
        } catch (Throwable e) {
            b_ret = false;
        }
        return b_ret;
    }

    public static void writeFileSdcard(String filePath, String message, boolean append) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath, append);
            byte[] bytes = message.getBytes();
            fos.write(bytes);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initRootPath(Context context) {
        if (BASE_ROOT_PATH == null) {
            File file = null;
            try {
                file = Environment.getExternalStorageDirectory();
                if (file.exists() && file.canRead() && file.canWrite()) {
                    //如果可读写，则使用此目录
                    String path = file.getAbsolutePath();
                    if (path.endsWith("/")) {
                        BASE_ROOT_PATH = file.getAbsolutePath() + PROJECT_FILE_NAME;
                    } else {
                        BASE_ROOT_PATH = file.getAbsolutePath() + File.separator + PROJECT_FILE_NAME;
                    }
                }
            } catch (Exception e) {

            }
            if (BASE_ROOT_PATH == null) {
                //如果走到这里，说明外置sd卡不可用
                if (context != null) {
                    file = context.getFilesDir();
                    String path = file.getAbsolutePath();
                    if (path.endsWith("/")) {
                        BASE_ROOT_PATH = file.getAbsolutePath() + PROJECT_FILE_NAME;
                    } else {
                        BASE_ROOT_PATH = file.getAbsolutePath() + File.separator + PROJECT_FILE_NAME;
                    }
                } else {
                    BASE_ROOT_PATH = "/sdcard/" + PROJECT_FILE_NAME;
                }
            }
        }
        File file = new File(BASE_ROOT_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static boolean isAvailable(Context mContext) {
        boolean available = true;
        if (!existSDCard()) {
            available = false;
//            Toast.makeText(mContext, R.string.sdcard_not_exist, Toast.LENGTH_SHORT);
        } else if (getSDFreeSize() < 20) {
            available = false;
//            Toast.makeText(mContext, R.string.sdcard_full, Toast.LENGTH_SHORT);
        }

        return available;

    }

    public static long getSDFreeSize() {
        //取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        //返回SD卡空闲大小
        //return freeBlocks * blockSize;  //单位Byte
        //return (freeBlocks * blockSize)/1024;   //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; //单位MB
    }

    public static long getSDAllSize() {
        //取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //获取所有数据块数
        long allBlocks = sf.getBlockCount();
        //返回SD卡大小
        //return allBlocks * blockSize; //单位Byte
        //return (allBlocks * blockSize)/1024; //单位KB
        return (allBlocks * blockSize) / 1024 / 1024; //单位MB
    }

    public static boolean existSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    public static boolean isCanCreateFile(String file) {
        boolean isSuc = false;
        File f = new File(file);
        if (f != null && !f.exists()) {
            try {
                f.createNewFile();
                isSuc = true;
            } catch (IOException e) {
                e.printStackTrace();
                isSuc = false;
            }
        }

        return isSuc;
    }

    public static long getFileSize(String path) {
        if (TextUtils.isEmpty(path)) {
            return -1;
        }

        File file = new File(path);
        return (file.exists() && file.isFile() ? file.length() : -1);
    }

    /**
     * 保存弹幕
     *
     * @param context
     */
    public static void saveDanmuList(Context context, JSONObject json) {

        if (FileUtils.isAvailable(context)) {//如果挂载了SD卡
            String dir = FileUtils.getInnerFloder(context) + "/danmu/";

            File fileDir = new File(dir);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            File file = new File(dir, "danmu");
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(file);
                if (json != null) {
                    String str = json.toString();
                    byte[] bytes = str.getBytes();
                    fileOutputStream.write(bytes);
                    fileOutputStream.flush();
                }
            } catch (Exception e) {
                file.delete();
                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static boolean renameFile(String oldPath, String newPath) {
        try {
            File file = new File(oldPath);
            file.renameTo(new File(newPath));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static File getCacheDir(Context context, String uniqueName) {

        final String cachePath = context.getCacheDir().getPath();
        return new File(cachePath + File.separator + uniqueName);
    }
}
