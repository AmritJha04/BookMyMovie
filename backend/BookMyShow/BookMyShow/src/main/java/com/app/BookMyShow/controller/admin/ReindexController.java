package com.app.BookMyShow.controller.admin;

import com.app.BookMyShow.search.service.ReindexService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/adminreindex")
@RequiredArgsConstructor
public class ReindexController {

    private final ReindexService reindexService;

    @PostMapping("/reindex")
    public ResponseEntity<String> reindex() {
        reindexService.reindexAll();
        return ResponseEntity.ok("Reindex complete");
    }
}