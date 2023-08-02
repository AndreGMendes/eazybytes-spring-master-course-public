package com.eazybytes.eazyschool.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data // Generate all @Getter and @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class Response {

    private String statusCode;
    private String statusMsg;

}
