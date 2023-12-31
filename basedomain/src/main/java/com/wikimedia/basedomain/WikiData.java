package com.wikimedia.basedomain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WikiData {
    private Long id;
    private String type;
    private String title;
    private String title_url;
    private String comment;
    private String user;
    private String wiki;
}
