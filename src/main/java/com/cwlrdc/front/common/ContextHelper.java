package com.cwlrdc.front.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by chenjia on 2017/4/21.
 */
@Slf4j
public class ContextHelper implements ApplicationContextAware {

  private static ApplicationContext ctx;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext)
      throws BeansException {
    ContextHelper.setCtx(applicationContext);
  }

  public static void setCtx(ApplicationContext applicationContext) {
    ctx = applicationContext;
  }

  public static <T> T getBean(String beanid, Class<T> clazz) {
    return ctx.getBean(beanid, clazz);
  }

  public static Object getBean(String beanName) {
    return ctx.getBean(beanName);
  }
}
