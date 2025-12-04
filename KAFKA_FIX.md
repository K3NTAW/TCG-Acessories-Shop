# Kafka Fix Applied

## Problem
Kafka container was failing with error:
```
Error: environment variable "KAFKA_PROCESS_ROLES" is not set
```

## Root Cause
The latest Confluent Kafka image (`latest` tag) uses **KRaft mode** (Kafka without Zookeeper), which requires the `KAFKA_PROCESS_ROLES` environment variable.

## Solution
Pinned Kafka and Zookeeper to version **7.5.0**, which uses the traditional Zookeeper-based setup:

```yaml
zookeeper:
  image: confluentinc/cp-zookeeper:7.5.0

kafka:
  image: confluentinc/cp-kafka:7.5.0
```

This version:
- ✅ Uses Zookeeper (as required for our setup)
- ✅ Works with our existing configuration
- ✅ No need for KRaft mode configuration

## Status
✅ Kafka should now start successfully!

## Verify
```bash
cd docker
docker-compose ps
```

All containers should show "Up" status.

