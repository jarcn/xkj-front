package com.cwlrdc.front.task;

import com.cwlrdc.front.common.ParaSysparameCache;
import com.cwlrdc.front.common.PeriodManager;
import com.cwlrdc.front.para.service.ParaGamePeriodInfoService;
import com.cwlrdc.front.para.service.ParaGameinfoService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by yangqiju on 2017/9/2.
 */
@Slf4j
@Component
public class LttoProvinceFileDownloadContainer {

  private ExecutorService executorService;
  private final Object locker = new Object();
  private LttoProvinceFileDownloadTask task;
  @Autowired
  private LttoProvinceFileDownloadManager manager;
  @Autowired
  private ParaSysparameCache paraSysparameCache;
  @Autowired
  private ParaGamePeriodInfoService periodInfoService;
  @Autowired
  private ParaGameinfoService paraGameinfoService;
  @Autowired
  private PeriodManager periodManager;
  private Future<?> future;
  private Integer poolMinSzie = 1;
  private Integer poolMaxSzie = 10;
  private Long keepAliveTime = 1L;
  private Integer capacity = 10;


  public void setUp() {
    synchronized (locker) {
      ThreadFactory namedThreadFactory = new LttoProvinceFileDownloadThreadFactory();
      executorService = new ThreadPoolExecutor(poolMinSzie,  poolMaxSzie, keepAliveTime, TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue<>(capacity), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
      task = new LttoProvinceFileDownloadTask(manager, paraSysparameCache, periodManager);
      future = this.executorService.submit(task);
    }
  }

  public void tearDown() {
    synchronized (locker) {
      task.shutdown();
      executorService.shutdown();
    }
  }

  public void reload() {
    task.shutdown();
    future.cancel(true);
    manager.afterPropertiesSet();
    task = new LttoProvinceFileDownloadTask(manager, paraSysparameCache, periodManager);
    future = executorService.submit(task);
  }

    @Scheduled(cron = "0 0 1 * * ?")
    public void runSchedule() {
        log.debug("执行更新下载zip文件参数定时任务");
        this.reload();
    }

  public class LttoProvinceFileDownloadThreadFactory implements ThreadFactory {
    @Override
    public Thread newThread(Runnable r) {
      return new LttoProvinceFileDownloadThread(r, "省明细文件下载线程");
    }
  }

  public class LttoProvinceFileDownloadThread extends Thread {

    public LttoProvinceFileDownloadThread(Runnable target, String name) {
      super(target, name);
      setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
          log.warn("线程[" + t.getName() + "]发生未捕获异常", e);
        }
      });
    }

    @Override
    public void run() {
      log.warn("线程[" + this.getName() + "]运行");
      try {
        super.run();
      } finally {
        log.warn("线程[" + this.getName() + "]退出");
      }
    }
  }


}
