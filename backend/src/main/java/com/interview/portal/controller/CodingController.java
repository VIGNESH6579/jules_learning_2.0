package com.interview.portal.controller;

import com.interview.portal.entity.CodingProblem;
import com.interview.portal.service.CodeExecutionService;
import com.interview.portal.service.CodingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coding")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CodingController {

    @Autowired
    private CodingService codingService;

    @Autowired
    private CodeExecutionService codeExecutionService;

    @GetMapping("/problems")
    public ResponseEntity<List<CodingProblem>> getAllProblems() {
        List<CodingProblem> problems = codingService.getAllProblems();
        return ResponseEntity.ok(problems);
    }

    @GetMapping("/problems/{id}")
    public ResponseEntity<CodingProblem> getProblemById(@PathVariable Long id) {
        try {
            CodingProblem problem = codingService.getProblemById(id);
            return ResponseEntity.ok(problem);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/problems/difficulty/{difficulty}")
    public ResponseEntity<List<CodingProblem>> getProblemsByDifficulty(@PathVariable String difficulty) {
        try {
            CodingProblem.DifficultyLevel level = CodingProblem.DifficultyLevel.valueOf(difficulty.toUpperCase());
            List<CodingProblem> problems = codingService.getProblemsByDifficulty(level);
            return ResponseEntity.ok(problems);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/problems/category/{category}")
    public ResponseEntity<List<CodingProblem>> getProblemsByCategory(@PathVariable String category) {
        List<CodingProblem> problems = codingService.getProblemsByCategory(category);
        return ResponseEntity.ok(problems);
    }

    @PostMapping("/problems")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CodingProblem> createProblem(@RequestBody CodingProblem problem) {
        CodingProblem createdProblem = codingService.createProblem(problem);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProblem);
    }

    @PutMapping("/problems/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CodingProblem> updateProblem(@PathVariable Long id, @RequestBody CodingProblem problemDetails) {
        try {
            CodingProblem updatedProblem = codingService.updateProblem(id, problemDetails);
            return ResponseEntity.ok(updatedProblem);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/problems/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProblem(@PathVariable Long id) {
        try {
            codingService.deleteProblem(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Coding problem deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/submit-solution")
    public ResponseEntity<?> submitSolution(@RequestBody Map<String, String> payload) {
        String code = payload.get("code");
        String language = payload.get("language");

        if ("java".equalsIgnoreCase(language)) {
            String output = codeExecutionService.executeJavaCode(code);
            Map<String, String> response = new HashMap<>();
            response.put("output", output);
            // TODO: Implement a proper solution for fetching the expected output
            response.put("expectedOutput", "This is the expected output for the problem.");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Unsupported language: " + language);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
