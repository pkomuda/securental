package pl.lodz.p.it.securental.controllers.accounts.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.it.securental.annotations.NeverTransaction;
import pl.lodz.p.it.securental.controllers.accounts.AccountController;
import pl.lodz.p.it.securental.dto.accounts.AccountDto;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;
import pl.lodz.p.it.securental.services.accounts.AccountService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import static pl.lodz.p.it.securental.utils.ApplicationProperties.APPLICATION_NAME;

@Slf4j
@CrossOrigin
@RestController
@AllArgsConstructor
@NeverTransaction
public class AccountControllerImpl implements AccountController {

    private final AccountService accountService;
    private final GoogleAuthenticator googleAuthenticator;

    @Override
    @PostMapping("/account")
    public void addAccount(@RequestBody AccountDto accountDto) throws ApplicationBaseException {
        accountService.addAccount(accountDto);
    }

    @Override
    @PostMapping("/register")
    public void register(@RequestBody AccountDto accountDto, HttpServletResponse response) throws Exception {
        accountService.addAccount(accountDto);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        final GoogleAuthenticatorKey key = googleAuthenticator.createCredentials(accountDto.getUsername());
        String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(APPLICATION_NAME, accountDto.getUsername(), key);
        BitMatrix bitMatrix = qrCodeWriter.encode(otpAuthURL, BarcodeFormat.QR_CODE, 200, 200);
        ServletOutputStream outputStream = response.getOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        outputStream.close();
    }

    @Override
    @GetMapping("/account/{username}")
    public AccountDto getAccount(@PathVariable String username) throws ApplicationBaseException {
        return accountService.getAccount(username);
    }
}
