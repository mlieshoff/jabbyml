[![](https://img.shields.io/badge/java-packagecloud.io-844fec.svg)](https://packagecloud.io/)
[![Nightlies](https://github.com/mlieshoff/jabbyml/actions/workflows/nightlies.yml/badge.svg)](https://github.com/mlieshoff/jabbyml/actions/workflows/nightlies.yml)

# jabbyml 1.0.0
A Java Wrapper For TabbyML Server API

## Why we don't use the Swagger scheme?

A big sorry for that, but the quality of that scheme changes from day to day.
Another big sorry, but the OpenApi Java generator is producing code quality we like much.
That's simple why :) If you think the same way (it may differ from case to case of course), feel free to continue using our wrapper.

## Why we moved to the amazing services of packagecloud?

We moved to packagecloud.io because the bintray closed their nice hosting... And packagecloud.io is a really nice place to be :)

## Join us on Discord

https://discord.gg/WNb5c8hn

## Simplest Usage

Note: Please combine the builder methods as it makes sense. The demonstrated is showing only all possibilities.
For more information please check

https://tabby.tabbyml.com/api

Use one of these endpoints:

Official endpoint
```
    https://my_host_with_tabbml_server:8080/
```

Use built-in standard connector
```java
Connector connector = new StandardConnector();
```

or use the new filesystem cached connector
```java
Connector connector = new FilesystemCachedConnector("jabbyml")
```

or use custom implementation
```java
    Connector connector = new Connector() {
        @Override
        public <T extends IResponse> T get(RequestContext requestContext) throws ConnectorException {
                // do not forget to use auth header with *Bearer*
                String authHeader =  "Authorization: Bearer " + requestContext.getApiKey();
            }
        }
    );
```

connect to the api with creating a *jabbyML* instance.
```java
    JabbyML jabbyML = new JabbyML("https://bsproxy.royaleapi.dev/v1", "my-api-key", connector);
```

list all supported apis
```java
    System.out.println(jabbyML.listApis());
```

### List of APIs and example usages

#### CompletionApi
```java
    // create an instance for the api
    CompletionApi api = jabbyML.getApi(CompletionApi.class);
```
```java
    // find
    CompletionResponse response = api.find(CompletionRequest.builder()
           .language()
           .segments()
           // store raw response
           .storeRawResponse()
        .build()
    ).get();
```
#### EventApi
```java
    // create an instance for the api
    EventApi api = jabbyML.getApi(EventApi.class);
```
```java
    // find
    EventResponse response = api.find(EventRequest.builder()
           .type()
           .completionId()
           .choiceIndex()
           .viewIndex()
           .elapsed()
           // store raw response
           .storeRawResponse()
        .build()
    ).get();
```
#### HealthApi
```java
    // create an instance for the api
    HealthApi api = jabbyML.getApi(HealthApi.class);
```
```java
    // get
    HealthResponse response = api.get(HealthRequest.builder()
           // store raw response
           .storeRawResponse()
        .build()
    ).get();
```
#### ChatCompletionApi
```java
    // create an instance for the api
    ChatCompletionApi api = jabbyML.getApi(ChatCompletionApi.class);
```
```java
    // get
    ChatCompletionResponse response = api.get(ChatCompletionRequest.builder()
           .messages()
           // store raw response
           .storeRawResponse()
        .build()
    ).get();
```
#### SearchApi
```java
    // create an instance for the api
    SearchApi api = jabbyML.getApi(SearchApi.class);
```
```java
    // search
    SearchResponse response = api.search(SearchRequest.builder()
           .q()
           .limit()
           .offset()
           // store raw response
           .storeRawResponse()
        .build()
    ).get();
```

## Add or replace registered API's

```java
    JabbyML jabbyML = new JabbyML(...);
    jabbyML.register(MyApi.class, MyApiImpl.class.getName());
    MyApi myApi = jabbyML.getApi(MyApi.class);
    GoodiesResponse goodiesResponse = myApi.findAllGoodies(new GoodiesRequest(...))).get();
```

Custom API implementations just need to inherit from *BaseApi*.

## Asynchronous usage

All requests are returning *java.concurrent.Future*. The execution will be asynchronous by default.

## How to bind the packagecloud repository

```xml
    <repositories>
        <repository>
            <id>packagecloud-jabbyml</id>
            <url>https://packagecloud.io/mlieshoff/jabbyml/maven2</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>
```

## Add dependency

to Gradle:
```groovy
    implementation group: 'jabbyml', name: 'jabbyml', version: '1.0.0'
```

to Maven:
```xml
    <dependency>
        <groupId>jabbyml</groupId>
        <artifactId>jabbyml</artifactId>
        <version>1.0.0</version>
    </dependency>
```

## Continuous Integration

https://github.com/mlieshoff/jabbyml/actions

## Repository

https://packagecloud.io/mlieshoff/jabbyml

## Logging

We are using SLF4j.

## Usage of RoyaleApi proxy

This wrapper can be easyly connected to the proxy of our friends on RoyaleAPI. Please proceed first the steps described here:

https://docs.royaleapi.com/#/proxy

Then initialize an instance of class Api like that:

```java
    JabbyML jabbyML = new JabbyML("https://bsproxy.royaleapi.dev/v1", API_KEY, CONNECTOR);
```

That's all, enjoy :)

## Contributing

1. Set up the formatting hook, via copying the files under ./jabbyml/hooks to ./jabbyml/.git/hooks