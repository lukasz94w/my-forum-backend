package pl.lukasz94w.myforum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

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
    @Type(type = "org.hibernate.type.ImageType")
    private byte[] data;
}
