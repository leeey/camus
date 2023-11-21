package project.camus.observability.member.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {

    private Long id;

    private String name;

    private Integer age;

    private String address;
}
