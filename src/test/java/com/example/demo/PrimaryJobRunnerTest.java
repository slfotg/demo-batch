package com.example.demo;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Spy;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class PrimaryJobRunnerTest {

    @Autowired
    Job job;

    @Spy
    ConfigurableApplicationContext context;

    @Spy
    JobLauncher jobLauncher;

    @Captor
    ArgumentCaptor<JobParameters> jobParamsCaptor;

    PrimaryJobRunner runner;

    @BeforeEach
    public void init() throws Exception {
        when(jobLauncher.run(eq(job), any(JobParameters.class))).thenReturn(null);
        runner = new PrimaryJobRunner(context, jobLauncher, job);
    }

    @Test
    void testRun() throws Exception {
        runner.run(new String[0]);
        verify(jobLauncher).run(eq(job), jobParamsCaptor.capture());
        verify(context, times(1)).close();
        JobParameters params = jobParamsCaptor.getValue();
    }
}
