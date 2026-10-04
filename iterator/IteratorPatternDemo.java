import java.util.*;

record Song(String title) {}

class LocalLibrary implements Iterable<Song> {
    private final Song[] songs;
    LocalLibrary(Song... songs) { this.songs = songs; }

    public Iterator<Song> iterator() {
        return new Iterator<>() {
            int i = 0;
            public boolean hasNext() { return i < songs.length; }
            public Song next() { return songs[i++]; }
        };
    }
}

class CloudCatalog implements Iterable<Song> {
    int requests = 0;

    List<Song> fetchPage(int page) { 
        requests++;
        List<Song> out = new ArrayList<>();
        for (int i = page * 10; i < Math.min(page * 10 + 10, 50_000); i++) out.add(new Song("Cloud " + i));
        return out;
    }

    public Iterator<Song> iterator() {
        return new Iterator<>() {
            int page = 0;
            Iterator<Song> current = Collections.emptyIterator();

            public boolean hasNext() {
                if (!current.hasNext()) current = fetchPage(page++).iterator(); // lazy fetch
                return current.hasNext();
            }
            public Song next() {
                if (!hasNext()) throw new NoSuchElementException();
                return current.next();
            }
        };
    }
}

public class IteratorPatternDemo {
    // The player knows nothing about arrays or APIs.
    static void play(Iterable<Song> songs, int limit) {
        int n = 0;
        for (Song s : songs) {
            if (n++ == limit) break;
            System.out.println("Playing: " + s.title());
        }
    }

    public static void main(String[] args) {
        play(new LocalLibrary(new Song("Local A"), new Song("Local B")), 10);

        CloudCatalog cloud = new CloudCatalog();
        play(cloud, 5);
        System.out.println("API requests: " + cloud.requests); // 1, not 5,000
    }
}