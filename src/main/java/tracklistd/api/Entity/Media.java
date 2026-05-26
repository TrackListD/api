package tracklistd.api.Entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type_media", discriminatorType = DiscriminatorType.STRING)
@NoArgsConstructor
public abstract class Media {

    @Id
    protected Long spotifyID;

    @Column(nullable = false, length = 150)
    protected String title;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "media_artists", joinColumns = @JoinColumn(name = "media_id"), inverseJoinColumns = @JoinColumn(name = "artist_id"))
    @OrderColumn(name = "artist_order")
    protected List<Artist> authors;

    protected String coverURL;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "media_genres", joinColumns = @JoinColumn(name = "media_id"))
    @Column(name = "genre")
    protected Set<String> musicGenres;

    @Column(name = "release_date")
    protected LocalDate releaseDate;

    public Artist getMainArtist() {
        if (this.authors != null && !this.authors.isEmpty())
            return this.authors.get(0);
        return null;
    }
}
