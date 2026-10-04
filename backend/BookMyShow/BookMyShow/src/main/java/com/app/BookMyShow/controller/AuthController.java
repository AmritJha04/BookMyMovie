package com.app.BookMyShow.controller;

import com.app.BookMyShow.dto.CredentialDto;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173",allowCredentials = "true")
public class AuthController {

    @PostMapping(value="/login" , consumes = {"application/json","text/plain"})
    public ResponseEntity<Void> userLogin(@RequestBody CredentialDto credentialDto){
        // System.out.println("hit");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
