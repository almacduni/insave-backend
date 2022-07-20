package org.save.validation.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import org.save.validation.NullOrNotBlankValidator;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = NullOrNotBlankValidator.class)
public @interface NullOrNotBlank {

  String message() default "{javax.validation.constraints.NotBlank.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
