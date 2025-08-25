뉴스봇 컨셉의 간단한 RAG(Retrieval-Augmented Generation) 데모 서비스 실습 프로젝트

RSS 피드를 수집하고 Qdrant(Vector DB)에 색인한 뒤, OpenAI Embedding + Chat 모델을 이용해 최신 뉴스를 의미 기반으로 요약/응답합니다.

- RSS Feed Ingestion  
  - 지정된 RSS 피드에서 뉴스 데이터 수집  
  - 본문 chunking → OpenAI Embedding 생성 → Qdrant 저장
- Semantic Search + RAG
  - 사용자의 질의 임베딩 → Qdrant 유사도 검색  
  - 검색된 컨텍스트 기반으로 LLM이 요약/답변 생성
- REST API
  - `/ingest/pull` : RSS 피드 색인  
  - `/rag/query` : 질의 → 요약 응답 반환
