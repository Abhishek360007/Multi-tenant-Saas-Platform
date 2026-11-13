package com.abhishek.tenanthub.common.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;


@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Push message")
public class SimpPushMessage implements java.io.Serializable {

    @Schema(description = "Id")
    private Long id;

    @Schema(description = "Title")
    private String title;

    @Schema(description = "Message content")
    private String content;

    @Schema(description = "Created at")
    private LocalDateTime createTime;

    @Schema(description = "Status: 0 = failed, 1 = succeeded ")
    private Byte status;

}
