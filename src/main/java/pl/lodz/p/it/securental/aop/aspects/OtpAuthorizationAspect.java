package pl.lodz.p.it.securental.aop.aspects;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pl.lodz.p.it.securental.exceptions.mok.InvalidOtpCodeException;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@AllArgsConstructor
public class OtpAuthorizationAspect {

    private final GoogleAuthenticator googleAuthenticator;

    @Around("@annotation(pl.lodz.p.it.securental.aop.annotations.OtpAuthorizationRequired)")
    public Object validateOtpCode(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            int otpCode = Integer.parseInt(request.getHeader("Otp-Code"));
            if (!googleAuthenticator.authorizeUser(username, otpCode)) {
                throw new InvalidOtpCodeException();
            }
        } catch (NumberFormatException e) {
            throw new InvalidOtpCodeException(e);
        }
        return joinPoint.proceed();
    }
}
