package pl.lukasz94w.myforum.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public final class BanRequest {

    @FutureOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBan;

    @NotBlank
    @Size(min = 4, max = 200)
    private String reasonOfBan;

    @NotBlank
    @Size(min = 4, max = 200)
    private String userName;
}
