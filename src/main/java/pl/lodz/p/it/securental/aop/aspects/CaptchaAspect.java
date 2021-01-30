package pl.lodz.p.it.securental.aop.aspects;

import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pl.lodz.p.it.securental.dto.model.mok.CaptchaResponse;
import pl.lodz.p.it.securental.exceptions.mok.InvalidCaptchaException;
import pl.lodz.p.it.securental.utils.ApplicationProperties;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@AllArgsConstructor
public class CaptchaAspect {

    private final RestTemplate restTemplate;

    @Around("@annotation(pl.lodz.p.it.securental.aop.annotations.CaptchaRequired)")
    public Object validateCaptcha(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String captchaResponse = request.getHeader("Captcha-Response");
        if (!validateCaptchaInternal(captchaResponse)) {
            throw new InvalidCaptchaException();
        }
        return joinPoint.proceed();
    }

    private boolean validateCaptchaInternal(String captchaResponse){
        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("secret", ApplicationProperties.CAPTCHA_SECRET_KEY);
        requestMap.add("response", captchaResponse);

        CaptchaResponse apiResponse = restTemplate.postForObject("https://www.google.com/recaptcha/api/siteverify", requestMap, CaptchaResponse.class);
        if(apiResponse == null){
            return false;
        }

        return apiResponse.isSuccess();
    }
}
