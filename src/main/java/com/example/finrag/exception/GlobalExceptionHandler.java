package com.example.finrag.exception;

import com.example.finrag.model.DocumentType;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidRequestException.class)
    public String handleInvalidRequest(InvalidRequestException ex, HttpServletRequest request, Model model) {
        log.warn("Invalid request: {}", ex.getMessage());
        return errorView(ex.getMessage(), model, request);
    }

    @ExceptionHandler({DocumentProcessingException.class, AnswerGenerationException.class})
    public String handleProcessingFailure(FinancialRagException ex, HttpServletRequest request, Model model) {
        log.error("Processing failed", ex);
        return errorView(ex.getMessage(), model, request);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleFileTooLarge(Model model) {
        return errorView("The file is too large. Maximum allowed size is 10 MB.", model, null);
    }

    @ExceptionHandler(Exception.class)
    public String handleUnexpected(Exception ex, HttpServletRequest request, Model model) {
        log.error("Unexpected error", ex);
        return errorView("Something went wrong. Please try again.", model, request);
    }

    //Re-renders the page with the error, keeping the active document so the user need not re-upload.
    private String errorView(String message, Model model, HttpServletRequest request) {
        model.addAttribute("error", message);
        model.addAttribute("docTypes", DocumentType.values());
        if (request != null) {
            model.addAttribute("documentId", request.getParameter("documentId"));
            model.addAttribute("fileName", request.getParameter("fileName"));
            model.addAttribute("question", request.getParameter("question"));
        }
        return "index";
    }
}