package com.example.user_service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user_service.dtos.UserDTO1;
import com.example.user_service.dtos.UserDto;
import com.example.user_service.models.User;

import jakarta.annotation.security.PermitAll;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService service;

	@PreAuthorize("hasAuthority('admin') || hasAuthority('user')")
	@GetMapping()
	public ResponseEntity<List<User>> getAllUsers() {

		return ResponseEntity.status(200).body(service.getAll());
	}

	@PreAuthorize("hasAuthority('admin')")
	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable("id") Long userId) {
		return service.getUserById(userId)
				.map(user -> ResponseEntity.ok(user))
				.orElseGet(() -> ResponseEntity.status(404).body(null));
	}

	@PermitAll
	@PostMapping("/create")
	public ResponseEntity<?> createUser(@RequestBody User user) {

		if (service.getUserByUsername(user.getUsername()) != null) {
			return ResponseEntity.status(400).body("Vec postoji korisnik sa ovim korisnickim imenom.");
		}
		service.createUser(user);
		return ResponseEntity.status(201).body(user);

	}

	@PreAuthorize("hasAuthority('admin')")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable("id") Long userId) {
		Optional<User> userOptional = service.getUserById(userId);

		if (userOptional.isEmpty()) {

			return ResponseEntity.status(404).body("Ovaj korisnik ne postoji!");
		}

		service.deleteUser(userId); 
		return ResponseEntity.status(200).body("Korisnik je obrisan!"); 
	}

	@PreAuthorize("hasAuthority('admin') || hasAuthority('user')")
	@PutMapping("/{id}")
	public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String userIdFromToken = authentication.getName(); 
		try {

			Optional<User> userToUpdateOptional = service.getUserById(id);
			if (userToUpdateOptional.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Korisnik nije pronađen!");
			}

			User userToUpdate = userToUpdateOptional.get();
			
			if (!authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin")) &&
					!userIdFromToken.equals(userToUpdate.getEmail())) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body("Nemate dozvolu da menjate podatke ovog korisnika!");
			}

			if (updatedUser.getUsername() != null &&
					!userToUpdate.getUsername().equals(updatedUser.getUsername()) &&
					service.getUserByUsername(updatedUser.getUsername()) != null) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body("Korisničko ime '" + updatedUser.getUsername() + "' je već zauzeto!");
			}

			userToUpdate.setEmail(updatedUser.getEmail() != null ? updatedUser.getEmail() : userToUpdate.getEmail());
			userToUpdate.setPassword(
					updatedUser.getPassword() != null ? updatedUser.getPassword() : userToUpdate.getPassword());
			userToUpdate.setRole(updatedUser.getRole() != null ? updatedUser.getRole() : userToUpdate.getRole());
			userToUpdate.setUsername(
					updatedUser.getUsername() != null ? updatedUser.getUsername() : userToUpdate.getUsername());

			service.updateUser(userToUpdate);

			return ResponseEntity.ok(userToUpdate);

		} catch (Exception ex) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Došlo je do greške: " + ex.getMessage());
		}
	}

	@PreAuthorize("hasAuthority('admin')|| hasAuthority('user')|| hasAuthority('SCOPE_internal')")
	@GetMapping("/forCourse")
	public List<UserDto> getUsersByIds(@RequestParam("ids") String ids) {
		List<Long> idsList = Arrays.stream(ids.split(","))
				.map(Long::parseLong)
				.collect(Collectors.toList());

		List<User> users = service.getUsersByIds(idsList);

		List<UserDto> userDtos = users.stream()
				.map(user -> new UserDto(user.getUsername(), user.getEmail()))
				.collect(Collectors.toList());
		userDtos.forEach(user -> {
			System.out.println("UserDto found: " + user.getUsername() + ", " + user.getEmail());
		});
		return userDtos;
	}

	@PreAuthorize("hasAuthority('admin')|| hasAuthority('user')|| hasAuthority('SCOPE_internal')")
	@GetMapping("/{userId}/courses")
	public ResponseEntity<List<String>> getCoursesForUser(@PathVariable Long userId) {
		List<String> courseNames = service.getCoursesForUser(userId);

		if (courseNames != null && courseNames.size() == 1
				&& "Korisnik nije upisan ni na jedan kurs.".equals(courseNames.get(0))) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(courseNames);
		}

		return ResponseEntity.ok(courseNames);
	}

	@PreAuthorize("hasAuthority('admin')|| hasAuthority('user')|| hasAuthority('SCOPE_internal')")
	@GetMapping("/forReview")
	public UserDto getUserById(@RequestParam("id") long id) {

		Optional<User> users = service.getUserById(id);
		User user = users.get();

		UserDto userDto = new UserDto(user.getUsername(), user.getEmail());
		return userDto;
	}

	@PreAuthorize("hasAuthority('admin')|| hasAuthority('user')|| hasAuthority('SCOPE_internal')")
	@GetMapping("/forNotification")
	public UserDTO1 getUserEmail(@RequestParam("id") long id) {
		Optional<User> users = service.getUserById(id);
		User user = users.get();

		UserDTO1 userDto = new UserDTO1(user.getId(), user.getEmail());
		return userDto;
	}

}
