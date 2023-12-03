package project.camus.batch.member;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import project.camus.aws.client.AwsKmsClient;

@Slf4j
@ComponentScan(basePackages = {
    "project.camus.aws.client"
})
@Configuration
public class MemberJobConfig {

    @Bean
    public Job memberJob(JobRepository repository, @Qualifier("memberStep") Step memberStep,
        MemberJobListener memberJobListener) {

        return new JobBuilder("memberJob", repository)
            .listener(memberJobListener)
            .start(memberStep)
            .build();
    }

    @Bean
    public Step memberStep(JobRepository repository,
        PlatformTransactionManager transactionManager, AwsKmsClient awsKmsClient,
        DataSource dataSource) {

        return new StepBuilder("memberStep", repository)
            .<MemberRecord, MemberRecord>chunk(2, transactionManager)
            .reader(memberItemReader())
            .processor(memberItemProcessor(awsKmsClient))
            .writer(memberItemWriter(dataSource))
            .build();
    }

    @Bean
    public FlatFileItemReader<MemberRecord> memberItemReader() {

        return new FlatFileItemReaderBuilder<MemberRecord>()
            .name("memberItemReader")
            .resource(new ClassPathResource("member-sample.csv"))
            .delimited()
            .names("username", "phone")
            .targetType(MemberRecord.class)
            .build();
    }

    @Bean
    public MemberItemProcessor memberItemProcessor(AwsKmsClient awsKmsClient) {

        return new MemberItemProcessor(awsKmsClient);
    }

    @Bean
    public JdbcBatchItemWriter<MemberRecord> memberItemWriter(DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<MemberRecord>()
            .sql("INSERT INTO member (username, phone) VALUES (:username, :phone)")
            .dataSource(dataSource)
            .beanMapped()
            .build();
    }
}
