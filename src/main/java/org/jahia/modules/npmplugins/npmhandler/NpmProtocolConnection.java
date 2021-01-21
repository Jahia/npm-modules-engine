package org.jahia.modules.npmplugins.npmhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.ops4j.pax.swissbox.bnd.BndUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;

/**
 * Npm protocol handler
 * Transform npm pack into bundle
 */
public class NpmProtocolConnection extends URLConnection {
    private URL wrappedUrl;

    public NpmProtocolConnection(URL url) throws MalformedURLException {
        super(url);
        wrappedUrl = new URL(this.url.toString().substring("npm://".length()));
    }

    @Override
    public void connect() throws IOException {
        //
    }

    @Override
    public InputStream getInputStream() throws IOException {
        connect();

        File outputDir = Files.createTempDirectory("npm.").toFile();
        TarUtils.unTar(new GZIPInputStream(wrappedUrl.openStream()), outputDir);

        Properties manifest = new Properties();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JarOutputStream jos = null;

        Collection<String> extensions = new HashSet<>();

        File f = new File(outputDir, "package");
        Collection<File> files = FileUtils.listFiles(f,null, true);
        try {
            jos = new JarOutputStream(byteArrayOutputStream);
            for (File file : files) {
                String path = f.toURI().relativize(file.toURI()).getPath();

                if (path.equals("package.json")) {
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> properties = mapper.readValue(file, Map.class);

                    String name = (String) properties.get("name");
                    manifest.put("Bundle-Name", "Javascript module : " + name);
                    manifest.put("Bundle-SymbolicName", "js-" + name.replace("@", "").replace('/', '-'));
                    manifest.put("Bundle-Version", properties.get("version"));
                    manifest.put("Bundle-Category", "jahia-module");
                    manifest.put("Jahia-GroupId", "org.jahia.npm");
                    manifest.put("Jahia-Required-Version", "8.0.0.0");
                    manifest.put("Jahia-Module-Type", "npm");
                    manifest.put("Jahia-Javascript-Name", name);
                    manifest.put("Jahia-Static-Resources", "/css,/icons,/images,/img,/javascript");
                }
                if (path.startsWith("jahia-views/")) {
                    path = path.substring("jahia-views/".length());
                    if (file.isFile()) {
                        extensions.add(StringUtils.substringAfterLast(file.getName(), "."));
                    }
                }
                if (path.equals("definitions.cnd")) {
                    jos.putNextEntry(new ZipEntry("META-INF/definitions.cnd"));
                } else {
                    jos.putNextEntry(new ZipEntry(path));
                }
                IOUtils.copy(new FileInputStream(file), jos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        IOUtils.closeQuietly(jos);
        FileUtils.deleteDirectory(outputDir);

        if (!extensions.isEmpty()) {
            manifest.put("Jahia-Module-Scripting-Views", StringUtils.join(extensions, ","));
        }

        return BndUtils.createBundle(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), manifest, wrappedUrl.toExternalForm());
    }
}