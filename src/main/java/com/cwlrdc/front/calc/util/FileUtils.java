package com.cwlrdc.front.calc.util;

import com.cwlrdc.front.common.Constant;
import com.unlto.twls.commonutil.component.HashComponent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.util.Assert;

/**
 * @author yangqiju
 * @author chenjia
 */
@Slf4j
public class FileUtils {

    // 判断ftp文件是否上传
    public static boolean fileExist(String filePath) {
        try {
            File flie = new File(filePath);
            if (flie.exists()) {
                return true;
            }
        } catch (Exception e) {
            log.debug("[" + filePath + "]异常", e);
            return false;
        }
        log.debug("文件[{}]未上传", filePath);
        return false;
    }

    public static String createFileName(String connector, String[] names) {
        StringBuilder buff = new StringBuilder();
        if (names.length > 0) {
            for (int i = 0; i < names.length; i++) {
                if (i == names.length - 2) {
                    buff.append(names[i]);
                } else if (i == names.length - 1) {
                    buff.append(names[i]);
                } else {
                    buff.append(names[i]).append(connector);
                }
            }
        }
        return buff.toString();
    }

    /**
     * @Description 比较两个文件hash值是否相等
     * @Author mafengge
     * @Date 2017/4/27 15:17
     */
    public static boolean sourceHashEqual(String sourcePath, String hashPath) {
        String hashPathStr = null;
        String sourceSha256 = null;
        try {
            hashPathStr = getFileSha(hashPath).toLowerCase();
            byte[] sourceHash = HashComponent.encrypt(new File(sourcePath), HashComponent.KEY_SHA_256);
            sourceSha256 = HashComponent.hex(sourceHash);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        return hashPathStr.equalsIgnoreCase(sourceSha256);
    }

    /**
     * @Description 读取文件内容
     * @Author mafengge
     * @Date 2017/4/27 15:18
     */
    public static String getFileSha(String src) throws IOException {
        StringBuilder result = new StringBuilder();
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(new File(src)), Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr)) {
            String s = null;
            while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
                result.append(s);
            }
        }
        return result.toString();
    }

    //TODO 不要把业务带进来
    public static String getCurrWinNum(String gameCode, String winNum) {
        StringBuilder winNumBuff = new StringBuilder();
        if (Constant.GameCode.GAME_CODE_SLTO.equals(gameCode) && StringUtils.isNotBlank(winNum)) {
            String[] nums = winNum.split("\\+");
            String[] redbollarr = nums[0].split(",");
            for (String str : redbollarr) {
                winNumBuff.append(str);
            }
            winNumBuff.append("+").append(nums[1]);
        }
        if (Constant.GameCode.GAME_CODE_LOTO.equals(gameCode) && StringUtils.isNotBlank(winNum)) {
            String[] nums = winNum.split("\\@");
            String[] redbollarr = nums[0].split(",");
            for (String str : redbollarr) {
                winNumBuff.append(str);
            }
            winNumBuff.append("@").append(nums[1]);
        }
        return winNumBuff.toString();
    }

    // 备份文件
    public static void copyFile(String soruceFilePath, String targetFilePath) {
        try(
            FileInputStream inStream = new FileInputStream(new File(soruceFilePath));
            FileOutputStream outStream = new FileOutputStream(new File(targetFilePath));
            FileChannel in = inStream.getChannel();
            FileChannel out = outStream.getChannel();
            ) {
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            log.debug("文件备份异常", e);
        }
    }

    // 获取指定空格
    public static String createSpace(int len) {
        StringBuilder spaceStr = new StringBuilder();
        if (len == 0) {
            return spaceStr.toString();
        } else {
            for (int i = 0; i < len; i++) {
                spaceStr.append(" ");
            }
            return spaceStr.toString();
        }
    }

    /**
     * 根据路径,写入数据
     *
     * @param filepath 文件路径你
     * @param bytes 数据内容
     * @throws IOException 文件写入过程中发生异常
     */
    public static void saveFile(String filepath, byte[] bytes) throws IOException {
        Assert.hasText(filepath, "文件路径不能为空");
        File sourcFile = new File(filepath);
        if (!sourcFile.getParentFile().exists()) {
            sourcFile.getParentFile().mkdirs();
        }
        if (!sourcFile.exists()) {
            sourcFile.createNewFile();
        }
        Files.write(sourcFile.toPath(), bytes, StandardOpenOption.TRUNCATE_EXISTING);
    }


    /**
     * 对文件进行备份操作
     *
     * @param filepath 文件路径
     * @return 备份文件的路径
     * @throws IOException 文件拷贝过程中的错误
     */
    public static String backFile(String filepath) throws IOException {
        Assert.hasText(filepath, "文件路径不能为空");
        Path source = new File(filepath).toPath();

        if (!Files.isReadable(source)) {
            throw new IllegalArgumentException("文件不存在或不可读[" + filepath + "]");
        }

        String suffix = DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmss");
        String backFilepath = filepath + "." + suffix;
        Path target = new File(backFilepath).toPath();
        Files.copy(source, target);
        return target.toString();
    }

    /**
     * 删除文件
     *
     * @param filepath 文件路径
     * @return 删除是否成功
     * @throws IOException 删除过程中的错误
     */
    public static boolean deleteFile(String filepath) throws IOException {
        Assert.hasText(filepath, "文件路径不能为空");
        Path source = new File(filepath).toPath();
        return Files.deleteIfExists(source);
    }

    /**
     * 根据路径读取数据
     *
     * @param filepath 文件路径
     * @return 数据的byte数组
     * @throws IOException 文件不存在或读取错误
     */
    public static byte[] readFile(String filepath) throws IOException {
        Assert.hasText(filepath, "文件路径不能为空");
        Path path = new File(filepath).toPath();
        if (!Files.isReadable(path)) {
            throw new IllegalArgumentException("文件不存在或不可读[" + filepath + "]");
        }
        return Files.readAllBytes(path);
    }


    /**
     * 查看文件是否存在
     * @param filepath 文件路径
     * @return 是否存在
     */
    public static boolean exists(String filepath) {
        Assert.hasText(filepath, "文件路径不能为空");
        return Files.exists(new File(filepath).toPath());
    }

}
