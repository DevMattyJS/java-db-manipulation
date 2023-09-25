package dev.msc;

import dev.msc.model.Artist;
import dev.msc.model.Datasource;
import dev.msc.model.SongArtist;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Datasource datasource = new Datasource();
        if(!datasource.open()) {
            System.out.println("Can't open datasource");
            return;
        }

        List<Artist> artists = datasource.queryArtists(Datasource.ORDER_BY_ASC);
        if (artists.isEmpty()) {
            System.out.println("No artists!");
            return;
        }

        for (Artist artist : artists) {
            System.out.println("ID = " + artist.getId() + ", Name = " + artist.getName());
        }

        List<String> albumsForArtist = datasource.queryAlbumsForArtist("Pink Floyd", Datasource.ORDER_BY_ASC);
        if (albumsForArtist.isEmpty()) {
            System.out.println("No albums!");
            return;
        }

        for (String album : albumsForArtist) {
            System.out.println(album);
        }

        List<SongArtist> artistsForSong = datasource.queryArtistsForSong("Go Your Own Way", Datasource.ORDER_BY_ASC);
        if(artistsForSong.isEmpty()) {
            System.out.println("No artists for selected song in our DB!");
            return;
        }

        for (SongArtist artist : artistsForSong) {
            System.out.println("Artist name = " + artist.getArtistName() + " || " +
                    "Album name = " + artist.getAlbumName() + " || " + "Track = " + artist.getTrack());
        }

//        datasource.querySongsMetadata();
//        int count = datasource.getCount("songs");
//        System.out.println("The number of songs in our database is " + count);
        datasource.createViewForSongArtists();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a song title: ");
        String title = scanner.nextLine();
        artistsForSong = datasource.querySongInfoView(title);
        if(artistsForSong.isEmpty()) {
            System.out.println("Couldn't find the artist for the song");
            return;
        }

        for (SongArtist artist : artistsForSong) {
            System.out.println("FROM VIEW - Artist name = " + artist.getArtistName() +
                               " Album name = " + artist.getAlbumName() +
                               " Track number = " + artist.getTrack());
        }

        datasource.close();
    }
}
