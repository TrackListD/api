package tracklistd.api.Entity;

import jakarta.persistence.*;
import tracklistd.api.Entity.Enums.ListType;
import tracklistd.api.Entity.Enums.Privacy;
import java.util.*;

@Entity
@DiscriminatorValue("media_list")
@PrimaryKeyJoinColumn(name = "media_list_id")
public class MediaList extends Publication {
    @Enumerated(EnumType.STRING)
    @Column(name = "type_of_list", nullable = false)
    private ListType typeOfList;

    @Column(name = "list_name", nullable = false)
    private String listName;

    @Enumerated(EnumType.STRING)
    @Column(name = "who_can_see", nullable = false)
    private Privacy whoCanSee;

    @Column(name = "is_favorite")
    private Boolean isFavorite;

    @ManyToMany
    @JoinTable(name = "media_list_media", joinColumns = @JoinColumn(name = "media_list_id"), inverseJoinColumns = @JoinColumn(name = "media_id"))
    private Set<Media> media = new LinkedHashSet<Media>();

    public MediaList(User author, ListType typeOfList, String listName, Privacy whoCanSee, Boolean isFavorite) {
        super(author);
        this.typeOfList = typeOfList;
        this.listName = listName;
        this.whoCanSee = whoCanSee;
        this.isFavorite = isFavorite;
    }

    public MediaList() {
    }

    public ListType getTypeOfList() {
        return typeOfList;
    }

    public String getListName() {
        return listName;
    }

    public void changeListName(String listName) {
        this.listName = listName;
    }

    public Privacy getWhoCanSee() {
        return whoCanSee;
    }

    public void changePrivacy(Privacy whoCanSee) {
        this.whoCanSee = whoCanSee;
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public Set<Media> getMedia() {
        return Collections.unmodifiableSet(media);
    }

    public void addMedia(Media media) {
        this.media.add(media);
    }

    public void removeMedia(Media media) {
        this.media.remove(media);
    }
}
