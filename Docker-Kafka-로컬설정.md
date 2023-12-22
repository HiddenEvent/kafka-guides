# 로컬에서 Docker를 이용한 Kafka 설정

## 브로커와 주키퍼 설정

- **docker-compose.yml**이 위치한 경로로 이동한 후 아래 명령어를 실행합니다.

```
docker-compose up
```

## 메시지 생성 및 소비

- 아래 명령어를 실행하여 컨테이너로 이동합니다.

```
docker exec -it kafka1 bash
```

- **kafka-topics** 명령어를 사용하여 Kafka 토픽을 생성합니다.
  - **kafka1:29092**는 docker-compose.yml 파일의 **KAFKA_ADVERTISED_LISTENERS**를 참조합니다.

```
kafka-topics --bootstrap-server kafka1:29092 \
             --create \
             --topic test-topic \
             --replication-factor 1 --partitions 1
```

- 토픽에 메시지를 생성합니다.

```
docker exec --interactive --tty kafka1  \
kafka-console-producer --bootstrap-server kafka1:29092 \
                       --topic test-topic
```

- 토픽에서 메시지를 소비합니다.

```
docker exec --interactive --tty kafka1  \
kafka-console-consumer --bootstrap-server kafka1:29092 \
                       --topic test-topic \
                       --from-beginning
```

## 키와 값이 있는 메시지 생성 및 소비

- 키와 값이 있는 메시지를 토픽에 생성합니다.

```
docker exec --interactive --tty kafka1  \
kafka-console-producer --bootstrap-server kafka1:29092 \
                       --topic test-topic \
                       --property "key.separator=-" --property "parse.key=true"
```

- 키와 값이 있는 메시지를 토픽에서 소비합니다.

```
docker exec --interactive --tty kafka1  \
kafka-console-consumer --bootstrap-server kafka1:29092 \
                       --topic test-topic \
                       --from-beginning \
                       --property "key.separator= - " --property "print.key=true"
```

### 컨슈머 그룹을 사용한 메시지 소비

```
docker exec --interactive --tty kafka1  \
kafka-console-consumer --bootstrap-server kafka1:29092 \
                       --topic test-topic --group console-consumer-41911\
                       --property "key.separator= - " --property "print.key=true"
```

- 예시 메시지:

```
a-abc
b-bus
```

### 헤더가 있는 메시지 소비

```
docker exec --interactive --tty kafka1  \
kafka-console-consumer --bootstrap-server kafka1:29092 \
                       --topic library-events.DLT \
                       --property "print.headers=true" --property "print.timestamp=true" 
```

- 예시 메시지:

```
a-abc
b-bus
```

### 3개의 브로커로 Kafka 클러스터 설정

- 아래 명령어를 실행하면 3개의 브로커로 구성된 Kafka 클러스터가 생성됩니다.

```
docker-compose -f docker-compose-multi-broker.yml up
```

- 복제 팩터를 3으로 설정하여 토픽을 생성합니다.

```
docker exec --interactive --tty kafka1  \
kafka-topics --bootstrap-server kafka1:29092 \
             --create \
             --topic test-topic \
             --replication-factor 3 --partitions 3
```

- 토픽에 메시지를 생성합니다.

```
docker exec --interactive --tty kafka1  \
kafka-console-producer --bootstrap-server localhost:9092,kafka2:19093,kafka3:19094 \
                       --topic test-topic
```

- 토픽에서 메시지를 소비합니다.

```
docker exec --interactive --tty kafka1  \
kafka-console-consumer --bootstrap-server localhost:9092,kafka2:19093,kafka3:19094 \
                       --topic test-topic \
                       --from-beginning
```
#### 멀티 Kafka 클러스터에서의 로그 파일

- 각 파티션에 대한 로그 파일이 Kafka 클러스터의 각 브로커 인스턴스에 생성됩니다.
 -  컨테이너 **kafka1**에 로그인합니다.
  ```
  docker exec -it kafka1 bash
  ```
 -  컨테이너 **kafka2**에 로그인합니다.
  ```
  docker exec -it kafka2 bash
  ```

- Kafka 클러스터를 종료합니다.

```
docker-compose -f docker-compose-multi-broker.yml down
```

### min.insync.replica 설정

- 토픽 - test-topic

```
docker exec --interactive --tty kafka1  \
kafka-configs  --bootstrap-server localhost:9092 --entity-type topics --entity-name test-topic \
--alter --add-config min.insync.replicas=2
```

- 토픽 - library-events

```
docker exec --interactive --tty kafka1  \
kafka-configs  --bootstrap-server localhost:9092 --entity-type topics --entity-name library-events \
--alter --add-config min.insync.replicas=2
```
## 고급 Kafka 명령어

### 클러스터의 토픽 목록 보기

```
docker exec --interactive --tty kafka1  \
kafka-topics --bootstrap-server kafka1:29092 --list

```

### 토픽 설명

- 모든 Kafka 토픽을 설명하는 명령어입니다.

```
docker exec --interactive --tty kafka1  \
kafka-topics --bootstrap-server kafka1:29092 --describe
```

- 특정 Kafka 토픽을 설명하는 명령어입니다.

```
docker exec --interactive --tty kafka1  \
kafka-topics --bootstrap-server kafka1:29092 --describe \
--topic test-topic
```

### 토픽 파티션 변경

```
docker exec --interactive --tty kafka1  \
kafka-topics --bootstrap-server kafka1:29092 \
--alter --topic test-topic --partitions 40
```

### 컨슈머 그룹 보기

```
docker exec --interactive --tty kafka1  \
kafka-consumer-groups --bootstrap-server kafka1:29092 --list
```

#### 컨슈머 그룹과 그들의 오프셋

```
docker exec --interactive --tty kafka1  \
kafka-consumer-groups --bootstrap-server kafka1:29092 \
--describe --group console-consumer-41911
```

## 로그 파일 및 관련 설정

- 컨테이너에 로그인합니다.

```
docker exec -it kafka1 bash
```

- 설정 파일은 아래 경로에 있습니다.

```
/etc/kafka/server.properties
```

- 로그 파일은 아래 경로에 있습니다.

```
/var/lib/kafka/data/
```

### 커밋 로그를 어떻게 보나요?

```
docker exec --interactive --tty kafka1  \
kafka-run-class kafka.tools.DumpLogSegments \
--deep-iteration \
--files /var/lib/kafka/data/test-topic-0/00000000000000000000.log

```