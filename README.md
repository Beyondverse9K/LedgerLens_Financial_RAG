# LedgerLens

LedgerLens is a document-grounded financial question-answering application. Upload a loan agreement, insurance policy, bond document, or mutual fund document, then ask questions about its terms, conditions, rules, and guidelines in plain language.

The application uses retrieval-augmented generation (RAG): it extracts text from the uploaded document, splits it into searchable chunks, stores vector embeddings in Pinecone, retrieves the most relevant passages for each question, and asks Google Gemini to answer using only those passages. This keeps answers focused on the uploaded document rather than unsupported general knowledge.

> **Important:** LedgerLens explains uploaded documents. It does not provide personalized investment, tax, legal, or financial advice. Consult a qualified professional before making decisions.

## Features

- Upload PDF, DOC, DOCX, and TXT documents.
- Classify documents as loans, insurance, bonds, or mutual funds.
- Extract text with Apache Tika.
- Split documents into token-based chunks for retrieval.
- Generate embeddings with Google Gemini.
- Store and search embeddings with Pinecone.
- Answer questions with Google Gemini using retrieved document context.
- Reject questions unrelated to the uploaded financial document.
- Avoid answering when relevant context cannot be found.
- Enforce upload limits and request validation.

## How it works

```text
Upload document
      |
      v
Apache Tika extracts text
      |
      v
TokenTextSplitter creates chunks
      |
      v
Gemini embeddings -> Pinecone
      |
      v
Question + document ID
      |
      v
Pinecone similarity search
      |
      v
Retrieved passages + question -> Gemini
      |
      v
Document-grounded answer
```

Each uploaded document receives a unique ID. That ID is stored as metadata with its chunks, so similarity search is restricted to the active document.

## Technology stack

| Area | Technology |
| --- | --- |
| Language | Java 17 |
| Application framework | Spring Boot 3.5 |
| AI integration | Spring AI 1.1 |
| Chat model | Google Gemini 2.5 Flash |
| Embedding model | Google Gemini Embedding |
| Vector database | Pinecone |
| Document extraction | Apache Tika through Spring AI |
| Web interface | Spring MVC and Thymeleaf |
| Build tool | Maven with Maven Wrapper |
| Deployment | Render |

## Requirements

- Java 17 or newer
- A Google Gemini API key
- A Pinecone API key
- A Pinecone index configured for the embedding model

## Configuration

- Maximum upload size: 10 MB
- Chunk size: 500 tokens
- Number of retrieved chunks: 5
- Similarity threshold: 0.5
- Gemini chat temperature: 0.1


## Link

```Enter Link
```

## Demo 

```Video
```

## Using the application

1. Choose a document category: Loan, Insurance, Bond, or Mutual Fund.
2. Upload a PDF, DOC, DOCX, or TXT file with extractable text.
3. Wait for the document to be indexed.
4. Enter a question about the document.
5. Review the answer returned from the relevant document passages.

Questions are limited to 500 characters. The assistant is intentionally restricted to terms, conditions, rules, and guidelines found in the uploaded document.

## Project structure

```text
src/
├── main/
    ├── java/com/example/finrag/
    │   ├── config/       # RAG settings and Spring AI beans
    │   ├── controller/   # Upload, question, and page endpoints
    │   ├── exception/    # Request and processing error handling
    │   ├── model/        # Document types and ingestion results
    │   └── service/      # Document ingestion and question answering
    └── resources/
        ├── prompts/      # Grounding and safety prompts
        └── templates/    # Thymeleaf user interface
```

## Current limitations

- Scanned or image-only PDFs are not supported because ingestion requires extractable text.
- There is no document deletion or retention workflow yet.
- The current interface is designed for one active document at a time.
- Answers do not yet expose source passages, page references, or clickable citations.
- Authentication, authorization, and per-user document isolation are not implemented.
- Availability and answer quality depend on Gemini and Pinecone.

## Future scope

- OCR support for scanned documents and image-based PDFs.
- Inline citations with page, section, and source-chunk references.
- Multi-document workspaces and financial product comparison.
- User authentication, authorization, and tenant-level data isolation.
- Document deletion, retention policies, and index cleanup.
- Streaming answers and richer conversation history.
- Improved upload validation, malware scanning, and observability.
- Evaluation datasets for retrieval quality, grounding, and refusal behavior.
- Containerized deployment and managed cloud infrastructure.
- Administrative controls for model, chunking, and retrieval tuning.
