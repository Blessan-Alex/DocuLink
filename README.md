# 📚 DocuLink

> **Multilingual RAG Application for Corporate Knowledge Management**

[![GitHub](https://img.shields.io/badge/GitHub-Repository-blue?logo=github)](https://github.com/Blessan-Alex/DocuLink)
[![Tech Stack](https://img.shields.io/badge/Tech-Stack-green)](./README.md#-tech-stack)
[![License](https://img.shields.io/badge/License-MIT-yellow)](./README.md)

---

## 🎯 Overview

DocuLink is an intelligent, multilingual knowledge management system that transforms how enterprises access, search, and interact with corporate documents. Built with **Retrieval-Augmented Generation (RAG)**, it provides conversational access to documents in **English and Malayalam** through a mobile-first Android application.

### The Problem

Large enterprises struggle with:
- 📁 **Siloed documents** scattered across email, SharePoint, Maximo, network drives
- 🔍 **Poor search** that fails on PDFs, images, and bilingual content
- ⏱️ **Time waste** manually searching and forwarding documents
- 🌐 **Language barriers** between English and local languages (e.g., Malayalam)

### The Solution

DocuLink delivers:
- 🧠 **Intelligent RAG** that understands context, not just keywords
- 🗣️ **Conversational interface** via Android app (text + voice)
- 🌍 **Multilingual support** for English and Malayalam
- 📱 **Mobile-first** access for field staff and on-ground operations
- 🔐 **Role-based access** with department-specific scoping
- 📄 **Source citations** for trustworthy, traceable answers

---

## ✨ Key Features

| Feature | Description |
|---------|-------------|
| 🔄 **Multilingual RAG** | Powered by Vyākhyarth embeddings + Gemini 2.0 Flash for English/Malayalam |
| 📱 **Android App** | Native Kotlin app with chat interface, voice input, and document management |
| 🔍 **Hybrid Search** | Vector similarity + BM25 keyword search in OpenSearch |
| 📤 **Multi-Source Ingestion** | Supports uploads from Android, email, SharePoint, Maximo |
| 🎤 **Voice Input** | Speech-to-text with support for `en-IN` and `ml-IN` locales |
| 🔒 **Secure Access** | Firebase Authentication with role-based permissions |
| 📊 **Document Dashboard** | Track embedding status and manage embedded documents |
| ⚡ **Real-time Updates** | Firestore listeners for live document status updates |

---

## 🏗️ Architecture

```
┌─────────────────┐
│  Android App    │
│  (Kotlin)       │
│  • Chat UI      │
│  • Voice Input  │
│  • Firebase     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  FastAPI        │
│  Backend        │
│  • /api/query   │
│  • /api/stats   │
└────────┬────────┘
         │
    ┌────┴────┐
    │         │
    ▼         ▼
┌────────┐ ┌──────────┐
│OpenSearch│ │ Gemini   │
│Vector DB │ │ 2.0 Flash│
└────────┘ └──────────┘
```

### Components

1. **Android Client (Kotlin)**
   - Firebase Auth for secure login
   - ChatActivity with drawer navigation
   - Voice input via RecognizerIntent
   - Firebase Storage + Firestore for document management

2. **Python FastAPI Backend**
   - RAG pipeline with Vyākhyarth embeddings
   - Hybrid search in OpenSearch (vector + BM25)
   - Gemini 2.0 Flash for answer generation
   - Redis queues for async document processing

3. **Document Processing Pipeline**
   - OCR for scanned PDFs/images
   - Language detection (en/ml)
   - Text chunking and embedding generation
   - Automated indexing into OpenSearch

---

## 🛠️ Tech Stack

### Frontend
- **Language**: Kotlin
- **Framework**: Native Android (View-based UI)
- **Services**:
  - Firebase Authentication
  - Firebase Firestore
  - Firebase Storage

### Backend
- **API**: Python FastAPI
- **LLM**: Google Gemini 2.0 Flash
- **Embeddings**: Vyākhyarth (multilingual English/Malayalam)
- **Search Engine**: OpenSearch (vector + BM25 hybrid)
- **Queue**: Redis + background workers
- **Storage**: OpenSearch indices, Firestore

### Infrastructure
- OpenSearch for vector search
- Redis for job queues
- Firebase for authentication and storage
- Docker for containerization

---

## 🚀 System Workflows

### Document Ingestion Flow
```
1. User uploads file from Android
   ↓
2. File stored in Firebase Storage
   ↓
3. Metadata written to Firestore (status: "embedding_in_progress")
   ↓
4. Document pushed to Redis queue
   ↓
5. Worker processes:
   • OCR/Text extraction
   • Language detection
   • Chunking + Embedding generation
   • Indexing into OpenSearch
   ↓
6. Firestore status updated to "ready"
   ↓
7. Android app shows "Embedded" status
```

### Query → Answer Flow
```
1. User enters query (text/voice)
   ↓
2. App sends QueryRequest to /api/query
   ↓
3. Backend:
   • Generates multilingual embedding
   • Runs hybrid search in OpenSearch
   • Retrieves top-k context chunks
   • Calls Gemini with context prompt
   ↓
4. Returns QueryResponse with:
   • Answer (in query language)
   • Source citations
   ↓
5. App displays chat bubble with sources
```

---

## 📊 Impact & Benefits

- ✅ **Single Source of Truth** for all corporate documents
- ⚡ **Reduced Time-to-Answer** for policy, SOP, and compliance questions
- 🛡️ **Improved Safety & Compliance** via fast retrieval of latest rules
- 👥 **Empowered Frontline Staff** with mobile & voice-accessible knowledge
- 📋 **Better Governance** with role-based access and document traceability
- 📈 **Scalable Foundation** for analytics, risk scoring, and proactive alerts

---

## 🔗 Resources

### 📚 Documentation
- **[Project Help Document](./help.txt)** - Comprehensive project documentation

### 📊 Presentations & Reports
- **[Presentation Slides](https://docs.google.com/presentation/)** - Project presentation
- **[Project Report](#)** - Detailed project report _(Link to be added)_

### 💻 Repository
- **[GitHub Repository](https://github.com/Blessan-Alex/DocuLink)** - Source code and contributions

---

## 🎯 Use Cases

- **Safety & Compliance**: Quick access to latest safety policies and incident guidelines
- **Operations**: Real-time SOP retrieval for signal failures, maintenance procedures
- **Knowledge Management**: Centralized repository for policies, contracts, technical documents
- **Field Operations**: Mobile-accessible knowledge for on-ground staff
- **Multilingual Support**: Seamless switching between English and Malayalam queries

---

## 🔐 Security & Privacy

- ✅ Secure storage and network (HTTPS, access control)
- ✅ Role-based metadata and filtered retrieval
- ✅ Firebase Authentication for user management
- ✅ On-prem/VPC deployment options for regulated sectors

---

## 🚧 Challenges & Mitigation

| Challenge | Mitigation |
|-----------|-----------|
| **LLM API Costs** | Aggressive retrieval, result caching, graceful fallbacks |
| **Data Privacy** | Secure storage, role-based access, on-prem options |
| **Multilingual Accuracy** | Vyākhyarth embeddings, prompt engineering, continuous evaluation |
| **Document Quality** | Image preprocessing, DPI checks, low-confidence flagging |
| **User Adoption** | Simple UX, voice input, high-value use case demonstrations |

---

## 📸 Screenshots

- **Login Screen** - Firebase Authentication
- **Metro Dashboard** - Corporate dashboard view
- **Chat Interface** - Bilingual query and answer display
- **Embedded Documents** - Document status tracking
- **Sidebar Navigation** - Quick access to features

---

## 📖 Research & References

1. OpenSearch Documentation – Vector search and hybrid search
2. Redis Documentation – Background job processing
3. Google Gemini API Docs – Generative model usage
4. Vyākhyarth Embedding Model – Multilingual sentence embeddings
5. Retrieval-Augmented Generation Survey – RAG design patterns
6. Enterprise Document Management Case Studies

---

## 📄 License

[License to be specified]

---

## 👥 Contributors

- **Blessan Alex** - [GitHub](https://github.com/Blessan-Alex)

---

<div align="center">

**Built with ❤️ for intelligent corporate knowledge management**

[⭐ Star on GitHub](https://github.com/Blessan-Alex/DocuLink) • [📖 Documentation](./help.txt) • [🎤 Presentation](https://docs.google.com/presentation/)

</div>

