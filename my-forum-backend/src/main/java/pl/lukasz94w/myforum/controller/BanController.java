package pl.lukasz94w.myforum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.lukasz94w.myforum.request.BanRequest;
import pl.lukasz94w.myforum.service.BanService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/ban")
public class BanController {

    private final BanService banService;

    @Autowired
    public BanController(BanService banService) {
        this.banService = banService;
    }

    @PreAuthorize("hasRole ('ADMIN')")
    @PostMapping("/banUser")
    public ResponseEntity<HttpStatus> banUser(@Valid @RequestBody BanRequest banRequest) {
        return banService.banUser(banRequest);
    }

    @PreAuthorize("hasRole ('ADMIN')")
    @PostMapping("/unBanUser")
    public ResponseEntity<HttpStatus> unBanUser(@RequestBody @NotBlank String userName) {
        return banService.unBanUser(userName);
    }

    // no authorization required because banned user cannot authorize
    @GetMapping("/checkBanStatus/{userName}")
    public boolean checkBanStatus(@NotBlank @PathVariable String userName) {
        return banService.checkBanStatus(userName);
    }
}
