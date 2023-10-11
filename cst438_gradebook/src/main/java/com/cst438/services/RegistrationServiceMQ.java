package com.cst438.services;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cst438.domain.Course;
import com.cst438.domain.FinalGradeDTO;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@ConditionalOnProperty(prefix = "registration", name = "service", havingValue = "mq")
public class RegistrationServiceMQ implements RegistrationService {

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public RegistrationServiceMQ() {
		System.out.println("MQ registration service ");
	}

	@Bean
	Queue createQueue() {
		return new Queue("gradebook-queue");
	}

	Queue registrationQueue = new Queue("registration-queue", true);

	/*
	 * Receive message for student added to course
	 */
	@RabbitListener(queues = "gradebook-queue")
	@Transactional
	public void receive(String message) {
		
		System.out.println("Gradebook has received: "+message);

		EnrollmentDTO enrollmentDTO = fromJsonString(message, EnrollmentDTO.class);
		Course course = courseRepository.findById(enrollmentDTO.courseId()).orElse(null);

		if (course!= null){
			Enrollment enrollment = new Enrollment();
			enrollment.setCourse(course);
			enrollment.setStudentName(enrollmentDTO.studentName());
			enrollment.setStudentEmail(enrollmentDTO.studentEmail());
			enrollmentRepository.save(enrollment);
		}

	}

	/*
	 * Send final grades to Registration Service 
	 */
	@Override
	public void sendFinalGrades(int course_id, FinalGradeDTO[] grades) {
		try {
			System.out.println("Start sendFinalGrades " + course_id);

			ObjectMapper objectMapper = new ObjectMapper();
			String gradeJson = objectMapper.writeValueAsString(grades);

			System.out.println("Sending grade message: " + gradeJson);

			rabbitTemplate.convertAndSend(registrationQueue.getName(), gradeJson);

			System.out.println("Grade message sent successfully.");
		} catch (Exception e) {
			throw new RuntimeException("Error sending final grades: " + e.getMessage(), e);
		}
	}


	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> T  fromJsonString(String str, Class<T> valueType ) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


}
