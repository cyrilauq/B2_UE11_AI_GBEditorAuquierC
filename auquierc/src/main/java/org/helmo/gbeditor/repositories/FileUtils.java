package org.helmo.gbeditor.repositories;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Définit des méthodes utiles pour manipuler des fichiers.
 * Ces méthodes servent par exemples à copier un fichier et vérifier son extension.
 */
public class FileUtils {
    private final static byte[] PNG_BYTES = {
        (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A
    };

    private final static byte[] JPG_BYTES = {
            (byte) 0xFF, (byte) 0xD8, (byte) 0xFF
    };

    /**
     * Vérifie qu'un repertoire donné existe.
     * Si le répertoire n'existe pas alors crée ce dernier.
     *
     * @param directoryPath     Chemin vers le répertoire que l'on cherche.
     */
    public static void createIfNotExists(final String directoryPath) {
        final var path = Paths.get(directoryPath);
        if(Files.notExists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Copie un fichier donné vers un chemin de destination donné.
     *
     * @param fileSrcPath        Fichier à copie
     * @param fileDestPath  Destination du fichier à copier.
     *
     * @return  Retourne le chemin de destination du fichier à copier.
     */
    public static String copyFile(final String fileSrcPath, final String fileDestPath, final String fileName) {
        try
        {
            File sourceFile = new File(fileSrcPath);
            File destinationFile = new File(
                    Paths.get(fileDestPath, fileName + isValidFile(sourceFile)).toAbsolutePath().toString()
            );
            createIfNotExists(destinationFile.toPath().getParent().toString());
            return Files.copy(sourceFile.toPath(),
                    destinationFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING).toString();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static String isValidFile(File sourceFile) throws Exception {
        if(!isPngFile(sourceFile)) {
            if(!isJpgFile(sourceFile)) {
                throw new Exception("Mauvais format de fichier");
            }
            return ".jpg";
        }
        return ".png";
    }

    /**
     * Détermine si un fichier donné est un fichier de type PNG ou non.
     *
     * @param file  Fichier
     *
     * @return  False, si le fichier donné n'est pas de type PNG.
     *          True, sinon.
     */
    private static boolean isPngFile(final File file) {
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(file)))) {
            return browseAndVerifyBytes(PNG_BYTES, dis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Détermine si un fichier donné est un fichier de type JPG ou non.
     *
     * @param file  Fichier
     *
     * @return  False, si le fichier donné n'est pas de type JPG.
     *          True, sinon.
     */
    private static boolean isJpgFile(final File file) {
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(file)))) {
            return browseAndVerifyBytes(JPG_BYTES, dis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parcourt byte par byte un Stream donné et vérifie Stream comment par les mêmes byte que le tableau de byt donneé.
     *
     * @param bytes
     * @param dis
     * @return
     *
     * @throws IOException
     */
    private static boolean browseAndVerifyBytes(byte[] bytes, DataInputStream dis) throws IOException {
        int i = 0;
        while(true) {
            if(i == bytes.length) {
                return true;
            }
            if(bytes[i++] != dis.readByte()) {
                return false;
            }
        }
    }

}
