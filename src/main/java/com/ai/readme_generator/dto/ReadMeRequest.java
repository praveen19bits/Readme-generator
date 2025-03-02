package com.ai.readme_generator.dto;

import com.ai.readme_generator.config.FilePatternsConfig;

public record ReadMeRequest(String pathOrRepoUrl, FilePatternsConfig  readConfig) {
   
}

