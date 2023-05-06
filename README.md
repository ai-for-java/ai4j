# AI4J: Supercharge your Java application with the power of AI

Java library for smooth integration with AI tools and services.

## Current capabilities:
- Integration with [OpenAI (ChatGPT)](https://platform.openai.com/docs/introduction) for:
  - [Chats](https://platform.openai.com/docs/guides/chatFlow)
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
- Integration with other vector DBs
- Automatic Document summarization
- Loading of multiple Documents from directory
- Loading Documents via HTTP

**Please [let us know what features you need](https://github.com/ai-for-java/ai4j/issues/new).**

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
- [OpenAI (ChatGPT) Chat API example](examples2/OpenAiChatExample.java)
- [Chat with your files using OpenAI (ChatGPT) and Pinecone (embeddings)](examples/src/main/java/PdfFileOpenAiPineconeExample.java)

## Request features
Please [let us know what features you need](https://github.com/ai-for-java/ai4j/issues/new). 

## Contribute
Please help us make this open-source library better by contributing.

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
- Use unusual delimiters, such as \```triple backticks\``` and \<<<triple angle brackets\>>> to help the LLM distinguish data or input from instructions.

## FAQ
- **Where do I get OpenAI api key?**
See [this tutorial](https://help.socialintents.com/article/188-how-to-find-your-openai-api-key-for-chatgpt)
 