package org.jahia.modules.npmplugins.npmhandler;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Util to untar
 */
public class TarUtils {
    static List<File> unTar(final InputStream is, final File outputDir) throws IOException {

        final List<File> untaredFiles = new LinkedList<>();
        try {
            final TarArchiveInputStream debInputStream = (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream("tar", is);
            TarArchiveEntry entry = null;
            while ((entry = (TarArchiveEntry) debInputStream.getNextEntry()) != null) {
                final File outputFile = new File(outputDir, entry.getName());
                if (entry.isDirectory()) {
                    if (!outputFile.exists()) {
                        if (!outputFile.mkdirs()) {
                            throw new IllegalStateException(String.format("Couldn't create directory %s.", outputFile.getAbsolutePath()));
                        }
                    }
                } else {
                    outputFile.getParentFile().mkdirs();
                    final OutputStream outputFileStream = new FileOutputStream(outputFile);
                    IOUtils.copy(debInputStream, outputFileStream);
                    outputFileStream.close();
                }
                untaredFiles.add(outputFile);
            }
            debInputStream.close();
        } catch (ArchiveException e) {
            throw new IOException(e);
        }

        return untaredFiles;
    }
}