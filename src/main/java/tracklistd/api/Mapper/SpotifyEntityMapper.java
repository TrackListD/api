package tracklistd.api.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import tracklistd.api.Dto.Artist.ArtistDetailsResponseDTO;
import tracklistd.api.Dto.Artist.ArtistMinDTO;
import tracklistd.api.Dto.Media.AlbumDetailsResponseDTO;
import tracklistd.api.Dto.SpotifyAPI.SpotifyAlbumResponseDTO;
import tracklistd.api.Dto.SpotifyAPI.SpotifyArtistResponseDTO;
import tracklistd.api.Dto.SpotifyAPI.SpotifyMusicResponseDTO;
import tracklistd.api.Entity.Album;
import tracklistd.api.Entity.Artist;
import tracklistd.api.Entity.Music;
import tracklistd.api.Mapper.Formatter.SpotifyDateConverter;

import java.util.List;

@Mapper(componentModel = "spring", uses = { SpotifyDateConverter.class })
public interface SpotifyEntityMapper {

    // ==========================================
    // 1. MAPEAMENTO DE ARTISTA
    // ==========================================
    @Mapping(source = "id", target = "spotifyID")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "genres", target = "musicalGenres") // MapStruct converte List (DTO) para Set (Entity)
                                                          // automaticamente
    @Mapping(target = "profilePictureURL", expression = "java(dto.getProfilePictureURL())") // Usa o seu método!
    @Mapping(target = "bio", ignore = true)
    @Mapping(target = "releasedMedia", ignore = true)
    Artist toArtistEntity(SpotifyArtistResponseDTO dto);

    @Mapping(source = "spotifyID", target = "id")
    ArtistMinDTO toArtistMinDTO(Artist artist);

    @Mapping(source = "artist", target = "artist")
    @Mapping(source = "albums", target = "albums")
    ArtistDetailsResponseDTO toArtistsDetailsResponseDTO(Artist artist, List<Album> albums);

    // ==========================================
    // 2. MAPEAMENTO DE MÚSICA
    // ==========================================
    // Como temos dois parâmetros (dto e artistasResolvidos), precisamos indicar o
    // "source" explicitamente
    @Mapping(source = "dto.id", target = "spotifyID")
    @Mapping(source = "dto.title", target = "title")
    @Mapping(source = "dto.durationMS", target = "duration") // Ajuste o target para o nome do campo na sua Entidade
    @Mapping(source = "resolvedArtists", target = "authors") // Injeta os artistas validados pelo banco
    @Mapping(target = "album", ignore = true) // Ignore ou mapeie dependendo de como você lida com o álbum da música no
                                              // banco
    @Mapping(source = "dto.releaseDate", target = "releaseDate")
    @Mapping(source = "dto.musicGenres", target = "musicGenres")
    @Mapping(target = "coverUrl", expression = "java(dto.getCoverURL())")
    Music toMusicEntity(SpotifyMusicResponseDTO dto, List<Artist> resolvedArtists);

    // ==========================================
    // 3. MAPEAMENTO DE ÁLBUM
    // ==========================================
    @Mapping(source = "dto.id", target = "spotifyID")
    @Mapping(source = "dto.title", target = "title") // Ajuste o target
    @Mapping(target = "single", expression = "java(\"single\".equalsIgnoreCase(dto.albumType()))")
    @Mapping(target = "coverUrl", expression = "java(dto.getCoverURL())") // Usa o seu método da DTO de álbum!
    @Mapping(source = "resolvedArtists", target = "authors")
    @Mapping(source = "dto.releaseDate", target = "releaseDate")
    @Mapping(source = "dto.musicGenres", target = "musicGenres")
    @Mapping(target = "musics", ignore = true)
    Album toAlbumEntity(SpotifyAlbumResponseDTO dto, List<Artist> resolvedArtists);

    @Mapping(source = "spotifyID", target = "spotifyID")
    @Mapping(source = "title", target = "name")
    @Mapping(source = "coverUrl", target = "imageUrl")
    AlbumDetailsResponseDTO toAlbumDetailsResponseDTO(Album album);

    @org.mapstruct.AfterMapping
    default void linkAlbumToMusics(@MappingTarget Album album) {
        if (album.getMusics() != null) {
            for (Music music : album.getMusics()) {
                music.setAlbum(album); // Garante que a Foreign Key seja salva no banco
            }
        }
    }
}