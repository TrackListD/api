package tracklistd.api.Dto.Artist;

import tracklistd.api.Entity.Artist;

public record ArtistMinDTO(
        String id,
        String name,
        String profilePictureURL) {
    public ArtistMinDTO(Artist artist) {
        this(
                artist.getSpotifyID(),
                artist.getName(),
                artist.getProfilePictureURL());
    }
}