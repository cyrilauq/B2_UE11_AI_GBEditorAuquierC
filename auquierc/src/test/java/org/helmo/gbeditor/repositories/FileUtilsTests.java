package org.helmo.gbeditor.repositories;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtilsTests {

    @Test
    public void textFileIsNotJpgSoCannotCopyThis() {
        Path src = Paths.get("src", "test", "resources", "FilesUtilsTests", "textFile.txt").toAbsolutePath();
        Path destination = Paths.get("src", "test", "resources", "FileUtilsTests.To").toAbsolutePath();
        assertThrows(Exception.class, () -> FileUtils.copyFile(
                src.toString(),
                destination.toString(),
                "textFile"));
        verifyNotExists(Paths.get(destination.toString(), "textFile.txt"));
    }

    /**
     * Le JFIF sera considéré comme un format valide, car il utilise la compression JPEG et est un variante de ce dernier.
     * Src : <a href="https://fileinfo.com/extension/jfif">Jfif info</a>
     */
    @Test
    public void jijfFileIsNotJpgSoCannotCopyThis() {
        Path src = Paths.get("src", "test", "resources", "FilesUtilsTests", "snkImgFile.jfif").toAbsolutePath();
        Path destination = Paths.get("src", "test", "resources", "FileUtilsTests.To").toAbsolutePath();
        assertDoesNotThrow(() -> FileUtils.copyFile(
                src.toString(),
                destination.toString(),
                "snkImgFile"));
        verifyExists(Paths.get(destination.toString(), "snkImgFile.jpg"));
    }

    @Test
    public void jpgFileIdJpgSoCanCopyThis() {
        Path src = Paths.get("src", "test", "resources", "FilesUtilsTests", "snkImgFile.jpg").toAbsolutePath();
        Path destination = Paths.get("src", "test", "resources", "FileUtilsTests.To").toAbsolutePath();
        assertDoesNotThrow(() -> FileUtils.copyFile(
                src.toString(),
                destination.toString(),
                "snkImgFile"));
        verifyExists(Paths.get(destination.toString(), "snkImgFile.jpg"));
    }

    @Test
    public void pngFileIsPngSoCanCopyThis() {
        Path src = Paths.get("src", "test", "resources", "FilesUtilsTests", "snkImgFile.png").toAbsolutePath();
        Path destination = Paths.get("src", "test", "resources", "FileUtilsTests.To").toAbsolutePath();
        assertDoesNotThrow(() -> FileUtils.copyFile(
                src.toString(),
                destination.toString(),
                "snkImgFile"));
        verifyExists(Paths.get(destination.toString(), "snkImgFile.png"));
    }

    @Test
    public void gifFileIsNotPngOrJpegSoCannotCopyThis() {
        Path src = Paths.get("src", "test", "resources", "FilesUtilsTests", "modPsychoFile.gif").toAbsolutePath();
        Path destination = Paths.get("src", "test", "resources", "FileUtilsTests.To").toAbsolutePath();
        assertThrows(Exception.class, () -> FileUtils.copyFile(
                src.toString(),
                destination.toString(),
                "modPsychoFile"));
        verifyNotExists(Paths.get(destination.toString(), "modPsychoFile.gif"));
    }

    @Test
    public void copyFileInNonExistingDirectory() {
        Path src = Paths.get("src", "test", "resources", "FilesUtilsTests", "snkImgFile.png").toAbsolutePath();
        Path destination = Paths.get("src", "test", "resources", "FileUtilsTests.To", "NonExistingDir").toAbsolutePath();
        assertDoesNotThrow(() -> FileUtils.copyFile(
                src.toString(),
                destination.toString(),
                "snkImgFile"));
        verifyExists(Paths.get(destination.toString(), "snkImgFile.png"));
        verifyExists(Paths.get(destination.toString(), "snkImgFile.png").getParent());
    }

    private void verifyExists(final Path path) {
        assertTrue(Files.exists(path));
        deleteFile(path);
    }

    private void verifyNotExists(final Path path) {
        assertFalse(Files.exists(path));
        deleteFile(path);
    }

    private void deleteFile(final Path path) {
        if(Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
