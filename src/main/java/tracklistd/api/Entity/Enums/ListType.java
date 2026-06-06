package tracklistd.api.Entity.Enums;

import tracklistd.api.Entity.Album;
import tracklistd.api.Entity.Media;
import tracklistd.api.Entity.Music;

public enum ListType {
    ALBUM{
        @Override
        public boolean matches(Media media) {
            return media instanceof Album;
        }

    },
    MUSIC{
        @Override
        public boolean matches(Media media) {
            return media instanceof Music;
        }

    };

    public abstract boolean matches(Media media);

}
