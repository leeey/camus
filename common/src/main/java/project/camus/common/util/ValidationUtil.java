package project.camus.common.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.StringJoiner;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import project.camus.common.exception.CamusClientException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationUtil {

    public static <T> void validate(T t) {

        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = validatorFactory.getValidator();
            Set<ConstraintViolation<T>> violations = validator.validate(t);

            if (!violations.isEmpty()) {
                StringJoiner joiner = new StringJoiner(", ");
                for (ConstraintViolation<T> violation : violations) {
                    joiner.add(violation.getPropertyPath().toString());
                }
                throw new CamusClientException(new IllegalArgumentException(joiner.toString()));
            }
        }
    }
}
