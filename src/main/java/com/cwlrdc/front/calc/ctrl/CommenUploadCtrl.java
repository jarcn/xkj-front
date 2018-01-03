package com.cwlrdc.front.calc.ctrl;

import static com.cwlrdc.front.common.PictureType.getPicName;
import static java.lang.System.currentTimeMillis;

import com.cwlrdc.commondb.ltto.entity.LttoFaxPicture;
import com.cwlrdc.commondb.ltto.entity.LttoFaxPictureKey;
import com.cwlrdc.front.calc.util.FileUtils;
import com.cwlrdc.front.common.Constant.Template;
import com.cwlrdc.front.common.OperatorsLogManager;
import com.cwlrdc.front.common.ParaSysparameCache;
import com.cwlrdc.front.common.PictureType;
import com.cwlrdc.front.common.Status.UploadStatus;
import com.cwlrdc.front.ltto.service.LttoFaxPictureService;
import com.joyveb.lbos.restful.common.ReturnInfo;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by mafengge on 2017/5/16.
 */
@Slf4j
@Controller
@RequestMapping("/file")
public class CommenUploadCtrl {

    private static final Pattern PATTERN = Pattern.compile("\\{(.*?)\\}");
    private static final String SAVE_FAIL = "fail";
    private static final String PICTRUE_PATH_KEY = "pictruePath";
    private Matcher matcher;
    @Resource
    private LttoFaxPictureService lttoFaxPictureService;
    @Resource
    private OperatorsLogManager operatorsLogManager;
    @Resource
    private ParaSysparameCache paraSysparameCache;
    private long filesize;
    @Resource
    private ParaSysparameCache sysparameCache;

    /**
     * @Description 新图片展示
     */
    @RequestMapping(value = "/base64/{type}/{gameCode}/{periodNum}/{provinceId}", method = RequestMethod.GET)
    @ResponseBody
    public String newReturnBase64(@PathVariable String type, @PathVariable String gameCode,
        @PathVariable String periodNum, @PathVariable String provinceId) {
        try {
            LttoFaxPicture pictureInfo = this.selectLttoFax(gameCode, periodNum, provinceId, type);

            if (pictureInfo == null || StringUtils.isBlank(pictureInfo.getPicturePath())) {
                log.info("图片为空,配置不存在或参数错误,游戏[{}]期号[{}]省份[{}]图片类型[{}]",
                    gameCode, periodNum, provinceId, getPicName(Integer.parseInt(type)));
                return "fail";
            }

            byte[] data = FileUtils.readFile(pictureInfo.getPicturePath());
            String imageBase64Data = Base64.encodeBase64String(data);

            return imageBase64Data + "%%" + pictureInfo.getUploadTime();
        } catch (Exception e) {
            log.warn("图片展示发生错误,游戏[" + gameCode + "]期号[" + periodNum + "]省份[" + provinceId + "]图片类型[" + type + "]", e);
            return "fail";
        }
    }

    /*    @RequestMapping(value = "/base64/{type}/{gameCode}/{periodNum}/{provinceId}", method = RequestMethod.GET)
        @ResponseBody*/
    public String returnBase64(@PathVariable String type, @PathVariable String gameCode,
        @PathVariable String periodNum, @PathVariable String provinceId) {
        LttoFaxPictureKey lttoFaxPictureKey = new LttoFaxPictureKey();
        lttoFaxPictureKey.setGameCode(gameCode);
        lttoFaxPictureKey.setPeriodNum(periodNum);
        lttoFaxPictureKey.setProvinceId(provinceId);
        lttoFaxPictureKey.setPictureType(Integer.parseInt(type));
        LttoFaxPicture selectByPrimaryKey = lttoFaxPictureService.selectByPrimaryKey(lttoFaxPictureKey);
        if (null != selectByPrimaryKey && StringUtils.isNotBlank(selectByPrimaryKey.getPicturePath())) {
            //String imageBinary = getImageBinary(selectByPrimaryKey.getPicturePath());
            String imageBinary = selectByPrimaryKey.getPicturePath();
            return imageBinary + "%%" + selectByPrimaryKey.getUploadTime();
        }
        return "fail";
    }

    //判断图片是否已经上传过
    @RequestMapping(value = "/isuploaded/{type}/{gameCode}/{periodNum}/{provinceId}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnInfo isUploaded(@PathVariable String type, @PathVariable String gameCode,
        @PathVariable String periodNum, @PathVariable String provinceId) {
        LttoFaxPictureKey lttoFaxPictureKey = new LttoFaxPictureKey();
        lttoFaxPictureKey.setGameCode(gameCode);
        lttoFaxPictureKey.setPeriodNum(periodNum);
        lttoFaxPictureKey.setProvinceId(provinceId);
        lttoFaxPictureKey.setPictureType(Integer.parseInt(type));
        LttoFaxPicture selectByPrimaryKey = lttoFaxPictureService.selectByPrimaryKey(lttoFaxPictureKey);
        if (null != selectByPrimaryKey && StringUtils.isNotBlank(selectByPrimaryKey.getPicturePath())) {
            return ReturnInfo.Success;
        } else {
            return ReturnInfo.Faild;
        }
    }

    /**
     * @Description 新文件上传
     * @Author zhaoxin
     * @Date 2017/12/12 16:10
     */
    @RequestMapping(value = "/upload/{type}/{gameCode}/{periodNum}/{provinceId}", method = RequestMethod.POST)
    @ResponseBody
    public String newUpload(@PathVariable String type, @PathVariable String gameCode,
        @PathVariable String periodNum, @PathVariable String provinceId,
        @RequestParam("file") MultipartFile file) {
        long start = System.currentTimeMillis();
        String imagePath = this.getTemplatePath(Template.TEMPLATE_PATH_TKEY);
        String imageName = this.getTemplateName(Template.TEMPLATE_NAME_TKEY);
        /*文件原名*/
        String ppName = file.getOriginalFilename();
        if (StringUtils.isBlank(ppName)) {
            log.debug("上传文件名称为空，请检查");
            return "fail";
        }
        Map<String, Object> param = this.getParam(type, gameCode, periodNum, provinceId);
        String picName = this.getReplaceValue(imageName, param);
        String newFileStr = this.getReplaceValue(imagePath, param);
        if (StringUtils.isBlank(picName) || StringUtils.isBlank(newFileStr)) {
            log.debug("gameCode:[{}],periodNum:[{}],provinceId:[{}],PicName:[{}]参数为空，请检查",
                gameCode, periodNum, provinceId, getPicName(Integer.parseInt(type)));
            return "fail";
        }
        /*根据上传图片类型拼接完整图片类型*/
        String[] imageSplit = ppName.split("\\.");
        picName = picName + "." + imageSplit[imageSplit.length - 1];
        File newFile = new File(newFileStr);
        if (!newFile.exists()) {
            newFile.mkdirs();
        }
        String fileName = newFileStr + picName;
        /*判断文件是否存在，存在则备份*/
        this.newBackUpPictrue(fileName);
        /*转存图片文件*/
        try {
            file.transferTo(new File(fileName));
        } catch (IOException e) {
            log.debug("gameCode:[{}],periodNum:[{}],provinceId:[{}],picName:[{}]上传传真出错",
                gameCode, periodNum, provinceId, getPicName(Integer.parseInt(type)), e);
            return "fail";
    }
         /*新图片信息*/
        LttoFaxPicture lttoFaxPicture = this.getLttoFax(type, provinceId, file, periodNum, gameCode,
            picName, fileName);
        try {
            lttoFaxPictureService.insert(lttoFaxPicture);
        } catch (DuplicateKeyException e) {
            if (lttoFaxPictureService.updateByPrimaryKey(lttoFaxPicture) <= 0) {
                log.debug("gameCode:[{}],periodNum:[{}],provinceId:[{}],picName:[{}]保存图片信息出错",
                    gameCode, periodNum, provinceId, getPicName(Integer.parseInt(type)), e);
            }
        }
        log.debug("[{}][{}][{}][{}]上传成功", gameCode, periodNum, provinceId,
            getPicName(Integer.parseInt(type)));
        return type;
    }

    /**
     * @Description 文件上传
     * @Author mafengge
     * @Date 2017/5/16 11:55
     */
/*    @RequestMapping(value = "/upload/{type}/{gameCode}/{periodNum}/{provinceId}", method = RequestMethod.POST)
    @ResponseBody*/
    public String upload(@PathVariable String type, @PathVariable String gameCode,
        @PathVariable String periodNum, @PathVariable String provinceId,
        @RequestParam("file") MultipartFile file) {
        long start = System.currentTimeMillis();
        String picName = gameCode + "_" + periodNum + "_" + provinceId + "_" + type + ".jpg";
        String ppName = file.getOriginalFilename();
        if (ppName == null || "".equals(ppName)) {
            log.debug("[{}][{}][{}][{}]上传文件名称为空", gameCode, periodNum, provinceId, getPicName(Integer.parseInt(type)));
            return "fail";
        }
        LttoFaxPictureKey lttoFaxPictureKey = new LttoFaxPictureKey();
        lttoFaxPictureKey.setGameCode(gameCode);
        lttoFaxPictureKey.setPeriodNum(periodNum);
        lttoFaxPictureKey.setProvinceId(provinceId);
        lttoFaxPictureKey.setPictureType(Integer.parseInt(type));
        LttoFaxPicture selectByPrimaryKey = lttoFaxPictureService.selectByPrimaryKey(lttoFaxPictureKey);
        LttoFaxPicture lttoFaxPicture = new LttoFaxPicture();
        lttoFaxPicture.setPictureType(Integer.parseInt(type));
        lttoFaxPicture.setPictureSize(Integer.parseInt(file.getSize() + ""));
        lttoFaxPicture.setProvinceId(provinceId);
        lttoFaxPicture.setPeriodNum(periodNum);
        lttoFaxPicture.setUploadTime(currentTimeMillis());
        lttoFaxPicture.setGameCode(gameCode);
        lttoFaxPicture.setPictureName(picName);
        try {
            lttoFaxPicture.setPicturePath(String.valueOf(Base64.encodeBase64String(file.getBytes())));
        } catch (IOException e) {
            log.debug("[{}][{}][{}][{}]上传传真出错", gameCode, periodNum, provinceId,
                getPicName(Integer.parseInt(type)), e);
        }
        lttoFaxPicture.setStatus(1);
        if (null == selectByPrimaryKey) {
            lttoFaxPictureService.insert(lttoFaxPicture);
        } else {
            lttoFaxPictureService.updateByPrimaryKey(lttoFaxPicture);
        }
        log.debug("[{}][{}][{}][{}]上传成功", gameCode, periodNum, provinceId,
            getPicName(Integer.parseInt(type)));
//    log.info(operatorsLogManager.getLogInfo("页面通用", "上传传真", start));
        return type;
    }

    @RequestMapping(value = "/upload/pictrue/{type}/{gameCode}/{periodNum}/{provinceId}", method = RequestMethod.POST)
    @ResponseBody
    public String uploadPicture(@PathVariable String type, @PathVariable String gameCode,
        @PathVariable String periodNum, @PathVariable String provinceId,
        @RequestParam("file") MultipartFile file) {
        long start = System.currentTimeMillis();
        String ppName = file.getOriginalFilename();
        if (StringUtils.isBlank(ppName)) {
            log.debug("[{}][{}][{}][{}]上传文件名称为空", gameCode, periodNum, provinceId, getPicName(Integer.parseInt(type)));
            return SAVE_FAIL;
        }
        String picName = this.getPictrueName(provinceId, gameCode, periodNum, type);
        String pictruePath = this.getPictruePath(provinceId, gameCode, periodNum);
        String picFile = pictruePath + picName;
        //如果文件已经上传需要备份原先上传到图片
        this.backUpPictrue(picFile);
        LttoFaxPicture lttoFaxPicture = new LttoFaxPicture();
        lttoFaxPicture.setPictureType(Integer.parseInt(type));
        lttoFaxPicture.setPictureSize(Integer.parseInt(file.getSize() + ""));
        lttoFaxPicture.setProvinceId(provinceId);
        lttoFaxPicture.setPeriodNum(periodNum);
        lttoFaxPicture.setUploadTime(currentTimeMillis());
        lttoFaxPicture.setGameCode(gameCode);
        lttoFaxPicture.setPictureName(picName);
        lttoFaxPicture.setPicturePath(picFile);
        lttoFaxPicture.setStatus(UploadStatus.UPLOADED_SUCCESS);
        if (lttoFaxPictureService.savePictrue(pictruePath, picName, file)) {
            log.debug("[{}][{}][{}][{}]上传成功", gameCode, periodNum, provinceId, getPicName(Integer.parseInt(type)));
            log.info(operatorsLogManager.getLogInfo("页面通用", "上传传真", start));
            return type;
        } else {
            return SAVE_FAIL;
        }
    }

    private void backUpPictrue(String fileAbsPath) {
        File pic = new File(fileAbsPath);
        if (pic.exists()) {
            pic.renameTo(new File(pic.getName() + "." + System.currentTimeMillis() + ".BAK"));
        }
    }

    private void newBackUpPictrue(String fileAbsPath) {
        File pic = new File(fileAbsPath);
        if (pic.exists()) {
            pic.renameTo(new File(fileAbsPath + "." + System.currentTimeMillis() + ".BAK"));
        }
    }

    /**
     * set传真图片信息
     */
    private LttoFaxPicture getLttoFax(String type, String provinceId, MultipartFile file,
        String periodNum, String gameCode, String picName, String fileName) {
        LttoFaxPicture lttoFaxPicture = new LttoFaxPicture();
        lttoFaxPicture.setPictureType(Integer.parseInt(type));
        lttoFaxPicture.setPictureSize(Integer.parseInt(file.getSize() + ""));
        lttoFaxPicture.setProvinceId(provinceId);
        lttoFaxPicture.setPeriodNum(periodNum);
        lttoFaxPicture.setUploadTime(currentTimeMillis());
        lttoFaxPicture.setGameCode(gameCode);
        lttoFaxPicture.setPictureName(picName);
    /*存放图片完整路径*/
        lttoFaxPicture.setPicturePath(fileName);
        lttoFaxPicture.setStatus(1);//TODO ?
        return lttoFaxPicture;
    }

    /**
     * 根据条件查询传真图片信息
     */
    private LttoFaxPicture selectLttoFax(String gameCode, String periodNum, String provinceId, String type) {
        LttoFaxPictureKey lttoFaxPictureKey = new LttoFaxPictureKey();
        lttoFaxPictureKey.setGameCode(gameCode);
        lttoFaxPictureKey.setPeriodNum(periodNum);
        lttoFaxPictureKey.setProvinceId(provinceId);
        lttoFaxPictureKey.setPictureType(Integer.parseInt(type));
        LttoFaxPicture selectByPrimaryKey = lttoFaxPictureService.selectByPrimaryKey(lttoFaxPictureKey);
        return selectByPrimaryKey;
    }

    /**
     * 创建图片明称
     */
    private String getPictrueName(String provinceId, String gameCode, String periodNum, String type) {
        StringBuilder sb = new StringBuilder();
        sb.append(provinceId).append("_");
        sb.append(gameCode).append("_");
        sb.append(periodNum).append("_");
        sb.append(periodNum).append("_");
        String picName = PictureType.getPicName(Integer.parseInt(type));
        sb.append(picName).append(".jpg");
        return sb.toString();
    }

    /**
     * 创建图片存放路径
     */
    private String getPictruePath(String provinceId, String gameCode, String periodNum) {
        String path = sysparameCache.getValue(PICTRUE_PATH_KEY);
        StringBuilder sb = new StringBuilder();
        if (StringUtils.endsWith(path, File.separator)) {
            sb.append(path).append(provinceId).append(File.separator);
        } else {
            sb.append(path).append(File.separator).append(provinceId).append(File.separator);
        }
        sb.append(gameCode).append(File.separator);
        sb.append(periodNum).append(File.separator);
        return sb.toString();
    }

    /**
     * 格式化字符串 (替换所有) 字符串中使用{key}表示占位符
     *
     * @param sourStr 模板
     * @param param 参数集
     * @return zhaoxin
     */
    private String getReplaceValue(String sourStr, Map<String, Object> param) {
        String tagerStr = sourStr;
        if (param == null) {
            return null;
        }
        matcher = PATTERN.matcher(tagerStr);
        while (matcher.find()) {
            String key = matcher.group();
            String keyclone = key.substring(1, key.length() - 1).trim();
            Object value = param.get(keyclone);
            if (value != null) {
                tagerStr = tagerStr.replace(key, value.toString());
            } else {
                return null;
            }
        }
        return tagerStr;
    }

    /**
     * 获取路径模板
     */
    private String getTemplatePath(String tkey) {
        String imagePath = paraSysparameCache.getValue(tkey);
        if (StringUtils.isBlank(imagePath)) {
            imagePath = "/home/test/{gameCode}/{periodNum}/{provinceId}/";
            log.info("imagePath：[{}]图片路径模板为空，请检查，暂使用默认模板", imagePath);
        }
        return imagePath;
    }

    /**
     * 获取名称模板
     */
    private String getTemplateName(String tkey) {
        String imageName = paraSysparameCache.getValue(tkey);
        if (StringUtils.isBlank(imageName)) {
            imageName = "{provinceId}_{gameCode}_{periodNum}_{typeName}.jpg";
            log.debug("imageName:[{}]图片名称模板为空，请检查，暂使用默认模板", imageName);
        }
        return imageName;
    }

    /*
      private String stringFormat(String sourStr, Map<String, Object> param){
        String[] str = sourStr.split("\\{");
        if (str.length - 1 <= param.size()){
          sourStr = sourStr.replace("{type}", param.get("type").toString());
          sourStr = sourStr.replace("{gameCode}", param.get("gameCode").toString());
          sourStr = sourStr.replace("{periodNum}", param.get("periodNum").toString());
          sourStr = sourStr.replace("{provinceId}", param.get("provinceId").toString());
          return sourStr;
        }
        return null;
      }*/
    private Map<String, Object> getParam(String type, String gameCode, String periodNum,
        String provinceId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("typeName", PictureType.getTypeName(Integer.parseInt(type)));
        param.put("type", type);
        param.put("gameCode", gameCode);
        param.put("periodNum", periodNum);
        param.put("provinceId", provinceId);
        return param;
    }
}
