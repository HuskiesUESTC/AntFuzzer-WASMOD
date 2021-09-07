#!/usr/bin/env bash
mvn clean
mvn compile
mvnDebug -e exec:java -Dexec.mainClass="edu.uestc.antfuzzer.Main" -Dexec.args="-fuzzingConfigFile ./config/test.json"