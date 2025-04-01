package com.example.course_service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import com.example.course_service.dtos.CourseDto;
import com.example.course_service.models.Course;

import com.example.course_service.RemoteServiceClient;

import com.example.course_service.PaymentServiceProxy;

import com.example.course_service.dtos.CourseWithUsersDto;
import com.example.course_service.dtos.UserDto;

import com.example.common.CustomException;

@RestController
@RequestMapping("/courses")
public class CourseController {

	@Autowired
	private CourseService service;

	@Autowired
	private PaymentServiceProxy paymentServiceProxy;

	@Autowired
	private RemoteServiceClient remoteServiceClient;

	@PreAuthorize("hasAuthority('user') || hasAuthority('admin')")
	@GetMapping()
	public ResponseEntity<List<Course>> getAllCourses() {

	
		return ResponseEntity.status(200).body(service.getAll());
	}



	@PreAuthorize("hasAuthority('user') || hasAuthority('admin')")
	@GetMapping("/{id}")
	public ResponseEntity<?> getCourseById(@PathVariable("id") Long courseId) {
		try {
			CourseWithUsersDto courseWithUsersDto = service.getCourseById1(courseId);
			return ResponseEntity.status(200).body(courseWithUsersDto);
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		} catch (CustomException e) {

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("message", e.getMessage(), "status", 503));
        }
	}

	@PreAuthorize("hasAuthority('admin')")
	@PostMapping()
	public ResponseEntity<?> createCourse(@RequestBody Course course) {

		if (service.getCourseByName(course.getName()) != null) {
			return ResponseEntity.status(400).body("Vec postoji kurs sa ovim nazivom.");
		}
		service.createCourse(course);
		return ResponseEntity.status(201).body(course);

	}
	@PreAuthorize("hasAuthority('admin')")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteCourse(@PathVariable("id") Long courseId) {
		if (service.getCourseById(courseId) == null) {
			ResponseEntity.status(404).body("Ovaj kurs ne postoji!");
		}
		service.deleteCourse(courseId);
		return ResponseEntity.status(200).body("Kurs je obrisan!");
	}
	@PreAuthorize("hasAuthority('admin')")
	@PutMapping("/{id}")
	public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody Course updatedCourse) {

		Optional<Course> courseToUpdateOptional = service.getCourseById(id);

		if (courseToUpdateOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found!");
		}

		Course courseToUpdate = courseToUpdateOptional.get();

		courseToUpdate.setName(updatedCourse.getName() != null ? updatedCourse.getName() : courseToUpdate.getName());
		courseToUpdate.setCategory(
				updatedCourse.getCategory() != null ? updatedCourse.getCategory() : courseToUpdate.getCategory());
		courseToUpdate.setDescription(updatedCourse.getDescription() != null ? updatedCourse.getDescription()
				: courseToUpdate.getDescription());
		courseToUpdate.setPrice(updatedCourse.getPrice() != 0 ? updatedCourse.getPrice() : courseToUpdate.getPrice());
		courseToUpdate.setDuration(
				updatedCourse.getDuration() != 0 ? updatedCourse.getDuration() : courseToUpdate.getDuration());

		service.updateCourse(courseToUpdate);

		return ResponseEntity.ok(courseToUpdate);

	}


	@PreAuthorize("hasAuthority('user') || hasAuthority('admin')")
	@GetMapping("/{courseId}/users")
	public ResponseEntity<?> getCourseWithUsers(@PathVariable Long courseId) {
		try{	
			return ResponseEntity.ok(service.getCourseWithUsers(courseId));
		}catch (CustomException e) {

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("message", e.getMessage(), "status", 503));
        }
	}

	@PreAuthorize("hasAuthority('user') || hasAuthority('admin')")
	@GetMapping("/search")
	public ResponseEntity<List<Course>> searchCourses(
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String category) {
		List<Course> courses = service.searchCourses(name, category);
		return ResponseEntity.ok(courses);
	}

	@PreAuthorize("hasAuthority('user') || hasAuthority('admin')|| hasAuthority('SCOPE_internal')")
	@GetMapping("/{userId}/names")
	public ResponseEntity<List<String>> getCourseNamesByUserId(@PathVariable Long userId) {
		List<String> courseNames = service.getCourseNamesByUserId(userId);

		if (courseNames.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of("No courses found for the given user."));
		}

		return ResponseEntity.ok(courseNames);
	}
	@PreAuthorize("hasAuthority('user') || hasAuthority('admin') || hasAuthority('SCOPE_internal')")
	@PostMapping("/{courseId}/enroll/{userId}")
	public ResponseEntity<?> enrollUser(@PathVariable Long courseId, @PathVariable Long userId) {
		try {
			Optional<Course> course1 = service.getCourseById(courseId);
			
			Course course = course1.get();
			float price = course.getPrice();

			if (remoteServiceClient.isTransactionApproved(userId, courseId, price)) {
				service.enrollUserToCourse(courseId, userId);
				return ResponseEntity.ok("Korisnik je upisan");
			}
			

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nemas pravo na upis");
		} catch (CustomException e) {

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("message", e.getMessage(), "status", 503));
        }catch (RuntimeException e) {
			if (e.getMessage().contains("Nema ovog kursa")) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Kurs ne postoji!");
			}
			if (e.getMessage().contains("Ovaj korisnik je vec upisan na kurs")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Korisnik je već upisan na kurs");
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Došlo je do greške: " + e.getMessage());
		}

	}
	@PreAuthorize("hasAuthority('admin')|| hasAuthority('SCOPE_internal')")
	@GetMapping("/forReview")
	public CourseDto getCourseById(@RequestParam("id") long id) {

		Optional<Course> courses = service.getCourseById(id);
		Course course = courses.get();

		CourseDto courseDto = new CourseDto(course.getName(), course.getDescription(), course.getPrice());
		return courseDto;
	}

}
