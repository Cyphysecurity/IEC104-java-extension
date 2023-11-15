/* @XI
 * This class is me doodling.
 */
import java.io.File;
import java.nio.file.*;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.*;

public class random {
    public static void main(String[] args) {
        // cross-platform path method 1:
        Path curPath = Paths.get(System.getProperty("user.dir"));
        Path homePath = Paths.get(System.getProperty("user.home"));
        Path p = Paths.get(curPath.toString(), "src");

        // cross-platform path method 2:
        /*
        Path currentRelativePath = Paths.get("");
        Path curPath = currentRelativePath.toAbsolutePath(); // <-- Get the Path and use resolve on it.
        String filename = "data" + File.separatorChar + "foo.txt";
        Path p = curPath.resolve(filename);
        */
        if (Files.exists(curPath) && Files.exists(homePath) && Files.exists(p)) {
            System.out.println(String.format("current path: %s \nhome path: %s \ntest path: %s\n", curPath, homePath, p));
        }

        System.out.println(System.getProperty("sun.arch.data.model"));

    }

}
