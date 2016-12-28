
package com.lanou.lilyxiao.myapplication.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 文件压缩解压工具。
 */
public class ZipUtil {
    /**
     * 缓冲区大小
     */
    private static final int BUFF_SIZE = 1024 * 48;

    /**
     * 压缩单个文件
     * 
     * @param inFile 待压缩的文件
     * @param outFolderPath 压缩文件的输出目录
     * @throws Exception
     */
    public static void zipFile(File inFile, String outFolderPath) throws Exception {
        if (inFile == null || !inFile.exists()) {
            return;
        }

        File desDir = new File(outFolderPath);
        if (!desDir.exists()) {
            desDir.mkdirs();
        }

        byte[] buffer = new byte[BUFF_SIZE];
        int len = 0;

        FileInputStream fis = new FileInputStream(inFile);
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(new File(outFolderPath,
                inFile.getName() + ".zip")));
        zos.setLevel(Deflater.BEST_COMPRESSION);
        zos.setMethod(ZipOutputStream.DEFLATED);
        ZipEntry zipEntry = new ZipEntry(inFile.getName());
        zos.putNextEntry(zipEntry);

        while ((len = fis.read(buffer)) != -1) {
            zos.write(buffer, 0, len);
        }

        zos.finish();
        zos.close();
        fis.close();
    }

    /**
     * 解压缩一个文件。
     * 
     * @param zipFile 压缩文件
     * @param folderPath 解压缩的目标目录
     * @throws IOException 当解压缩过程出错时抛出
     */
    public static void upZipFile(File zipFile, String folderPath) throws ZipException, IOException {
        File desDir = new File(folderPath);
        if (!desDir.exists()) {
            desDir.mkdirs();
        }
        ZipFile zf = new ZipFile(zipFile);
        InputStream in;
        ZipEntry entry;
        String str;
        File desFile;
        OutputStream out;
        byte buffer[] = new byte[BUFF_SIZE];
        int realLength;
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
            entry = ((ZipEntry)entries.nextElement());

            if (entry.isDirectory()) {
                str = folderPath + File.separator + entry.getName();
                desFile = new File(str);
                desFile.mkdir();
                continue;
            }

            in = zf.getInputStream(entry);
            str = folderPath + File.separator + entry.getName();
            desFile = new File(str);
            if (!desFile.exists()) {
                File fileParentDir = desFile.getParentFile();
                if (!fileParentDir.exists()) {
                    fileParentDir.mkdirs();
                }
                desFile.createNewFile();
            }
            out = new FileOutputStream(desFile);
            while ((realLength = in.read(buffer)) > 0) {
                out.write(buffer, 0, realLength);
            }
            out.flush();
            in.close();
            out.close();
        }
        zf.close();
    }

    /**
     * 压缩二进制数据。
     * 
     * @param data 要压缩的数据
     * @return 压缩后的数据
     */
    public static byte[] deflate(byte[] data) {
        ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();
        DeflaterOutputStream out = new DeflaterOutputStream(bufferOut);
        try {
            out.write(data, 0, data.length);
            out.finish();
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bufferOut.toByteArray();
    }

    /**
     * 解压二进制数据。
     * 
     * @param data 要解压的数据
     * @return 解压后的数据
     */
    public static byte[] inflate(byte[] data) {
        InflaterInputStream is = new InflaterInputStream(new ByteArrayInputStream(data));
        byte[] rdata = new byte[0];
        int total = 0;
        byte[] data_atime = new byte[1024];
        int len;
        do {
            try {
                len = is.read(data_atime);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            if (len > 0) {
                total += len;
                byte[] temp = new byte[total];
                System.arraycopy(rdata, 0, temp, 0, rdata.length);
                System.arraycopy(data_atime, 0, temp, rdata.length, len);
                rdata = temp;
            }

        } while (len > 0);
        return rdata;
    }

    /**
     * 要解压的压缩包内有多个文件
     * 
     * @param zipFile
     * @param folderPath
     *            目标目录
     */
    public static void upZipFiles(File zipFile, String folderPath) {
        try {
            InputStream is = new FileInputStream(zipFile);
            File desDir = new File(folderPath);
            upZipFileFromInputStream(is, desDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件流中解压
     * 
     * @param is
     * @param desDir
     *            目标目录
     * @throws ZipException
     * @throws IOException
     */
    public static void upZipFileFromInputStream(InputStream is, File desDir) throws ZipException, IOException {
        if (!desDir.exists()) {
            desDir.mkdirs();
        }
        ZipInputStream zis = new ZipInputStream(is);
        String str;
        File desFile;
        OutputStream out;
        byte buffer[] = new byte[BUFF_SIZE];
        int realLength;
        ZipEntry entry = zis.getNextEntry();
        while (entry != null) {
            if (entry.isDirectory()) {
                String name = entry.getName();
                name = name.substring(0, name.length() - 1);
                desFile = new File(desDir + File.separator + name);
                desFile.mkdir();
            } else {
                desFile = new File(desDir, File.separator + entry.getName());
                if (!desFile.exists()) {
                    File fileParentDir = desFile.getParentFile();
                    if (!fileParentDir.exists()) {
                        fileParentDir.mkdirs();
                    }
                    desFile.createNewFile();
                }
                out = new FileOutputStream(desFile);
                while ((realLength = zis.read(buffer)) > 0) {
                    out.write(buffer, 0, realLength);
                }
                out.flush();
                out.close();
            }
            // 读取下一个ZipEntry
            entry = zis.getNextEntry();
        }
        is.close();
        zis.close();
    }

}
