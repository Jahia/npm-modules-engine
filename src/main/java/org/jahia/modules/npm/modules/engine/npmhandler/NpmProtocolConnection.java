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
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;

/**
 * Npm protocol handler
 * Transform npm pack into bundle
 */
public class NpmProtocolConnection extends URLConnection {
    public static final String BUNDLE_HEADER_NPM_INIT_SCRIPT = "Jahia-NPM-InitScript";

    private static final Logger logger = LoggerFactory.getLogger(NpmProtocolConnection.class);

    private URL wrappedUrl;

    public NpmProtocolConnection(URL url) throws MalformedURLException {
        super(url);
        String urlStr = this.url.toString();
        if (urlStr.startsWith("npm://")) {
            wrappedUrl = new URL(urlStr.substring("npm://".length()));
        } else {
            wrappedUrl = new URL(urlStr.substring("npm:".length()));
        }
    }


    @Override
    public void connect() throws IOException {
        // Do nothing
    }

    @Override
    public InputStream getInputStream() throws IOException {
        connect();

        logger.info("Handling JS module using npm protocol wrapper for package: {}", wrappedUrl);
        File outputDir = Files.createTempDirectory("npm.").toFile();
        TarUtils.unTar(new GZIPInputStream(wrappedUrl.openStream()), outputDir);
        Properties instructions = new Properties();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        File packageDir = new File(outputDir, "package");
        Collection<File> files = FileUtils.listFiles(packageDir, null, true);
        try (JarOutputStream jos = new JarOutputStream(byteArrayOutputStream)) {
            Set<ZipEntry> processedImages = new HashSet<>();

            // Process files of the packages
            List<File> cndFiles = new ArrayList<>();
            for (File file : files) {
                if (file.getName().endsWith(".cnd")) {
                    // Postpone processing of CND files
                    cndFiles.add(file);
                    continue;
                }

                // Calculate relative path of the file in the package
                String packageRelativePath = packageDir.toURI().relativize(file.toURI()).getPath();

                // Extract instructions from package.json
                if (packageRelativePath.equals("package.json")) {
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> properties = mapper.readValue(file, Map.class);
                    Map<String, Object> jahiaProps = (Map<String, Object>) properties.getOrDefault("jahia", new HashMap<>());
                    instructions.putAll(generateInstructions(properties, jahiaProps));
                }

                // Copy file path (try to detect good path for file in the final package jar.)
                if (packageRelativePath.startsWith("settings/")) {
                    jos.putNextEntry(new ZipEntry("META-INF/" + StringUtils.substringAfter(packageRelativePath, "settings/")));
                } else if (packageRelativePath.startsWith("public")) {
                    String filePath = StringUtils.substringAfter(packageRelativePath, "public/");
                    if (filePath.startsWith("locales")) {
                        jos.putNextEntry(new ZipEntry(filePath));
                    } else if (filePath.startsWith("javascript")) {
                        jos.putNextEntry(new ZipEntry(filePath));
                    } else {
                        jos.putNextEntry(new ZipEntry("assets/" + filePath));
                    }
                } else if (packageRelativePath.startsWith("configuration")) {
                    String filePath = StringUtils.substringAfter(packageRelativePath, "configuration/");
                    if (filePath.equals("import.xml")) {
                        jos.putNextEntry(new ZipEntry("META-INF/" + filePath));
                    } else if(filePath.startsWith("content-editor-forms")) {
                        jos.putNextEntry(new ZipEntry("META-INF/jahia-" + filePath));
                    }  else if(filePath.startsWith("content-types-icons")) {
                        jos.putNextEntry(new ZipEntry("icons/" + StringUtils.substringAfter(filePath, "content-types-icons/")));
                    } else if(filePath.startsWith("resources") && filePath.endsWith(".properties")) {
                        jos.putNextEntry(new ZipEntry(filePath));
                    }
                } else if (packageRelativePath.startsWith("components") && packageRelativePath.endsWith(".png")) {
                    String[] parts = StringUtils.split(packageRelativePath, "/");
                    String nodeTypeName = parts[2];
                    if (file.getName().equals(nodeTypeName + ".icon.png")) {
                        jos.putNextEntry(new ZipEntry("icons/" + parts[1] + "_" + nodeTypeName + ".png"));
                    } else {
                        ZipEntry entry = new ZipEntry("images/" + file.getName());
                        if (!processedImages.contains(entry)) {
                            jos.putNextEntry(entry);
                            processedImages.add(entry);
                        } else {
                            logger.warn("File with the name {} already copied into the /images folder, the current file won't be copied", file.getName());
                            continue;
                        }
                    }
                } else {
                    jos.putNextEntry(new ZipEntry(packageRelativePath));
                }

                // Copy file content
                try (FileInputStream input = new FileInputStream(file)) {
                    IOUtils.copy(input, jos);
                }
            }

            // Process CND files (merging them in a single file if necessary)
            if (!cndFiles.isEmpty()) {
                if (cndFiles.size() == 1) {
                    // Single cnd file, just copy it, no need for merge
                    if (logger.isDebugEnabled()) {
                        logger.debug("Single CND file detected in the package.");
                    }
                    File cndFile = cndFiles.get(0);
                    jos.putNextEntry(new ZipEntry("META-INF/definitions.cnd"));
                    try (FileInputStream input = new FileInputStream(cndFile)) {
                        IOUtils.copy(input, jos);
                    }
                } else {
                    // Multiple cnd files, merge them
                    if (logger.isDebugEnabled()) {
                        logger.debug("Multiple CND files detected in the package, they will be merged into a single file");
                    }
                    File mergedDefinitionFile = mergeDefinitionFiles(cndFiles, packageDir);
                    if (mergedDefinitionFile != null) {
                        jos.putNextEntry(new ZipEntry("META-INF/definitions.cnd"));
                        try (FileInputStream input = new FileInputStream(mergedDefinitionFile)) {
                            IOUtils.copy(input, jos);
                        } finally {
                            FileUtils.delete(mergedDefinitionFile);
                        }
                    }
                }
            }

        } catch (Exception e) {
            logger.error("An error occurred during JS module transformation", e);
        }

        FileUtils.deleteDirectory(outputDir);
        if (logger.isDebugEnabled()) {
            logger.debug("JS module transformed to bundle using instructions: {}", instructions);
        }
        return BndUtils.createBundle(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), instructions, wrappedUrl.toExternalForm());
    }

    private Properties generateInstructions(Map<String, Object> properties, Map<String, Object> jahiaProps) {
        Properties instructions = new Properties();

        // First let's setup Bundle headers
        instructions.put("Bundle-Category", jahiaProps.getOrDefault("category", "jahia-npm-module"));
        setIfPresent(properties, "description", instructions, "Bundle-Description");
        String name = (String) properties.get("name");
        instructions.put("Bundle-Name", name + " (npm module)");
        instructions.put("Bundle-SymbolicName", name.replace("@", "")
                .replace('/', '-')
                .replaceAll("[^a-zA-Z0-9\\-\\.\\s]", "_"));
        setIfPresent(properties, "author", instructions, "Bundle-Vendor");
        instructions.put("Bundle-Version", properties.get("version"));
        setIfPresent(properties, "license", instructions, "Bundle-License");

        // Next lets setup Jahia headers
        instructions.put("Jahia-Depends", jahiaProps.getOrDefault("module-dependencies", "default"));
        setIfPresent(jahiaProps, "deploy-on-site", instructions, "Jahia-Deploy-On-Site");
        instructions.put("Jahia-GroupId", jahiaProps.getOrDefault("group-id", "org.jahia.npm"));
        setIfPresent(jahiaProps,"module-signature", instructions, "Jahia-Module-Signature");
        setIfPresent(jahiaProps, "module-priority", instructions, "Jahia-Module-Priority");
        instructions.put("Jahia-Module-Type", jahiaProps.getOrDefault("module-type", "module"));
        setIfPresent(jahiaProps, "private-app-store", instructions, "Jahia-Private-App-Store");
        instructions.put("Jahia-Required-Version", jahiaProps.getOrDefault("required-version", "8.2.0.0"));
        setIfPresent(jahiaProps, "server", instructions, BUNDLE_HEADER_NPM_INIT_SCRIPT);
        instructions.put("Jahia-Static-Resources", jahiaProps.getOrDefault("static-resources", "/css,/icons,/images,/img,/javascript"));

        instructions.put("-removeheaders", "Private-Package, Export-Package");
        return instructions;
    }

    private void setIfPresent(Map<String, Object> inputProperties,String propertyName, Properties instructions, String instructionName) {
        if (inputProperties.containsKey(propertyName)) {
            instructions.put(instructionName, inputProperties.get(propertyName));
        }
    }

    private File mergeDefinitionFiles(Collection<File> cndFiles, File packageDir) {
        Set<String> namespaces = new HashSet<>();
        StringBuilder lines = new StringBuilder();

        cndFiles.forEach(definitionFile -> {
            if (logger.isDebugEnabled()) {
                logger.debug("Merging CND file: {}", definitionFile);
            }
            lines.append(System.lineSeparator());
            lines.append("// From ");
            lines.append(packageDir.toURI().relativize(definitionFile.toURI()).getPath());
            lines.append(" : ");
            lines.append(System.lineSeparator());
            try (BufferedReader reader = new BufferedReader(new FileReader(definitionFile.getPath()))) {
                String line = reader.readLine();

                while (line != null) {
                    if (line.startsWith("<")) {
                        namespaces.add(line);
                    } else if (line.isBlank()) {
                        lines.append(System.lineSeparator());
                    } else {
                        lines.append(line);
                        lines.append(System.lineSeparator());
                    }
                    line = reader.readLine();
                }
            } catch (IOException e) {
                logger.error("Error reading definition lines from {}", definitionFile, e);
            }
            lines.append(System.lineSeparator());
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

            String mergedNamespaces = namespaces.stream().reduce("", (partialString, element) -> partialString + element + System.lineSeparator());
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
