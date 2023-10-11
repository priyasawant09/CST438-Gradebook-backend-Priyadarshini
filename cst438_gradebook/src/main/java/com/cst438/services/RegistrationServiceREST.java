package com.cst438.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.cst438.domain.FinalGradeDTO;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Enrollment;

@Service
@ConditionalOnProperty(prefix = "registration", name = "service", havingValue = "rest")
@RestController
public class RegistrationServiceREST implements RegistrationService {

	
	RestTemplate restTemplate = new RestTemplate();
	
	@Value("${registration.url}") 
	String registration_url;


	public RegistrationServiceREST() {
		System.out.println("REST registration service ");
	}

	@Override
	public void sendFinalGrades(int course_id , FinalGradeDTO[] grades) {
		//TODO use restTemplate to send final grades to registration service
		String url = registration_url +"/" +  Integer.toString(course_id);
		System.out.println("Inside sendFinalGrades ");
		System.out.println(url);
		restTemplate.put(url ,grades);
	}
	
	@Autowired

	CourseRepository courseRepository;

	@Autowired
	EnrollmentRepository enrollmentRepository;

	
	/*
	 * endpoint used by registration service to add an enrollment to an existing
	 * course.
	 */
	@PostMapping("/enrollment")
	@Transactional
	public EnrollmentDTO addEnrollment(@RequestBody EnrollmentDTO enrollmentDTO) {

		// Receive message from registration service to enroll a student into a course.
		Course course = courseRepository.findById(enrollmentDTO.courseId()).orElse(null);
		Enrollment enroll = new Enrollment();
		enroll.setCourse(course);
		enroll.setStudentName(enrollmentDTO.studentName());
		enroll.setStudentEmail(enrollmentDTO.studentEmail());

		enrollmentRepository.save(enroll);

		//System.out.println("GradeBook addEnrollment "+enrollmentDTO);
		return enrollmentDTO;
		
	}

}
