package com.example.module1.controller;

import com.example.module1.entities.Medecin;
import com.example.module1.entities.ProfessionnelSante;
import com.example.module1.exception.ConfirmationMailException;
import com.example.module1.exception.InvalidTokenException;
import com.example.module1.exception.TokenExpiredException;
import com.example.module1.service.ConfirmeMailService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
public class ConfirmationMailController {

    private ConfirmeMailService confirmeMailService;

    @GetMapping("/register/confirmation")
    public RedirectView confirmEmail(@RequestParam("token") String token) throws ConfirmationMailException {
        Object person = confirmeMailService.confirmEmail(token);

        if (person instanceof Medecin) {
            return new RedirectView("http://localhost:3000/auth/medecins");
        } else if (person instanceof ProfessionnelSante) {
            return new RedirectView("http://localhost:3000/auth/professionnels");
        } else {
            throw new ConfirmationMailException("Unknown person type");
        }
    }

    @PostMapping("/register/resend-token")
    public ResponseEntity<String> resendToken(@RequestParam("email") String email) {
        confirmeMailService.resendToken(email);
        return ResponseEntity.ok("Token resent successfully");

    }

    @ExceptionHandler(ConfirmationMailException.class)
    public ResponseEntity<String> handleConfirmationMailException(ConfirmationMailException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<String> handleTokenExpiredException(TokenExpiredException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.GONE);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
