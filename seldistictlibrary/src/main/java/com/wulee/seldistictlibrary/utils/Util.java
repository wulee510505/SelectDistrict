package com.wulee.seldistictlibrary.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Util {
    // 复制文件
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }

    // 复制文件
    public static void copyFile(String sourceFile, String targetFile) throws IOException {
        File sourceFileT = new File(sourceFile);
        File targetFileT = new File(targetFile);
        copyFile(sourceFileT, targetFileT);
    }

    // 复制文件夹
    public static void copyDirectory(String sourceDir, String targetDir) throws IOException {
        // 新建目标目录
        (new File(targetDir)).mkdirs();
        // 获取源文件夹当前下的文件或目录
        File[] file = (new File(sourceDir)).listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                // 源文件
                File sourceFile = file[i];
                // 目标文件
                File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
                copyFile(sourceFile, targetFile);
            }
            if (file[i].isDirectory()) {
                // 准备复制的源文件夹
                String dir1 = sourceDir + "/" + file[i].getName();
                // 准备复制的目标文件夹
                String dir2 = targetDir + "/" + file[i].getName();
                copyDirectory(dir1, dir2);
            }
        }
    }

    public static void delFile(String filepath) {
        if (filepath == null || filepath.equals(""))
            return;
        try {
            File f = new File(filepath);
            if (f.exists() && f.isFile())
                f.delete();
        } catch (Exception e) {
        }
    }

    /**
     * 删除目录及以下所有文件
     *
     * @param path
     * @return
     */
    public static void deleteDir(String path) {
        File file = new File(path);
        if (file.exists()) {
            File[] list = file.listFiles();
            if (list != null) {
                int len = list.length;
                for (int i = 0; i < len; ++i) {
                    if (list[i].isDirectory()) {
                        deleteDir(list[i].getPath());
                    } else {
                        list[i].delete();
                    }
                }
            }
            file.delete();
        }
    }

    /**
     * 文件转成字节数组
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static byte[] readFileToBytes(String path) throws IOException {
        byte[] b = null;
        InputStream is = null;
        File f = new File(path);
        try {
            is = new FileInputStream(f);
            b = new byte[(int) f.length()];
            is.read(b);
        } finally {
            if (is != null)
                is.close();
        }
        return b;
    }

    /**
     * 将byte写入文件中
     *
     * @param fileByte
     * @param filePath
     * @throws IOException
     */
    public static void byteToFile(byte[] fileByte, String filePath) throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(filePath));
            os.write(fileByte);
            os.flush();
        } finally {
            if (os != null)
                os.close();
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath
     * @throws IOException
     */
    public static boolean fileIsExists(String filePath) {
        try {
            File f = new File(filePath);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    /**
     * 判断是否是特别行政区
     *
     * @param provinceId
     * @return
     */
    public static boolean isSpeRegion(int provinceId) {
        boolean isSpeRegion = false;
        int[] speRegionId = new int[] { 710000, 810000, 820000 }; // 台湾、香港、澳门
        for (int i = 0; i < speRegionId.length; i++) {
            if (provinceId == speRegionId[i]) {
                isSpeRegion = true;
            }
        }
        return isSpeRegion;
    }

    /**
     * 判断是否是直辖市
     *
     * @param provinceId
     * @return
     */
    public static boolean isDireGovernment(int provinceId) {
        boolean isDireGovernment = false;
        int[] direGovernment = new int[] { 110000, 120000, 310000, 500000 };// 北京、天津、上海、重庆
        for (int i = 0; i < direGovernment.length; i++) {
            if (provinceId == direGovernment[i]) {
                isDireGovernment = true;
            }
        }
        return isDireGovernment;
    }

}
