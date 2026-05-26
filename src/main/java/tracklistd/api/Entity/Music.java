package tracklistd.api.Entity;

import java.time.Duration;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Table(name = "musics")
@DiscriminatorValue("music")
@NoArgsConstructor
public class Music extends Media {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    @Setter
    @Column(name = "duration")
    private Duration duration;

    public boolean isSingle() {
        return this.album == null;
    }
}
