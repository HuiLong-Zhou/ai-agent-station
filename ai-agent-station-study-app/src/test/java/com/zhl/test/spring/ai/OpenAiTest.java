package com.zhl.test.spring.ai;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.content.Media;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * @author zhl
 * @since 2026-03-11 9:46
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class OpenAiTest {

    @Value("classpath:data/file.txt")
    private Resource fileTextResource;

    @Value("classpath:data/article-prompt-words.txt")
    private Resource articlePromptWordsResource;

    @Value("classpath:data/dog.png")
    private Resource dogPngResource;

    @Autowired
    private OpenAiChatModel openAiChatModel;

    @Autowired
    private PgVectorStore pgVectorStore;

    private final TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();

    /**
     * 非流式调用
     */
    @Test
    public void test_call(){
        ChatResponse chatResponse = openAiChatModel.call(new Prompt(
            "1+1",
            OpenAiChatOptions.builder()
                .model("gpt-4o")
                .build()
        ));

        log.info("test_call 结果：{}", JSON.toJSONString(chatResponse));
    }

    @Test
    public void test_call_image(){

        UserMessage userMessage = UserMessage.builder()
            .text("请描述这张图片的主要内容，并说明图中物品的可能用途。")
            .media(Media.builder()
                .data(dogPngResource)
                .mimeType(MimeType.valueOf(MimeTypeUtils.IMAGE_PNG_VALUE))
                .build())
            .build();

        ChatResponse chatResponse = openAiChatModel.call(new Prompt(
            userMessage,
            OpenAiChatOptions.builder()
                .model("gpt-4o")
                .build()
        ));

        log.info("test_call_text 结果：{}", JSON.toJSONString(chatResponse));
    }


    @Test
    public void test_stream() throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(1);

        Flux<ChatResponse> stream = openAiChatModel.stream(new Prompt(
            "1+1",
            OpenAiChatOptions.builder()
                .model("gpt-4o")
                .build()
        ));

        stream.subscribe(
            chatResponse ->{
                AssistantMessage output = chatResponse.getResult().getOutput();
                log.info("test_stream 测试结果：{}", JSON.toJSONString(output));
            },
            Throwable::printStackTrace,
            ()->{
                countDownLatch.countDown();
                log.info("测试结果(stream): done!");
            }
        );

        countDownLatch.await();

    }


    @Test
    public void testUpload(){

        TikaDocumentReader documentReader = new TikaDocumentReader(fileTextResource);
//        TikaDocumentReader documentReader = new TikaDocumentReader(articlePromptWordsResource);
        List<Document> documents = documentReader.get();
        List<Document> documentSplitterList = tokenTextSplitter.apply(documents);

//        System.out.println(fileTextResource.contentLength());
        documentSplitterList.forEach(doc -> doc.getMetadata().put("knowledge", "知识库名称-v4"));
        documents.forEach(doc -> doc.getMetadata().put("knowledge", "知识库名称-v4"));
//        documentSplitterList.forEach(doc -> doc.getMetadata().put("knowledge", "article-prompt-words"));
//        documents.forEach(doc -> doc.getMetadata().put("knowledge", "article-prompt-words"));

        System.out.println(documentSplitterList.size());
        pgVectorStore.accept(documentSplitterList);

        log.info("上传成功");

    }
    
    @Test
    public void testChat(){

        String message = "王大瓜今年几岁";

        String SYSTEM_PROMPT = """
                Use the information from the DOCUMENTS section to provide accurate answers but act as if you knew this information innately.
                If unsure, simply state that you don't know.
                Another thing you need to note is that your reply must be in Chinese!
                DOCUMENTS:
                    {documents}
                """;

        SearchRequest request = SearchRequest.builder()
            .query(message)
            .topK(5)
            .filterExpression("knowledge == 'file-知识库名称-v4'")
            .build();

        List<Document> documents = pgVectorStore.similaritySearch(request);

        String documentsCollectors = null == documents ? "" : documents.stream().map(Document::getText).collect(Collectors.joining());

        Message ragMessage = new SystemPromptTemplate(SYSTEM_PROMPT).createMessage(Map.of("documents", documentsCollectors));

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(new UserMessage(message));
        messages.add(ragMessage);

        ChatResponse chatResponse = openAiChatModel.call(new Prompt(
            messages,
            OpenAiChatOptions.builder()
                .model("gpt-4o")
                .build()));

        log.info("测试结果:{}", JSON.toJSONString(chatResponse));


    }




}
