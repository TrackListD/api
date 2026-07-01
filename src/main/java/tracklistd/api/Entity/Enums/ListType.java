package tracklistd.api.Entity.Enums;

import tracklistd.api.Entity.Album;
import tracklistd.api.Entity.Media;
import tracklistd.api.Entity.Music;

public enum ListType {
    ALBUM {
        @Override
        public boolean matches(Media media) {
            return media instanceof Album;
        }

        @Override
        public String toString() {
            return "ALBUM";
        }

    },
    MUSIC {
        @Override
        public boolean matches(Media media) {
            return media instanceof Music;
        }

        @Override
        public String toString() {
            return "MUSIC";
        }

    };

    public abstract boolean matches(Media media);

}
