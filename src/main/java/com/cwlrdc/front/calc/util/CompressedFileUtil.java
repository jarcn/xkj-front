package com.cwlrdc.front.calc.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;


/**
 * 文件及文件夹压缩工具
 * Created by chenjia on 2017/8/23.
 */
@Slf4j
public class CompressedFileUtil {

  public static final String FILE_TYPE_SUFFX = ".zip";

  /**
   * @param resourcesDir 源文件/文件夹
   * @param saveDir 目的压缩文件保存路径
   * @return void
   * @desc 将源文件/文件夹生成指定格式的压缩文件,格式zip
   */
  public static String compressedFile(String resourcesDir, String saveDir, String zipName)
      throws IOException {
    File resourcesFile = new File(resourcesDir);     //源文件
    File targetFile = new File(saveDir);           //目的
    //如果目的路径不存在，则新建
    if (!targetFile.exists()) {
      boolean mkdirs = targetFile.mkdirs();
    }
    FileOutputStream outputStream = new FileOutputStream(saveDir + zipName + FILE_TYPE_SUFFX);
    try (ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(outputStream));) {
      createCompressedFile(out, resourcesFile, "");
    }
    return saveDir + zipName + FILE_TYPE_SUFFX;
  }

  /**
   * @desc 生成压缩文件。 如果是文件夹，则使用递归，进行文件遍历、压缩 如果是文件，直接压缩
   * @param out  输出流
   * @param file  目标文件
   * @return void
   * @throws Exception
   */
  private static String ndir;

  public static void createCompressedFile(ZipOutputStream out, File file, String dir)
      throws IOException {
    //如果当前的是文件夹，则进行进一步处理
    if (file.isDirectory()) {
      //得到文件列表信息
      File[] files = file.listFiles();
      //循环将文件夹中的文件打包
      if (files != null && files.length > 0) {
        for (int i = 0; i < files.length; i++) {
          out.putNextEntry(new ZipEntry(dir + File.separator)); //将文件夹添加到下一级打包目录
          ndir = dir.length() == 0 ? "" : new String((dir + File.separator).getBytes(Charset.defaultCharset()));
          if (files[i].getName().endsWith(".xls")) {
            compressedFile(out, files[i], ndir + files[i].getName());
          }
        }
      }
    } else {//当前的是文件，打包处理
      compressedFile(out, file, dir);
    }
  }

  private static void compressedFile(ZipOutputStream out, File file, String dir) {
    try (FileInputStream fis = new FileInputStream(file)) {
      out.putNextEntry(new ZipEntry(dir));
      //进行写操作
      int j = 0;
      byte[] buffer = new byte[1024];
      while ((j = fis.read(buffer)) > 0) {
        out.write(buffer, 0, j);
      }
    } catch (IOException e) {
      log.error("文件压缩异常", e);
    }
  }

  /**
   * 源zip文件和txt文件压缩到新的zip文件中
   */
  public static String zipAppendZipFile(File soruceZip, File txtFile, String saveDir,
      String zipName) {
    if (!txtFile.exists()) {
      return "源文件不存在";
    }
    File outDir = new File(saveDir);
    if (!outDir.exists()) {
      boolean mkdirs = outDir.mkdirs();
    }
    String zipFilePath = outDir.getAbsolutePath() + File.separator + zipName + FILE_TYPE_SUFFX;
    File zipFile = new File(zipFilePath);// 定义压缩文件名称
    int txttemp = 0;
    int ziptemp = 0;
    try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
        InputStream input = new FileInputStream(txtFile);
        InputStream zipIn = new FileInputStream(soruceZip)) {
      zipOut.setComment("监控数据");  // 设置注释
      zipOut.setEncoding("UTF-8");
      zipOut.putNextEntry(new ZipEntry(txtFile.getName()));  // 设置ZipEntry对象
      while ((txttemp = input.read()) != -1) { // 读取内容
        zipOut.write(txttemp);    // 压缩输出
      }
      zipOut.putNextEntry(new ZipEntry(soruceZip.getName()));// 设置ZipEntry对象
      while ((ziptemp = zipIn.read()) != -1) { // 读取内容
        zipOut.write(ziptemp);    // 压缩输出
      }
      zipOut.flush();
    } catch (IOException e) {
      log.warn("导出监控数据异常", e);
    }
    return zipFile.getAbsolutePath();
  }

}
