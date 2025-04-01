package com.example.review_service;

import com.example.common.CustomException;
import com.example.review_service.dtos.ReviewWithUserDto;
import com.example.review_service.models.Review;

import feign.FeignException;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService service;

    @PreAuthorize("hasAuthority('user') || hasAuthority('admin')|| hasAuthority('SCOPE_internal')")
    @GetMapping
    public ResponseEntity<?> getAllReviews() {
        try {
            List<ReviewWithUserDto> reviewsWithUsers = service.getAll();
            return ResponseEntity.ok(reviewsWithUsers);
        } catch (CustomException e) {

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("message", e.getMessage(), "status", 503));
        }
    }

    /*@PreAuthorize("hasAuthority('user') || hasAuthority('admin')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable("id") Long id) {
        try {
            ReviewWithUserDto review = service.getReviewById(id);
            return ResponseEntity.ok(review);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
            /*
             * } catch (CustomException e) {
             * return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
             * .body(Map.of("message", e.getMessage(), "status", 503));
             * }
             } catch (FeignException.ServiceUnavailable e) {
            // Ako servis nije dostupan, vraćamo 503 i poruku o grešci
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("message", e.getMessage(), "status", 503));
        }
    }
*/
@PreAuthorize("hasAuthority('user') || hasAuthority('admin')")
@GetMapping("/{id}")
public ResponseEntity<?> getReviewById(@PathVariable("id") Long id) {
    try {
        ReviewWithUserDto review = service.getReviewById(id);
        return ResponseEntity.ok(review);
    } catch (FeignException e) {
        // Ako je neka druga Feign greška, vraćamo 500 i detalje greške
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Internal server error. GReska", "details", e.getMessage()));
    }  catch (IllegalArgumentException ex) {
        // Ako nije pronađen review, vraćamo 404
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    } catch (Exception e) {
        // Za sve ostale neuhvaćene greške, vraćamo 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Internal Server Error", "details", e.getMessage()));
    }
}
    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable("id") Long id) {
        if (service.getReviewById(id) == null) {
            ResponseEntity.status(404).body("Ovaj review ne postoji!");
        }
        service.deleteReview(id);
        return ResponseEntity.status(200).body("Review je obrisan!");
    }

    @PreAuthorize("hasAuthority('user') || hasAuthority('admin')")
    @PostMapping()
    public ResponseEntity<?> postReview(@RequestBody Review review) {
        try {
            service.createReview(review);
            return ResponseEntity.status(201).body(service.createReview(review));
        } catch (CustomException e) {

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("message", e.getMessage(), "status", 503));
        }

    }

    @PreAuthorize("hasAuthority('user') || hasAuthority('admin')")
    @PutMapping("/{id}")
    public ResponseEntity<?> putReview(@PathVariable("id") Long id, @RequestBody Review updatedReview) {
        Review reviewToUpdate = service.getReviewById1(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdFromToken = authentication.getName();
        try {

        if (reviewToUpdate == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review nije pronadjen!");
        }
             if (!authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin")) &&
					!userIdFromToken.equals(service.getUserAuthorOfReview(id).getEmail())) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body("Nemate dozvolu da menjate podatke ovog korisnika!");
			}

        reviewToUpdate
                .setRating(updatedReview.getRating() != null ? updatedReview.getRating() : reviewToUpdate.getRating());
        reviewToUpdate.setComment(
                updatedReview.getComment() != null ? updatedReview.getComment() : reviewToUpdate.getComment());

        reviewToUpdate.setUpdatedAt(new Date());

        service.updateReview(reviewToUpdate);

        return ResponseEntity.ok(reviewToUpdate);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal Server Error", "details", e.getMessage()));
        }   
    }

}
