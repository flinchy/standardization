package com.chisom.standardizationservice.domain;

public enum BetOutcome1X2 {
    ONE, X, TWO;

    public static BetOutcome1X2 getBetOutcome(String val) {
        return switch (val.toLowerCase()) {
            case "1", "home" -> ONE;
            case "x", "draw" -> X;
            case "2", "away" -> TWO;
            default -> throw new IllegalArgumentException("Unknown outcome: " + val);
        };
    }
}
