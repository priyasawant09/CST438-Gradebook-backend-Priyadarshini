package com.cst438.controllers;

import java.security.Principal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import com.cst438.domain.*;
import com.cst438.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;

@RestController
@CrossOrigin
public class AssignmentController {

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/assignment") //get by professor
    public AssignmentDTO[] getAllAssignmentsForInstructor(Principal principal) {
        // get all assignments for this instructor
        System.out.print(principal.getName());
        String userEmail = principal.getName();
        //User user = userRepository.findByUsername(userEmail);

       // if (user.getRole().equals("ADMIN")) {
            List<Assignment> assignments = assignmentRepository.findByEmail(userEmail);
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
//        } else {
//
//            return new AssignmentDTO[0];
        }





    @GetMapping("/assignment/{assignment_id}") //get by ID

    public AssignmentDTO getListAssignment(@PathVariable("assignment_id") int assignment_id, Principal principal) {
        String userEmail = principal.getName();
        Assignment assignment = assignmentRepository.findById(assignment_id).orElse(null);
        if (assignment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found " + assignment_id);
        }
        if (!assignment.getCourse().getInstructor().equals(userEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized " + assignment_id);
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
            @PathVariable("assignment_id") int id ,@RequestParam(value = "force") Optional<String> force , Principal principal){
        String instructorEmail = principal.getName();
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
            @RequestBody AssignmentDTO assign , Principal principal) {
        String instructorEmail = principal.getName();
        Assignment updateAssignment = assignmentRepository.findById(id).orElse(null);
        if (updateAssignment==null || ! updateAssignment.getCourse().getInstructor().equals(instructorEmail)){
            throw  new ResponseStatusException( HttpStatus.NOT_FOUND, "assignment not found or not authorized "+id);
        }
        updateAssignment.setName(assign.assignmentName());
        updateAssignment.setDueDate(Date.valueOf(assign.dueDate()));
        assignmentRepository.save(updateAssignment);
    }

    @PostMapping("/assignment")
    public int AddAssignment(@RequestBody AssignmentDTO assignmentDTO , Principal principal) {
        String instructor_email = principal.getName();
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
