package com.swsm.license.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;

/**
 * description 压缩工具类
 */
public class ZipUtil {

    private ZipUtil() {}

    public static void main(String[] args) {
        // 需打包的文件夾
        String inputFileName = "D:\\projects\\aaa-example";
        // 打包后文件名字
        String outputFileName = "D:\\projects\\aaa-example.zip";
        try {
            zip(inputFileName, outputFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 压缩文件夹
     *
     * @param inputFileName 输入文件(夹)全路径名称
     * @param outputFileName 输出文件全路径名称
     * @throws Exception 异常
     */
    public static void zip(String inputFileName, String outputFileName) throws Exception {
        zip(outputFileName, new File(inputFileName));
    }

    public static void zip(String zipFileName, File inputFile) throws Exception {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
        zip(out, inputFile, "");
        System.out.println("zip done");
        out.close();
    }

    public static void zip(ZipOutputStream out, File f, String base) throws Exception {
        if (f.isDirectory()) {
            File[] fl = f.listFiles();
            out.putNextEntry(new ZipEntry(base + "/"));
            base = base.length() == 0 ? "" : base + "/";
            for (int i = 0; i < Objects.requireNonNull(fl).length; i++) {
                zip(out, fl[i], base + fl[i].getName());
            }
        } else {
            out.putNextEntry(new ZipEntry(base));
            FileInputStream in = new FileInputStream(f);
            int read;
            byte[] bytes=new byte[1024];
            while((read = in.read(bytes)) > 0){
                out.write(bytes, 0, read);
            }
            in.close();
        }
    }

    /**
     * 压缩指定目录为zip文件之后读取成输出流字节码
     *
     * @param response         响应对象
     * @param inputFileName    压缩前目录名(全路径)
     * @param outputFileName   压缩后文件名(全路径)
     * @param downloadFileName 下载时的文件名(需要带扩展名)
     * @param clearZip         是否删除zip临时文件
     * @param clearDir         是否删除临时目录
     * @return {@link byte[] }
     * @throws Exception 异常
     */
    public static byte[] getOutputBytes(HttpServletResponse response, String inputFileName, String outputFileName, String downloadFileName, boolean clearZip, boolean clearDir) throws Exception {
        ZipUtil.zip(inputFileName, outputFileName);
        File file = new File(outputFileName);
        byte[] data = FileUtils.readFileToByteArray(file);
        if (clearZip) {
            // 文件加载到内存之后删除磁盘文件(删除压缩包)
            FileUtils.deleteQuietly(file);
        }
        if (clearDir) {
            // 删除文件夹
            FileUtils.deleteQuietly(new File(inputFileName));
        }
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=" + downloadFileName);
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        return data;
    }

    /**
     * 压缩指定目录为zip文件之后读取成输出流字节码
     *
     * @param response 响应对象
     * @param sourceFileName 压缩文件(全路径)
     * @param downloadFileName 下载时的文件名(需要带扩展名)
     * @return 返回输出文件读取到的字节码
     * @throws Exception 异常
     */
    public static byte[] getOutputBytes(HttpServletResponse response, String sourceFileName, String downloadFileName) throws Exception {
        File file = new File(sourceFileName);
        byte[] data = FileUtils.readFileToByteArray(file);
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=" + downloadFileName);
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        return data;
    }
}
