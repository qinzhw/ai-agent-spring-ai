-- =============================================
-- 多 Agent 平台数据库初始化脚本（PostgreSQL + pgvector）
-- Database: ai_agent
-- =============================================

CREATE DATABASE ai_agent;

-- 启用 pgvector 扩展
CREATE EXTENSION IF NOT EXISTS vector;

-- ---------------------------------------------
-- 1. 智能体表
-- ---------------------------------------------
CREATE TABLE agent (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name            TEXT NOT NULL,              -- Agent 名称
    description     TEXT,                       -- 描述（用户可见）
    system_prompt   TEXT,                       -- 系统指令
    model           TEXT,                       -- 默认使用的模型
    allowed_tools   JSONB,                      -- 允许使用的工具列表
    allowed_kbs     JSONB,                      -- 允许访问的知识库
    chat_options    JSONB,                      -- 其它配置项（温度、top_p、消息窗口长度）

    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
);

-- ---------------------------------------------
-- 2. 聊天会话表
-- ---------------------------------------------
CREATE TABLE chat_session (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    agent_id        UUID REFERENCES agent(id) ON DELETE SET NULL,   -- 绑定的 Agent

    title           TEXT,                       -- 自动生成的标题
    metadata        JSONB,                      -- 扩展（例如输入语言、设备类型）

    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
);

-- ---------------------------------------------
-- 3. 聊天消息表
-- ---------------------------------------------
CREATE TABLE chat_message (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    session_id      UUID NOT NULL REFERENCES chat_session(id) ON DELETE CASCADE,

    role            TEXT NOT NULL,              -- user / assistant / system / tool
    content         TEXT,                       -- 主体内容
    metadata        JSONB,                      -- 工具调用、RAG 片段、模型参数等

    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
);

-- ---------------------------------------------
-- 4. 工具表
-- ---------------------------------------------
CREATE TABLE tool (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name            TEXT NOT NULL UNIQUE,       -- 工具名称
    description     TEXT,                       -- 工具描述
    type            TEXT NOT NULL DEFAULT 'OPTIONAL',   -- FIXED-系统内置 / OPTIONAL-可选
    enabled         BOOLEAN NOT NULL DEFAULT TRUE,

    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
);

-- ---------------------------------------------
-- 5. 知识库表
-- ---------------------------------------------
CREATE TABLE knowledge_base (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name            TEXT NOT NULL,
    description     TEXT,
    metadata        JSONB,                      -- 业务属性，如行业/标签

    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
);

-- ---------------------------------------------
-- 6. 文档表
-- ---------------------------------------------
CREATE TABLE document (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    kb_id           UUID NOT NULL REFERENCES knowledge_base(id) ON DELETE CASCADE,

    filename        TEXT NOT NULL,
    filetype        TEXT,                       -- pdf / md / txt 等
    size            BIGINT,                     -- 文件大小
    metadata        JSONB,                      -- 页数、上传方式、解析参数等

    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
);

-- ---------------------------------------------
-- 7. 文档分块向量表（bge_m3 模型，1024 维）
-- ---------------------------------------------
CREATE TABLE chunk_bge_m3 (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    kb_id           UUID NOT NULL REFERENCES knowledge_base(id) ON DELETE CASCADE,
    doc_id          UUID NOT NULL REFERENCES document(id) ON DELETE CASCADE,

    content         TEXT NOT NULL,              -- 切片后的文本内容
    metadata        JSONB,                      -- 页码、段落号、chunk index 等

    embedding       VECTOR(1024) NOT NULL,      -- bge_m3 模型是 1024 维的向量

    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
);

-- 给向量加索引（ivfflat，L2 距离）
CREATE INDEX idx_chunk_embedding
ON chunk_bge_m3
USING ivfflat (embedding vector_l2_ops)
WITH (lists = 100);
