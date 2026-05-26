package tracklistd.api.Entity;

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
@Table(name = "artists")
@NoArgsConstructor
public class Artist {

    @Id
    private String spotifyID;

    @Column(nullable = false, length = 150)
    private String name;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String bio;

    @Setter
    @Column(name = "profile_picture_url")
    private String profilePictureURL;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "artist_genres", joinColumns = @JoinColumn(name = "artist_id"))
    @Column(name = "genre")
    private Set<String> musicalGenres = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Media> releasedMedia = new ArrayList<>();
}
