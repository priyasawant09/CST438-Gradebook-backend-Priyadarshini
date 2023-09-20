package com.cst438;
import static com.cst438.JunitTestGradebook.fromJsonString;
import static com.google.common.base.CharMatcher.any;
import static com.google.common.base.Verify.verify;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import aj.org.objectweb.asm.TypeReference;
import com.cst438.domain.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.verification.VerificationMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.sql.Date;
import java.util.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AssignmentControllerTests {
    @Autowired
    private MockMvc mvc;

    @Mock
    private AssignmentRepository assignmentRepository;
    @Mock
    private CourseRepository courseRepository;

    @Test
    public void DeleteAssignmentTest() throws Exception {
        int assignmentId = 1;
        Assignment currentAssign = new Assignment();
        currentAssign.setId(assignmentId);

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(currentAssign));
        MockHttpServletResponse response;
         response = mvc.perform(MockMvcRequestBuilders.delete("/assignment/{assignment_id}", assignmentId)
                        .accept(MediaType.APPLICATION_JSON))
                 .andReturn().getResponse();

        assertEquals(200, response.getStatus());


    }
    @Test
    public void AddAssignmentTest() throws Exception {

        AssignmentDTO assignmentDTO = new AssignmentDTO(1, "logic", "2023-09-20", "Reasoning with Logic", 2);
        Assignment new_assignment = new Assignment();
        new_assignment.setName(assignmentDTO.assignmentName());

        when(assignmentRepository.save(new_assignment)).thenReturn(new_assignment);

        MockHttpServletResponse response;
        response = mvc.perform(MockMvcRequestBuilders.post("/assignment")
                        .content(asJsonString(assignmentDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
    }




    @Test
    public void ListAssignmentsTest() throws Exception {

        String instructorEmail = "dwisneski@csumb.edu";
        Assignment assign1 = new Assignment();
        assign1.setId(1);
        assign1.setName("Rest assignment");
        assign1.setDueDate(Date.valueOf("2023-09-23"));

        Course course1 = new Course();
        course1.setTitle("Software Engineering ");
        course1.setCourse_id(1);
        assign1.setCourse(course1);

        Assignment assign2 = new Assignment();
        assign2.setId(2);
        assign2.setName("Modus tollens");
        assign2.setDueDate(Date.valueOf("2023-10-27"));

        Course course2 = new Course();
        course2.setTitle("Reasoning with Logic");
        course2.setCourse_id(2);
        assign2.setCourse(course2);
        List<Assignment> assignments = Arrays.asList(assign1, assign2);

        when(assignmentRepository.findByEmail(instructorEmail)).thenReturn(assignments);
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/assignment")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        AssignmentDTO[] res = new AssignmentDTO[assignments.size()];
        for (int i = 0; i < assignments.size(); i++) {
            Assignment assignment = assignments.get(i);
            res[i] = new AssignmentDTO(
                    assignment.getId(),
                    assignment.getName(),
                    assignment.getDueDate().toString(),
                    assignment.getCourse().getTitle(),
                    assignment.getCourse().getCourse_id()
            );
        }
        assertEquals(2, res.length);
        assertEquals("Rest assignment", res[0].assignmentName());
        assertEquals("Modus tollens", res[1].assignmentName());
        assertEquals(200, response.getStatus());
    }

//    @Test
//    public void UpdateAssignmentsTest() throws Exception{
//        Assignment newAssignment = new Assignment();
//        newAssignment.setName("New Assignment");
//        newAssignment.setDueDate(Date.valueOf("2023-09-21"));
//        Assignment currentAssignment = assignmentRepository.save(newAssignment);
//
//        if (currentAssignment != null) {
//            System.out.println("Current Assignment ID: " + currentAssignment.getId());
//        } else {
//            System.out.println("Current Assignment is null");
//        }
//        assertNotNull(currentAssignment);
//        AssignmentDTO updatedAssignmentDTO = new AssignmentDTO(
//                currentAssignment.getId(),
//                "Updated Assignment",
//                "2023-09-22",
//                "Updated Course",
//                2
//        );
//        mvc.perform(MockMvcRequestBuilders.put("/assignment/{assignment_id}", currentAssignment.getId())
//                        .content(asJsonString(updatedAssignmentDTO))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        mvc.perform(MockMvcRequestBuilders.get("/assignment/{assignment_id}", currentAssignment.getId())
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.assignmentName").value("Updated Assignment"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.dueDate").value("2023-09-22"))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.courseTitle").value("Updated Course"));
//
//        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/assignment/{assignment_id}",currentAssignment.getId())
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andReturn().getResponse();
//        assertEquals(200, response.getStatus());
//    }




    private static String asJsonString(final Object obj) {
        try {

            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    public static <T> T fromJsonString(String str, Class<T> valueType) {
        try {
            return new ObjectMapper().readValue(str, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
