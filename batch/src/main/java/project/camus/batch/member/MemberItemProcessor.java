package project.camus.batch.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import project.camus.aws.client.AwsKmsClient;

@Slf4j
@RequiredArgsConstructor
public class MemberItemProcessor implements ItemProcessor<MemberRecord, MemberRecord> {

    private final AwsKmsClient awsKmsClient;

    @Override
    public MemberRecord process(MemberRecord member) {

        log.info("member is encrypted...");
        return MemberRecord.builder()
            .username(awsKmsClient.encrypt(member.username()))
            .phone(awsKmsClient.encrypt(member.phone()))
            .build();
    }
}
