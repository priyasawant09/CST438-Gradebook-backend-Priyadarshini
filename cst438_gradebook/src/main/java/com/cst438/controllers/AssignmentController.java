package com.cst438.controllers;

import java.sql.Date;
import java.util.ArrayList;
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

    @GetMapping("/assignment") //get by professor
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



    @GetMapping("/assignment/{assignment_id}") //get by ID

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
            @PathVariable("assignment_id") int id ,@RequestParam(value = "force") Optional<Boolean> force){
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
        if (force.isPresent() && force.get() == true){
            assignmentRepository.delete(assignment);
        }else if (assignment.getAssignmentGrades().isEmpty()) {
            assignmentRepository.delete(assignment);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Grades are there");
        }

    }


    @PutMapping("/assignment/{assignment_id}")
    public void updateAssignment(
            @PathVariable("assignment_id") int id,
            @RequestBody AssignmentDTO assign) {
        Assignment updateAssignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));
        updateAssignment.setName(assign.assignmentName());
        updateAssignment.setDueDate(Date.valueOf(assign.dueDate()));
        updateAssignment.setCourse(courseRepository.findById(assign.courseId()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Assignment not found")));
        assignmentRepository.save(updateAssignment);
    }

    @PostMapping("/assignment")
    public void AddAssignment(@RequestBody AssignmentDTO assignmentDTO) {
        Assignment new_assignment = new Assignment();
        new_assignment.setName(assignmentDTO.assignmentName());
        new_assignment.setCourse(courseRepository.findById(assignmentDTO.courseId()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Assignment not found")));
        new_assignment.setDueDate(Date.valueOf(assignmentDTO.dueDate()));
        assignmentRepository.save(new_assignment);
    }


}
