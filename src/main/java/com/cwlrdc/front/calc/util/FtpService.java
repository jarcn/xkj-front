package com.cwlrdc.front.calc.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 * Created by chenjia on 2017/6/17.
 */
@Slf4j
public class FtpService implements Closeable {

  private FTPClient ftp;

  /**
   * 参看父类中的注释 @see cn.mr.mohurd.service.sftp.FileTransferService#getConnect(java.lang.String, int,
   * java.lang.String, java.lang.String)
   */
  public void getConnect(String host, int port, String username, String password)
      throws IOException {
    try {
      ftp = new FTPClient();
      ftp.setConnectTimeout(3000); //超时时间
      ftp.connect(host, port);//连接FTP服务器
      ftp.login(username, password);//登录
      int reply = ftp.getReplyCode();
      if (!FTPReply.isPositiveCompletion(reply)) {
        disConn();
        throw new IOException("connect error.");
      }
    } catch (IOException e) {
      log.debug(
          "连接ftp服务器失败,请检查主机[" + host + "],端口[" + port + "],用户名[" + username + "],密码[" + password
              + "]是否正确,以上信息正确的情况下请检查网络连接是否正常或者请求被防火墙拒绝.", e);
      throw e;
    }
  }

  /**
   * 断开连接
   * 参看父类中的注释 @see cn.mr.mohurd.service.sftp.FileTransferService#disConn()
   */
  public void disConn() {
    try {
      if (ftp.isConnected()) {
        ftp.logout();
        ftp.disconnect();
      }
    } catch (Exception e) {
      log.error("断开ftp连接失败", e);
    }
  }

  /**
   * 上传文件 参看父类中的注释 @see cn.mr.mohurd.service.sftp.FileTransferService#upload(java.lang.String,
   * java.lang.String)
   */
  public void upload(String directory, String uploadFile) {
    File file = new File(uploadFile);
    try (FileInputStream input = new FileInputStream(file)) {
      //如果目录不存在，则创建目录
      if (StringUtils.isNotBlank(directory)) {
        String[] pathes = directory.split(File.pathSeparatorChar == '\\' ? "\\\\" : File.separator);
        for (String onepath : pathes) {
          if (StringUtils.isBlank(onepath)) {
            continue;
          }
          if (!ftp.changeWorkingDirectory(onepath)) {
            ftp.makeDirectory(onepath);
            ftp.changeWorkingDirectory(onepath);
          }
        }
      }
      ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
      ftp.storeFile(file.getName(), input);
    } catch (Exception e) {
      log.debug("文件传异常", e);
    } finally {
      disConn();
    }
  }

  /**
   * 下载文件
   * 参看父类中的注释 @see cn.mr.mohurd.service.sftp.FileTransferService
   * #download(java.lang.String, java.lang.String, java.lang.String)
   */
  public void download(String directory, String downloadFile, String saveFile) throws IOException {
    ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
    ftp.changeWorkingDirectory(directory);//转移到FTP服务器目录
    FTPFile[] fs = ftp.listFiles();
    boolean hasFile = false;
    for (FTPFile ff : fs) {
      if (ff.getName().equalsIgnoreCase(downloadFile)) {
        hasFile = true;
        File localFile = new File(saveFile);
        if (!localFile.exists()) {
          boolean mkdirs = localFile.mkdirs();
        }
        try (OutputStream is = new FileOutputStream(
            new File(localFile + File.separator + downloadFile))) {
          ftp.retrieveFile(ff.getName(), is);
          break;
        }
      }
    }
    if (!hasFile) {
      log.debug("没有在ftp服务器[{}]目录找到需要下载的文件[{}]", directory, downloadFile);
    }
  }

  public FTPFile getFileInfo(String directory, String downloadFile) throws IOException {
    ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
    ftp.changeWorkingDirectory(directory);//转移到FTP服务器目录
    FTPFile ftpFile = ftp.mlistFile(downloadFile);
    return ftpFile;
  }

  public long getModificationTime(String directory, String downloadFile)
      throws IOException, ParseException {
    if (StringUtils.isBlank(directory) || StringUtils.isBlank(downloadFile)) {
      return -1;
    }
    ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
    ftp.changeWorkingDirectory(directory);//转移到FTP服务器目录
    FTPFile[] ftpFiles = ftp.listFiles();
    String fileRealName = downloadFile;
    for (FTPFile ftpFile : ftpFiles) {
      if (ftpFile.isDirectory()) {
        continue;
      }
      String name = ftpFile.getName();
      if (downloadFile.equalsIgnoreCase(name)) {
        fileRealName = name;
      }
    }
    String time = ftp.getModificationTime(fileRealName);
    if (time == null) {
      return -1;
    }
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    try {
      String timePart = time.split(" ")[1];
      Date modificationTime = dateFormat.parse(timePart);
      return modificationTime.getTime();
    } catch (ParseException ex) {
      log.warn("文件获得最后编辑时间错误", ex);
      throw ex;
    }
  }

  /**
   * 删除文件
   */
  public void delete(String directory, String deleteFile) throws IOException {
    if (!ftp.deleteFile(directory + "/" + deleteFile)) {
      log.warn("删除文件失败，原因为" + ftp.getReplyString());
    }
  }

  /**
   * 获取ftp上文件的最后修改时间
   *
   * @param path 文件路径    e.g. "ftputil/test.txt"
   */
  public Long getFileUploadTime(String path) throws IOException {
    //获取ftp上path路径下的文件
    Long modifyTime = null;
    FTPFile[] fileList = ftp.listFiles(path);
    for (int i = 0; i < fileList.length; i++) {
      FTPFile ftpFile = fileList[i];
      modifyTime = ftpFile.getTimestamp().getTime().getTime();
    }
    return modifyTime;
  }

  @Override
  public void close() throws IOException {
    try {
      if (ftp.isConnected()) {
        ftp.logout();
        ftp.disconnect();
      }
    } catch (Exception e) {
      log.error("断开ftp连接失败", e);
    }
  }
}