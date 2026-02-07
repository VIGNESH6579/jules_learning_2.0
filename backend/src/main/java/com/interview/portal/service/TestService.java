package com.interview.portal.service;

import com.interview.portal.entity.Test;
import com.interview.portal.entity.TestResult;
import com.interview.portal.entity.User;
import com.interview.portal.repository.TestRepository;
import com.interview.portal.repository.TestResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TestService {

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private TestResultRepository testResultRepository;

    public List<Test> getAllTests() {
        return testRepository.findAll();
    }

    public List<Test> getActiveTests() {
        return testRepository.findByIsActive(true);
    }

    public Test getTestById(Long id) {
        return testRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test not found"));
    }

    public Test createTest(Test test) {
        test.setIsActive(true);
        // Save the test first to get an ID
        Test savedTest = testRepository.save(test);
        if (test.getTestQuestions() != null) {
            for (com.interview.portal.entity.TestQuestion tq : test.getTestQuestions()) {
                tq.setTest(savedTest);
            }
        }
        return testRepository.save(savedTest);
    }

    public Test updateTest(Long id, Test testDetails) {
        Test test = getTestById(id);
        test.setTitle(testDetails.getTitle());
        test.setDescription(testDetails.getDescription());
        test.setDurationMinutes(testDetails.getDurationMinutes());
        test.setPassingScore(testDetails.getPassingScore());
        test.setIsActive(testDetails.getIsActive());

        // Update test questions
        if (testDetails.getTestQuestions() != null) {
            test.getTestQuestions().clear();
            for (com.interview.portal.entity.TestQuestion tq : testDetails.getTestQuestions()) {
                tq.setTest(test);
                test.getTestQuestions().add(tq);
            }
        }
        return testRepository.save(test);
    }

    public void deleteTest(Long id) {
        testRepository.deleteById(id);
    }

    public TestResult submitTestResult(User user, Test test, Integer score, Integer correctAnswers,
                                       Integer wrongAnswers, Integer skippedQuestions, Integer timeTakenSeconds) {
        TestResult result = new TestResult();
        result.setUser(user);
        result.setTest(test);
        result.setScore(score);
        result.setCorrectAnswers(correctAnswers);
        result.setWrongAnswers(wrongAnswers);
        result.setSkippedQuestions(skippedQuestions);
        result.setTotalQuestions(test.getTestQuestions().size());
        result.setPercentage((double) score / test.getTestQuestions().size() * 100);
        result.setStatus(score >= test.getPassingScore() ? TestResult.ResultStatus.PASSED : TestResult.ResultStatus.FAILED);
        result.setTimeTakenSeconds(timeTakenSeconds);
        result.setCompletedAt(LocalDateTime.now());

        return testResultRepository.save(result);
    }

    public List<TestResult> getUserTestResults(Long userId) {
        return testResultRepository.findByUserIdOrderByCompletedAtDesc(userId);
    }

    public TestResult getLatestTestResult(User user, Test test) {
        return testResultRepository.findFirstByUserAndTestOrderByCompletedAtDesc(user, test)
                .orElseThrow(() -> new RuntimeException("Test result not found"));
    }

    public long getTotalTests() {
        return testRepository.count();
    }
}
