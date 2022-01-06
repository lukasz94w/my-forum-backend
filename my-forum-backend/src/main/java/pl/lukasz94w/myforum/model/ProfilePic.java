package pl.lukasz94w.myforum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table (name = "profile_pics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfilePic {

    @Id
    private Long id;

    @Lob
    private byte[] data;
}
