package itda.ieoso.Translate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;

@Service
public class TranslateService {

    private final TranslateClient translateClient;

    public TranslateService(
            @Value("${aws.translate.access-key:}") String accessKey,
            @Value("${aws.translate.secret-key:}") String secretKey,
            @Value("${aws.translate.region:ap-northeast-2}") String region
    ) {
        // EC2 환경에서는 accessKey가 비어있도록 설정하고,
        // 그 경우 IAM 역할을 자동 사용하게 한다.
        AwsCredentialsProvider credentialsProvider;

        if (accessKey == null || accessKey.isBlank()) {
            credentialsProvider = DefaultCredentialsProvider.create(); // IAM 역할 자동 사용
        } else {
            credentialsProvider = StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
            );
        }

        this.translateClient = TranslateClient.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();
    }

    public String translate(String text, String sourceLang, String targetLang) {

        // 만약 targetLang과 sourceLang이 같다면 번역을 안 한다
        if (sourceLang.equals(targetLang)) {
            return text; // 그냥 원본 텍스트를 리턴
        }

        TranslateTextRequest request = TranslateTextRequest.builder()
                .text(text)
                .sourceLanguageCode(sourceLang)
                .targetLanguageCode(sourceLang)
                .build();

        TranslateTextResponse response = translateClient.translateText(request);
        return response.translatedText();
    }
}

