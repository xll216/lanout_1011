
package com.lanou.lilyxiao.myapplication.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.nio.channels.FileChannel;

import android.os.Environment;
import android.os.StatFs;

/**
 * 文件操作工具类。
 */
public class FileUtil {
    /**
     * 获取路径对应文件系统的可用空间。
     *
     * @param path 文件路径
     * @return 可用空间
     */
    public static long getAvailStorage(String path) {
        try {
            StatFs stfs = new StatFs(path);
            long blockSize = stfs.getBlockSize();
            long availBlocks = stfs.getAvailableBlocks();

            return (blockSize * availBlocks);
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 获取路径对应文件系统的总大小。
     *
     * @param path 文件路径
     * @return 总大小
     */
    public static long getTotalStorge(String path) {
        try {
            StatFs stfs = new StatFs(path);
            long blockSize = stfs.getBlockSize();
            long totalBlocks = stfs.getBlockCount();

            return (blockSize * totalBlocks);
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 从输入流中读取一个长度较短的字符串，前提是此字符串是由 @see
     * {@link #writeShortString(OutputStream, String)}
     * 方法写入的。工作原理：先读取一个byte获得字符串byte数，再读取对应长度的byte构成字符串。
     *
     * @param in 输入流
     * @return 读取的字符串
     * @throws IOException
     */
    public static String readShortString(InputStream in) throws Exception {
        int len = in.read();
        if (len <= 0)
            return "";
        byte[] buf = new byte[len];
        in.read(buf, 0, len);
        return new String(buf);
    }

    /**
     * 向输出流中写入一个长度较短的字符串。工作原理：先写入一个byte表示字符串byte数，再写入字符串的bytes。
     *
     * @param out 输出流
     * @param str 要写入的字符串
     * @throws IOException
     */
    public static void writeShortString(OutputStream out, String str) throws Exception {
        if (str == null || str.length() == 0) {
            out.write(0);
            return;
        }
        byte[] buf = str.getBytes();
        int len = buf.length;
        if (len > 255) {
            out.write(0);
            return;
        }
        out.write(len);
        out.write(buf);
    }

    /**
     * 从输入流中读取一个长字符串，前提是此字符串是由 @see
     * 方法写入的。工作原理：先读取一个int（4
     * byte）获得字符串byte数，再读取对应长度的byte构成字符串。
     *
     * @param in 输入流
     * @return 读取的字符串
     * @throws IOException
     */
    public static String readLongString(DataInputStream in) throws Exception {
        int len = in.readInt();
        if (len <= 0)
            return "";
        byte[] buf = new byte[len];
        in.read(buf, 0, len);
        return new String(buf);
    }

    /**
     * 向输出流中写入一个长字符串。工作原理：先写入一个int（4 byte）表示字符串byte数，再写入字符串的bytes。
     *
     * @param out 输出流
     * @param str 要写入的字符串
     * @throws IOException
     */
    public static void writeLongString(DataOutputStream out, String str) throws Exception {
        if (str == null || str.length() == 0) {
            out.writeInt(0);
            return;
        }
        byte[] buf = str.getBytes();
        int len = buf.length;
        out.writeInt(len);
        out.write(buf);
    }

    public static boolean writeObject(File file, Object object) {
        try {
            FileOutputStream fs = new FileOutputStream(file);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(object);
            os.flush();
            os.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Object readObject(File file) {
        try {
            FileInputStream fs = new FileInputStream(file);
            ObjectInputStream os = new ObjectInputStream(fs);
            Object object = os.readObject();
            os.close();
            return object;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将输入流中的内容完全的读取到内存buffer中
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static byte[] readFull(InputStream is) throws IOException {
        byte[] buffer = new byte[10240];
        int readLen = -1;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            while ((readLen = is.read(buffer)) != -1) {
                bos.write(buffer, 0, readLen);
            }
        } catch (EOFException e) {
            // 发生文件结尾异常 正常返回
            return bos.toByteArray();
        } finally {
            bos.close();
        }

        return bos.toByteArray();
    }


    /**
     * 递归删除文件（夹）。
     *
     * @return 删除的文件(夹)的大小
     */
    public static long deleteFiles(String dir) {
        File file = new File(dir);
        final File to = new File(dir + System.currentTimeMillis());
        file.renameTo(to);
        return deleteFiles(to);
    }


    /**
     * 递归删除文件（夹）。
     *
     * @param f
     * @return 删除的文件(夹)的大小
     */
    public static long deleteFiles(File f) {
        if (null == f || !f.exists()) {
            return 0;
        }

        long freeSize = 0;

        if (f.isDirectory()) {
            File[] childs = f.listFiles();
            if (childs != null) {
                for (File child : childs) {
                    freeSize += deleteFiles(child);
                }
            }
        } else {
            freeSize = f.length();
        }

        f.delete();

        return freeSize;
    }


    /**
     * 删除文件夹下指定扩展名的所有文件。
     *
     * @param extension 文件扩展名
     * @return 删除的文件(夹)的大小
     */
    public static long deleteFilesByExtension(String dir, String extension) {
        File f = new File(dir);
        return deleteFilesByExtension(f, extension);
    }

    /**
     * 删除文件夹下指定扩展名的所有文件。
     *
     * @param f
     * @param extension 文件扩展名
     * @return 删除的文件(夹)的大小
     */
    public static long deleteFilesByExtension(File f, String extension) {
        if (null == f || !f.exists()) {
            return 0;
        }

        long freeSize = 0;

        if (f.isDirectory()) {
            File[] childs = f.listFiles();
            if (childs != null) {
                for (File child : childs) {
                    if (child.isFile() && child.getName().endsWith(extension)) {
                        freeSize = child.length();
                        child.delete();
                    }
                }
            }
        }

        return freeSize;
    }

    /**
     * 删除文件夹下指定前缀的所有文件。
     *
     * @param prefix 文件扩展名
     * @return 删除的文件(夹)的大小
     */
    public static long deleteFilesByPrefix(String dir, String prefix) {
        File f = new File(dir);
        return deleteFilesByPrefix(f, prefix);
    }

    /**
     * 删除文件夹下指定前缀的所有文件。
     *
     * @param f
     * @param prefix 文件扩展名
     * @return 删除的文件(夹)的大小
     */
    public static long deleteFilesByPrefix(File f, String prefix) {
        if (null == f || !f.exists()) {
            return 0;
        }

        long freeSize = 0;

        if (f.isDirectory()) {
            File[] childs = f.listFiles();
            if (childs != null) {
                for (File child : childs) {
                    if (child.isFile() && child.getName().startsWith(prefix)) {
                        freeSize = child.length();
                        child.delete();
                    }
                }
            }
        }

        return freeSize;
    }

    /**
     * 移动文件
     *
     * @param srcFileName 源文件完整路径
     * @param destDirName 目的目录完整路径
     * @return 文件移动成功返回true，否则返回false
     */
    public static long moveFile(String srcFileName, String destDirName) throws IOException {
        File srcFile = new File(srcFileName);
        if (!srcFile.exists() || !srcFile.isFile()) {
            return 0;
        }

        String srcName = srcFile.getName();
        File destDir = new File(destDirName);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        final long size = srcFile.length();

        // 跳过destdir已经存在文件
        File destFile = new File(destDir + "/" + srcName);
        if (destFile.exists()) {
            FileUtil.deleteFiles(srcFile);
            return size;
        }

        FileInputStream fis = new FileInputStream(srcFile);
        FileOutputStream fos = new FileOutputStream(destDir + "/" + srcName);
        int readLen = -1;

        byte[] buffer = new byte[20480];
        while ((readLen = fis.read(buffer)) != -1) {
            fos.write(buffer, 0, readLen);
        }

        fos.flush();
        fos.close();
        fis.close();

        // 删除源文件
        FileUtil.deleteFiles(srcFile);

        return size;
    }


    /**
     * 复制文件 复制离线地图用，需要对离线地图进行版本比较
     *
     * @param srcFileName 源文件完整路径
     * @param destDirName 目的目录完整路径
     * @return 文件复制成功返回true，否则返回false
     */
    public static long mergeFile(String srcFileName, String destDirName, boolean srcDel,
                                 DestFileExistHandler handler) throws IOException {
        File srcFile = new File(srcFileName);
        if (!srcFile.exists() || !srcFile.isFile()) {
            return 0;
        }

        String srcName = srcFile.getName();
        File destDir = new File(destDirName);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        final long size = srcFile.length();

        // 跳过destdir已经存在文件
        File destFile = new File(destDir + "/" + srcName);
        if (destFile.exists()) {
            if (handler != null) {
                boolean needCopy = handler.needCopy(srcFile, destFile);
                if (!needCopy) {
                    if (srcDel) {
                        // 删除源文件
                        FileUtil.deleteFiles(srcFile);
                    }
                    return size;
                } else {
                    FileUtil.deleteFiles(destFile);
                }
            }
        }

        FileInputStream fis = new FileInputStream(srcFile);
        FileOutputStream fos = new FileOutputStream(destDir + "/" + srcName);
        int readLen = -1;

        byte[] buffer = new byte[20480];
        while ((readLen = fis.read(buffer)) != -1) {
            fos.write(buffer, 0, readLen);
        }

        fos.flush();
        fos.close();
        fis.close();

        if (srcDel) {
            // 删除源文件
            FileUtil.deleteFiles(srcFile);
        }

        return size;
    }

    /**
     * 获取一个文件 |文件夹大小
     *
     * @param file
     * @return, 文件夹大小bytes
     */
    public static long getFileLen(File file) {
        if (null == file || !file.exists()) {
            return 0L;
        }

        // 如果是目录则递归计算其内容的总大小，如果是文件则直接返回其大小
        if (file.isDirectory()) {
            // 因为卫星图会非常多，导致在计算大小的时候会很慢，使用估算的方法，文件数*固定大小（25k）
            if (file.getName().equalsIgnoreCase("sat")) {
                long num = 0;
                File[] flAll = file.listFiles();
                if (flAll != null) {
                    num = flAll.length;
                }

                return 25 * 1024 * num;
            }
            // 获取文件大小
            File[] fl = file.listFiles();
            if (fl == null || fl.length <= 0) {
                return 0;
            }

            long len = 0;
            for (File f : fl) {
                len += getFileLen(f);
            }
            return len;
        } else {
            return file.length();
        }
    }


    public static boolean reName(String oldPath, String newPath) {
        File file = new File(oldPath);
        File newFile = new File(newPath);
        if (newFile.exists()) {
            deleteFiles(newFile);
        }
        return file.renameTo(new File(newPath));
    }

    public interface FilePathFilter {
        public boolean accept(String path);
    }

    public static void copyFiles(String from, String to) {
        copyFiles(from, to, null);
    }

    public static void copyFiles(String from, String to, FilePathFilter filter) {
        if (isEmpty(from) || isEmpty(to)) {
            return;
        }

        File file = new File(from);
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            copyFile(from, to, filter);
            return;
        }

        String[] paths = file.list();
        if (paths == null) {
            return;
        }
        for (String sub : paths) {
            copyFiles(sub, to + File.separator + getPathName(sub), filter);
        }
    }

    private static void copyFile(String fromPath, String toPath, FilePathFilter filter) {
        if (isEmpty(fromPath) || isEmpty(toPath)) {
            return;
        }
        if (filter != null && !filter.accept(fromPath)) {
            return;
        }

        try {
            File fromFile = new File(fromPath);
            if (!fromFile.exists() || !fromFile.isFile()) {
                return;
            }

            File toFile = new File(toPath);
            if (toFile.exists()) {
                if (fromFile.length() == toFile.length()) {
                    // consider as the same.
                    return;
                } else {
                    // delete it in case of folder.
                    delete(toFile);
                }
            }

            File toParent = toFile.getParentFile();
            if (toParent.isFile()) {
                delete(toParent);
            }
            if (!toParent.exists() && !toParent.mkdirs()) {
                return;
            }

            FileInputStream fileInputStream = new FileInputStream(fromPath);
            FileOutputStream fileOutputStream = new FileOutputStream(toPath);

            FileChannel inc = (fileInputStream).getChannel();
            FileChannel ouc = (fileOutputStream).getChannel();

            ouc.transferFrom(inc, 0, inc.size());

            fileInputStream.close();
            fileOutputStream.close();
            inc.close();
            ouc.close();

        } catch (Throwable e) {
            e.printStackTrace();
            // exception occur, delete broken file.
            delete(toPath);
        }
    }


    public static void delete(String path) {
        delete(path, false);
    }

    public static void delete(String path, boolean ignoreDir) {
        if (isEmpty(path)) {
            return;
        }
        delete(new File(path), ignoreDir);
    }

    public static void delete(File file) {
        delete(file, false);
    }

    public static void delete(File file, boolean ignoreDir) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
            return;
        }

        File[] fileList = file.listFiles();
        if (fileList == null) {
            return;
        }

        for (File f : fileList) {
            delete(f, ignoreDir);
        }
        // delete the folder if need.
        if (!ignoreDir) file.delete();
    }


    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    private static String getPathName(String path) {
        if (isEmpty(path)) {
            return "";
        }
        int separatorIndex = path.lastIndexOf(File.separator);
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1,
                path.length());
    }

    /**
     * 创建文件夹
     *
     * @param path 文件夹路径（不能为相对路径）
     * @return 成功返回true, 失败返回false
     */
    public static boolean createDir(String path) {
        boolean ret = false;
        try {
            File file = new File(path);
            if (!file.exists()) {
                ret = file.mkdirs();
                if (!ret) {
                }
            } else
                ret = true;
        } catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    /**
     * 删除一个文件夹下的所有文件，如果文件夹下还有文件夹，则会递归删除
     *
     * @param path 文件夹
     * @return 成功返回true, 失败返回false
     */
    public static boolean delAllFile(String path) {
        boolean bea = false;
        File file = new File(path);
        if (!file.exists()) {
            return bea;
        }
        if (!file.isDirectory()) {
            return bea;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            } else if (temp.isDirectory()) {
                delAllFile(path + File.separator + tempList[i]);
                delDir(path + File.separator + tempList[i], true);
                bea = true;
            }
        }
        return bea;
    }

    /**
     * 删除指定的文件
     *
     * @param file 文件名，包括路径
     */
    public static void delFile(String file) {
        try {
            File f = new File(file);
            f.delete();
        } catch (Exception ex) {
        }
    }

    /**
     * 删除文件夹，如果文件夹下有文件，会一同删除
     *
     * @param folderPath 要删除的文件夹
     * @param dirDel     是否要把这个目录也删除，True删除
     */
    public static void delDir(String folderPath, boolean dirDel) {
        try {
            delAllFile(folderPath);
            if (dirDel) {
                String filePath = folderPath;
                filePath = filePath.toString();
                File myFilePath = new File(filePath);
                myFilePath.delete();
            }
        } catch (Exception e) {
        }
    }

    /**
     * 确定一个指定的文件是否存在
     *
     * @param fileName 文件名，包括路径
     * @return 存在返回true，否则返回false
     */
    public static boolean fileIsExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * 复制一个文件
     *
     * @param oldPathFile 复制前的文件名，包括路径
     * @param newPathFile 复制后的文件名，包括路径
     * @return 成功返回true, 否则返回false
     */
    public static boolean copyFile(String oldPathFile, String newPathFile) {
        InputStream inStream = null;
        FileOutputStream fs = null;
        try {
            int byteread = 0;
            File oldfile = new File(oldPathFile);
            if (oldfile.exists()) {
                inStream = new FileInputStream(oldPathFile);
                fs = new FileOutputStream(newPathFile);
                byte[] buffer = new byte[1024];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                    inStream = null;
                } catch (Exception ex) {
                }
                if (fs != null) {
                    try {
                        fs.close();
                        fs = null;
                    } catch (Exception ex) {
                    }
                }
            }
        }
    }


    /**
     * 获取手机SD卡下的目录结构(内部sd卡)。
     *
     * @param path sd卡下的文件夹结构路径
     * @return 返回SD卡下指定文件名的文件 如果SD卡不可用 返回空
     */
    public static File getSDCardFileDir(String path) {
        String rootPath = getIntSDCardRootPath();
        if (null == rootPath) {
            return null;
        }

        File fileDir = new File(rootPath + path);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        return fileDir;
    }

    /**
     * 获取内部(主)SD卡存储路径(只有一个)。
     *
     * @return, 如果不存在（挂载或者被移除）， 返回null, 反之返回根目录
     */
    public static String getIntSDCardRootPath() {
        String state = Environment.getExternalStorageState();
        if (state == null || !state.equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 拷贝过程中如果目标文件已经存在的处理器
     *
     * @author dazhengyang
     */
    public static interface DestFileExistHandler {

        /**
         * 处理目标文件已经存在的情况
         *
         * @param src
         * @param dest
         * @return 是否需要copy
         */
        boolean needCopy(File src, File dest);
    }
}
