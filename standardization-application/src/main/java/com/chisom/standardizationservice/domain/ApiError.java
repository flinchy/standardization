package com.chisom.standardizationservice.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@JsonPropertyOrder(value = {"timeStamp", "message", "path", "errorCode", "infoLink", "details"})
public record ApiError(
        @JsonProperty("timeStamp") @SerializedName("timeStamp")
        @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
        @JsonDeserialize(using = InstantDeserializer.class)
        @JsonSerialize(using = InstantSerializer.class)
        Instant timeStamp,

        @JsonProperty("message") @SerializedName("message")
        String message,

        @JsonProperty("path") @SerializedName("path")
        String path,

        @JsonProperty("errorCode") @SerializedName("errorCode")
        String errorCode,

        @JsonProperty("infoLink") @SerializedName("infoLink")
        String infoLink,

        @JsonProperty("details") @SerializedName("details")
        List<Object> details) {

    @Builder
    public static ApiError create(String message, String path, String errorCode, String infoLink, List<Object> details) {
        return new ApiError(Instant.now(), message, path, errorCode, infoLink, details == null ? List.of() : details);
    }
}
