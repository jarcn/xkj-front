package com.cwlrdc.front.task;

import com.cwlrdc.commondb.para.entity.ParaGamePeriodInfo;
import com.cwlrdc.front.common.ParaSysparameCache;
import com.cwlrdc.front.common.PeriodManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by yangqiju on 2017/9/2.
 */
@Slf4j
public class LttoProvinceFileDownloadTask implements Runnable {

  private volatile boolean stop = false;

  private Object lock = new Object();
  private LttoProvinceFileDownloadManager manager;
  private ParaSysparameCache paraSysparameCache;
  private PeriodManager periodManager;

  public LttoProvinceFileDownloadTask(LttoProvinceFileDownloadManager manager,
      ParaSysparameCache paraSysparameCache,
      PeriodManager periodManager) {
    this.manager = manager;
    this.periodManager = periodManager;
    this.paraSysparameCache = paraSysparameCache;
  }


  public void shutdown() {
    stop = true;
    this.wakeup();
    manager.destroy();
  }

  private void wakeup() {
    synchronized (lock) {
      lock.notify();
    }
  }

  private ParaGamePeriodInfo getAndWaitCurrentGameAndPeriod() throws InterruptedException {
    ParaGamePeriodInfo currentGameAndPeriod = periodManager.getCurrentGameAndPeriod();
    synchronized (lock) {
      while (!stop && !Thread.currentThread().isInterrupted() && currentGameAndPeriod == null) {
        currentGameAndPeriod = periodManager.getCurrentGameAndPeriod();
        log.debug("当前没有获得当前游戏期号,等待5秒");
        lock.wait(TimeUnit.SECONDS.toMillis(5));
      }
    }
    return currentGameAndPeriod;
  }

  @Override
  public void run() {
    log.debug("启动zip文件下载线程");
    try {
      ParaGamePeriodInfo currentGameAndPeriod = this.getAndWaitCurrentGameAndPeriod();
      if (stop) {
        log.debug("取消线程下载等待");
        return;
      }
      String gameCode = currentGameAndPeriod.getGameCode();
      String periodNum = currentGameAndPeriod.getPeriodNum();

      long planSleepMilles = this.caclWaitMilliseSeconds(gameCode);
      while (!stop && !Thread.currentThread().isInterrupted() && planSleepMilles > 0) {
        planSleepMilles = this.caclWaitMilliseSeconds(gameCode);
        this.waitPlanTime(planSleepMilles, gameCode, periodNum);

        currentGameAndPeriod = this.getAndWaitCurrentGameAndPeriod();
        gameCode = currentGameAndPeriod.getGameCode();
        periodNum = currentGameAndPeriod.getPeriodNum();
      }

      if (!stop) {
        log.warn("游戏[" + gameCode + "]期号[" + periodNum + "]当天为开奖日,开始进行文件下载..");
        manager.execute(gameCode, periodNum);
        log.warn("游戏[" + gameCode + "]期号[" + periodNum + "]完成文件下载..");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.debug("取消线程下载等待", e);
    } catch (Exception e) {
      log.warn("文件等待处理异常", e);
    }
  }

  private void waitPlanTime(long planSleepMilles, String gameCode, String periodNum)
      throws InterruptedException {
    synchronized (lock) {
      long tmpWaitMillisecondes = TimeUnit.MINUTES.toMillis(1);
      long waitTime = planSleepMilles > tmpWaitMillisecondes ? tmpWaitMillisecondes : planSleepMilles;
      log.info("游戏[" + gameCode + "]期号[" + periodNum + "]销量明细文件获取线程等待[" + waitTime + "]毫秒,总等待时间["
          + planSleepMilles + "]");
      lock.wait(TimeUnit.MILLISECONDS.toMillis(waitTime));
    }
  }

  /**
   * 计算出当前需要的等待时间
   */
  private long caclWaitMilliseSeconds(String gameCode) {
    int downloadLttoFileTime = paraSysparameCache.getDownloadLttoFileTime(gameCode);
    Date date = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
    date = DateUtils.addHours(date, downloadLttoFileTime);
    return date.getTime() - System.currentTimeMillis();
  }

}
