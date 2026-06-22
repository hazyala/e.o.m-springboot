package polytech.aisw.eom.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudinaryMediaStorageService {

    private static final long DEFAULT_MAX_FILE_SIZE_BYTES = 50L * 1024L * 1024L;

    private final RestClient restClient;
    private final String cloudName;
    private final String apiKey;
    private final String apiSecret;
    private final String folder;
    private final long maxFileSizeBytes;

    public CloudinaryMediaStorageService(
            RestClient.Builder restClientBuilder,
            @Value("${app.cloudinary.url:}") String cloudinaryUrl,
            @Value("${app.cloudinary.cloud-name:}") String cloudName,
            @Value("${app.cloudinary.api-key:}") String apiKey,
            @Value("${app.cloudinary.api-secret:}") String apiSecret,
            @Value("${app.cloudinary.folder:eom-posts}") String folder,
            @Value("${app.media.max-file-size-bytes:" + DEFAULT_MAX_FILE_SIZE_BYTES + "}") long maxFileSizeBytes
    ) {
        CloudinaryCredentials credentials = CloudinaryCredentials.from(cloudinaryUrl, cloudName, apiKey, apiSecret);
        this.restClient = restClientBuilder.build();
        this.cloudName = credentials.cloudName();
        this.apiKey = credentials.apiKey();
        this.apiSecret = credentials.apiSecret();
        this.folder = folder;
        this.maxFileSizeBytes = maxFileSizeBytes;
    }

    public MediaUploadResult upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        assertConfigured();
        assertAllowed(file);

        Map<String, String> signedParams = signedParams();
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileResource(file));
        body.add("api_key", apiKey);
        signedParams.forEach(body::add);
        body.add("signature", signature(signedParams));

        Map<String, Object> response;
        try {
            response = restClient.post()
                    .uri("https://api.cloudinary.com/v1_1/{cloudName}/auto/upload", cloudName)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (RuntimeException exception) {
            throw new MediaUploadException("Cloudinary upload failed. Check the Cloudinary environment variables.", exception);
        }

        String mediaUrl = stringValue(response, "secure_url");
        if (mediaUrl.isBlank()) {
            throw new MediaUploadException("Cloudinary upload did not return a media URL.");
        }

        String resourceType = stringValue(response, "resource_type");
        String thumbnailUrl = "video".equals(resourceType) ? videoThumbnailUrl(mediaUrl) : mediaUrl;
        return new MediaUploadResult(mediaUrl, thumbnailUrl);
    }

    private void assertConfigured() {
        if (cloudName.isBlank() || apiKey.isBlank() || apiSecret.isBlank()) {
            throw new MediaUploadException("Cloudinary is not configured. Set CLOUDINARY_URL or CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, and CLOUDINARY_API_SECRET.");
        }
    }

    private void assertAllowed(MultipartFile file) {
        if (file.getSize() > maxFileSizeBytes) {
            throw new MediaUploadException("The attached file is too large. Upload a file under 50 MB.");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/") && !contentType.startsWith("video/"))) {
            throw new MediaUploadException("Only image and video files can be attached.");
        }
    }

    private Map<String, String> signedParams() {
        Map<String, String> params = new TreeMap<>();
        params.put("folder", folder);
        params.put("timestamp", String.valueOf(Instant.now().getEpochSecond()));
        return params;
    }

    private ByteArrayResource fileResource(MultipartFile file) {
        try {
            return new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
        } catch (IOException exception) {
            throw new MediaUploadException("Could not read the attached file.", exception);
        }
    }

    private String signature(Map<String, String> params) {
        String payload = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((left, right) -> left + "&" + right)
                .orElse("") + apiSecret;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            return HexFormat.of().formatHex(digest.digest(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new MediaUploadException("Could not sign the Cloudinary upload request.", exception);
        }
    }

    private String stringValue(Map<String, Object> response, String key) {
        if (response == null || response.get(key) == null) {
            return "";
        }
        return response.get(key).toString();
    }

    private String videoThumbnailUrl(String mediaUrl) {
        int extensionIndex = mediaUrl.lastIndexOf('.');
        if (extensionIndex <= mediaUrl.lastIndexOf('/')) {
            return mediaUrl + ".jpg";
        }
        return mediaUrl.substring(0, extensionIndex) + ".jpg";
    }

    private record CloudinaryCredentials(String cloudName, String apiKey, String apiSecret) {

        private static CloudinaryCredentials from(
                String cloudinaryUrl,
                String cloudName,
                String apiKey,
                String apiSecret
        ) {
            if (!cloudName.isBlank() && !apiKey.isBlank() && !apiSecret.isBlank()) {
                return new CloudinaryCredentials(cloudName, apiKey, apiSecret);
            }
            if (cloudinaryUrl == null || cloudinaryUrl.isBlank()) {
                return new CloudinaryCredentials("", "", "");
            }

            try {
                URI uri = URI.create(cloudinaryUrl.trim());
                String userInfo = uri.getRawUserInfo();
                String host = uri.getHost();
                if (!"cloudinary".equalsIgnoreCase(uri.getScheme()) || userInfo == null || host == null) {
                    return new CloudinaryCredentials("", "", "");
                }

                String[] authParts = userInfo.split(":", 2);
                if (authParts.length != 2) {
                    return new CloudinaryCredentials("", "", "");
                }

                return new CloudinaryCredentials(
                        decode(host),
                        decode(authParts[0]),
                        decode(authParts[1])
                );
            } catch (IllegalArgumentException exception) {
                return new CloudinaryCredentials("", "", "");
            }
        }

        private static String decode(String value) {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        }
    }
}
