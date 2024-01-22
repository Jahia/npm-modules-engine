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
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
    public static final String BUNDLE_HEADER_NPM_INIT_SCRIPT = "Jahia-NPM-InitScript";

    private static final Logger logger = LoggerFactory.getLogger(NpmProtocolConnection.class);

    private final URL wrappedUrl;

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

        File outputDir = Files.createTempDirectory("npm.").toFile();

        InputStream inputStream;
        if ("http".equals(wrappedUrl.getProtocol()) || "https".equals(wrappedUrl.getProtocol())) {
            URL finalUrl = wrappedUrl;
            HttpClient client;
            try {
                if (wrappedUrl.getUserInfo() != null) {
                    String user = wrappedUrl.getUserInfo().split(":")[0];
                    String password = wrappedUrl.getUserInfo().split(":")[1];
                    finalUrl = new URL(wrappedUrl.toString().replace(user + ":" + password + "@", ""));

                    client = HttpClient.newBuilder().authenticator(new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new java.net.PasswordAuthentication(user, password.toCharArray());
                        }
                    }).build();
                } else {
                    client = HttpClient.newHttpClient();
                }

                HttpResponse<InputStream> response = client.send(
                        HttpRequest.newBuilder(finalUrl.toURI()).build(),
                        HttpResponse.BodyHandlers.ofInputStream());
                inputStream = response.body();
            } catch (URISyntaxException | InterruptedException e) {
                throw new IOException(e.getMessage(), e);
            }
        } else {
            inputStream = wrappedUrl.openStream();
        }

        TarUtils.unTar(new GZIPInputStream(inputStream), outputDir);
        Properties instructions = new Properties();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Collection<String> extensions = new HashSet<>();

        File mergedDefinitionFile = null;
        File packageDir = new File(outputDir, "package");
        Collection<File> files = FileUtils.listFiles(packageDir, null, true);
        try (JarOutputStream jos = new JarOutputStream(byteArrayOutputStream)) {
            Set<ZipEntry> processedImages = new HashSet<>();
            // first we get all the files that are not definitions
            List<File> filesWithMergedDefinitions = files.stream().filter(file -> !file.getName().endsWith(".cnd")).collect(Collectors.toList());
            // now we retrieve the merged definition file
            mergedDefinitionFile = getMergedDefinitionFile(files, packageDir);
            if (mergedDefinitionFile != null) {
                filesWithMergedDefinitions.add(mergedDefinitionFile);
            }
            for (File file : filesWithMergedDefinitions) {
                boolean shouldCopyFile = true;
                String path = packageDir.toURI().relativize(file.toURI()).getPath();

                if (path.equals("package.json")) {
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> properties = mapper.readValue(file, Map.class);
                    Map<String, Object> jahiaProps = (Map<String, Object>) properties.getOrDefault("jahia", new HashMap<>());

                    instructions.putAll(generateInstructions(properties, jahiaProps));
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
                } else if (path.startsWith("settings/")) {
                    jos.putNextEntry(new ZipEntry("META-INF/" + StringUtils.substringAfter(path, "settings/")));
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

    private File getMergedDefinitionFile(Collection<File> npmFiles, File packageDir) {
        List<File> definitionsFiles = npmFiles.stream().filter(file -> file.getName().endsWith(".cnd")).collect(Collectors.toList());
        Set<String> namespaces = new HashSet<>();
        StringBuilder lines = new StringBuilder();

        definitionsFiles.forEach(definitionFile -> {
            lines.append(System.getProperty("line.separator"));
            lines.append("// From ");
            String definitionFilePath = packageDir.toURI().relativize(definitionFile.toURI()).getPath();
            lines.append(definitionFilePath);
            lines.append(" : ");
            lines.append(System.getProperty("line.separator"));
            try (BufferedReader reader = new BufferedReader(new FileReader(definitionFile.getPath()))) {
                String line = reader.readLine();

                while (line != null) {
                    if (line.startsWith("<")) {
                        namespaces.add(line);
                    } else if (line.isBlank()) {
                        lines.append(System.getProperty("line.separator"));
                    } else {
                        lines.append(line);
                        lines.append(System.getProperty("line.separator"));
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
