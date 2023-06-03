# AI4J: Supercharge your Java application with the power of AI

Java library for smooth integration with AI tools and services.

## Current capabilities:
- Integration with [OpenAI (ChatGPT)](https://platform.openai.com/docs/introduction) for:
  - [Chats](https://platform.openai.com/docs/guides/chat)
  - [Completions](https://platform.openai.com/docs/guides/completion)
  - [Embeddings](https://platform.openai.com/docs/guides/embeddings)
- [Chat History](https://github.com/ai-for-java/ai4j-examples/blob/master/src/main/java/ChatExamples.java) (convenient short-term memory for conversations with AI)
- [Chat Flow](https://github.com/ai-for-java/ai4j-examples/blob/master/src/main/java/ChatExamples.java) (easy way to chat with AI while keeping history)
- [Document QnA Flow](https://github.com/ai-for-java/ai4j-examples/blob/master/src/main/java/DocumentQnAExamples.java) (easy way to ask AI questions based on specific document/knowledgebase)
- Integration with [Pinecone](https://docs.pinecone.io/docs/overview) vector database (embeddings)
- [Structured outputs](https://github.com/ai-for-java/ai4j-examples/blob/master/src/main/java/StructuredOutputExamples.java) (get responses from LLM with a strict structure)
- [Prompt templates](https://github.com/ai-for-java/ai4j-examples/blob/master/src/main/java/PromptTemplateExamples.java)
- Parsing and loading documents:
  - Text
  - PDF
- Splitting big documents into chunks
- Tokenizer (tokenize/detokenize, count tokens)

## Coming soon:
- In-memory vector DB (good for prototyping and testing)
- Composable flows (or chains): you will be able to define complex AI-based flows with your specific business logic
- Powerful autonomous agents that can use tools:
  - Searching internet for up-to-date information
  - Scrapping web pages
  - Executing code
  - Sending E-mails and messages
  - etc
- Integration with more LLM providers (including local models)
- Integrations with more vector DB providers
- Long-term memory for chats and agents
- Automatic Document summarization
- More document loaders
- Loading of multiple Documents from a directory
- Recursively crawl and load the whole website (if you want to kreate knowledgebase/embeddings from a website/website section)
- Loading Documents via HTTP

**Please [let us know what features you need](https://github.com/ai-for-java/ai4j/issues/new).**

## Start using
Maven:
```
<dependency>
  <groupId>dev.ai4j</groupId>
  <artifactId>ai4j</artifactId>
  <version>0.3.0</version>
</dependency>
```

Gradle:
```
implementation 'dev.ai4j:ai4j:0.3.0'
```

## See code examples
- [Chat with AI](https://github.com/ai-for-java/ai4j-examples/blob/master/src/main/java/ChatExamples.java)
- [Ask AI questions based on your document/knowledgebase](https://github.com/ai-for-java/ai4j-examples/blob/master/src/main/java/DocumentQnAExamples.java)
- [Get structured responses from AI](https://github.com/ai-for-java/ai4j-examples/blob/master/src/main/java/StructuredOutputExamples.java)
- [Prompt templates](https://github.com/ai-for-java/ai4j-examples/blob/master/src/main/java/PromptTemplateExamples.java)

## Request features
Please [let us know what features you need](https://github.com/ai-for-java/ai4j/issues/new). 

## Contribute
Please help us make this open-source library better by contributing.

## Setup project locally
```
git clone https://github.com/ai-for-java/ai4j.git

cd ai4j

./mvnw clean install
```

## Best practices
We highly recommend viewing [this amazing 90-minute tutorial](https://www.deeplearning.ai/short-courses/chatgpt-prompt-engineering-for-developers/) on prompt engineering best practices, presented by Andrew Ng (DeepLearning.AI) and Isa Fulford (OpenAI).
This course will teach you how to use LLMs efficiently and achieve the best possible results. Good investment of your time!

Here are some best practices for using LLMs:
- Be responsible. Use AI for Good.
- Be specific. The more specific your query, the best results you will get.
- Add [magical "Let’s think step by step" instruction](https://arxiv.org/pdf/2205.11916.pdf) to your prompt.
- Specify steps to achieve the desired goal yourself. This will make the LLM do what you want it to do.
- Provide examples. Sometimes it is best to show LLM a few examples of what you want instead of trying to explain it.
- Ask LLM to provide structured output (JSON, XML, etc). This way you can parse response more easily and distinguish different parts of it.
- Use unusual delimiters, such as \```triple backticks``` and \<<<triple angle brackets\>>> to help the LLM distinguish data or input from instructions.
