package tracklistd.api.Entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type_media", discriminatorType = DiscriminatorType.STRING)
@NoArgsConstructor
public abstract class Media {

    @Id
    protected String spotifyID;

    @Column(nullable = false, length = 150)
    protected String title;

    @Column(name = "cover_url", length = 500) // Links de imagens do Spotify podem ser longos, 500 é um tamanho seguro
    protected String coverUrl;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "media_artists", joinColumns = @JoinColumn(name = "media_id"), inverseJoinColumns = @JoinColumn(name = "artist_id"))
    @OrderColumn(name = "artist_order")
    protected List<Artist> authors = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "media_genres", joinColumns = @JoinColumn(name = "media_id"))
    @Column(name = "genre")
    protected Set<String> musicGenres = new LinkedHashSet<>();

    @Column(name = "release_date")
    protected LocalDate releaseDate;

    public Artist getMainArtist() {
        if (this.authors != null && !this.authors.isEmpty())
            return this.authors.get(0);
        return null;
    }

    public void addGenre(String genre) {
        this.musicGenres.add(genre);
    }

    public void addAuthor(Artist author) {
        this.authors.add(author);
    }

    public abstract Integer getTotalDurationMs();
}
