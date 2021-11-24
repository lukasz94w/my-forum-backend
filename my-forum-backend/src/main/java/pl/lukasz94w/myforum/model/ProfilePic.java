package pl.lukasz94w.myforum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table (name = "profilepics")
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
