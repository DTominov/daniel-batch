package com.daniel.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class AccuracyDecider implements JobExecutionDecider {

	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		String orderedItem = "SOCKS";
		String shippedItem = "SHIRTS";		
		String result;
		
		if(orderedItem.equalsIgnoreCase(shippedItem)) {
			result = "THANKS";
		}else {
			result = "REFUND";
		}
		
		return new FlowExecutionStatus(result);
	}

}
