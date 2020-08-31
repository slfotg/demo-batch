package com.example.demo;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.example.demo.reader.ListItemStreamReader;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableBatchProcessing
@Slf4j
public class BatchConfig {

    public static final String MAP_CONTEXT = "map";

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(8);
        return executor;
    }

    @Primary
    @Bean
    public Job job1(JobBuilderFactory jobBuilderFactory, Step step1) {
        // @formatter:off
        return jobBuilderFactory.get("job1")
                .start(step1)
                .build();
        // @formatter:on
    }

    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory, TaskExecutor taskExecutor) {
        // @formatter:off
        return stepBuilderFactory.get("step1")
                .listener(stepListener())
                .<Integer, Integer>chunk(10000)
                .reader(intReader())
                .writer(intWriter())
                .listener(writeListener(null))
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    @StepScope
    public ItemWriteListener<Integer> writeListener(
            @Value("#{stepExecutionContext['" + MAP_CONTEXT + "']}") final Map<Integer, Integer> map) {
        return new ItemListenerSupport<Integer, Integer>() {

            @Override
            public void afterWrite(List<? extends Integer> items) {
                items.forEach(item -> map.put(item, map.getOrDefault(item, 0) + 1));
            }

        };
    }

    @Bean
    public StepExecutionListener stepListener() {
        return new StepExecutionListener() {

            @Override
            public void beforeStep(StepExecution stepExecution) {
                stepExecution.getExecutionContext().put(MAP_CONTEXT, new ConcurrentHashMap<Integer, Integer>());
            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                @SuppressWarnings("unchecked")
                Map<Integer, Integer> map = (Map<Integer, Integer>) stepExecution.getExecutionContext()
                        .get(MAP_CONTEXT);
                for (Entry<Integer, Integer> entry : map.entrySet()) {
                    if (entry.getValue() != 1) {
                        log.error("Something didn't work properly");
                        return ExitStatus.FAILED;
                    }
                }
                log.info("Everything worked perfectly");
                return null;
            }

        };
    }

    @Bean
    public ItemReader<Integer> intReader() {
        ItemStreamReader<Integer> streamReader = new ListItemStreamReader<>(
                IntStream.range(1, 100000).boxed().collect(Collectors.toList()));

        SynchronizedItemStreamReader<Integer> reader = new SynchronizedItemStreamReader<>();
        reader.setDelegate(streamReader);
        return reader;
    }

    @Bean
    public ItemWriter<Integer> intWriter() {
        return items -> items.forEach(i -> log.info("int: {}", i));
    }
}
