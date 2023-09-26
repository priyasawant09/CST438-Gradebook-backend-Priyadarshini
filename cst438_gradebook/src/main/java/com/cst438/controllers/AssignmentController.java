package com.cst438.controllers;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

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
        String instructorEmail = "dwisneski@csumb.edu";
        Assignment assignment = assignmentRepository.findById(assignment_id).orElse(null);
        if (assignment == null){
            throw  new ResponseStatusException( HttpStatus.NOT_FOUND, "assignment not found "+ assignment_id);
        }
        if (! assignment.getCourse().getInstructor().equals(instructorEmail)) {
            throw  new ResponseStatusException( HttpStatus.FORBIDDEN, "not authorized "+assignment_id);
        }
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
            @PathVariable("assignment_id") int id ,@RequestParam(value = "force") Optional<String> force){
        String instructorEmail = "dwisneski@csumb.edu";
        Assignment assignment = assignmentRepository.findById(id).orElse(null);
        if (assignment == null){
            return;
        }
        if (!assignment.getCourse().getInstructor().equals(instructorEmail)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"not authorized "+id);
        }
        if (assignment.getAssignmentGrades().isEmpty() || force.isPresent()) {
            assignmentRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Grades are there");
        }

    }


    @PutMapping("/assignment/{assignment_id}")
    public void updateAssignment(
            @PathVariable("assignment_id") int id,
            @RequestBody AssignmentDTO assign) {
        String instructorEmail = "dwisneski@csumb.edu";
        Assignment updateAssignment = assignmentRepository.findById(id).orElse(null);
        if (updateAssignment==null || ! updateAssignment.getCourse().getInstructor().equals(instructorEmail)){
            throw  new ResponseStatusException( HttpStatus.NOT_FOUND, "assignment not found or not authorized "+id);
        }
        updateAssignment.setName(assign.assignmentName());
        updateAssignment.setDueDate(Date.valueOf(assign.dueDate()));
        assignmentRepository.save(updateAssignment);
    }

    @PostMapping("/assignment")
    public int AddAssignment(@RequestBody AssignmentDTO assignmentDTO) {
        String instructor_email = "dwisneski@csumb.edu";
        Course course = courseRepository.findById(assignmentDTO.courseId()).orElse(null);
        if(course == null || ! course.getInstructor().equals(instructor_email))
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "course id not found or not authorized" + assignmentDTO.courseId());
        Assignment new_assignment = new Assignment();
        new_assignment.setName(assignmentDTO.assignmentName());
        new_assignment.setCourse(course);
        new_assignment.setDueDate(Date.valueOf(assignmentDTO.dueDate()));
        assignmentRepository.save(new_assignment);
        return new_assignment.getId();
    }




}
