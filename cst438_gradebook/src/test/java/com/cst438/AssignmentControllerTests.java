package com.cst438;
import com.cst438.controllers.AssignmentController;
import com.cst438.domain.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        mvc.perform(delete("/assignment/{assignment_id}", assignmentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void GetAssignmentByIdTest() throws Exception {
        int assignmentId = 1;
        Assignment assignment = new Assignment();
        assignment.setId(assignmentId);
        assignment.setName("software");
        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));

        MockHttpServletResponse response;
        response = mvc.perform(MockMvcRequestBuilders
                        .get("/assignment/{assignment_id}", assignmentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
    }
    @Test
    public void AddAssignmentTest() throws Exception {
        AssignmentDTO assignmentDTO = new AssignmentDTO(1, "logic", "2023-09-28", "BUS 203 - Financial Accounting", 30157);

        Assignment new_assignment = new Assignment();
        new_assignment.setName(assignmentDTO.assignmentName());
        new_assignment.setDueDate(Date.valueOf(assignmentDTO.dueDate()));
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(new_assignment);

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

        //when(assignmentRepository.findByEmail(instructorEmail)).thenReturn(assignments);
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
    @Test
    public void UpdateAssignmentTest() throws Exception {

        AssignmentDTO assignmentDTO = new AssignmentDTO(1, "Updated Assignment", "2023-09-23", "CST 363 - Introduction to Database Systems", 31045);
        Assignment existingAssignment = new Assignment();
        existingAssignment.setId(1);
        existingAssignment.setName("Original Assignment");
        existingAssignment.setDueDate(Date.valueOf("2023-09-28"));

        when(assignmentRepository.findById(1)).thenReturn(Optional.of(existingAssignment));
        when(courseRepository.findById(3)).thenReturn(Optional.of(new Course()));
        mvc.perform(put("/assignment/{assignment_id}", 1)
                        .content(asJsonString(assignmentDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

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
