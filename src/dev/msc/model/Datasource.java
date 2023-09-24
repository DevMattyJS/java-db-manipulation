package dev.msc.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Datasource {

    public static final String DB_NAME = "music.db";

    public static final String CONNECTION_STRING = "jdbc:sqlite:C:\\Users\\matko\\Documents\\Development\\Java\\04_Databases\\Music\\" + DB_NAME;

    public static final String TABLE_ALBUMS = "albums";
    public static final String COLUMN_ALBUM_ID = "_id";
    public static final String COLUMN_ALBUM_NAME = "name";
    public static final String COLUMN_ALBUM_ARTIST = "artist";
    // Assigning index to each column (indexes in SQL starting from 1)
    // It is more efficient to use an index instead of a column name, because getter methods knows exactly where to go in a result set
    public static final int INDEX_ALBUM_ID = 1;
    public static final int INDEX_ALBUM_NAME = 2;
    public static final int INDEX_ALBUM_ARTIST = 3;

    public static final String TABLE_ARTISTS = "artists";
    public static final String COLUMN_ARTIST_ID = "_id";
    public static final String COLUMN_ARTIST_NAME = "name";
    public static final int INDEX_ARTIST_ID = 1;
    public static final int INDEX_ARTIST_NAME = 2;

    public static final String TABLE_SONGS = "songs";
    public static final String COLUMN_SONG_ID = "_id";
    public static final String COLUMN_SONG_TRACK = "track";
    public static final String COLUMN_SONG_TITLE = "title";
    public static final String COLUMN_SONG_ALBUM = "album";
    public static final int INDEX_SONG_ID = 1;
    public static final int INDEX_SONG_TRACK = 2;
    public static final int INDEX_SONG_TITLE = 3;
    public static final int INDEX_SONG_ALBUM = 4;

    public static final int ORDER_BY_NONE = 1;
    public static final int ORDER_BY_ASC = 2;
    public static final int ORDER_BY_DESC = 3;

    // SELECT albums.name FROM albums INNER JOIN artists ON albums.artist = artists._id WHERE artists.name = "Iron Maiden";
    public static final String QUERY_ALBUMS_BY_ARTIST_START =
            "SELECT " + TABLE_ALBUMS + "." + COLUMN_ALBUM_NAME + " FROM " + TABLE_ALBUMS +
                    " INNER JOIN " + TABLE_ARTISTS + " ON " + TABLE_ALBUMS + "." + COLUMN_ALBUM_ARTIST +
                    " = " + TABLE_ARTISTS + "." + COLUMN_ARTIST_ID +
                    " WHERE " + TABLE_ARTISTS + "." + COLUMN_ARTIST_NAME + " = \"";

    public static final String QUERY_ALBUMS_BY_ARTIST_SORT =
            "ORDER BY " + TABLE_ALBUMS + "." + COLUMN_ALBUM_NAME + " COLLATE NOCASE ";

    // SELECT artists.name, albums.name, songs.track FROM songs INNER JOIN albums ON songs.album = albums._id
    // INNER JOIN artists ON albums.artist = artists._id WHERE songs.title = "Go Your Own Way"
    public static final String QUERY_ARTIST_FOR_SONG_START =
            "SELECT " + TABLE_ARTISTS + "." + COLUMN_ARTIST_NAME + ", " +
            TABLE_ALBUMS + "." + COLUMN_ALBUM_NAME + ", " +
            TABLE_SONGS + "." + COLUMN_SONG_TRACK + " FROM " + TABLE_SONGS +
                    " INNER JOIN " + TABLE_ALBUMS + " ON " + TABLE_SONGS + "." + COLUMN_SONG_ALBUM + " = " + TABLE_ALBUMS + "." + COLUMN_ALBUM_ID +
                    " INNER JOIN " + TABLE_ARTISTS + " ON " + TABLE_ALBUMS + "." + COLUMN_ALBUM_ARTIST + " = " + TABLE_ARTISTS + "." + COLUMN_ARTIST_ID +
                    " WHERE " + TABLE_SONGS + "." + COLUMN_SONG_TITLE + " =\"";

    // ORDER BY artists.name, albums.name COLLATE NOCASE ASC;
    public static final String QUERY_ARTIST_FOR_SONG_SORT =
            " ORDER BY " + TABLE_ARTISTS + "." + COLUMN_ARTIST_NAME + ", " +
                    TABLE_ALBUMS + "." + COLUMN_ALBUM_NAME + " COLLATE NOCASE ";

    // CREATE VIEW IF NOT EXISTS artist_list AS SELECT artists.name, albums.name AS album, songs.track, songs.title
    // FROM songs INNER JOIN albums ON songs.album = albums._id INNER JOIN artists ON albums.artist = artists._id
    // ORDER BY artists.name, albums.name, songs.track;
    public static final String TABLE_ARTIST_SONG_VIEW = "artist_list";
    public static final String CREATE_ARTIST_SONG_VIEW =
            "CREATE VIEW IF NOT EXISTS " + TABLE_ARTIST_SONG_VIEW + " AS SELECT " + TABLE_ARTISTS + "." + COLUMN_ARTIST_NAME + ", " +
                    TABLE_ALBUMS + "." + COLUMN_ALBUM_NAME + " AS album, " + TABLE_SONGS + "." + COLUMN_SONG_TRACK + ", " +
                    TABLE_SONGS + "." + COLUMN_SONG_TITLE + " FROM " + TABLE_SONGS + " INNER JOIN " + TABLE_ALBUMS +
                    " ON " + TABLE_SONGS + "." + COLUMN_SONG_ALBUM + " = " + TABLE_ALBUMS + "." + COLUMN_ALBUM_ID +
                    " INNER JOIN " + TABLE_ARTISTS + " ON " + TABLE_ALBUMS + "." + COLUMN_ALBUM_ARTIST + " = " + TABLE_ARTISTS + "." + COLUMN_ARTIST_ID +
                    " ORDER BY " + TABLE_ARTISTS + "." + COLUMN_ARTIST_NAME + ", " + TABLE_ALBUMS + "." + COLUMN_ALBUM_NAME + "," +
                    TABLE_SONGS + "." + COLUMN_SONG_TRACK;

    // SELECT name, album, track FROM artist_list WHERE title = "Go Your Own Way";
    public static final String QUERY_VIEW_SONG_INFO =
            "SELECT " + COLUMN_ARTIST_NAME + ", " + COLUMN_SONG_ALBUM + ", " + COLUMN_SONG_TRACK +
            " FROM " + TABLE_ARTIST_SONG_VIEW + " WHERE " + COLUMN_SONG_TITLE + " = \"";

    // Prepared statement - protects our database from SQL injection attack of malicious user (hacker)
    //                    - we should use prepared statements for all queries instead of concatinating them using Stringbuilder
    //                    - '?' is used as a placeholder for value (one ? for each value)
    public static final String QUERY_VIEW_SONG_INFO_PREP =
            "SELECT " + COLUMN_ARTIST_NAME + ", " + COLUMN_SONG_ALBUM + ", " + COLUMN_SONG_TRACK +
                    " FROM " + TABLE_ARTIST_SONG_VIEW + " WHERE " + COLUMN_SONG_TITLE + " = ? ";



    private Connection conn;

    private PreparedStatement querySongInfoView;

    public boolean open() {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);
            querySongInfoView = conn.prepareStatement(QUERY_VIEW_SONG_INFO_PREP);

            return true;
        } catch (SQLException e) {
            System.out.println("Couldn't connect to database: " + e.getMessage());
            return false;
        }
    }

    public void close() {
        try {

            if (querySongInfoView != null) {
                querySongInfoView.close();
            }

            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Couldn't close connection: " + e.getMessage());
        }
    }

    public List<Artist> queryArtists(int sortOrder) {

        StringBuilder sb = new StringBuilder("SELECT * FROM ");
        sb.append(TABLE_ARTISTS);
        if (sortOrder != ORDER_BY_NONE) {
            sb.append(" ORDER BY ");
            sb.append(COLUMN_ARTIST_NAME);
            sb.append(" COLLATE NOCASE ");
            if (sortOrder == ORDER_BY_DESC) {
                sb.append("DESC");
            } else {
                sb.append("ASC");
            }
        }

        try (Statement statement = conn.createStatement();
             ResultSet results = statement.executeQuery(sb.toString())) {

            List<Artist> artists = new ArrayList<>();
            while (results.next()) {
                Artist artist = new Artist();
                artist.setId(results.getInt(INDEX_ARTIST_ID));
                artist.setName(results.getString(INDEX_ARTIST_NAME));
                artists.add(artist);
            }

            return artists;

        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return null;
        }
    }

    public List<String> queryAlbumsForArtist(String artistName, int sortOrder) {

        // Creates the start of our SQL query
        StringBuilder sb = new StringBuilder(QUERY_ALBUMS_BY_ARTIST_START);
        sb.append(artistName);
        sb.append("\"");

        // Add the part related to sorting to our SQL query
        if (sortOrder != ORDER_BY_NONE) {
            sb.append(QUERY_ALBUMS_BY_ARTIST_SORT);
            if (sortOrder == ORDER_BY_DESC) {
                sb.append("DESC");
            } else {
                sb.append("ASC");
            }
        }

//        System.out.println("SQL statement = " + sb.toString());

        // Creates a statement and execute our SQL query, store returned ResultSet in a results variable
        try (Statement statement = conn.createStatement();
             ResultSet results = statement.executeQuery(sb.toString())) {

            List<String> albums = new ArrayList<>();
            while (results.next()) {
                // column index in getString() method refers to column index in ResultSet, not in a table
                // and since we have just 1 column in a ResultSet (album name), we know that the index is 1
                albums.add(results.getString(1));
            }

            return albums;
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return null;
        }
    }

    public List<SongArtist> queryArtistsForSong(String songTitle, int sortOrder) {

        StringBuilder sb = new StringBuilder(QUERY_ARTIST_FOR_SONG_START);
        sb.append(songTitle);
        sb.append("\"");

        if (sortOrder != ORDER_BY_NONE) {
            sb.append(QUERY_ARTIST_FOR_SONG_SORT);
            if(sortOrder == ORDER_BY_DESC) {
                sb.append("DESC");
            } else {
                sb.append("ASC");
            }
        }

//        System.out.println("SQL Statement = " + sb.toString());

        try (Statement statement = conn.createStatement();
             ResultSet results = statement.executeQuery(sb.toString())) {

            List<SongArtist> songArtists = new ArrayList<>();
            while (results.next()) {
                SongArtist artist = new SongArtist();
                artist.setArtistName(results.getString(1));
                artist.setAlbumName(results.getString(2));
                artist.setTrack(results.getInt(3));
                songArtists.add(artist);
            }

            return songArtists;

        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return null;
        }
    }

    public boolean createViewForSongArtists() {

        try(Statement statement = conn.createStatement()) {

            statement.execute(CREATE_ARTIST_SONG_VIEW);
            System.out.println("SQL = " + CREATE_ARTIST_SONG_VIEW);
            return true;
        } catch (SQLException e) {
            System.out.println("Create View Failed: " + e.getMessage());
            return false;
        }
    }

    //TODO Refactor duplicated code to separate method in a future
    public List<SongArtist> querySongInfoView(String title) {

        try {
            // we need to set values for each placeholder e.g. preparedStatement.setString(parameterIndex, parameterValue)
            // parameter indexes starting from 1
            querySongInfoView.setString(1, title);
            ResultSet results = querySongInfoView.executeQuery();

            List<SongArtist> songArtists = new ArrayList<>();
            while (results.next()) {
                SongArtist artist = new SongArtist();
                artist.setArtistName(results.getString("name"));
                artist.setAlbumName(results.getString("album"));
                artist.setTrack(results.getInt("track"));
                songArtists.add(artist);
            }

            return songArtists;

        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return null;
        }
    }

    public int getCount(String table) {
        String sql = "SELECT COUNT(*) AS count FROM " + table;
        try (Statement statement = conn.createStatement();
             ResultSet results = statement.executeQuery(sql)) {

            int count = results.getInt("count");

            return count;

        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return -1;
        }
    }

    // A method to show how to get metadata (schema information)
    public void querySongsMetadata() {
        String sql = "SELECT * FROM " + TABLE_SONGS;

        try (Statement statement = conn.createStatement();
             ResultSet results = statement.executeQuery(sql)) {

            ResultSetMetaData meta = results.getMetaData();
            int numColumns = meta.getColumnCount();
            for (int i = 1; i <= numColumns; i++) {
                System.out.printf("Column %d in the songs table is named %s\n", i, meta.getColumnName(i));
            }

        } catch (SQLException e) {
            System.out.println("Failed to load songs metadata: " + e.getMessage());
        }
    }
}
