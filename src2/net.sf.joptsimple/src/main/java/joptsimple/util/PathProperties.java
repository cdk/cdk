package joptsimple.util;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Enum for checking common conditions of files and directories.
 *
 * @see joptsimple.util.PathConverter
 */
public enum PathProperties {
    FILE_EXISTING( "file.existing" ) {
        @Override public boolean accept(Path path) {
            return Files.isRegularFile( path );
        }
    },
    DIRECTORY_EXISTING( "directory.existing" ) {
        @Override public boolean accept(Path path) {
            return Files.isDirectory( path );
        }
    },
    NOT_EXISTING( "file.not.existing" ) {
        @Override public boolean accept(Path path) {
            return Files.notExists( path );
        }
    },
    FILE_OVERWRITABLE( "file.overwritable" ) {
        @Override public boolean accept(Path path) {
            return FILE_EXISTING.accept( path ) && WRITABLE.accept( path );
        }
    },
    READABLE( "file.readable" ) {
        @Override public boolean accept(Path path) {
            return Files.isReadable( path );
        }
    },
    WRITABLE( "file.writable" ) {
        @Override public boolean accept(Path path) {
            return Files.isWritable( path );
        }
    };

    private final String messageKey;

    PathProperties(String messageKey) {
        this.messageKey = messageKey;
    }

    public abstract boolean accept( Path path );

    String getMessageKey() {
        return messageKey;
    }
}
