package dev.pearch001.devopsgpt.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VectorStoreIngestor {

    private static final Logger logger = LoggerFactory.getLogger(VectorStoreIngestor.class);

    private final VectorStore vectorStore;

    @Value("classpath:/documents/*.md")
    private Resource[] documentResources;

    public VectorStoreIngestor(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * This method is executed after the bean has been initialized.
     * It loads the documents, converts them to vectors, and stores them in ChromaDB.
     */
    @PostConstruct
    public void ingestDocuments() throws IOException {
        logger.info("Starting document ingestion process...");

        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true)
                .withIncludeCodeBlock(false)
                .withIncludeBlockquote(true)
                .withAdditionalMetadata("source", "devops-doc")
                .withAdditionalMetadata("category", "infrastructure")
                .build();


        TokenTextSplitter splitter = new TokenTextSplitter(
                1000, // defaultChunkSize: balances size with searchability
                300,  // minChunkSizeChars: shorter because code blocks are often short but meaningful
                10,   // minChunkLengthToEmbed: avoid embedding trivial lines (like empty configs or `# comments`)
                5000, // maxNumChunks: conservative limit to avoid memory overload
                true  // keepSeparator: true to preserve formatting (YAML, bash, etc.)
        );


        for (Resource resource : documentResources) {
            MarkdownDocumentReader documentReader = new MarkdownDocumentReader(resource, config);
            List<Document> documents = documentReader.get();
            List<Document> splitDocs = splitter.apply(documents);

            logger.info("Ingesting document: {} with {} parts", resource.getFilename(), splitDocs.size());

            vectorStore.add(splitDocs);
        }

        logger.info("Document ingestion complete.");
    }
}