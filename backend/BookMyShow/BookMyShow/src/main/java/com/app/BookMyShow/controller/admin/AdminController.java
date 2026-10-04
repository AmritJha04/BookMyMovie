package com.app.BookMyShow.controller.admin;

import com.app.BookMyShow.dto.CreateShowRequest;
import com.app.BookMyShow.dto.ShowResponse;
import com.app.BookMyShow.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private  AdminService adminShowService;

    @PostMapping(value = "/show",consumes = "application/json")
    public ResponseEntity<ShowResponse> createShow( @RequestBody CreateShowRequest request) throws Exception {
        try{
        ShowResponse response = adminShowService.createShow(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (Exception e){
           // System.out.println(e.gey);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }




}
