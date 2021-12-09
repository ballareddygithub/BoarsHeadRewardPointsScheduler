package com.bh.rewardpoints.scheduler;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import com.bh.rewardpoints.exception.ISpringSchedulerException;
import com.bh.rewardpoints.service.SchedulerService;

@Named
public class RewardPointsScheduler {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Inject
	private SchedulerService schedulerService;
	
	@Scheduled(cron = "${scheduler.cron.expression}")  
	public void schedule() throws ISpringSchedulerException {
		logger.info("Scheduler started ......");
		schedulerService.schedule();
		logger.info("Scheduler end ......");
	}

}
