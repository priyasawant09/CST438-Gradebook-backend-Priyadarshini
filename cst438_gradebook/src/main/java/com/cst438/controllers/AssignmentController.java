package com.cst438.controllers;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;

@RestController
@CrossOrigin
public class AssignmentController {

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    CourseRepository courseRepository;

    @GetMapping("/assignment")
    public AssignmentDTO[] getAllAssignmentsForInstructor() {
        // get all assignments for this instructor
        String instructorEmail = "dwisneski@csumb.edu";  // user name (should be instructor's email)
        List<Assignment> assignments = assignmentRepository.findByEmail(instructorEmail);
        AssignmentDTO[] result = new AssignmentDTO[assignments.size()];
        for (int i = 0; i < assignments.size(); i++) {
            Assignment as = assignments.get(i);
            AssignmentDTO dto = new AssignmentDTO(
                    as.getId(),
                    as.getName(),
                    as.getDueDate().toString(),
                    as.getCourse().getTitle(),
                    as.getCourse().getCourse_id());
            result[i] = dto;
        }
        return result;
    }



    @GetMapping("/assignment/{assignment_id}")

    public AssignmentDTO getListAssignment(@PathVariable("assignment_id") int assignment_id) {
        Assignment assignment = assignmentRepository.findById(assignment_id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
        return new AssignmentDTO(
                assignment.getId(),
                assignment.getName(),
                assignment.getDueDate().toString(),
                assignment.getCourse().getTitle(),
                assignment.getCourse().getCourse_id()
        );

    }


    @DeleteMapping("/assignment/{assignment_id}")
    public void deleteAssignment(
            @PathVariable("assignment_id") int id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
        assignmentRepository.delete(assignment);

    }

    @PutMapping("/assignment/{assignment_id}")
    public Assignment updateAssignment(
            @PathVariable("assignment_id") int id,
            @RequestBody AssignmentDTO assign) {
        Assignment updateAssignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
        updateAssignment.setName(assign.assignmentName());
        updateAssignment.setDueDate(Date.valueOf(assign.dueDate()));
        return assignmentRepository.save(updateAssignment);
    }

    @PostMapping("/assignment")
    public Assignment AddAssignment(@RequestBody AssignmentDTO assignmentDTO) {
        Assignment new_assignment = new Assignment();
        new_assignment.setName(assignmentDTO.assignmentName());
        return assignmentRepository.save(new_assignment);
    }


}
