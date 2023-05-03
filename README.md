# AI4J: Supercharge your Java application with the power of AI

Java library for smooth integration with AI tools and services.

## Current capabilities:
- Integration with [OpenAI (ChatGPT)](https://platform.openai.com/docs/introduction) for:
  - [Chats](https://platform.openai.com/docs/guides/chat)
  - [Completions](https://platform.openai.com/docs/guides/completion)
  - [Embeddings](https://platform.openai.com/docs/guides/embeddings)
- Integration with [Pinecone](https://docs.pinecone.io/docs/overview) vector database (embeddings)
- Parsing and loading documents:
  - Text
  - PDF
- Splitting big documents into overlapping chunks

Coming soon:
- Powerful autonomous agents that can use tools:
  - Searching the internet for up-to-date information
  - Scrapping web pages
  - etc
- Memory for chats and agents
- Integration with [Chroma](https://www.trychroma.com/) vector DB
- Automatic Document summarization
- Loading of multiple Documents from directory
- Loading Documents by HTTP
- etc

## Start using
Maven:
```
<dependency>
  <groupId>dev.ai4j</groupId>
  <artifactId>ai4j</artifactId>
  <version>0.0.1</version>
</dependency>
```

Gradle:
```
implementation 'dev.ai4j:ai4j:0.0.1'

```

## See code examples
- [OpenAI (ChatGPT) Chat API example](examples/OpenAiChatExample.java)
- [Chat with your files using OpenAI (ChatGPT) and Pinecone (embeddings)](examples/PdfFileOpenAiPineconeExample.java)

## Request features
Please [let us know what features do you need](https://github.com/ai-for-java/ai4j/issues/new). 

## Contribute
Please help us make this open-source library better by contributing.

## Best practices
We highly recommend viewing [this amazing 90-minute tutorial](https://www.deeplearning.ai/short-courses/chatgpt-prompt-engineering-for-developers/) on prompt engineering best practices, presented by Andrew Ng (DeepLearning.AI) and Isa Fulford (OpenAI).
This course will teach you how to use LLMs efficiently and achieve the best possible results. Good investment of your time!

Here are some best practices for using LLMs:
- Be responsible. Use AI for Good.
- Be specific. The more specific your query, the best results you will get.
- Provide examples. Sometimes it is best to show LLM a few examples of what you want instead of trying to explain it.
- Use [magical "Let’s think step by step" instruction](https://arxiv.org/pdf/2205.11916.pdf) in your prompt.
- Specify steps to achieve the goal yourself. This will make the LLM do what you want it to do.
- Ask LLM to provide structured output (JSON, XML, etc). This way you can parse response more easily and distinguish different parts of it.
- Use unusual delimiters such as \```triple quotes``` and <<<triple angle brackets>>> to help LLM distinguish data/input from instructions.
