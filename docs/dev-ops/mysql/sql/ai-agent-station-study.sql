CREATE database if NOT EXISTS `ai-agent-station-study` default character set utf8mb4 collate utf8mb4_0900_ai_ci;
use `ai-agent-station-study`;

--智能体
CREATE TABLE `ai_agent` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                            `agent_id` varchar(64) NOT NULL COMMENT '智能体ID',
                            `agent_name` varchar(50) NOT NULL COMMENT '智能体名称',
                            `description` varchar(255) DEFAULT NULL COMMENT '描述',
                            `channel` varchar(32) DEFAULT NULL COMMENT '渠道类型(agent，chat_stream)',
                            `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_agent_id` (`agent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI智能体配置表';

CREATE TABLE `ai_agent_task_schedule` (
                                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                          `agent_id` bigint NOT NULL COMMENT '智能体ID',
                                          `task_name` varchar(64) DEFAULT NULL COMMENT '任务名称',
                                          `description` varchar(255) DEFAULT NULL COMMENT '任务描述',
                                          `cron_expression` varchar(50) NOT NULL COMMENT '时间表达式(如: 0/3 * * * * *)',
                                          `task_param` text COMMENT '任务入参配置(JSON格式)',
                                          `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:无效,1:有效)',
                                          `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          PRIMARY KEY (`id`),
                                          KEY `idx_agent_id` (`agent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='智能体任务调度配置表';

CREATE TABLE `ai_agent_flow_config` (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                        `agent_id` bigint NOT NULL COMMENT '智能体ID',
                                        `client_id` bigint NOT NULL COMMENT '客户端ID',
                                        `sequence` int NOT NULL COMMENT '序列号(执行顺序)',
                                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        PRIMARY KEY (`id`),
                                        UNIQUE KEY `uk_agent_client_seq` (`agent_id`,`client_id`,`sequence`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='智能体-客户端关联表';

-- 功能表
CREATE TABLE `ai_client_api` (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
                                 `api_id` varchar(64) NOT NULL COMMENT '全局唯一配置ID',
                                 `base_url` varchar(255) NOT NULL COMMENT 'API基础URL',
                                 `api_key` varchar(255) NOT NULL COMMENT 'API密钥',
                                 `completions_path` varchar(255) NOT NULL COMMENT '补全API路径',
                                 `embeddings_path` varchar(255) NOT NULL COMMENT '嵌入API路径',
                                 `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_api_id` (`api_id`),
                                 KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='OpenAI API配置表';


CREATE TABLE `ai_client_model` (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
                                   `model_id` varchar(64) NOT NULL COMMENT '全局唯一模型ID',
                                   `api_id` varchar(64) NOT NULL COMMENT '关联的API配置ID',
                                   `model_name` varchar(64) NOT NULL COMMENT '模型名称',
                                   `model_type` varchar(32) NOT NULL COMMENT '模型类型：openai、deepseek、claude',
                                   `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
                                   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `uk_model_id` (`model_id`),
                                   KEY `idx_api_config_id` (`api_id`),
                                   KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='聊天模型配置表';


CREATE TABLE `ai_client` (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                             `client_id` varchar(64) NOT NULL COMMENT '客户端ID',
                             `client_name` varchar(50) NOT NULL COMMENT '客户端名称',
                             `description` varchar(1024) DEFAULT NULL COMMENT '描述',
                             `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
                             `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `client_id` (`client_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI客户端配置表';

-- 工具表

CREATE TABLE `ai_client_advisor` (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                     `advisor_id` varchar(64) NOT NULL COMMENT '顾问ID',
                                     `advisor_name` varchar(50) NOT NULL COMMENT '顾问名称',
                                     `advisor_type` varchar(50) NOT NULL COMMENT '顾问类型(PromptChatMemory/RagAnswer/SimpleLoggerAdvisor等)',
                                     `order_num` int DEFAULT '0' COMMENT '顺序号',
                                     `ext_param` varchar(2048) DEFAULT NULL COMMENT '扩展参数配置，json 记录',
                                     `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `uk_advisor_id` (`advisor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='顾问配置表';

CREATE TABLE `ai_client_system_prompt` (
                                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                           `prompt_id` varchar(64) NOT NULL COMMENT '提示词ID',
                                           `prompt_name` varchar(50) NOT NULL COMMENT '提示词名称',
                                           `prompt_content` text NOT NULL COMMENT '提示词内容',
                                           `description` varchar(1024) DEFAULT NULL COMMENT '描述',
                                           `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
                                           `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                           PRIMARY KEY (`id`),
                                           UNIQUE KEY `uk_prompt_id` (`prompt_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统提示词配置表';

CREATE TABLE `ai_client_tool_mcp` (
                                      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                      `mcp_id` varchar(64) NOT NULL COMMENT 'MCP名称',
                                      `mcp_name` varchar(50) NOT NULL COMMENT 'MCP名称',
                                      `transport_type` varchar(20) NOT NULL COMMENT '传输类型(sse/stdio)',
                                      `transport_config` varchar(1024) DEFAULT NULL COMMENT '传输配置(sse/stdio)',
                                      `request_timeout` int DEFAULT '180' COMMENT '请求超时时间(分钟)',
                                      `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
                                      `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `uk_mcp_id` (`mcp_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='MCP客户端配置表';

-- 链接表
CREATE TABLE `ai_client_config` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                    `source_type` varchar(32) NOT NULL COMMENT '源类型（model、client）',
                                    `source_id` varchar(64) NOT NULL COMMENT '源ID（如 chatModelId、chatClientId 等）',
                                    `target_type` varchar(32) NOT NULL COMMENT '目标类型（model、client）',
                                    `target_id` varchar(64) NOT NULL COMMENT '目标ID（如 openAiApiId、chatModelId、systemPromptId、advisorId 等）',
                                    `ext_param` varchar(1024) DEFAULT NULL COMMENT '扩展参数（JSON格式）',
                                    `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    PRIMARY KEY (`id`),
                                    KEY `idx_source_id` (`source_id`),
                                    KEY `idx_target_id` (`target_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI客户端统一关联配置表';


CREATE TABLE `ai_client_rag_order` (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                       `rag_id` varchar(50) NOT NULL COMMENT '知识库ID',
                                       `rag_name` varchar(50) NOT NULL COMMENT '知识库名称',
                                       `knowledge_tag` varchar(50) NOT NULL COMMENT '知识标签',
                                       `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
                                       `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `uk_rag_id` (`rag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='知识库配置表';

