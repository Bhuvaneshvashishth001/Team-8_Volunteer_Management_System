package com.volunteerhub.controller;

import com.volunteerhub.dto.ApiDtos.FeedbackRequest;
import com.volunteerhub.model.Feedback;
import com.volunteerhub.repository.FeedbackRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
    private final FeedbackRepository feedback;

    public FeedbackController(FeedbackRepository feedback) {
        this.feedback = feedback;
    }

    @PostMapping
    public Feedback submit(@Valid @RequestBody FeedbackRequest request) {
        Feedback entry = new Feedback();
        entry.setFromUserId(request.fromUserId());
        entry.setToUserId(request.toUserId());
        entry.setApplicationId(request.applicationId());
        entry.setRating(request.rating());
        entry.setComment(request.comment());
        return feedback.save(entry);
    }
}
