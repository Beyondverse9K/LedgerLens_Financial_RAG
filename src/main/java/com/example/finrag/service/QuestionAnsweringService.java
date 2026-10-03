package com.example.finrag.service;

import com.example.finrag.config.RagProperties;
import com.example.finrag.exception.AnswerGenerationException;
import com.example.finrag.exception.InvalidRequestException;
import com.example.finrag.model.MetadataKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionAnsweringService {

    private static final Logger log = LoggerFactory.getLogger(QuestionAnsweringService.class);
    private static final int MAX_QUESTION_LENGTH = 500;
    static final String NO_CONTEXT_MESSAGE =
            "I could not find anything relevant in the uploaded document. "
                    + "I can only answer questions about the terms, conditions, rules and guidelines it contains.";

    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private final RagProperties properties;
    private final Resource userPromptTemplate;

    public QuestionAnsweringService(VectorStore vectorStore,
                                    ChatClient chatClient,
                                    RagProperties properties,
                                    @Value("classpath:prompts/user-prompt.st") Resource userPromptTemplate) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClient;
        this.properties = properties;
        this.userPromptTemplate = userPromptTemplate;
    }

    public String answer(String documentId, String question) {
        validate(documentId, question);

        List<Document> matches = retrieve(documentId, question);
        if (matches.isEmpty()) {
            log.info("No relevant chunks for document {}; skipping the model call", documentId);
            return NO_CONTEXT_MESSAGE;
        }

        String context = matches.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));
        return generate(context, question);
    }

    private List<Document> retrieve(String documentId, String question) {
        Filter.Expression onlyThisDocument =
                new FilterExpressionBuilder().eq(MetadataKeys.DOCUMENT_ID, documentId).build();

        SearchRequest request = SearchRequest.builder()
                .query(question)
                .topK(properties.topK())
                .similarityThreshold(properties.similarityThreshold())
                .filterExpression(onlyThisDocument)
                .build();
        try {
            List<Document> results = vectorStore.similaritySearch(request);
            log.info("DEBUG: Found {} chunks from Pinecone", results.size());
            return results;
        } catch (RuntimeException ex) {
            throw new AnswerGenerationException("Could not search the document. Please try again.", ex);
        }
    }

    private String generate(String context, String question) {
        try {
            String answer = chatClient.prompt()
                    .user(u -> u.text(userPromptTemplate)
                            .param("context", context)
                            .param("question", question))
                    .call()
                    .content();
            if (!StringUtils.hasText(answer)) {
                throw new AnswerGenerationException("The AI service returned an empty answer.", null);
            }
            return answer;
        } catch (AnswerGenerationException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new AnswerGenerationException("The AI service could not answer right now. Please try again.", ex);
        }
    }

    private void validate(String documentId, String question) {
        if (!StringUtils.hasText(documentId)) {
            throw new InvalidRequestException("Please upload a document first.");
        }
        if (!StringUtils.hasText(question)) {
            throw new InvalidRequestException("Please type a question.");
        }
        if (question.length() > MAX_QUESTION_LENGTH) {
            throw new InvalidRequestException("Question is too long (max " + MAX_QUESTION_LENGTH + " characters).");
        }
    }
}