package project.camus.batch.member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MemberJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {

        //TODO: beforeJob
        log.info("MemberJobListener's beforeJob");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        //TODO: afterJob
        log.info("MemberJobListener's afterJob");
    }
}
