package pl.lodz.p.it.securental.annotations;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.lodz.p.it.securental.utils.ApplicationProperties;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(public * pl.lodz.p.it.securental.controllers..*.*(..)) " +
            "|| execution(public * pl.lodz.p.it.securental.services..*.*(..)) " +
            "|| execution(public * pl.lodz.p.it.securental.adapters..*.*(..)) " +
            "|| execution(public * pl.lodz.p.it.securental.repositories..*.*(..))")
    public Object logInvocation(ProceedingJoinPoint jp) throws Throwable {
        LocalDateTime interceptionTime = LocalDateTime.now();
        StringBuilder message = new StringBuilder("Intercepted method invocation: ");
        message.append(jp.getSignature().getDeclaringTypeName().substring(jp.getSignature().getDeclaringTypeName().lastIndexOf(".") + 1))
                .append("::")
                .append(jp.getSignature().getName());
        message.append(" Interception time: ").append(interceptionTime);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            message.append(" User: ").append(ApplicationProperties.UNAUTHENTICATED_PRINCIPAL);
        } else {
            message.append(" User: ").append(authentication.getName());
        }
        message.append(" With parameters: ");

        if (jp.getArgs() != null) {
            for (Object arg : jp.getArgs()) {
                if (arg != null) {
                    message.append(arg.toString()).append(", ");
                } else {
                    message.append("null, ");
                }
            }
        } else {
            message.append("null ");
        }

        Object result;
        try {
            result = jp.proceed();
        } catch (Throwable e) {
            message.append("With exception: ");
            message.append(e);
            log.error(message.toString());
            throw e;
        }

        message.append(" Returned value: ");
        if (null == result) {
            message.append("null ");
        } else {
            message.append(result.toString());
        }
        log.info(message.toString());
        return result;
    }
}
