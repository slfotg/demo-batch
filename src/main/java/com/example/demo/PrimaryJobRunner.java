package com.example.demo;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class PrimaryJobRunner implements CommandLineRunner {

    @NonNull
    private final ConfigurableApplicationContext context;

    @NonNull
    private final JobLauncher jobLauncher;

    @NonNull
    private final Job primaryJob;

    @Override
    public void run(String... args) throws Exception {
        JobParameters params = new JobParametersBuilder().addDate("startTime", new Date()).toJobParameters();
        jobLauncher.run(primaryJob, params);
        context.close();
    }
}
