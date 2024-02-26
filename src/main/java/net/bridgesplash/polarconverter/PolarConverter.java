package net.bridgesplash.polarconverter;

import net.hollowcube.polar.AnvilPolar;
import net.hollowcube.polar.PolarWorld;
import net.hollowcube.polar.PolarWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class PolarConverter {

    public static void main(String @NotNull [] args) {

        if (args.length != 1) {
            System.out.println("Usage: java PolarConverter <world_folder|multi_world_folder>");
            System.exit(1);
        }

        String fileName = args[0];
        Path worldPath = Paths.get(fileName);

        if(!Files.isDirectory(worldPath)){
            System.err.println("The path provided `" + fileName + "` is not a directory");
            System.exit(1);
        }

        System.out.println("Converting world(s) at " + worldPath);
        PolarConverter converter = new PolarConverter(worldPath);


        int converted = converter.convert();
        System.out.println("Converted " + converted + " worlds");
    }

    private final Path initalPath;
    private final Map<String, Path> worldFolders = new HashMap<>();

    public PolarConverter(Path worldPath) {
        this.initalPath = worldPath;
        try(Stream<Path> paths = Files.walk(worldPath, FileVisitOption.FOLLOW_LINKS)) {
            paths.filter(Files::isDirectory).forEach(path -> {
                // check if file is a region folder, if so add parent to worldFolders
                if(path.getFileName().toString().equals("region")){
                    worldFolders.put(path.getParent().getFileName().toString(), path.getParent());
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public int convert() {
        int converted = 0;

        for (Map.Entry<String, Path> entry : worldFolders.entrySet()) {
            String worldName = entry.getKey();
            Path worldFolder = entry.getValue();
            System.out.println("Converting world " + worldName + " at " + worldFolder);
            try {
                PolarWorld world = AnvilPolar.anvilToPolar(worldFolder);
                byte[] worldBytes = PolarWriter.write(world);
                // save to [worldName].polar
                Files.write(initalPath.resolve(worldName + ".polar"), worldBytes);
            }catch (IOException e){
                System.err.println("Failed to convert world " + worldName + " at " + worldFolder);
                e.printStackTrace();
            }
            converted++;
        }

        return converted;
    }




}
