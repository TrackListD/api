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
    }

    @Override
    public Integer getTotalDurationMs() {
        if (this.musics == null || this.musics.isEmpty()) {
            return 0;
        }
        return this.musics.stream()
                .mapToInt(Music::getTotalDurationMs)
                .sum();
    }
}
