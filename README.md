# README
스프링 부트 + 스프링 AOP + 커스텀 어노테이션 + 로그 출력

## 요구 사항
- 컨트롤러로 들어오는 모든 요청에 대해 로그를 남긴다. 다만, 로그를 출력하지 않는 예외 메서드도 일부 존재한다.
- 컨트롤러 작업 전과 후의 데이터를 한 줄의 JSON 형식으로 출력한다.
- 로그는 콘솔에 출력된다.
- 로그의 필드로 다음의 정보를 가진다.
  - 요청 시간
  - 요청자의 IP
  - 요청 대상 - item
  - 대상에 시도한 작업 - action
  - 요청의 성공 여부, 실패한 경우 사유까지
  - 요청 uri
  - 요청 도메인 주소
  - 호출한 메서드 이름
  - (요청 세부 정보)

## 개발 환경
- Spring Boot 2.5.5
- Java 11
- H2 Database
- slf4j + logback

## 구현 내용
- annotation.Logging 커스텀 어노테이션을 구현했다. 이 어노테이션이 붙은 메서드는 로그를 출력하는 로직이 동작하는 대상이 된다.  
- 로그를 남기는 방법에는 Interceptor, Filter, AOP 등이 있다. 로그 출력을 위해서 요청자의 요청과 그 결과 모두를 확인해야 하기 때문에 스프링 AOP를 선택하여 구현했다.
- 기본 데이터 세팅을 위해 schema.sql, data.sql을 구성했다.
- 로그 출력 세팅은 logback-spring.xml에서 구성했다.
- 클라이언트의 ip를 구할 때 IPv6로 되어있어 이를 IPv4로 변환시켰다. (IntelliJ 기준) Run Configurations... -> Add VM Options -> -Djava.net.preferIPv4Stack=true 입력 후 Apply로 설정한다.
- 커스텀 어노테이션에 파라미터 구성하여 사용할 수 있다. 파라미터로 로그를 위한 정보(action, item)를 전달했다.
- customLog를 JSON 형식으로 변환하려면 ObjectMapper().writeValueAsString()에 Map을 전달해야 한다. 여기서 Map을 HashMap으로 만든 경우 put한 순서를 보장하지 않고 데이터가 섞였다. TreeMap으로 만든 경우 key의 데이터로 abc 순으로 자연 정렬되었다. LinkedHashMap으로 만든 경우 put한 순서 그대로 데이터가 쌓였다. 따라서 최종적으로 LinkedHashMap으로 Map을 구성하여 전달했다.

## 추가 내용
```java
log.info(MDC.get("album_id"));
```
- MDC를 통해 요청 컨텍스트의 다양한 정보를 로그에 포함할 수 있다. JSON Appender로 로그를 출력했을 때, message element와 별개로 존재하는 mdc element에서 데이터를 확인할 수 있다. MDC로 트랜잭션 id를 설정하는 경우가 많다.

```java
Method method = methodSignature.getMethod();

log.info("1) 호출한 메서드의 이름 : " + method.getName() + "()");
//getAllAlbums()

log.info("2) 호출 결과 타입 : " + method.getReturnType().getCanonicalName());
//org.springframework.http.ResponseEntity
        
log.info("2) 호출 결과 타입 : " + method.getGenericReturnType());
//org.springframework.http.ResponseEntity<java.util.List<com.example.demo.model.Album>>

log.info("2) 호출 결과 타입 : " + method.getReturnType().getSimpleName()); 
//ResponseEntity

log.info("3) 메서드 인자 : ");
for (Object arg : joinPoint.getArgs()) {
	log.info("	" + arg.getClass().getSimpleName() + " 타입의 값 : " + arg.toString());
}

for (Parameter parameter : method.getParameters()) {
	log.info("parameter = " + parameter.getName());
	log.info("parameter.getType() = " + parameter.getType());
}
```
- 실행되는 메서드에 대한 다양한 데이터(이름, 리턴 타입, 인자, 파라미터..)들을 확인해볼 수도 있다.

## 구현 결과
### CONSOLE
```console
2021-09-27 21:24:38.744 DEBUG 39405 --- [nio-8080-exec-1] org.hibernate.SQL                        : 
    select
        album0_.album_id as album_id1_0_,
        album0_.artist as artist2_0_,
        album0_.price as price3_0_,
        album0_.stock_quantity as stock_qu4_0_,
        album0_.title as title5_0_ 
    from
        album album0_
2021-09-27 21:24:38.796  INFO 39405 --- [nio-8080-exec-1] com.example.demo.aspect.LoggerAspect     : {"createdAt":"2021-09-27 21:24:38.567","ip":"127.0.0.1","item":"Album","action":"search","result":"success","uri":"/album/","domain":"localhost","method":"getAllAlbums"}
2021-09-27 21:27:34.729 DEBUG 39405 --- [nio-8080-exec-6] org.hibernate.SQL                        : 
    insert 
    into
        album
        (album_id, artist, price, stock_quantity, title) 
    values
        (null, ?, ?, ?, ?)
2021-09-27 21:27:34.752  INFO 39405 --- [nio-8080-exec-6] com.example.demo.aspect.LoggerAspect     : {"createdAt":"2021-09-27 21:27:34.716","ip":"127.0.0.1","item":"Album","action":"create","result":"success","uri":"/album","domain":"localhost","method":"postAlbum"}
2021-09-27 21:28:03.564 DEBUG 39405 --- [nio-8080-exec-7] org.hibernate.SQL                        : 
    select
        album0_.album_id as album_id1_0_0_,
        album0_.artist as artist2_0_0_,
        album0_.price as price3_0_0_,
        album0_.stock_quantity as stock_qu4_0_0_,
        album0_.title as title5_0_0_ 
    from
        album album0_ 
    where
        album0_.album_id=?
2021-09-27 21:28:03.577  INFO 39405 --- [nio-8080-exec-7] com.example.demo.aspect.LoggerAspect     : {"createdAt":"2021-09-27 21:28:03.557","ip":"127.0.0.1","item":"Album","action":"update","result":"fail-Not Found","uri":"/album/990","domain":"localhost","method":"putAlbum"}
```

### JSON
```json lines
{"timestamp":"2021-09-27 12:41:18.764","level":"INFO","thread":"http-nio-8080-exec-1","logger":"com.example.demo.aspect.LoggerAspect","message":"{\"createdAt\":\"2021-09-27 21:41:18.528\",\"ip\":\"127.0.0.1\",\"item\":\"Album\",\"action\":\"search\",\"result\":\"success\",\"uri\":\"/album/\",\"domain\":\"localhost\",\"method\":\"getAllAlbums\"}","context":"default","만든 이":"yoo-jaein"}
{"timestamp":"2021-09-27 12:41:40.742","level":"DEBUG","thread":"http-nio-8080-exec-2","logger":"org.hibernate.SQL","message":"\n    select\n        album0_.album_id as album_id1_0_0_,\n        album0_.artist as artist2_0_0_,\n        album0_.price as price3_0_0_,\n        album0_.stock_quantity as stock_qu4_0_0_,\n        album0_.title as title5_0_0_ \n    from\n        album album0_ \n    where\n        album0_.album_id=?","context":"default","만든 이":"yoo-jaein"}
{"timestamp":"2021-09-27 12:41:40.759","level":"INFO","thread":"http-nio-8080-exec-2","logger":"com.example.demo.aspect.LoggerAspect","message":"{\"createdAt\":\"2021-09-27 21:41:40.715\",\"ip\":\"127.0.0.1\",\"item\":\"Album\",\"action\":\"update\",\"result\":\"fail-Not Found\",\"uri\":\"/album/990\",\"domain\":\"localhost\",\"method\":\"putAlbum\"}","context":"default","만든 이":"yoo-jaein"}
{"timestamp":"2021-09-27 12:42:07.942","level":"DEBUG","thread":"http-nio-8080-exec-3","logger":"org.hibernate.SQL","message":"\n    select\n        album0_.album_id as album_id1_0_0_,\n        album0_.artist as artist2_0_0_,\n        album0_.price as price3_0_0_,\n        album0_.stock_quantity as stock_qu4_0_0_,\n        album0_.title as title5_0_0_ \n    from\n        album album0_ \n    where\n        album0_.album_id=?","context":"default","만든 이":"yoo-jaein"}
{"timestamp":"2021-09-27 12:42:07.994","level":"DEBUG","thread":"http-nio-8080-exec-3","logger":"org.hibernate.SQL","message":"\n    delete \n    from\n        album \n    where\n        album_id=?","context":"default","만든 이":"yoo-jaein"}
{"timestamp":"2021-09-27 12:42:08.012","level":"INFO","thread":"http-nio-8080-exec-3","logger":"com.example.demo.aspect.LoggerAspect","message":"{\"createdAt\":\"2021-09-27 21:42:07.927\",\"ip\":\"127.0.0.1\",\"item\":\"Album\",\"action\":\"delete\",\"result\":\"success\",\"uri\":\"/album/2\",\"domain\":\"localhost\",\"method\":\"deleteAlbum\"}","context":"default","만든 이":"yoo-jaein"}
```