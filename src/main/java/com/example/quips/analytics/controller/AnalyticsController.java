package com.example.quips.analytics.controller;

import com.example.quips.analytics.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private CycleAnalyticsService cycleAnalyticsService;
    @Autowired
    private TokenAnalyticsService tokenAnalyticsService;
    @Autowired
    private TransactionGraphService transactionGraphService;
    @Autowired
    private TopUsersTransactionService topUsersTransactionService;
    @Autowired
    private ReferralRewardsService referralRewardsService;
    @Autowired
    private PhaseTimelineService phaseTimelineService;
    @Autowired
    private TransactionActivityService transactionActivityService;

    @GetMapping("/cycle-status")
    public ResponseEntity<?> getCycleStatus() {
        return ResponseEntity.ok(cycleAnalyticsService.getCycleStatus());
    }

    @GetMapping("/token-status")
    public ResponseEntity<?> getTokenStatus() {
        return ResponseEntity.ok(tokenAnalyticsService.getTokenStatus());
    }

    @GetMapping("/transaction-graph")
    public ResponseEntity<?> getTransactionGraph() {
        return ResponseEntity.ok(transactionGraphService.getTransactionGraph());
    }

    @GetMapping("/top-users-transactions")
    public ResponseEntity<?> getTopUsersByTransactions() {
        return ResponseEntity.ok(topUsersTransactionService.getTopUsersByTransactions());
    }

    @GetMapping("/top-users-referrals")
    public ResponseEntity<?> getTopUsersByReferrals() {
        return ResponseEntity.ok(referralRewardsService.getTopUsersByReferrals());
    }

    @GetMapping("/phase-timeline")
    public ResponseEntity<?> getPhaseTimeline() {
        return ResponseEntity.ok(phaseTimelineService.getPhaseTimeline());
    }

    @GetMapping("/transaction-activity")
    public ResponseEntity<?> getTransactionActivityByHour() {
        return ResponseEntity.ok(transactionActivityService.getTransactionActivityByHour());
    }
}
