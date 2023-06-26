/*
 * Copyright (C) 2002-2023 Jahia Solutions Group SA. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jahia.modules.npm.modules.engine.npmhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.ops4j.pax.swissbox.bnd.BndUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.*;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;

/**
 * Npm protocol handler
 * Transform npm pack into bundle
 */
public class NpmProtocolConnection extends URLConnection {
    private static final Logger logger = LoggerFactory.getLogger(NpmProtocolConnection.class);

    private final URL wrappedUrl;

    public NpmProtocolConnection(URL url) throws MalformedURLException {
        super(url);
        wrappedUrl = new URL(this.url.toString().substring("npm://".length()));
    }

    @Override
    public void connect() throws IOException {
        // Do nothing
    }

    @Override
    public InputStream getInputStream() throws IOException {
        connect();

        File outputDir = Files.createTempDirectory("npm.").toFile();
        TarUtils.unTar(new GZIPInputStream(wrappedUrl.openStream()), outputDir);

        Properties instructions = new Properties();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Collection<String> extensions = new HashSet<>();

        File mergedDefinitionFile = null;
        File f = new File(outputDir, "package");
        Collection<File> files = FileUtils.listFiles(f, null, true);
        try (JarOutputStream jos = new JarOutputStream(byteArrayOutputStream)) {
            Set<ZipEntry> processedImages = new HashSet<>();
            List<File> filesWithMergedDefinitions = files.stream().filter(file -> !file.getName().endsWith(".cnd")).collect(Collectors.toList());
            mergedDefinitionFile = getMergedDefinitionFile(files);
            if (mergedDefinitionFile != null) {
                filesWithMergedDefinitions.add(mergedDefinitionFile);
            }
            for (File file : filesWithMergedDefinitions) {
                boolean shouldCopyFile = true;
                String path = f.toURI().relativize(file.toURI()).getPath();

                if (path.equals("package.json")) {
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> properties = mapper.readValue(file, Map.class);
                    Map<String, Object> jahiaProps = (Map<String, Object>) properties.getOrDefault("jahia", new HashMap<>());

                    String name = (String) properties.get("name");
                    instructions.put("Bundle-Name", name + " (npm module)");
                    instructions.put("Bundle-SymbolicName", name.replace("@", "").replace('/', '-'));
                    instructions.put("Bundle-Version", properties.get("version"));
                    instructions.put("Bundle-Category", "jahia-module");
                    instructions.put("Jahia-GroupId", "org.jahia.npm");
                    instructions.put("Jahia-Required-Version", "8.0.0.0");
                    instructions.put("Jahia-Module-Type", jahiaProps.getOrDefault("module-type", "module"));
                    instructions.put("Jahia-Javascript-Name", name);
                    instructions.put("Jahia-Static-Resources", "/css,/icons,/images,/img,/javascript");
                    instructions.put("-removeheaders", "Private-Package, Export-Package");
                }
                if (path.startsWith("jahia-views/")) {
                    path = path.substring("jahia-views/".length());
                    if (file.isFile()) {
                        extensions.add(StringUtils.substringAfterLast(file.getName(), "."));
                    }
                }

                if (mergedDefinitionFile != null && path.equals(mergedDefinitionFile.getPath())) {
                    jos.putNextEntry(new ZipEntry("META-INF/definitions.cnd"));
                } else if (path.equals("import.xml")) {
                    jos.putNextEntry(new ZipEntry("META-INF/" + path));
                } else if (path.startsWith("components") && path.endsWith(".png")) {
                    String[] parts = StringUtils.split(path, "/");
                    String nodeTypeName = parts[2];
                    if (file.getName().equals(nodeTypeName + ".icon.png")) {
                        jos.putNextEntry(new ZipEntry("icons/" + parts[1] + "_" + nodeTypeName + ".png"));
                    } else {
                        ZipEntry entry = new ZipEntry("images/" + file.getName());
                        if (!processedImages.contains(entry)) {
                            jos.putNextEntry(entry);
                            processedImages.add(entry);
                        } else {
                            shouldCopyFile = false;
                            logger.warn("File with the name {} already copied into the /images folder, the current file won't be copied", file.getName());
                        }
                    }
                } else {
                    jos.putNextEntry(new ZipEntry(path));
                }
                if (shouldCopyFile) {
                    try (FileInputStream input = new FileInputStream(file)) {
                        IOUtils.copy(input, jos);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Cannot transform npm-module", e);
        }
        FileUtils.deleteDirectory(outputDir);
        if (mergedDefinitionFile != null) {
            FileUtils.delete(mergedDefinitionFile);
        }

        if (!extensions.isEmpty()) {
            instructions.put("Jahia-Module-Scripting-Views", StringUtils.join(extensions, ","));
        }

        return BndUtils.createBundle(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), instructions, wrappedUrl.toExternalForm());
    }

    private File getMergedDefinitionFile(Collection<File> npmFiles) {
        List<File> definitionsFiles = npmFiles.stream().filter(file -> file.getName().endsWith(".cnd")).collect(Collectors.toList());
        Set<String> namespaces = new HashSet<>();
        StringBuilder lines = new StringBuilder();

        definitionsFiles.forEach(definitionFile -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(definitionFile.getPath()))) {
                String line = reader.readLine();

                while (line != null) {
                    if (line.startsWith("<")) {
                        namespaces.add(line);
                    } else if (line.isBlank()) {
                        lines.append(System.getProperty("line.separator"));
                    } else {
                        lines.append(line);
                    }
                    line = reader.readLine();
                }
            } catch (IOException e) {
                logger.error("Error reading definition lines from {}", definitionFile, e);
            }
            lines.append(System.getProperty("line.separator"));
        });

        return buildDefinitionFile(namespaces, lines.toString());
    }

    private File buildDefinitionFile(Set<String> namespaces, String allDefinitionLines) {
        File mergedDefinitions;
        try {
            mergedDefinitions = Files.createTempFile("definitions", ".cnd").toFile();
        } catch (IOException e) {
            logger.error("Error creating temporary definitions.cnd file", e);
            return null;
        }
        try (FileWriter writer = new FileWriter(mergedDefinitions);
             BufferedWriter bw = new BufferedWriter(writer)) {

            String mergedNamespaces = namespaces.stream().reduce("", (partialString, element) -> partialString + element + System.getProperty("line.separator"));
            bw.write(mergedNamespaces);
            bw.write(allDefinitionLines);
            if (logger.isDebugEnabled()) {
                logger.debug("Generated definition file: {} {}", mergedNamespaces, allDefinitionLines);
            }
        } catch (IOException e) {
            logger.error("Error while generating definitions.cnd file", e);
        }
        return mergedDefinitions;

    }
}
