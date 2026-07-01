package tracklistd.api.Entity;

import jakarta.persistence.*;
import tracklistd.api.Entity.Enums.ListType;
import tracklistd.api.Entity.Enums.ModerationStatus;
import tracklistd.api.Entity.Enums.Privacy;
import tracklistd.api.Entity.Interfaces.Reportable;
import tracklistd.api.Entity.Enums.ModerationStatus;

import java.util.*;

@Entity
@DiscriminatorValue("media_list")
@PrimaryKeyJoinColumn(name = "media_list_id")
public class MediaList extends Publication implements Reportable {
    @Enumerated(EnumType.STRING)
    @Column(name = "type_of_list", nullable = false)
    private ListType typeOfList;

    @Column(name = "list_name", nullable = false)
    private String listName;

    @Column(name = "is_favorite")
    private Boolean isFavorite;

    @ManyToMany
    @JoinTable(name = "media_list_media", joinColumns = @JoinColumn(name = "media_list_id"), inverseJoinColumns = @JoinColumn(name = "media_id"))
    private Set<Media> media = new LinkedHashSet<Media>();

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "cover_image_url", nullable = true)
    private String coverImageUrl;

    @ElementCollection
    @CollectionTable(name = "media_list_tags", joinColumns = @JoinColumn(name = "media_list_id"))
    @Column(name = "tag_name", length = 50)
    private Set<String> tags = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", nullable = false, updatable = true)
    private ModerationStatus moderationStatus = ModerationStatus.ACTIVE;

    public MediaList(User author, ListType typeOfList, String listName, Privacy whoCanSee,
            Boolean isFavorite, String description, String coverImageUrl, Set<String> tags) {
        super(author, whoCanSee);
        this.typeOfList = typeOfList;
        this.listName = listName;
        this.isFavorite = isFavorite;
        this.description = description;
        this.coverImageUrl = coverImageUrl;
        this.tags = tags != null ? tags : new HashSet<>();
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

    public void changePrivacy(Privacy whoCanSee) {
        this.setWhoCanSee(whoCanSee);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public void addTags(String tag) {
        this.tags.add(tag);
    }

    public void updateTags(Set<String> newTags) {
        this.tags.clear();
        if (newTags != null) {
            this.tags.addAll(newTags);
        }
    }

    public Integer calculateTotalDurationMs() {
        if (this.media == null || this.media.isEmpty()) {
            return 0;
        }
        return this.media.stream()
                .mapToInt(Media::getTotalDurationMs)
                .sum();
    }

    @Override
    public ModerationStatus getStatusModeration() {
        return this.moderationStatus;
    }

    public void setModerationStatus(ModerationStatus moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    @Override
    public String getContentReported() {
        return this.listName + (this.description != null ? " - " + this.description : "");
    }

    @Override
    public Reportable getTarget() {
        return super.getAuthorPublication();
    }
}
