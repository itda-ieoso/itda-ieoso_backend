package itda.ieoso.Translate;

import itda.ieoso.Response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/translate")
public class TranslateController {

    @Autowired
    private TranslateService translateService;

    // 강의 공지 생성 API와 유사하게 번역 API 변경
    @PostMapping()
    public Response<String> translateText(@RequestBody TranslateRequest translateRequest) {
        // 번역 서비스 호출 (Amazon Translate 사용)
        String translatedText = translateService.translate(
                translateRequest.getText(),
                translateRequest.getSourceLang(),
                translateRequest.getTargetLang()
        );

        // 성공적으로 번역된 텍스트 반환
        return Response.success("텍스트 번역 성공", translatedText);
    }
}

