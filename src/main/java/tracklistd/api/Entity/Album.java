package tracklistd.api.Entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "albums")
@DiscriminatorValue("album")
@NoArgsConstructor
public class Album extends Media {

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderColumn(name = "track_number")
    private List<Music> musics = new ArrayList<>();

    @Column(name = "is_single", nullable = false)
    private boolean isSingle;

    public void addMusic(Music newMusic) {
        this.musics.add(newMusic);
        if (newMusic.getDuration() != null) {
            this.duration = (this.duration == null ? 0 : this.duration) + newMusic.getDuration();
        }
    }

    @Column(name = "duration")
    private Integer duration = 0;

    @Override
    public Integer getTotalDurationMs() {
        return this.duration != null ? this.duration : 0;
    }
}
