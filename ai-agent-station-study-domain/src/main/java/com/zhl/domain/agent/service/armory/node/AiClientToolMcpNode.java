package com.zhl.domain.agent.service.armory.node;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import com.alibaba.fastjson2.JSON;
import com.zhl.domain.agent.adapter.security.IMcpSecurityService;
import com.zhl.domain.agent.model.entity.ArmoryCommandEntity;
import com.zhl.domain.agent.model.valobj.enums.AiAgentEnumVO;
import com.zhl.domain.agent.model.valobj.AiClientToolMcpVO;
import com.zhl.domain.agent.service.armory.node.factory.DefaultArmoryStrategyFactory;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;


/**
 * <p>
 *  MCP客户端配置节点
 * @author zhl
 * @since 2026-05-02 13:35
 */
@Slf4j
@Service
public class AiClientToolMcpNode extends AbstractArmorySupport{


    @Resource
    private AiClientModelNode aiClientModelNode;

    @Resource
    private IMcpSecurityService mcpSecurityService;


    @Override
    protected String doApply(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("Ai Agent 构建，ToolMcp 构建节点 {}", JSON.toJSONString(requestParameter));
        List<AiClientToolMcpVO> aiClientToolMcpVOList = dynamicContext.getValue(dataName());

        if(aiClientToolMcpVOList == null || aiClientToolMcpVOList.isEmpty()) {
            log.warn("没有需要被初始化的 ai client tool mcp");
            return router(requestParameter, dynamicContext);
        }

        for (AiClientToolMcpVO aiClientToolMcpVO : aiClientToolMcpVOList) {

            // 构建 mcp 对象
            McpSyncClient mcpSyncClient = createMcpSyncClient(requestParameter, aiClientToolMcpVO);

            // 注册 mcp bean 对象
            registerBean(beanName(aiClientToolMcpVO.getMcpId()), McpSyncClient.class, mcpSyncClient);

        }

        return router(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<ArmoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext, String> get(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return aiClientModelNode;
    }

    private McpSyncClient createMcpSyncClient(ArmoryCommandEntity requestParameter,
                                              AiClientToolMcpVO aiClientToolMcpVO) {

        String transportType = aiClientToolMcpVO.getTransportType();

        switch (transportType) {
                case "sse" ->{
                    AiClientToolMcpVO.TransportConfigSse transportConfigSse = aiClientToolMcpVO.getTransportConfigSse();
                    String originalBaseUri = transportConfigSse.getBaseUri();
                    String baseUri;
                    String sseEndpoint;

                    /*
                        sse 方式可能是 "baseUri" : "https://mcp.amap.com/sse?key=xxx"
                        或者 "baseUri" : "https://mcp.amap.com" , "sseEndpoint" : "/sse?xxx"
                     */
                    int index = originalBaseUri.indexOf("sse");
                    if(index != -1) {
                        // 说明没有sseEndpoint，需要拆分
                        baseUri = originalBaseUri.substring(0, index - 1);
                        sseEndpoint = originalBaseUri.substring(index - 1);
                    }else {
                        baseUri = originalBaseUri;
                        sseEndpoint = transportConfigSse.getSseEndpoint();
                    }

                    sseEndpoint = StringUtils.isBlank(sseEndpoint) ? "/sse" : sseEndpoint;
                    mcpSecurityService.validateSseEndpoint(aiClientToolMcpVO.getMcpId(), baseUri);
                    String accessToken = mcpSecurityService.issueAccessToken(
                        requestParameter.getAgentId(), aiClientToolMcpVO.getMcpId(), baseUri);

                    HttpClientSseClientTransport.Builder transportBuilder =
                        HttpClientSseClientTransport
                            .builder(baseUri)
                            .sseEndpoint(sseEndpoint);
                    if (accessToken != null && !accessToken.isBlank()) {
                        transportBuilder.customizeRequest(requestBuilder -> requestBuilder
                            .header("Authorization", "Bearer " + accessToken)
                            .header("X-MCP-Id", aiClientToolMcpVO.getMcpId()));
                    }
                    HttpClientSseClientTransport sseClientTransport = transportBuilder.build();

                    McpSyncClient mcpSyncClient = McpClient.sync(sseClientTransport)
                        .requestTimeout(Duration.ofMinutes(aiClientToolMcpVO.getRequestTimeout())).build();

                    McpSchema.InitializeResult init = mcpSyncClient.initialize();
                    log.info("SSE MCP Initialized: {}", init);

                    return mcpSyncClient;
                }
                case "stdio" -> {
                    AiClientToolMcpVO.TransportConfigStdio transportConfigStdio = aiClientToolMcpVO.getTransportConfigStdio();
                    Map<String, AiClientToolMcpVO.TransportConfigStdio.Stdio> stdioMap = transportConfigStdio.getStdio();
                    AiClientToolMcpVO.TransportConfigStdio.Stdio stdio = stdioMap.get(aiClientToolMcpVO.getMcpName());
                    mcpSecurityService.validateStdioCommand(
                        aiClientToolMcpVO.getMcpId(), stdio.getCommand());

                    ServerParameters  stdioParams = ServerParameters.builder(stdio.getCommand())
                        .args(stdio.getArgs())
                        .env(stdio.getEnv()).build();

                    McpSyncClient mcpClient = McpClient.sync(new StdioClientTransport(stdioParams))
                        .requestTimeout(Duration.ofSeconds(aiClientToolMcpVO.getRequestTimeout())).build();

                    McpSchema.InitializeResult init = mcpClient.initialize();

                    log.info("Stdio MCP Initialized: {}", init);
                    return mcpClient;
                }
        }

        throw new RuntimeException("err! transportType " + transportType + " not exist!");
    }


    @Override
    protected String dataName() {
        return AiAgentEnumVO.AI_CLIENT_TOOL_MCP.getDataName();
    }

    @Override
    protected String beanName(String beanId) {
        return AiAgentEnumVO.AI_CLIENT_TOOL_MCP.getBeanName(beanId);
    }
}
