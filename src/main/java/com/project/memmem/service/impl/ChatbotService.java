package com.project.memmem.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.memmem.domain.dto.bot.AnswerDTO;
import com.project.memmem.domain.dto.bot.MessageDTO;
import com.project.memmem.domain.dto.bot.QuestionDTO;
import com.project.memmem.domain.entity.AnswerEntity;
import com.project.memmem.domain.entity.KeywordEntity;
import com.project.memmem.domain.entity.NNPIntentionEntity;
import com.project.memmem.domain.entity.ScenarioEntity;
import com.project.memmem.domain.repository.AnswerRepository;
import com.project.memmem.domain.repository.KeywordRepository;
import com.project.memmem.domain.repository.ScenarioRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ChatbotService {

	private final KomoranService komoranService;
	private final KeywordRepository keywordRepository;
	private final AnswerRepository answerRepository;
	private final ScenarioRepository scenarioRepository;
	private final WeatherService weatherService;
	private final LocationService locationService;

	private ScenarioEntity currentScenario;
    private String pendingWeatherLocation;

    @Transactional
    public AnswerDTO processUserQuestion(QuestionDTO questionDTO) {
        MessageDTO analysisResult = komoranService.nlpAnalyze(questionDTO.getContent());
        Set<String> nouns = analysisResult.getNouns();
        
        // 날씨 관련 키워드 체크
        if (nouns.contains("날씨") || nouns.contains("기온") || nouns.contains("습도") || questionDTO.getWeatherStep() > 0) {
            return handleWeatherQuery(questionDTO);
        }
        
        // 시나리오 처리
        if (questionDTO.isInScenario() || "소모임 추천해주세요!".equals(questionDTO.getContent())) {
            return processScenario(questionDTO.getContent());
        }
        
        Set<NNPIntentionEntity> nnpIntentions = findNNPIntention(nouns);
        Optional<AnswerEntity> answerEntityOptional = Optional.empty();
        
        if (nnpIntentions.isEmpty()) {
            answerEntityOptional = answerRepository.findByNnpIntention_NnpNo(0);
        } else {
            for (NNPIntentionEntity nnpIntention : nnpIntentions) {
                if (answerEntityOptional.isEmpty()) {
                    answerEntityOptional = answerRepository.findByNnpIntention_NnpNo(nnpIntention.getNnpNo());
                }
            }
        }
        
        if (answerEntityOptional.isPresent()) {
            AnswerEntity answerEntity = answerEntityOptional.get();
            return AnswerDTO.builder()
                    .answer(answerEntity.getAnswer())
                    .nnpNo(answerEntity.getNnpIntention().getNnpNo())
                    .build();
        } else {
            return AnswerDTO.builder()
                    .answer("죄송합니다. 답변을 찾을 수 없습니다.")
                    .nnpNo(0)
                    .build();
        }
    }

    private AnswerDTO handleWeatherQuery(QuestionDTO questionDTO) {
        if (questionDTO.getWeatherStep() == 0) {
            return AnswerDTO.builder()
                .answer("어느 지역의 날씨를 알려드릴까요?")
                .nnpNo(0)
                .build();
        } else if (questionDTO.getWeatherStep() == 2) {
            return processWeatherQuery(questionDTO.getSelectedLocation());
        } else {
            String location = extractLocation(new HashSet<>(Arrays.asList(questionDTO.getContent().split("\\s+"))));
            return processWeatherQuery(location);
        }
    }

    private String extractLocation(Set<String> nouns) {
        Optional<String> location = nouns.stream()
                .filter(locationService::isValidLocation)
                .findFirst();

        if (location.isPresent()) {
            return location.get();
        }

        for (String noun : nouns) {
            Optional<String> partialMatch = locationService.findMatchingLocation(noun);
            if (partialMatch.isPresent()) {
                return partialMatch.get();
            }
        }

        return "서울";
    }

    private AnswerDTO processWeatherQuery(String location) {
        String weatherInfo = weatherService.getCurrentWeather(location);
        return AnswerDTO.builder()
                .answer(weatherInfo)
                .nnpNo(0)
                .build();
    }

	private AnswerDTO processScenario(String userInput) {
		if (currentScenario == null) {
			// 시나리오 시작
			Optional<ScenarioEntity> rootScenario = scenarioRepository.findByDeptAndParentIsNull(0);
			if (rootScenario.isPresent()) {
				currentScenario = rootScenario.get();
				return getNextScenarioStep(currentScenario);
			}
		} else {
			// 다음 시나리오 찾기
			Optional<ScenarioEntity> nextScenario = findNextScenario(currentScenario, userInput);
			if (nextScenario.isPresent()) {
				currentScenario = nextScenario.get();
				return getNextScenarioStep(currentScenario);
			}
		}
		// 시나리오를 찾지 못한 경우
		currentScenario = null; // 시나리오 초기화
		return AnswerDTO.builder().answer("죄송합니다. 해당 내용을 이해하지 못했습니다. 처음 질문으로 돌아가겠습니다.").endScenario(true).build();
	}

	private Optional<ScenarioEntity> findNextScenario(ScenarioEntity currentScenario, String userInput) {
		return scenarioRepository.findByParentAndContentContaining(currentScenario, userInput);
	}

	private AnswerDTO getNextScenarioStep(ScenarioEntity scenario) {
		List<ScenarioEntity> children = scenarioRepository.findByParentOrderByDept(scenario);
		List<String> options = children.stream().map(ScenarioEntity::getContent).toList();

		AnswerDTO answerDTO = new AnswerDTO();
		answerDTO.setAnswer(scenario.getContent());
		answerDTO.setOptions(options);
		return answerDTO;
	}

	private Set<NNPIntentionEntity> findNNPIntention(Set<String> nouns) {
		Set<NNPIntentionEntity> nnpi = new HashSet<>();
		for (String noun : nouns) {
			Optional<KeywordEntity> keyword = keywordRepository.findByKeyword(noun);
			if (keyword.isPresent()) {
				nnpi.add(keyword.get().getNnpIntention());
			}
		}
		return nnpi;
	}

	public void resetScenario() {
		currentScenario = null;
	}

	public boolean isInScenario() {
		return currentScenario != null;
	}
}