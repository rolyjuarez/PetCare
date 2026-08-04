package bo.capital.tec.pet.common.upload;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/uploads")
public class UploadController {

    @Value("${petcare.upload.dir:uploads}")
    private String uploadDir;

    @PostMapping("/mascotas")
    public ResponseEntity<Map<String, String>> uploadMascotaFoto(@RequestParam("file") MultipartFile file) throws IOException {
        return saveFile("mascotas", file);
    }

    @GetMapping("/mascotas/{filename:.+}")
    public ResponseEntity<Resource> getMascotaFoto(@PathVariable String filename) {
        return serveFile("mascotas", filename);
    }

    @PostMapping("/certificados")
    public ResponseEntity<Map<String, String>> uploadCertificado(@RequestParam("file") MultipartFile file) throws IOException {
        return saveFile("certificados", file);
    }

    @GetMapping("/certificados/{filename:.+}")
    public ResponseEntity<Resource> getCertificado(@PathVariable String filename) {
        return serveFile("certificados", filename);
    }

    private ResponseEntity<Map<String, String>> saveFile(String subdir, MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID() + extension;

        Path uploadPath = Paths.get(uploadDir, subdir);
        Files.createDirectories(uploadPath);

        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        String url = "/uploads/" + subdir + "/" + filename;
        return ResponseEntity.ok(Map.of("url", url, "filename", filename));
    }

    private ResponseEntity<Resource> serveFile(String subdir, String filename) {
        Path filePath = Paths.get(uploadDir, subdir, filename);
        Resource resource = new FileSystemResource(filePath.toFile());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = "application/octet-stream";
        try {
            contentType = Files.probeContentType(filePath);
            if (contentType == null) contentType = "application/octet-stream";
        } catch (IOException ignored) {}

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }
}
