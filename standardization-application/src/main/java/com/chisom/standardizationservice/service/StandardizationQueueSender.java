package com.chisom.standardizationservice.service;

import com.chisom.standardizationservice.domain.StandardBetSettlement;
import com.chisom.standardizationservice.domain.StandardOddsChange;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import static com.chisom.standardizationservice.util.JsonUtil.builder;

@Log4j2
@Service
@RequiredArgsConstructor
public class StandardizationQueueSender implements StandardizationPublisher {

    @Override
    public void send(StandardOddsChange msg) {
        logger.info("[ODDS_CHANGE] {}", builder().create().toJson(msg));
    }

    @Override
    public void send(StandardBetSettlement msg) {
        logger.info("[BET_SETTLEMENT] {}", builder().create().toJson(msg));
    }
}
