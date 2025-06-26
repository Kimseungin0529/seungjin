package com.project.doongdoong.global.util;


import com.project.doongdoong.domain.analysis.dto.response.FellingStateCreateResponse;
import com.project.doongdoong.domain.voice.model.Voice;
import com.project.doongdoong.global.dto.request.ConsultRequest;
import com.project.doongdoong.global.dto.request.VoiceToS3Request;
import com.project.doongdoong.global.dto.response.CounselAiResponse;
import com.project.doongdoong.global.exception.servererror.ExternalApiCallException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WebClientUtil {
    private final WebClient webClient;

    private WebClient defaultWebClient;

    @Value("${spring.lambda.text.url}")
    private String lambdaTextApiUrl;

    @Value("${spring.lambda.consult}")
    private String lambdaConsultApiUrl;

    @Value("${spring.lambda.emotion_voice}")
    private String lambdaVoiceApiUrl;

    private static final String VOICE_KEY = "voice/";
    private static final String CATEGORY_PARAMETER = "category";
    private static final String QUESTION_PARAMETER = "question";
    private static final String ANALYSIS_CONTENT_PARAMETER = "analysisContent";


    @PostConstruct
    public void init() {
        this.defaultWebClient = webClient.mutate().build();
        /**
         * [ 전후 비교: WebClient 재사용 적용 ]
         * WebClient 생성 방식	webClient.mutate().build() 매번 호출	@PostConstruct에서 한 번만 생성 후 재사용
         * 객체 생성 비용	매번 새로운 인스턴스 생성 → GC 부담 ↑	단일 인스턴스 재사용으로 비용 ↓
         * 코드 중복	매 API 호출마다 URI, contentType, build 반복	공통 메서드(callLambda)로 추출
         * 가독성	API마다 WebClient 설정이 흩어져 있음	설정 일원화 → 읽기 쉬움
         * 예외 처리	doOnError로 side-effect만 가능	onErrorMap 등 더 적절한 처리 가능
         * 유지보수성	URL이나 contentType 변경 시 여러 군데 수정	한 곳에서 수정 가능
         *
         * [ 검색용 키워드 ]
         * 상황	추천 키워드
         * WebClient 재사용 관련	spring webclient reuse, webclient mutate build vs reuse
         * 성능 이슈 체크	spring webclient performance memory gc, webclient per request vs singleton
         * 클린 코드 관점	webclient builder duplicate code, spring webclient refactor
         * 예외 처리 개선	webclient error handling onErrorMap vs doOnError
         * 테스트 시 불편함	mock webclient mutate build test, webclient reuse in tests
         *
         * [ 직접 검증 아이디어]
         * 로깅 체크: WebClient.Builder.build() 호출 횟수를 로그로 찍어보면 재사용 여부 확인 가능
         * GC 성능 체크: JVisualVM, JFR, YourKit 등으로 GC 비용을 추적해보기 (전역 재사용 시 적게 나옴)
         * 테스트 효율성: Mockito로 WebClient mocking 시, mutate().build()를 여러 번 부르면 mocking 어려움 → 재사용 시 훨씬 간단해짐
         * [ 추가로 알아두면 좋은 개념]
         * WebClient.Builder	WebClient를 만들기 위한 팩토리 역할, 재사용 가능한 설정 가능
         * Spring WebFlux	Reactive 프로그래밍을 위한 스프링 모듈, non-blocking I/O 기반
         * WebClient 인스턴스의 Thread-Safe 여부	WebClient는 thread-safe → 전역 재사용 OK
         * Builder Pattern과 효율성	builder는 의도적으로 단기 사용 → 남발하면 성능 저하 가능
         */
    }


    public List<FellingStateCreateResponse> callAnalyzeEmotion(List<Voice> voices) {

        Flux<FellingStateCreateResponse> responseFlux = Flux
                .fromIterable(voices)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(this::callLambdaApi)
                .sequential();

        return responseFlux.collectList().block();
    }

    private Mono<FellingStateCreateResponse> callLambdaApi(Voice voice) {

        VoiceToS3Request body = VoiceToS3Request.builder()
                .fileKey(VOICE_KEY + voice.getStoredName())
                .build();

        return defaultWebClient
                .post()
                .uri(lambdaTextApiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .map(ExternalApiCallException::new)
                )
                .bodyToMono(FellingStateCreateResponse.class);

    }

    public List<FellingStateCreateResponse> callAnalyzeEmotionVoice(List<Voice> voices) {

        Flux<FellingStateCreateResponse> responseFlux = Flux
                .fromIterable(voices)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(this::callLambdaApiVoice)
                .sequential();


        return responseFlux.collectList().block();
    }

    private Mono<FellingStateCreateResponse> callLambdaApiVoice(Voice voice) {

        VoiceToS3Request body = VoiceToS3Request.builder()
                .fileKey(VOICE_KEY + voice.getStoredName())
                .build();

        return defaultWebClient
                .post()
                .uri(lambdaVoiceApiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .map(ExternalApiCallException::new)
                )
                .bodyToMono(FellingStateCreateResponse.class);

    }

    public CounselAiResponse callConsult(HashMap<String, Object> parameters) {

        ConsultRequest body = ConsultRequest.builder()
                .category(parameters.get(CATEGORY_PARAMETER).toString())
                .question(parameters.get(QUESTION_PARAMETER).toString())
                .analysisContent(parameters.get(ANALYSIS_CONTENT_PARAMETER).toString())
                .build();

        /**
         * Mock 용도와 같은 테스트 용도 주석 코드
         */
        CounselAiResponse counselAiResponse = new CounselAiResponse("답변입니다.", "임시 imageUrl 입니다.");
        return  counselAiResponse;
        /*return defaultWebClient
                .post()
                .uri(lambdaConsultApiUrl)
                .bodyValue(body)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .map(ExternalApiCallException::new)
                )
                .bodyToMono(CounselAiResponse.class)
                .block();*/
    }
}
