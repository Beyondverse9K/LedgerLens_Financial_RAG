package com.example.finrag.controller;

import com.example.finrag.model.DocumentType;
import com.example.finrag.model.IngestionResult;
import com.example.finrag.service.DocumentIngestionService;
import com.example.finrag.service.QuestionAnsweringService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class DocumentController {

    private final DocumentIngestionService ingestionService;
    private final QuestionAnsweringService questionAnsweringService;

    public DocumentController(DocumentIngestionService ingestionService,
                              QuestionAnsweringService questionAnsweringService) {
        this.ingestionService = ingestionService;
        this.questionAnsweringService = questionAnsweringService;
    }

    @ModelAttribute("docTypes")
    public DocumentType[] docTypes() {
        return DocumentType.values();
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         @RequestParam("docType") DocumentType docType,
                         Model model) {
        IngestionResult result = ingestionService.ingest(file, docType);

        model.addAttribute("documentId", result.documentId());
        model.addAttribute("fileName", result.fileName());
        model.addAttribute("message",
                "Indexed %d sections from %s. You can now ask questions.".formatted(result.chunkCount(), result.fileName()));
        return "index";
    }

    @PostMapping("/ask")
    public String ask(@RequestParam(required = false) String documentId,
                      @RequestParam(required = false) String fileName,
                      @RequestParam(required = false) String question,
                      Model model) {
        String answer = questionAnsweringService.answer(documentId, question);

        model.addAttribute("documentId", documentId);
        model.addAttribute("fileName", fileName);
        model.addAttribute("question", question);
        model.addAttribute("answer", answer);
        return "index";
    }
}