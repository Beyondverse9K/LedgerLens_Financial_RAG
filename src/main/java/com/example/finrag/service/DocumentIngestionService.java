package com.example.finrag.service;

import com.example.finrag.exception.DocumentProcessingException;
import com.example.finrag.exception.InvalidRequestException;
import com.example.finrag.model.DocumentType;
import com.example.finrag.model.IngestionResult;
import com.example.finrag.model.MetadataKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
public class DocumentIngestionService {

    private static final Logger log = LoggerFactory.getLogger(DocumentIngestionService.class);
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "docx", "doc", "txt");

    private final VectorStore vectorStore;
    private final TokenTextSplitter splitter;

    public DocumentIngestionService(VectorStore vectorStore, TokenTextSplitter splitter) {
        this.vectorStore = vectorStore;
        this.splitter = splitter;
    }

    public IngestionResult ingest(MultipartFile file, DocumentType docType) {
        validate(file);

        String documentId = UUID.randomUUID().toString();
        String fileName = Objects.requireNonNullElse(file.getOriginalFilename(), "unknown");

        try {
            List<Document> pages = new TikaDocumentReader(file.getResource()).get();
            pages.forEach(page -> {
                page.getMetadata().put(MetadataKeys.DOCUMENT_ID, documentId);
                page.getMetadata().put(MetadataKeys.FILE_NAME, fileName);
                page.getMetadata().put(MetadataKeys.DOC_TYPE, docType.name());
            });

            List<Document> chunks = splitter.apply(pages); // chunk metadata is copied from the source
            if (chunks.isEmpty()) {
                throw new DocumentProcessingException(
                        "No readable text was found in the document. Scanned PDFs are not supported.");
            }

            vectorStore.add(chunks); // embeds with Gemini and upserts to Pinecone
            log.info("Indexed {} chunks for document {} ({})", chunks.size(), documentId, fileName);
            return new IngestionResult(documentId, fileName, docType, chunks.size());

        } catch (DocumentProcessingException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DocumentProcessingException("Could not process the document. Please try another file.", ex);
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidRequestException("Please choose a file to upload.");
        }
        String name = Objects.requireNonNullElse(file.getOriginalFilename(), "");
        String extension = name.contains(".")
                ? name.substring(name.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT)
                : "";
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new InvalidRequestException("Unsupported file type. Please upload a PDF, DOCX or TXT file.");
        }
    }
}