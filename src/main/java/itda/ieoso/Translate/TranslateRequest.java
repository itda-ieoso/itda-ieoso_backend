package itda.ieoso.Translate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TranslateRequest {

    private String text;
    private String sourceLang;
    private String targetLang;

}