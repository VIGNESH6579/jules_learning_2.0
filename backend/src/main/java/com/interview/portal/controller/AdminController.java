package com.interview.portal.controller;

import com.interview.portal.entity.CodingProblem;
import com.interview.portal.entity.Test;
import com.interview.portal.entity.User;
import com.interview.portal.service.CodingService;
import com.interview.portal.service.TestService;
import com.interview.portal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private TestService testService;

    @Autowired
    private CodingService codingService;

    // Test Management Endpoints

    @PostMapping("/tests")
    public ResponseEntity<Test> createTest(@RequestBody Test test) {
        return ResponseEntity.status(HttpStatus.CREATED).body(testService.createTest(test));
    }

    @PutMapping("/tests/{id}")
    public ResponseEntity<Test> updateTest(@PathVariable Long id, @RequestBody Test test) {
        return ResponseEntity.ok(testService.updateTest(id, test));
    }

    @DeleteMapping("/tests/{id}")
    public ResponseEntity<Void> deleteTest(@PathVariable Long id) {
        testService.deleteTest(id);
        return ResponseEntity.noContent().build();
    }

    // Coding Problem Management Endpoints

    @PostMapping("/coding-problems")
    public ResponseEntity<CodingProblem> createCodingProblem(@RequestBody CodingProblem problem) {
        return ResponseEntity.status(HttpStatus.CREATED).body(codingService.createProblem(problem));
    }

    @PutMapping("/coding-problems/{id}")
    public ResponseEntity<CodingProblem> updateCodingProblem(@PathVariable Long id, @RequestBody CodingProblem problem) {
        return ResponseEntity.ok(codingService.updateProblem(id, problem));
    }

    @DeleteMapping("/coding-problems/{id}")
    public ResponseEntity<Void> deleteCodingProblem(@PathVariable Long id) {
        codingService.deleteProblem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/students")
    public ResponseEntity<List<User>> getAllStudents() {
        List<User> students = userService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/pending-approvals")
    public ResponseEntity<List<User>> getPendingApprovals() {
        List<User> pendingUsers = userService.getPendingApprovals();
        return ResponseEntity.ok(pendingUsers);
    }

    @PutMapping("/users/{userId}/approve")
    public ResponseEntity<?> approveUser(@PathVariable Long userId) {
        try {
            User approvedUser = userService.approveUser(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User approved successfully");
            response.put("user", approvedUser);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PutMapping("/users/{userId}/reject")
    public ResponseEntity<?> rejectUser(@PathVariable Long userId) {
        try {
            User rejectedUser = userService.rejectUser(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User rejected successfully");
            response.put("user", rejectedUser);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PutMapping("/users")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(user);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User updated successfully");
            response.put("user", updatedUser);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getAdminStats() {
        try {
            long totalUsers = userService.getAllUsers().size();
            long totalStudents = userService.getAllStudents().size();
            long pendingApprovals = userService.getPendingApprovals().size();

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", totalUsers);
            stats.put("totalStudents", totalStudents);
            stats.put("pendingApprovals", pendingApprovals);
            stats.put("approvedUsers", totalUsers - pendingApprovals);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error fetching admin stats");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
