package com.cst438.service;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cst438.domain.FinalGradeDTO;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@ConditionalOnProperty(prefix = "gradebook", name = "service", havingValue = "mq")
public class GradebookServiceMQ implements GradebookService {
	
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	@Autowired
	EnrollmentRepository enrollmentRepository;
	
	Queue gradebookQueue = new Queue("gradebook-queue", true);
	@Bean
	public Queue registrationQueue() {
	    return new Queue("registration-queue", true);
	}
	// send message to grade book service about new student enrollment in course
	@Override
	public void enrollStudent(String student_email, String student_name, int course_id) {
		System.out.println("Start Message "+ student_email +" " + course_id); 
		System.out.println("INSIDERABBITMQ"); 
		// create EnrollmentDTO, convert to JSON string and send to gradebookQueue
		EnrollmentDTO dto = new EnrollmentDTO(1,student_email,student_name,course_id);
		String enroll = asJsonString(dto);
		rabbitTemplate.convertAndSend(gradebookQueue.getName(),enroll);// TODO
	}
	
	@RabbitListener(queues = "registration-queue")
	@Transactional
	public void receive(String message) {
		System.out.println("Receive grades :" + message);
		/*
		 * for each student grade in courseDTO,  find the student enrollment 
		 * entity and update the grade.
		 */
		
		// deserialize the string message to FinalGradeDTO[] 
		FinalGradeDTO[] grades = fromJsonString(message,FinalGradeDTO[].class);
		System.out.println(grades.length);
		for(int i=0;i<grades.length;i++) {
			Enrollment enrollment = enrollmentRepository.findByEmailAndCourseId(grades[i].studentEmail(), grades[i].courseId());
			if (enrollment == null) {
				System.out.println("NULL");
			}
			else {
				enrollment.setCourseGrade(grades[i].grade());
				enrollmentRepository.save(enrollment);
			}
			}
		
		// TODO

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
