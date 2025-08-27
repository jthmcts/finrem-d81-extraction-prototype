package uk.gov.hmcts.finrem.d81;

import com.azure.ai.documentintelligence.DocumentIntelligenceClient;
import com.azure.ai.documentintelligence.DocumentIntelligenceClientBuilder;
import com.azure.ai.documentintelligence.models.*;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.polling.SyncPoller;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class TestModel {

    private static final String ENDPOINT = "YOUR_ENDPOINT";
    private static final String KEY = "YOUR_KEY";
    private static final String MODEL_ID = "YOUR_MODEL_ID";

    public static void main(String[] args) {

        // Create DocumentIntelligence client with key credential
        DocumentIntelligenceClient client = new DocumentIntelligenceClientBuilder()
            .endpoint(ENDPOINT)
            .credential(new AzureKeyCredential(KEY))
            .buildClient();

        String documentUrl = "https://github.com/jthmcts/finrem-d81-extraction-prototype/blob/main/D81_0425_save_1-5-validation.pdf?raw=true";

        AnalyzeDocumentOptions options = new AnalyzeDocumentOptions(documentUrl)
                .setDocumentAnalysisFeatures(DocumentAnalysisFeature.LANGUAGES)
                .setLocale("en-US")
                .setStringIndexType(StringIndexType.TEXT_ELEMENTS)
                .setPages(List.of("1", "2"));

        SyncPoller<AnalyzeOperationDetails, AnalyzeResult> analyzeDocumentPoller = client.beginAnalyzeDocument(MODEL_ID, options);
        AnalyzeResult analyzeResult = analyzeDocumentPoller.getFinalResult();

        analyzeResult.getDocuments().forEach(
            analyzedDocument -> analyzedDocument.getFields().forEach(TestModel::logDocumentField)
        );
    }

    private static void logDocumentField(String fieldName, DocumentField documentField) {
        switch (documentField.getType().getValue()) {
            case "string":
                logString(fieldName, documentField);
                break;
            case "selectionMark":
                logSelectionMark(fieldName, documentField);
                break;
            case "object":
                logObject(fieldName, documentField);
                break;
            default: log.warn("Field '{}' has unsupported type {}", fieldName, documentField.getType());
        }
    }

    private static void logString(String fieldName, DocumentField documentField) {
        log.info("Field '{}' has value '{}' with a confidence score of {}",
                fieldName, documentField.getContent(), documentField.getConfidence());
    }

    private static void logSelectionMark(String fieldName, DocumentField documentField) {
        String content = documentField.getContent();
        if (":selected".equals(content)) {
            log.info("Field '{}' is selected with a confidence score of {}", fieldName, documentField.getConfidence());
        } else if (":unselected".equals(content)) {
            log.info("Field '{}' is unselected with a confidence score of {}", fieldName, documentField.getConfidence());
        }
    }

    private static void logObject(String fieldName, DocumentField documentField) {
        log.info(fieldName);
        documentField.getValueMap().forEach(TestModel::logDocumentField);
    }
}
