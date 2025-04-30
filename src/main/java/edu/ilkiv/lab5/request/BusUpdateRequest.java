package edu.ilkiv.lab5.request;

/**
 * @author Bodya
 * @project lab5
 * @class BusUpdateRequest
 * version 1.0.0
 * @since 30.04.2025 - 21:15
 */
public record BusUpdateRequest(String id, String boardNumber, String code, String description) {
}