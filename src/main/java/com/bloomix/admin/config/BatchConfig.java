package com.bloomix.admin.config;

import com.bloomix.admin.domain.batch.support.BatchJobHistoryListener;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.job.AbstractJob;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.batch.autoconfigure.BatchTaskExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class BatchConfig {

  @Bean
  @BatchTaskExecutor
  public ThreadPoolTaskExecutor batchTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setThreadNamePrefix(JpaAuditingConfig.BATCH_THREAD_PREFIX);
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(4);
    executor.setQueueCapacity(20);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(30);
    return executor;
  }

  @Bean
  public ThreadPoolTaskScheduler batchTaskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setThreadNamePrefix(JpaAuditingConfig.BATCH_THREAD_PREFIX + "scheduler-");
    scheduler.setPoolSize(2);
    return scheduler;
  }

  @Bean
  public JobRegistry jobRegistry() {
    return new MapJobRegistry();
  }

  @Bean
  public static BeanPostProcessor batchJobHistoryListenerRegistrar(ObjectProvider<BatchJobHistoryListener> listener) {
    return new BeanPostProcessor() {
      @Override
      public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof AbstractJob job) {
          job.registerJobExecutionListener(listener.getObject());
        }
        return bean;
      }
    };
  }
}
