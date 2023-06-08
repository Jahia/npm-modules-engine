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
                        return new java.net.PasswordAuthentication(user, password.toCharArray());;
                    }
                }).build();
            } else {
                client = HttpClient.newHttpClient();
            }

            HttpRequest request = HttpRequest.newBuilder(finalUrl.toURI())
                    .build();

            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            TarUtils.unTar(new GZIPInputStream(response.body()), outputDir);
        } catch (URISyntaxException | InterruptedException e) {
            throw new IOException(e.getMessage(), e);
        }

        Properties instructions = new Properties();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Collection<String> extensions = new HashSet<>();

        File f = new File(outputDir, "package");
        Collection<File> files = FileUtils.listFiles(f,null, true);
        try (JarOutputStream jos = new JarOutputStream(byteArrayOutputStream)) {
            for (File file : files) {
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
                if (path.equals("definitions.cnd") || path.equals("import.xml")) {
                    jos.putNextEntry(new ZipEntry("META-INF/" + path));
                } else {
                    jos.putNextEntry(new ZipEntry(path));
                }
                try (FileInputStream input = new FileInputStream(file)) {
                    IOUtils.copy(input, jos);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot transform npm-module", e);
        }
        FileUtils.deleteDirectory(outputDir);

        if (!extensions.isEmpty()) {
            instructions.put("Jahia-Module-Scripting-Views", StringUtils.join(extensions, ","));
        }

        return BndUtils.createBundle(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), instructions, wrappedUrl.toExternalForm());
    }
}