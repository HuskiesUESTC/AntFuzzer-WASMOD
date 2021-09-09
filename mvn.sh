#!/usr/bin/env bash
mvn -e exec:java -Dexec.mainClass="edu.uestc.antfuzzer.Main" -Dexec.args="-fuzzingConfigFile $1"