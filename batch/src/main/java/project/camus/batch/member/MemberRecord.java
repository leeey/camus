package project.camus.batch.member;

import lombok.Builder;

@Builder
public record MemberRecord(String username, String phone) {

}
