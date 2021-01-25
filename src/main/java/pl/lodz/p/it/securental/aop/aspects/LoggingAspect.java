package pl.lodz.p.it.securental.aop.aspects;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.infinispan.Cache;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.lodz.p.it.securental.aop.annotations.RequiresNewTransaction;
import pl.lodz.p.it.securental.utils.ApplicationProperties;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@AllArgsConstructor
@RequiresNewTransaction
public class LoggingAspect {

    private final Cache<String, String> logCache;

    @Around("execution(public * pl.lodz.p.it.securental.controllers.mo*..*.*(..)) " +
            "|| execution(public * pl.lodz.p.it.securental.services.mo*..*.*(..)) " +
            "|| execution(public * pl.lodz.p.it.securental.adapters.mo*..*.*(..)) " +
            "|| execution(public * pl.lodz.p.it.securental.repositories.mo*..*.*(..))")
    public Object logInvocation(ProceedingJoinPoint jp) throws Throwable {
        LocalDateTime interceptionTime = LocalDateTime.now();
        StringBuilder message = new StringBuilder("Intercepted method invocation: ");
        message.append(jp.getSignature().getDeclaringTypeName().substring(jp.getSignature().getDeclaringTypeName().lastIndexOf(".") + 1))
                .append("::")
                .append(jp.getSignature().getName());
        message.append(" | Interception time: ").append(interceptionTime);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            message.append(" | User: ").append(ApplicationProperties.UNAUTHENTICATED_PRINCIPAL);
        } else {
            message.append(" | User: ").append(authentication.getName());
        }
        message.append(" | With parameters: ");

        if (jp.getArgs() != null) {
            for (int i = 0; i < jp.getArgs().length; i++) {
                if (jp.getArgs()[i] != null) {
                    message.append(jp.getArgs()[i].toString());
                } else {
                    message.append("null");
                }
                if (i != jp.getArgs().length - 1) {
                    message.append(", ");
                }
            }
        } else {
            message.append("null");
        }

        Object result;
        try {
            result = jp.proceed();
        } catch (Throwable e) {
            message.append(" | With exception: ");
            message.append(e);
            log.error(message.toString());
            throw e;
        }

        message.append(" | Returned value: ");
        if (null == result) {
            message.append("null");
        } else {
            message.append(result.toString());
        }

        String messageString = message.toString();
        log.info(messageString);
        logCache.put(messageString, "");
//        logRepository.save(Log.builder()
//                .message(messageString)
//                .build()
//        );
        return result;
    }
}
