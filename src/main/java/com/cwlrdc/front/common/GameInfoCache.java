package com.cwlrdc.front.common;

import com.cwlrdc.commondb.para.entity.ParaGameinfo;
import com.cwlrdc.front.para.service.ParaGameinfoService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chenjia on 2017/4/21.
 */

@Slf4j
public class GameInfoCache implements CoreCache {

    private @Resource ParaGameinfoService paraGameinfoService;
    private Map<String, ParaGameinfo> games = new ConcurrentHashMap<>();

    @Override
    public void init() {
        List<ParaGameinfo> gameinfos = paraGameinfoService.findAll(null);
        for (ParaGameinfo bean:gameinfos) {
            games.put(bean.getGameCode(),bean);
        }
    }

    //刷新缓存
    public void reload(){
        games.clear();
        init();
    }

    public String getGameName(String gameCode){
        reload();
        ParaGameinfo gameInfo = games.get(gameCode);
        if(null!=gameInfo){
            return gameInfo.getGameName();
        }else{
            return "";
        }
    }

}
