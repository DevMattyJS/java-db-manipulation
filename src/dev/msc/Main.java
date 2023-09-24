package dev.msc;

import dev.msc.model.Artist;
import dev.msc.model.Datasource;
import dev.msc.model.SongArtist;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        Datasource datasource = new Datasource();
        if(!datasource.open()) {
            System.out.println("Can't open datasource");
            return;
        }

        List<Artist> artists = datasource.queryArtists(Datasource.ORDER_BY_ASC);
        if (artists == null) {
            System.out.println("No artists!");
            return;
        }

        for (Artist artist : artists) {
            System.out.println("ID = " + artist.getId() + ", Name = " + artist.getName());
        }

        List<String> albumsForArtist = datasource.queryAlbumsForArtist("Pink Floyd", Datasource.ORDER_BY_ASC);
        if (albumsForArtist == null) {
            System.out.println("No albums!");
            return;
        }

        for (String album : albumsForArtist) {
            System.out.println(album);
        }

        List<SongArtist> artistsForSong = datasource.queryArtistsForSong("Go Your Own Way", Datasource.ORDER_BY_ASC);
        if(artistsForSong == null) {
            System.out.println("No artists for selected song in our DB!");
            return;
        }

        for (SongArtist artist : artistsForSong) {
            System.out.println("Artist name = " + artist.getArtistName() + " || " +
                    "Album name = " + artist.getAlbumName() + " || " + "Track = " + artist.getTrack());
        }

//        datasource.querySongsMetadata();

        datasource.close();
    }
}
