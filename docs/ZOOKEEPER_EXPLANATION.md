# What is Zookeeper?

## 🎯 Overview

**Apache Zookeeper** is a distributed coordination service that helps manage and coordinate distributed systems. Think of it as a "phone book" or "registry" for distributed applications.

## 🔧 What Does It Do?

Zookeeper provides:
1. **Configuration Management** - Stores configuration data
2. **Synchronization** - Coordinates actions between distributed processes
3. **Naming Service** - Provides a registry for services
4. **Leader Election** - Helps elect leaders in distributed systems
5. **Distributed Locks** - Manages locks across multiple nodes

## 📊 In Our Project

In the TCG Accessories Shop, we use Zookeeper because **Kafka requires it**.

### Why Kafka Needs Zookeeper

Kafka uses Zookeeper for:

1. **Broker Management**
   - Zookeeper keeps track of which Kafka brokers are alive
   - Manages broker metadata (which topics exist, partition info)
   - Handles broker failures and recovery

2. **Configuration Management**
   - Stores Kafka cluster configuration
   - Tracks topic configurations
   - Manages access control lists (ACLs)

3. **Leader Election**
   - When a Kafka broker fails, Zookeeper helps elect a new leader
   - Coordinates partition leadership

4. **Consumer Group Coordination**
   - Helps manage consumer groups
   - Tracks which consumers are in which groups

## 🏗️ Architecture in Our Setup

```
┌─────────────┐
│  Zookeeper  │  ← Manages Kafka cluster
│  Port: 2181 │     - Broker registration
└──────┬──────┘     - Topic metadata
       │            - Leader election
       │
       ▼
┌─────────────┐
│    Kafka    │  ← Message broker
│  Port: 9092 │     - Stores messages
└─────────────┘     - Handles producers/consumers
```

## 📝 In docker-compose.yml

```yaml
zookeeper:
  image: confluentinc/cp-zookeeper:latest
  environment:
    ZOOKEEPER_CLIENT_PORT: 2181
    ZOOKEEPER_TICK_TIME: 2000

kafka:
  depends_on:
    - zookeeper
  environment:
    KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
```

**Kafka connects to Zookeeper** to:
- Register itself as a broker
- Get cluster configuration
- Coordinate with other brokers (if we had multiple)

## 🔄 How It Works Together

1. **Zookeeper starts first** (Kafka depends on it)
2. **Kafka connects to Zookeeper** on startup
3. **Kafka registers** itself with Zookeeper
4. **Kafka uses Zookeeper** to:
   - Store topic metadata
   - Track broker health
   - Coordinate partitions

## 🎓 Simple Analogy

Think of Zookeeper as:
- **A traffic controller** - Coordinates who does what
- **A phone book** - Keeps track of where everything is
- **A referee** - Makes sure everyone follows the rules

## ⚠️ Important Note

**Newer Kafka versions (2.8+) can run without Zookeeper** using KRaft mode, but:
- Most setups still use Zookeeper (more stable, widely used)
- Our setup uses the traditional Zookeeper approach
- This is the standard and recommended way for learning

## 🔍 In Practice

When you start Docker:
```bash
docker-compose up -d
```

You'll see:
- `zookeeper` container running (port 2181)
- `kafka` container running (port 9092) - connected to Zookeeper

Kafka **cannot start** without Zookeeper - that's why we have `depends_on: zookeeper` in docker-compose.yml.

## 📚 Summary

- **Zookeeper** = Coordination service for distributed systems
- **Kafka** = Message broker that needs coordination
- **Together** = Reliable, distributed messaging system
- **In our project** = Enables event-driven communication between microservices

---

**TL;DR**: Zookeeper is Kafka's "manager" - it keeps track of everything and makes sure Kafka runs smoothly in a distributed environment.

