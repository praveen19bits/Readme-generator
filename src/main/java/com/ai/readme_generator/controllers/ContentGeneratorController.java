package com.ai.readme_generator.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ai.readme_generator.services.ContentGeneratorService;
import com.ai.readme_generator.services.LocalRepoService;


@RestController
public class ContentGeneratorController{
    private static final Logger log = LoggerFactory.getLogger(ContentGeneratorController.class);

    private final LocalRepoService localFileService;
    //private final TemplateEngine templateEngine;
    private final ContentGeneratorService contentGeneratorService;

    private final GroqOpenAIController groqOpenAIController;

    private final AWSBedRockController awsBedRockController;

    public ContentGeneratorController(LocalRepoService localFileService,
                                            ContentGeneratorService contentGeneratorService,
                                            GroqOpenAIController groqOpenAIController,
                                            AWSBedRockController awsBedRockController) {
        this.localFileService = localFileService;
        this.contentGeneratorService = contentGeneratorService;
        this.groqOpenAIController = groqOpenAIController;
        this.awsBedRockController = awsBedRockController;
    }

    @PostMapping("/generate")
    @ResponseBody
    public ResponseEntity<String> generate(@RequestParam(required = false) String localPath) {

        try {
            String content = contentGeneratorService.generateContent("C:\\dev-work\\GenAI\\spring-boot\\repo-content-generator");
            //String generarteReadMeStr = groqOpenAIController.generarteReadMeStr(content);
            String generarteReadMeStr = awsBedRockController.callAwsBedRock(content);
            System.out.println("##############\n\n\n\n\n");
            System.out.println("generarteReadMeStr: " + generarteReadMeStr);
            // StringOutput output = new StringOutput();
            // templateEngine.render("result.jte", Map.of("content", content), output);
            return ResponseEntity.ok(generarteReadMeStr.toString());
        } catch (Exception e) {
            log.error("Error generating content", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating content: " + e.getMessage());
        }
    }
}