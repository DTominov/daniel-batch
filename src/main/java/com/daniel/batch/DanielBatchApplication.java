package com.daniel.batch;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableBatchProcessing
public class DanielBatchApplication {
	
	private boolean GOT_LOST = false;
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	
	@Bean
	public Job deliverPackageJob() {
		return this.jobBuilderFactory.get("deliverPackageJob")
				.start(packageItemStep())
				.next(driveToAddressStep())
				.on("FAILED").to(storePackageForCustomerStep())
				.from(driveToAddressStep())
				.on("*").to(decider())
				.on("PRESENT").to(givePackageToCustomerStep())			
				.from(decider()).on("NOT PRESENT").to(leavePackageAtDoorStep())
				.next(checkOrderAccuracyStep()).on("*").to(accuracyDecider())
				.on("THANKS").to(orderAccurateStep())
				.from(accuracyDecider()).on("REFUND").to(orderNotAccurateStep())				
				.end()
				.build();

	}
	
	@Bean
	public JobExecutionDecider decider() {
		return new DeliveryDecider();		
	}
	
	@Bean
	public JobExecutionDecider accuracyDecider() {
		return new AccuracyDecider();		
	}
	

	
	@Bean
	public Step storePackageForCustomerStep() {
		return stepBuilderFactory.get("storePackageForCustomerStep").tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("Storing package for customer until address is located ");
				return RepeatStatus.FINISHED;
			}
		}).build();

	}

	@Bean
	public Step givePackageToCustomerStep() {
		return stepBuilderFactory.get("givePackageToCustomerStep").tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("Item has been delivered");
				return RepeatStatus.FINISHED;
			}
		}).build();

	}

	@Bean
	public Step driveToAddressStep() {
		return stepBuilderFactory.get("driveToAddressStep").tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				if(GOT_LOST) {
					throw new RuntimeException("Cannot locate customer with address provided");
				}
				System.out.println("Item is being delivered");
				return RepeatStatus.FINISHED;
			}
		}).build();

	}

	@Bean
	public Step packageItemStep() {
		return this.stepBuilderFactory.get("packageItemStep").tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("item has been packaged");
				return RepeatStatus.FINISHED;
			}
		}).build();
	}
	
	@Bean
	public Step leavePackageAtDoorStep() {
		return this.stepBuilderFactory.get("leavePackageAtDoorStep").tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("Package was left at door");
				return RepeatStatus.FINISHED;
			}
		}).build();
	}
	
	public Step orderAccurateStep() {
		return this.stepBuilderFactory.get("orderAccurateStep").tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("Thank you for your patronage");
				return RepeatStatus.FINISHED;
			}
		}).build();
	}
	
	public Step orderNotAccurateStep() {
		return this.stepBuilderFactory.get("orderNotAccurateStep").tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("Issuing refund to customer");
				return RepeatStatus.FINISHED;
			}
		}).build();
	}
	
	public Step checkOrderAccuracyStep() {
		return this.stepBuilderFactory.get("checkOrderAccuracyStep").tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("Checking accuracy of order!!!");
				return RepeatStatus.FINISHED;
			}
		}).build();
	}
	
	

	public static void main(String[] args) {
		SpringApplication.run(DanielBatchApplication.class, args);
	}

}
