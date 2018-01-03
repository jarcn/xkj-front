package com.cwlrdc.front.common;

import com.cwlrdc.front.task.LttoProvinceFileDownloadContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Map;

@Slf4j
public class Starter implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.debug("[新开奖服务]开始启动");
        try {
            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
            @SuppressWarnings("unchecked")
            Map<String, CoreCache> caches = (Map<String, CoreCache>) webApplicationContext.getBean("caches");
            for (Map.Entry<String, CoreCache> entry : caches.entrySet()) {
                entry.getValue().init();
            }
            LttoProvinceFileDownloadContainer bean = webApplicationContext.getBean(LttoProvinceFileDownloadContainer.class);
            bean.setUp();

            log.debug("[新开奖服务]启动完成");
        } catch (Exception e) {
            log.warn("[新开奖服务]启动异常", e);
            throw e;
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
            LttoProvinceFileDownloadContainer bean = webApplicationContext.getBean(LttoProvinceFileDownloadContainer.class);
            bean.tearDown();
        } catch (BeansException e) {
            log.warn("[新开奖服务]注销异常", e);
        }
        log.debug("[新开奖服务]注销完成");
    }

}
