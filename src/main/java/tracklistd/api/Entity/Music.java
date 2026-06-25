package tracklistd.api.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "musics")
@DiscriminatorValue("music")
@NoArgsConstructor
public class Music extends Media {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    @Column(name = "duration")
    private Integer duration;
}
