package tracklistd.api.Entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "albums")
@DiscriminatorValue("album")
@NoArgsConstructor
public class Album extends Media {

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderColumn(name = "track_number")
    private List<Music> musics;

    public void addMusic(Music newMusic) {
        this.musics.add(newMusic);
    }
}
