package com.chisom.standardizationservice.service;

import com.chisom.standardizationservice.domain.StandardBetSettlement;
import com.chisom.standardizationservice.domain.StandardOddsChange;

public interface StandardizationPublisher {
    void send(StandardOddsChange msg);
    void send(StandardBetSettlement msg);
}
