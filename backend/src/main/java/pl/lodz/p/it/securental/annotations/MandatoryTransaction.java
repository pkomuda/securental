package pl.lodz.p.it.securental.annotations;

import org.springframework.core.annotation.AliasFor;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.exceptions.db.PropertyNotFoundException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Transactional
public @interface MandatoryTransaction {

    @AliasFor(annotation = Transactional.class, attribute = "propagation")
    Propagation propagation() default Propagation.MANDATORY;

    @AliasFor(annotation = Transactional.class, attribute = "isolation")
    Isolation isolation() default Isolation.READ_COMMITTED;

    @AliasFor(annotation = Transactional.class, attribute = "rollbackFor")
    Class<? extends Throwable>[] rollbackFor() default ApplicationBaseException.class;

    @AliasFor(annotation = Transactional.class, attribute = "noRollbackFor")
    Class<? extends Throwable>[] noRollbackFor() default {PropertyNotFoundException.class, PropertyReferenceException.class};
}
