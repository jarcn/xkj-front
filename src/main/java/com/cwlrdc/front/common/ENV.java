package com.cwlrdc.front.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangqiju on 2017/9/6.
 */
public enum ENV {
    //测试环境
    test,
    //生产环境
    pro;

    private static Map<String,ENV> envs = new HashMap<>();
    static {
        for(ENV env : ENV.values()){
            envs.put(env.name(),env);
        }
    }

    public static ENV toEnv(String env){
        return envs.get(env);
    }
}
