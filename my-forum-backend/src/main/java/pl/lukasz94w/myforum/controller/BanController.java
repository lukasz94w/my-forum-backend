package pl.lukasz94w.myforum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.lukasz94w.myforum.request.BanRequest;
import pl.lukasz94w.myforum.service.BanService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ban")
public class BanController {

    private final BanService banService;

    @PreAuthorize("hasRole ('ADMIN')")
    @PutMapping("/banUser")
    public ResponseEntity<Void> banUser(@Valid @RequestBody BanRequest banRequest) {
        banService.banUser(banRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole ('ADMIN')")
    @PutMapping("/unBanUser")
    public ResponseEntity<Void> unBanUser(@RequestBody @NotBlank String userName) {
        banService.unBanUser(userName);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/checkBanStatus/{userName}")
    public ResponseEntity<Boolean> checkBanStatus(@NotBlank @PathVariable String userName) {
        return new ResponseEntity<>(banService.checkBanStatus(userName), HttpStatus.OK);
    }
}
