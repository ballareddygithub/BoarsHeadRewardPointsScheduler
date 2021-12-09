package com.bh.rewardpoints.scheduler;

import static org.mockito.Mockito.times;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.bh.rewardpoints.exception.ISpringSchedulerException;
import com.bh.rewardpoints.service.SchedulerService;

@RunWith(MockitoJUnitRunner.class)
public class RewardPointsSchedulerTest {

	@InjectMocks
	private RewardPointsScheduler pointsScheduler;
	
	@Mock
	private SchedulerService schedulerService;
	
	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	@Test
	public void scheduleTest() throws ISpringSchedulerException {
		Mockito.doNothing().when(schedulerService).schedule();
		pointsScheduler.schedule();
		Mockito.verify(schedulerService, times(1)).schedule();
	}
}
